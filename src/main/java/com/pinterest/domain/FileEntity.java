package com.pinterest.domain;

import lombok.Getter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "file")
@Getter
@ToString
public class FileEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long id;

    @Column(length = 2000)
    private String fileName;
    @Column(length = 2000)
    private String savedName;
    @Column(length = 2000)
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
