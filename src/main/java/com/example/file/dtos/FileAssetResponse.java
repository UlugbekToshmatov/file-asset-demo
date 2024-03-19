package com.example.file.dtos;

import com.example.file.enums.FileAssetType;
import com.example.file.enums.FileType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileAssetResponse {
    private Long id;
    private Long parentId;
    private String name;
    private FileAssetType fileAssetType;
    private String path;
    private FileType fileType;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
}
