package com.pinterest.service;

import com.pinterest.domain.FileEntity;
import com.pinterest.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileService {

    @Value("${file.dir}")
    private String fileRootDir;
    private final FileRepository fileRepository;

    @Transactional
    public FileEntity saveFile(MultipartFile file) {
        if (file.isEmpty()) {
            return null;
        }

        String fileName = file.getOriginalFilename();
        String uuid = UUID.randomUUID().toString();
        String extension = fileName.substring(fileName.lastIndexOf("."));
        String savedName = uuid + extension;
        String savedPath = fileRootDir + savedName;

        try {
            file.transferTo(new File(savedPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        FileEntity entity = FileEntity.of(fileName, savedName, savedPath);

        return fileRepository.save(entity);
    }
}
