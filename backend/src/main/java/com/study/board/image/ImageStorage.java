package com.study.board.image;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface ImageStorage {
    String save(MultipartFile file, String extension);
    Resource load(String fileName);
    void delete(String fileName);   // 저장해 둔 이미지를 이름으로 지운다
}
