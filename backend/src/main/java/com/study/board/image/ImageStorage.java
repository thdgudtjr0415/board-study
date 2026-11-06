package com.study.board.image;

import org.springframework.web.multipart.MultipartFile;

public interface ImageStorage {
    String save(MultipartFile file, String extension);
}
