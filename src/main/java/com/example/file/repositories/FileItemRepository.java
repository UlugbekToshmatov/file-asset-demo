package com.example.file.repositories;

import com.example.file.entities.FileAsset;
import com.example.file.entities.FileItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileItemRepository extends JpaRepository<FileItem, Long> {
//    boolean existsByFileAssetAndNameAndFileExtensionAndDeletedFalse(FileAsset fileAsset, String name, String fileExtension);
    List<FileItem> findByNameAndDeletedFalse(String name);
    Optional<FileItem> findByFileAssetAndDeletedFalse(FileAsset fileAsset);
    Optional<FileItem> findByIdAndDeletedFalse(Long id);
}
