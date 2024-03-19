package com.example.file.entities;

import com.example.file.enums.FileAssetType;
import com.example.file.enums.FileType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
//@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"parent_id", "file_asset_type", "name"}))
public class FileAsset extends BaseEntity {
    @Column(nullable = false)
    private String name;

    @Column(nullable = false, name = "file_asset_type")
    @Enumerated(EnumType.STRING)
    private FileAssetType fileAssetType;

    @Column(nullable = false)
    private String path;

    @ManyToOne
    private FileAsset parent;

    @Enumerated(EnumType.STRING)
    private FileType fileType;
}
