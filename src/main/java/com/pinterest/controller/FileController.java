package com.pinterest.controller;

import com.pinterest.dto.FileDto;
import com.pinterest.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class FileController {

    private final FileRepository fileRepository;

    @GetMapping("/image/{fileId}")
    @ResponseBody
    public Resource downloadImage(@PathVariable("fileId") Long id) throws IOException {
        FileDto file = fileRepository.findById(id).map(FileDto::from).orElse(null);
        return new UrlResource("file:" + file.getSavedPath());
    }
}
