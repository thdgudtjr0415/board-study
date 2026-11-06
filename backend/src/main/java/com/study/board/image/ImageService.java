package com.study.board.image;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class ImageService {
    // 허용하는 목록 (바뀌지 않는 값이라 static final, 이름은 대문자)
    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png");
    private static final List<String> ALLOWED_TYPES = List.of("image/jpeg", "image/png");
    private final ImageStorage imageStorage;

    @Autowired
    public ImageService(ImageStorage imageStorage) {
        this.imageStorage = imageStorage;
    }

    public void validate(MultipartFile file) {
        // 1) 파일이 비었으면 400
        // 2) 원래 파일 이름을 꺼낸다. 이름이 없거나 "."이 없으면 400 (확장자가 없음)
        // 3) 확장자를 꺼내서(마지막 "." 뒤, 소문자로) 허용 목록에 없으면 400
        // 4) Content-Type이 없거나 허용 목록에 없으면 400

        if(file == null || file.isEmpty()){
            throw new InvalidImageException("빈 파일입니다.");
        }
        String fileName = file.getOriginalFilename(); // getOriginalFilename 해야 .png 까지 불러옴
        if(fileName == null || fileName.isBlank() || !fileName.contains(".")){
            throw new InvalidImageException("파일 확장자가 없습니다.");
        }
        String ext = fileName.substring(fileName.lastIndexOf(".")+1).toLowerCase();
        if(!ALLOWED_EXTENSIONS.contains(ext)){
            throw new InvalidImageException("허용하지 않는 확장자입니다: " + ext);
        }
        String type = file.getContentType();
        if(type == null || !ALLOWED_TYPES.contains(type)){
            throw new InvalidImageException("허용하지 않는 파일 종류입니다: " + type);
        }
    }
    public String upload(MultipartFile file){
        validate(file);
        String fileName = file.getOriginalFilename();
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return imageStorage.save(file, ext);
    }

}
