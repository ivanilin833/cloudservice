package com.netology.diplombackend.services;

import com.netology.diplombackend.domain.dto.request.EditFileNameRequest;
import com.netology.diplombackend.domain.dto.response.FileResponse;
import com.netology.diplombackend.domain.model.StorageFile;
import com.netology.diplombackend.domain.model.User;
import com.netology.diplombackend.exceptions.InputDataException;
import com.netology.diplombackend.repository.FileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StorageFileServiceTest {

    @Mock
    private FileRepository fileRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private StorageFileService storageFileService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testUser");

        when(userService.getCurrentUser()).thenReturn(testUser);
    }

    @Test
    void uploadFileSuccess() {
        String filename = "testFile.txt";
        MultipartFile file = new MockMultipartFile("file", filename, "text/plain", "test content".getBytes());

        boolean result = storageFileService.uploadFile(filename, file);

        assertTrue(result);
        verify(fileRepository, times(1)).save(any());
    }

    @Test
    void deleteFileSuccess() {
        String filename = "testFile.txt";

        storageFileService.deleteFile(filename);

        verify(fileRepository, times(1)).deleteByUserAndFilename(testUser, filename);
    }

    @Test
    void deleteFileNotExisting() {
        String filename = "notExistingFile.txt";

        storageFileService.deleteFile(filename);

        verify(fileRepository, times(1)).deleteByUserAndFilename(testUser, filename);
    }

    @Test
    void downloadFileSuccess() {
        String filename = "testFile.txt";
        byte[] fileContent = "test content".getBytes();
        StorageFile storageFile = new StorageFile();
        storageFile.setFileContent(fileContent);

        when(fileRepository.findByUserAndFilename(testUser, filename)).thenReturn(storageFile);

        byte[] result = storageFileService.downloadFile(filename);

        assertArrayEquals(fileContent, result);
        verify(fileRepository, times(1)).findByUserAndFilename(testUser, filename);
    }

    @Test
    void downloadFileNotFound() {
        String filename = "nonExistingFile.txt";

        when(fileRepository.findByUserAndFilename(testUser, filename)).thenReturn(null);

        assertThrows(InputDataException.class, () -> storageFileService.downloadFile(filename));
    }

    @Test
    void editFileNameSuccess() {
        String oldFilename = "oldTestFile.txt";
        String newFilename = "newTestFile.txt";
        EditFileNameRequest request = new EditFileNameRequest(newFilename);


        when(fileRepository.findByUserAndFilename(testUser, oldFilename)).thenReturn(null);

        storageFileService.editFileName(oldFilename, request);

        verify(fileRepository, times(1)).editFileNameByUser(testUser, oldFilename, newFilename);
    }

    @Test
    void editFileNameWithExistingOldFilename() {
        String oldFilename = "existingOldFile.txt";
        String newFilename = "newFile.txt";
        EditFileNameRequest request = new EditFileNameRequest(newFilename);
        StorageFile existingFile = new StorageFile();

        when(fileRepository.findByUserAndFilename(testUser, oldFilename)).thenReturn(existingFile);

        assertThrows(InputDataException.class, () -> storageFileService.editFileName(oldFilename, request));
    }

    @Test
    void getAllFilesWithFiles() {
        StorageFile file1 = new StorageFile();
        file1.setFilename("file1.txt");
        file1.setSize(100L);
        StorageFile file2 = new StorageFile();
        file2.setFilename("file2.txt");
        file2.setSize(200L);

        when(fileRepository.findAllByUser(testUser)).thenReturn(Arrays.asList(file1, file2));

        List<FileResponse> files = storageFileService.getAllFiles();

        assertEquals(2, files.size());
        assertEquals("file1.txt", files.get(0).getFilename());
        assertEquals(100L, files.get(0).getSize());
    }

    @Test
    void getAllFilesWithNoFiles() {
        when(fileRepository.findAllByUser(testUser)).thenReturn(Collections.emptyList());

        List<FileResponse> files = storageFileService.getAllFiles();

        assertTrue(files.isEmpty());
    }
}
