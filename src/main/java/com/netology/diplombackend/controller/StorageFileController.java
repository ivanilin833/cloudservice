package com.netology.diplombackend.controller;

import com.netology.diplombackend.domain.dto.request.EditFileNameRequest;
import com.netology.diplombackend.domain.dto.response.FileResponse;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.netology.diplombackend.services.StorageFileService;
import java.util.List;


@RestController
@RequestMapping("/")
@AllArgsConstructor
public class StorageFileController {

    private StorageFileService cloudStorageService;

    @PostMapping("/file")
    public ResponseEntity<?> uploadFile(@RequestParam("filename") String filename, MultipartFile file) {
        cloudStorageService.uploadFile(filename, file);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/file")
    public ResponseEntity<?> deleteFile(@RequestParam("filename") String filename) {
        cloudStorageService.deleteFile(filename);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/file")
    public ResponseEntity<Resource> downloadFile( @RequestParam("filename") String filename) {
        byte[] file = cloudStorageService.downloadFile(filename);
        return ResponseEntity.ok().body(new ByteArrayResource(file));
    }

    @PutMapping(value = "/file")
    public ResponseEntity<?> editFileName(@RequestParam("filename") String filename, @RequestBody EditFileNameRequest editFileNameRQ) {
        cloudStorageService.editFileName(filename, editFileNameRQ);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/list")
    public List<FileResponse> getAllFiles() {
        return cloudStorageService.getAllFiles();
    }
}