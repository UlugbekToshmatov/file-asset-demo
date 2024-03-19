package com.example.file.services;

import com.example.file.dtos.FileItemResponse;
import com.example.file.dtos.NameRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface FileItemService {
    FileItemResponse createFile(Long folderId, MultipartFile multipartFile);
    ResponseEntity<?> getByFileAssetId(Long id);
    FileItemResponse updateByFileAssetId(Long id, NameRequest request);
    void deleteByFileAssetId(Long id);
}
