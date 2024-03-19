package com.example.file.repositories;

import com.example.file.entities.FileAsset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileAssetRepository extends JpaRepository<FileAsset, Long> {
    boolean existsByPathAndDeletedFalse(String path);
    boolean existsByParentIsNullAndNameAndDeletedFalse(String name);
    boolean existsByParentIdAndNameAndDeletedFalse(Long parentId, String name);
    boolean existsByIdAndDeletedFalse(Long id);
    Optional<FileAsset> findByIdAndDeletedFalse(Long id);
    Optional<FileAsset> findByParentIdAndName(Long parentId, String name);
    List<FileAsset> findAllByParentIdAndDeletedFalseOrderByFileAssetTypeDescName(Long parentId);


}
