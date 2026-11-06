package com.study.board.image;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/images")
public class ImageController {
    private final ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    // 파일을 받는 API. FORM-DATA 로 보낸 "file" 이라는 이름의 파일을 받는다
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public String upload(@RequestParam("file")MultipartFile file){
        // validate
        return imageService.upload(file); // 이쪽에 이미 validate 있음
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<Resource> get(@PathVariable String fileName) {
        // 1) 서비스에서 Resource를 꺼낸다
        Resource file = imageService.load(fileName);
        // 2) 확장자로 종류를 정한다: fileName이 ".png"로 끝나면 MediaType.IMAGE_PNG, 아니면 MediaType.IMAGE_JPEG
        MediaType imageType = fileName.endsWith(".png") ? MediaType.IMAGE_PNG : MediaType.IMAGE_JPEG;
        // 3) ResponseEntity.ok().contentType(종류).body(resource) 로 돌려준다
        return ResponseEntity.ok().contentType(imageType).body(file);
    }
}
