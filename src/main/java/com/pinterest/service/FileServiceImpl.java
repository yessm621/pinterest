package com.pinterest.service;

import com.pinterest.domain.FileEntity;
import com.pinterest.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.UUID;

@Service
@Profile("local")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileServiceImpl implements FileService {

    @Value("${file.dir}")
    private String fileRootDir;
    private final FileRepository fileRepository;

    public String getFullPath(String fileName) {
        return fileRootDir + fileName;
    }

    @Override
    @Transactional
    public FileEntity upload(MultipartFile file) {
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

    @Override
    public void deleteImage(String filename) {
        String srcFileName = null;

        try {
            srcFileName = URLDecoder.decode(filename, "UTF-8");
            File file = new File(fileRootDir + File.separator + srcFileName);
            file.delete();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
