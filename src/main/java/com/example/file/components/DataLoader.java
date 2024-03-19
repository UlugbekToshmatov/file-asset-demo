package com.example.file.components;

import com.example.file.entities.FileAsset;
import com.example.file.enums.FileAssetType;
import com.example.file.repositories.FileAssetRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.text.MessageFormat;

@Component
@RequiredArgsConstructor
public class DataLoader {
    private final FileAssetRepository fileAssetRepository;
    @Value("${file-item.upload.folder}")
    private String DEFAULT_PATH;

    @PostConstruct
    private void run() {

        if (!fileAssetRepository.existsByPathAndDeletedFalse(DEFAULT_PATH)) {
            final String DEFAULT_FOLDER = "default";
            String location = MessageFormat.format("{0}\\{1}", DEFAULT_PATH, DEFAULT_FOLDER);

            fileAssetRepository.save(
                FileAsset.builder()
                    .name(DEFAULT_FOLDER)
                    .fileAssetType(FileAssetType.FOLDER)
                    .fileType(null)
                    .parent(null)
                    .path(DEFAULT_PATH)
                    .build()
            );

            new File(location).mkdir();
        }
    }
}
