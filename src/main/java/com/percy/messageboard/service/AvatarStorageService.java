package com.percy.messageboard.service;

import org.springframework.web.multipart.MultipartFile;

public interface AvatarStorageService {

    String store(MultipartFile file);

    void delete(String filename);
}
