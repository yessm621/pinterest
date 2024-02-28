package com.pinterest.dto;

import com.pinterest.domain.FileEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class FileDto implements Serializable {

    private String fileName;
    private String savedName;
    private String savedPath;

    public static FileDto from(FileEntity entity) {
        return new FileDto(
                entity.getFileName(),
                entity.getSavedName(),
                entity.getSavedPath()
        );
    }
}
