package com.example.file.services.impls;

import com.example.file.dtos.FileAssetResponse;
import com.example.file.dtos.NameRequest;
import com.example.file.entities.FileAsset;
import com.example.file.enums.FileAssetType;
import com.example.file.repositories.FileAssetRepository;
import com.example.file.services.FolderService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.MessageFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FolderServiceImpl implements FolderService {
    private final FileAssetRepository fileAssetRepository;
    private final ModelMapper modelMapper;

    @Value("${file-item.upload.folder}")
    private String DEFAULT_PATH;


    @Override
    public FileAssetResponse createFolder(Long parentId, NameRequest request) {
        FileAsset savedFileAsset;

//        if (parentId == null) {
//            if (fileAssetRepository.existsByParentIsNullAndNameAndDeletedFalse(request.getName()))
//                throw new RuntimeException(
//                    MessageFormat.format("Folder with name '{0}' already exists", request.getName())
//                );
//
//            String dir = MessageFormat.format("{0}\\{1}", DEFAULT_PATH, "default");
//            savedFileAsset = fileAssetRepository.save(
//                FileAsset.builder()
//                    .name(request.getName())
//                    .fileAssetType(FileAssetType.FOLDER)
//                    .fileType(null)
//                    .parent(null)
//                    .path(dir)
//                    .build()
//            );
//            new File(MessageFormat.format("{0}\\{1}", dir, request.getName())).mkdirs();
//        } else {
        FileAsset parent = fileAssetRepository.findByIdAndDeletedFalse(parentId).orElseThrow(
            () -> new RuntimeException(MessageFormat.format("Folder with id={0} not found", parentId))
        );

        if (fileAssetRepository.existsByParentIdAndNameAndDeletedFalse(parentId, request.getName()))
            throw new RuntimeException(
                MessageFormat.format("Folder with name '{0}' already exists", request.getName())
            );

        String dir = MessageFormat.format("{0}\\{1}", parent.getPath(), parent.getName());
        savedFileAsset = fileAssetRepository.save(
            FileAsset.builder()
                .name(request.getName())
                .fileAssetType(FileAssetType.FOLDER)
                .fileType(null)
                .parent(parent)
                .path(dir)
                .build()
        );
        new File(MessageFormat.format("{0}\\{1}", dir, request.getName())).mkdirs();
//        }

        return modelMapper.map(savedFileAsset, FileAssetResponse.class);
    }

    @Override
    public List<FileAssetResponse> getByFolderId(Long id) {
        if (!fileAssetRepository.existsByIdAndDeletedFalse(id))
            throw new RuntimeException(MessageFormat.format("Folder with id={0} not found", id));

        return fileAssetRepository.findAllByParentIdAndDeletedFalseOrderByFileAssetTypeDescName(id).stream()
            .map(fileAsset -> modelMapper.map(fileAsset, FileAssetResponse.class)).toList();
    }

    @Override
    public FileAssetResponse update(Long folderId, NameRequest request) {
        FileAsset fileAsset = fileAssetRepository.findByIdAndDeletedFalse(folderId).orElseThrow(
            () -> new RuntimeException(MessageFormat.format("Folder with id={0} not found", folderId))
        );

        if (fileAssetRepository.existsByParentIdAndNameAndDeletedFalse(fileAsset.getParent().getId(), request.getName()))
            throw new RuntimeException(
                MessageFormat.format("Folder with name '{0}' already exists", request.getName())
            );

        new File(
            MessageFormat.format("{0}\\{1}", fileAsset.getPath(), fileAsset.getName())
        ).renameTo(new File(MessageFormat.format("{0}\\{1}", fileAsset.getPath(), request.getName())));

        fileAsset.setName(request.getName());
        fileAsset = fileAssetRepository.save(fileAsset);

        return modelMapper.map(fileAsset, FileAssetResponse.class);
    }

    @Override
    public void delete(Long folderId) {
        FileAsset fileAsset = fileAssetRepository.findByIdAndDeletedFalse(folderId).orElseThrow(
            () -> new RuntimeException(MessageFormat.format("Folder with id={0} not found", folderId))
        );

        fileAsset.setDeleted(Boolean.TRUE);
        fileAssetRepository.save(fileAsset);

        // Todo: If a folder, whose deleted parameter is true, is to be re-created again,
        //      it gets deleted=false again, which makes its inner files/folders show up again.
        //      However, File deletes its items without the ability to restore them back!!!
//        new File(
//            MessageFormat.format("{0}\\{1}", fileAsset.getPath(), fileAsset.getName())
//        ).delete();
    }
}
