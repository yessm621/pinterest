package com.pinterest.controller;

import com.pinterest.service.FileService;
import com.pinterest.service.S3ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;

@Controller
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final S3ImageService s3ImageService;

    @GetMapping("/attach/{filename}")
    @ResponseBody
    public Resource getImage(@PathVariable String filename) throws MalformedURLException {
        return new UrlResource("file:" + fileService.getFullPath(filename));
    }

    @PostMapping("/s3/upload")
    public ResponseEntity<?> s3Upload(@RequestPart(value = "image", required = false) MultipartFile image) {
        String profileImage = s3ImageService.upload(image);
        return ResponseEntity.ok(profileImage);
    }

    @GetMapping("/s3/delete")
    public ResponseEntity<?> s3delete(@RequestParam String addr) {
        s3ImageService.deleteImageFromS3(addr);
        return ResponseEntity.ok(null);
    }
}
