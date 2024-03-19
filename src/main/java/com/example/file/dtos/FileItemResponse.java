package com.example.file.dtos;

import com.example.file.enums.FileAssetType;
import com.example.file.enums.FileType;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class FileItemResponse {
    private Long id;
    private Long folderId;
    private FileAssetType fileAssetType;
    private FileType fileType;
    private String name;
    private String fileContentType;
    private String fileExtension;
    private String fileSize;
}
