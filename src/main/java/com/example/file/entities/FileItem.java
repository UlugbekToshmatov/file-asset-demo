package com.example.file.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FileItem extends BaseEntity {
    private String name;

    @ManyToOne
    private FileAsset fileAsset;

    @Column(nullable = false)
    private String fileExtension;

    @Column(nullable = false)
    private String fileContentType;

    @Column(nullable = false)
    private Long fileSize;

    @Column(nullable = false)
    private String uploadFileName;
}
