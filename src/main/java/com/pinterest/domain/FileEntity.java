package com.pinterest.domain;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Table(name = "file")
@Getter
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long id;

    private String fileName;
    private String savedName;
    private String savedPath;

    protected FileEntity() {
    }

    private FileEntity(String fileName, String savedName, String savedPath) {
        this.fileName = fileName;
        this.savedName = savedName;
        this.savedPath = savedPath;
    }

    public static FileEntity of(String fileName, String savedName, String savedPath) {
        return new FileEntity(fileName, savedName, savedPath);
    }
}
