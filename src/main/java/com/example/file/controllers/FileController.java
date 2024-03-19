package com.example.file.controllers;

import com.example.file.dtos.FileItemResponse;
import com.example.file.dtos.NameRequest;
import com.example.file.services.FileItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("api/v1/file")
@RequiredArgsConstructor
public class FileController {
    private final FileItemService fileItemService;

    @PostMapping("{folder-id}")
    public ResponseEntity<FileItemResponse> createFile(@PathVariable("folder-id") Long folderId, @RequestParam MultipartFile multipartFile) {
        return ResponseEntity.ok(fileItemService.createFile(folderId, multipartFile));
    }

    @GetMapping("{file-asset-id}")
    public ResponseEntity<?> getByFileAssetId(@PathVariable("file-asset-id") Long id) {
        return fileItemService.getByFileAssetId(id);
    }

    @PatchMapping("{file-asset-id}")
    public ResponseEntity<FileItemResponse> updateByFileAssetId(@PathVariable("file-asset-id") Long id, @RequestBody NameRequest request) {
        return ResponseEntity.ok(fileItemService.updateByFileAssetId(id, request));
    }

    @DeleteMapping("{file-asset-id}")
    public void deleteByFileAssetId(@PathVariable("file-asset-id") Long id) {
        fileItemService.deleteByFileAssetId(id);
    }
}
