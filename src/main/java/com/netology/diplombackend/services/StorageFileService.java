package com.netology.diplombackend.services;

import com.netology.diplombackend.domain.dto.request.EditFileNameRequest;
import com.netology.diplombackend.domain.dto.response.FileResponse;
import com.netology.diplombackend.domain.model.StorageFile;
import com.netology.diplombackend.domain.model.User;
import com.netology.diplombackend.exceptions.InputDataException;
import com.netology.diplombackend.repository.FileRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class StorageFileService {
    private final FileRepository fileRepository;
    private final UserService userService;

    public boolean uploadFile(String filename, MultipartFile file) {
        final User user = userService.getCurrentUser();
        file.getSize();
        try {
            fileRepository.save(StorageFile.builder()
                    .filename(filename)
                    .date(LocalDateTime.now())
                    .size(file.getSize())
                    .fileContent(file.getBytes())
                    .user(user)
                    .build());
            log.info("Success upload file. User {}", user.getUsername());
            return true;
        } catch (IOException e) {
            log.error("Upload file: Input data exception");
            throw new InputDataException("Upload file: Input data exception");
        }
    }

    @Transactional
    public void deleteFile(String filename) {
        final User user = userService.getCurrentUser();

        fileRepository.deleteByUserAndFilename(user, filename);

        final StorageFile tryingToGetDeletedFile = fileRepository.findByUserAndFilename(user, filename);
        if (tryingToGetDeletedFile != null) {
            log.error("Delete file: Input data exception");
            throw new InputDataException("Delete file: Input data exception");
        }
        log.info("Success delete file. User {}", user.getUsername());
    }

    public byte[] downloadFile(String filename) {
        final User user = userService.getCurrentUser();

        final StorageFile file = fileRepository.findByUserAndFilename(user, filename);
        if (file == null) {
            log.error("Download file: Input data exception");
            throw new InputDataException("Download file: Input data exception");
        }
        log.info("Success download file. User {}", user.getUsername());
        return file.getFileContent();
    }

    @Transactional
    public void editFileName(String filename, EditFileNameRequest editFileNameRQ) {
        final User user = userService.getCurrentUser();

        fileRepository.editFileNameByUser(user, filename, editFileNameRQ.getFilename());

        final StorageFile fileWithOldName = fileRepository.findByUserAndFilename(user, filename);
        if (fileWithOldName != null) {
            log.error("Edit file name: Input data exception");
            throw new InputDataException("Edit file name: Input data exception");
        }
        log.info("Success edit file name. User {}", user.getUsername());
    }

    public List<FileResponse> getAllFiles() {
        final User user = userService.getCurrentUser();

        log.info("Success get all files. User {}", user.getUsername());
        return fileRepository.findAllByUser(user).stream()
                .map(o -> new FileResponse(o.getFilename(), o.getSize()))
                .collect(Collectors.toList());
    }
}
