package com.example.file.services.impls;

import com.example.file.dtos.FileItemResponse;
import com.example.file.dtos.NameRequest;
import com.example.file.entities.FileAsset;
import com.example.file.entities.FileItem;
import com.example.file.enums.FileAssetType;
import com.example.file.enums.FileType;
import com.example.file.repositories.FileAssetRepository;
import com.example.file.repositories.FileItemRepository;
import com.example.file.services.FileItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileUrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FileItemServiceImpl implements FileItemService {
    private final FileAssetRepository fileAssetRepository;
    private final FileItemRepository fileItemRepository;

    @Value("${file-item.upload.folder}")
    private String DEFAULT_PATH;

    @Transactional
    @Override
    public FileItemResponse createFile(Long folderId, MultipartFile multipartFile) {
        FileAsset parent = fileAssetRepository.findByIdAndDeletedFalse(folderId).orElseThrow(
            () -> new RuntimeException(MessageFormat.format("Folder with id={0} not found", folderId))
        );

        FileAsset savedFileAsset;
        FileItem savedFileItem;
        String originalFileName = multipartFile.getOriginalFilename();
//        assert originalFileName != null;
        if (originalFileName == null)
            throw new RuntimeException("Missing original filename!");

        List<FileItem> fileItems = fileItemRepository.findByNameAndDeletedFalse(originalFileName);
        String name = originalFileName.substring(0, originalFileName.lastIndexOf("."));
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
//        if (fileItems.isEmpty()) {
//            String dir = MessageFormat.format("{0}\\{1}", parent.getPath(), parent.getName());
//            savedFileAsset = fileAssetRepository.save(
//                FileAsset.builder()
//                    .name(originalFileName)
//                    .fileAssetType(FileAssetType.FILE)
//                    .fileType(FileType.SINGLE)
//                    .parent(parent)
//                    .path(dir)
//                    .build()
//            );
//
//            savedFileItem = fileItemRepository.save(FileItem.builder()
//                .name(originalFileName)
//                .fileAsset(savedFileAsset)
//                .fileExtension(extension)
//                .fileContentType(multipartFile.getContentType())
//                .fileSize(multipartFile.getSize())
//                .uploadFileName(name)
//                .build());
//
//            Path path = Paths.get(dir, originalFileName);
//            try (OutputStream outputStream = Files.newOutputStream(path)){
//                // MultipartFile also provides transferTo() method to create file(s) from multipart file
////                multipartFile.transferTo(path);
////                multipartFile.transferTo(new File(dir + "\\" + originalFileName));
//                outputStream.write(multipartFile.getBytes());
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        } else {
        List<FileItem> filteredFileItems = fileItems.stream()
            .filter(fileItem -> fileItem.getFileAsset().getParent().getId().equals(folderId)).toList();

        if (!filteredFileItems.isEmpty())
            throw new RuntimeException(MessageFormat.format("File with name '{0}' already exists", originalFileName));

        String dir = MessageFormat.format("{0}\\{1}", parent.getPath(), parent.getName());
        savedFileAsset = fileAssetRepository.save(
            FileAsset.builder()
                .name(originalFileName)
                .fileAssetType(FileAssetType.FILE)
                .fileType(FileType.SINGLE)
                .parent(parent)
                .path(dir)
                .build()
        );

        savedFileItem = fileItemRepository.save(
            FileItem.builder()
                .name(originalFileName)
                .fileAsset(savedFileAsset)
                .fileExtension(extension)
                .fileContentType(multipartFile.getContentType())
                .fileSize(multipartFile.getSize())
                .uploadFileName(name)
                .build()
        );

        // Here, the received file is saved to local memory
        Path path = Paths.get(dir + "\\" + originalFileName);
        try (OutputStream outputStream = Files.newOutputStream(path)) {
//                multipartFile.transferTo(new File(dir + "\\" + originalFileName));
            outputStream.write(multipartFile.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        }

        return FileItemResponse.builder()
            .id(savedFileAsset.getId())
            .folderId(savedFileAsset.getParent().getId())
            .fileAssetType(FileAssetType.FILE)
            .fileType(FileType.SINGLE)
            .name(savedFileItem.getUploadFileName())
            .fileContentType(savedFileItem.getFileContentType())
            .fileExtension(savedFileItem.getFileExtension())
            .fileSize(savedFileItem.getFileSize().toString().concat(" KB"))
            .build();
    }

    @Override
    public ResponseEntity<?> getByFileAssetId(Long id) {
        FileAsset fileAsset = fileAssetRepository.findByIdAndDeletedFalse(id).orElseThrow(
            () -> new RuntimeException(MessageFormat.format("File with id={0} does not exist", id))
        );

        Optional<FileItem> optionalFileItem = fileItemRepository.findByFileAssetAndDeletedFalse(fileAsset);
        FileItem fileItem = optionalFileItem.get();
        String path = fileAsset.getPath() + "\\" + fileItem.getName();
//        byte[] bytes;
//        try {
//            bytes = Files.readAllBytes(new File(path).toPath());
//            return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileItem.getName())
//                .contentType(MediaType.parseMediaType(fileItem.getFileContentType()))
//                .contentLength(fileItem.getFileSize())
//                .body(bytes);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

        try {
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileItem.getName())
                .contentType(MediaType.parseMediaType(fileItem.getFileContentType()))
                .contentLength(fileItem.getFileSize())
                .body(new FileUrlResource(path));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public FileItemResponse updateByFileAssetId(Long id, NameRequest request) {
        FileAsset fileAsset = fileAssetRepository.findByIdAndDeletedFalse(id).orElseThrow(
            () -> new RuntimeException(MessageFormat.format("File with id={0} does not exist", id))
        );

        String name = fileAsset.getName().substring(0, fileAsset.getName().lastIndexOf("."));
        if (name.equals(request.getName()))
            return null;

        String ext = fileAsset.getName().substring(fileAsset.getName().lastIndexOf(".") + 1);
        if (fileAssetRepository.existsByParentIdAndNameAndDeletedFalse(fileAsset.getParent().getId(), request.getName().concat(ext)))
            throw new RuntimeException(MessageFormat.format("File with name '{0}' already exists", name));

        FileItem fileItem = fileItemRepository.findByFileAssetAndDeletedFalse(fileAsset).get();
        fileItem.setUploadFileName(request.getName());
        fileItem.setName(request.getName().concat(fileItem.getFileExtension()));
        fileItemRepository.save(fileItem);

        String oldName = fileAsset.getName();
        String newName = request.getName().concat(fileItem.getFileExtension());
        fileAsset.setName(newName);
        fileAssetRepository.save(fileAsset);

        File file = new File(MessageFormat.format("{0}\\{1}", fileAsset.getPath(), oldName));
        file.renameTo(new File(MessageFormat.format("{0}\\{1}", fileAsset.getPath(), newName)));

        return FileItemResponse.builder()
            .id(fileAsset.getId())
            .folderId(fileAsset.getParent().getId())
            .fileAssetType(FileAssetType.FILE)
            .fileType(FileType.SINGLE)
            .name(fileItem.getUploadFileName())
            .fileContentType(fileItem.getFileContentType())
            .fileExtension(fileItem.getFileExtension())
            .fileSize(fileItem.getFileSize().toString().concat(" KB"))
            .build();
    }

    @Override
    public void deleteByFileAssetId(Long id) {
        FileAsset fileAsset = fileAssetRepository.findByIdAndDeletedFalse(id).orElseThrow(
            () -> new RuntimeException(MessageFormat.format("Folder/File with id={0} not found", id))
        );

        fileAsset.setDeleted(Boolean.TRUE);
        fileAssetRepository.save(fileAsset);
    }
}
