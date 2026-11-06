package com.study.board.image;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
}
