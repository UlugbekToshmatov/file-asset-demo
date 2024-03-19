package com.example.file.services;

import com.example.file.dtos.FileAssetResponse;
import com.example.file.dtos.NameRequest;

import java.util.List;

public interface FolderService {
    FileAssetResponse createFolder(Long parentId, NameRequest request);
    List<FileAssetResponse> getByFolderId(Long id);
    FileAssetResponse update(Long folderId, NameRequest request);
    void delete(Long folderId);
}
