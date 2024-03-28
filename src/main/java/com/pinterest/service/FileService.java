package com.pinterest.service;

import com.pinterest.domain.FileEntity;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    FileEntity upload(MultipartFile image);

    String getFullPath(String filename);

    void deleteImage(String filename);
}
