package com.study.board.image;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

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
    public Map<String, Object> upload(@RequestParam("file")MultipartFile file){
        // vaildate
        imageService.validate(file);
        // 아직 저장하지 않고, 받은 파일의 정보만 돌려준다.
        return Map.of(
                        "originalName", file.getOriginalFilename(),   // 사용자 컴퓨터에서의 원래 파일 이름
                        "contentType", file.getContentType(),         // 요청에 적혀 온 종류 (image/png 등)
                        "size", file.getSize()                        // 크기(바이트)
                );
    }
}
