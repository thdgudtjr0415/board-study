package com.study.board.image;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

@Service
@Slf4j
public class ImageService {
    // 허용하는 목록 (바뀌지 않는 값이라 static final, 이름은 대문자)
    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png");
    private static final List<String> ALLOWED_TYPES = List.of("image/jpeg", "image/png");
    private final ImageStorage imageStorage;

    // UUID(8-4-4-4-12자리 16진수) + "." + jpg, jpeg, png
    // 이미지 이름을 정규표현식으로 검사(post 보낼때 아무 글자를 넣어도 통과되는 문제 때문에)
    private static final Pattern IMAGE_NAME = Pattern.compile(
            "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\\.(jpg|jpeg|png)$");

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

    public Resource load(String fileName) {
        // ㄱ) 이름이 우리가 만든 모양이 아니면 404 (../ 같은 경로 조작을 막는다)
        if(!IMAGE_NAME.matcher(fileName).matches()){
            throw new ImageNotFoundException("이미지를 찾을 수 없습니다.");
        }
        // ㄴ) 저장소에서 꺼낸다
        Resource file = imageStorage.load(fileName);
        if(!file.exists()){
            throw new ImageNotFoundException("이미지를 찾을 수 없습니다.");
        }
        // ㄹ) Resource를 돌려준다
        return file;
    }

}
