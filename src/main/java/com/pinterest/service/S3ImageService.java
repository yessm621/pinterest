package com.pinterest.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import com.pinterest.domain.FileEntity;
import com.pinterest.error.PinterestException;
import com.pinterest.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Profile("prod")
@RequiredArgsConstructor
@Slf4j
public class S3ImageService implements FileService {

    private final FileRepository fileRepository;
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    public String getFullPath(String fileName) {
        return fileName;
    }

    public FileEntity upload(MultipartFile image) {
        if (image.isEmpty() || Objects.isNull(image.getOriginalFilename())) {
            throw new PinterestException("S3 에러, 파일이 없습니다.");
        }
        return this.uploadImage(image);
    }

    private FileEntity uploadImage(MultipartFile image) {
        this.validateImageFileExtension(image.getOriginalFilename());
        try {
            return this.uploadImageToS3(image);
        } catch (IOException e) {
            throw new PinterestException("S3 에러, 이미지 업로드 시 에러가 발생하였습니다.");
        }
    }

    private void validateImageFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            throw new PinterestException("S3 에러, 파일 형식이 잘못되었습니다.");
        }

        String extension = filename.substring(lastDotIndex + 1).toLowerCase();
        List<String> allowedExtensionList = Arrays.asList("jpg", "jpeg", "png", "gif");

        if (!allowedExtensionList.contains(extension)) {
            throw new PinterestException("S3 에러, 파일 형식은 jpg, jpeg, png, gif 이어야 합니다.");
        }
    }

    private FileEntity uploadImageToS3(MultipartFile image) throws IOException {
        String originalFilename = image.getOriginalFilename(); //원본 파일 명
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".")); //확장자 명

        String s3FileName = UUID.randomUUID().toString().substring(0, 10) + originalFilename; //변경된 파일 명

        InputStream is = image.getInputStream();
        byte[] bytes = IOUtils.toByteArray(is);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("image/" + extension);
        metadata.setContentLength(bytes.length);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        String savedName = null;
        try {
            PutObjectRequest putObjectRequest =
                    new PutObjectRequest(bucketName, s3FileName, byteArrayInputStream, metadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead);
            amazonS3.putObject(putObjectRequest); // put image to S3

            savedName = amazonS3.getUrl(bucketName, s3FileName).toString();
        } catch (Exception e) {
            throw new PinterestException("S3 파일 에러가 발생하였습니다.");
        } finally {
            byteArrayInputStream.close();
            is.close();
        }

        return save(originalFilename, savedName);
    }

    @Transactional
    public FileEntity save(String originalFilename, String savedName) {
        FileEntity entity = FileEntity.of(originalFilename, savedName, "");
        return fileRepository.save(entity);
    }

    public void deleteImage(String imageAddress) {
        String key = getKeyFromImageAddress(imageAddress);
        try {
            amazonS3.deleteObject(new DeleteObjectRequest(bucketName, key));
        } catch (Exception e) {
            throw new PinterestException("S3 에러, 이미지 삭제 시 오류가 발생하였습니다.");
        }
    }

    private String getKeyFromImageAddress(String imageAddress) {
        try {
            URL url = new URL(imageAddress);
            String decodingKey = URLDecoder.decode(url.getPath(), "UTF-8");
            return decodingKey.substring(1); // 맨 앞의 '/' 제거
        } catch (MalformedURLException | UnsupportedEncodingException e) {
            throw new PinterestException("S3 파일 에러가 발생하였습니다.");
        }
    }
}
