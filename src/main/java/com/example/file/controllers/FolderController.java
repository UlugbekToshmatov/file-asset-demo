package com.example.file.controllers;

import com.example.file.dtos.FileAssetResponse;
import com.example.file.dtos.NameRequest;
import com.example.file.services.FolderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/folder")
@RequiredArgsConstructor
public class FolderController {
    private final FolderService folderService;

    @PostMapping("{parent-id}")
    public ResponseEntity<FileAssetResponse> createFolder(
        @PathVariable("parent-id") Long parentId,
        @RequestBody NameRequest request
    ) {
        return ResponseEntity.ok(folderService.createFolder(parentId, request));
    }

    @GetMapping("{id}")
    public ResponseEntity<List<FileAssetResponse>> getByFolderId(@PathVariable Long id) {
        return ResponseEntity.ok(folderService.getByFolderId(id));
    }

    @PutMapping("{folder-id}")
    public ResponseEntity<FileAssetResponse> update(
        @PathVariable("folder-id") Long folderId,
        @RequestBody NameRequest request
    ) {
        return ResponseEntity.ok(folderService.update(folderId, request));
    }

    @DeleteMapping("{folder-id}")
    @ResponseStatus(value = HttpStatus.OK)
    public void delete(@PathVariable("folder-id") Long folderId) {
        folderService.delete(folderId);
    }
}
