package com.study.board.image;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Component
public class LocalImageStorage implements ImageStorage {

    // application.yaml의 image.upload-dir 값("uploads")을 읽어서 넣는다
    // (DB 비밀번호를 읽던 ${...}와 같은 방식)
    @Value("${image.upload-dir}")
    private String uploadDir;

    @Override
    public String save(MultipartFile file, String extension) {
        // ㄱ) 저장 폴더 경로를 만든다
        Path dir = Path.of(uploadDir);

        String fileName;
        // ㄴ) 폴더가 없으면 만든다 (이미 있으면 그대로 둔다). IOException이 날 수 있다
        try{
            Files.createDirectories(dir);
        // ㄷ) 파일 이름을 만든다: UUID 글자 + "." + 확장자
            fileName = UUID.randomUUID().toString() + "." + extension;
        // ㄹ) 최종 경로 = 폴더 + 파일 이름
            Path filePath = dir.resolve(fileName);
        // ㅁ) 업로드된 파일의 내용을 그 경로에 쓴다 (file.transferTo(경로))
            file.transferTo(filePath);
        }catch (IOException e){
            throw new RuntimeException("이미지를 저장하지 못했습니다." , e);
        }
        // ㅂ) 저장한 파일 이름을 돌려준다
        return fileName;
    }

    @Override
    public Resource load(String fileName) {
        // 저장할 때와 같은 폴더 경로를 만든다
        Path dir = Path.of(uploadDir);
        // 폴더 + 파일 이름으로 파일의 경로를 만든다 (save와 같은 방식)
        Path filePath = dir.resolve(fileName);
        // FileSystemResource : 그 경로의 파일을 가리키는 Resource를 만들어 돌려준다
        return new FileSystemResource(filePath);
    }
}