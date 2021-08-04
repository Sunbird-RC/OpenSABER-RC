package io.opensaber.registry.controller;

import io.opensaber.registry.model.dto.DocumentsResponse;
import io.opensaber.registry.service.FileStorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

// TODO: authorize user | Get should be viewed by both attestor and reviewer
@Controller
public class FileStorageController {
    private final FileStorageService fileStorageService;

    FileStorageController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/api/v1/{entity}/{entityId}/{property}/documents")
    public ResponseEntity<DocumentsResponse> save(@RequestParam MultipartFile[] multipartFiles,
                                                  @PathVariable String entity,
                                                  @PathVariable String entityId,
                                                  @PathVariable String property,
                                                  HttpServletRequest httpServletRequest) {

        DocumentsResponse documentsResponse = fileStorageService.saveAndFetchFileNames(multipartFiles, httpServletRequest.getRequestURI());
        return new ResponseEntity<>(documentsResponse, HttpStatus.OK);
    }

    @DeleteMapping("/api/v1/{entity}/{entityId}/{property}/documents")
    public ResponseEntity<DocumentsResponse> deleteMultipleFiles(@PathVariable String entity,
                                                    @PathVariable String entityId,
                                                    @PathVariable String property,
                                                    @RequestBody List<String> files,
                                                    HttpServletRequest httpServletRequest) {
        DocumentsResponse documentsResponse = fileStorageService.deleteFiles(files);
        return new ResponseEntity<>(documentsResponse, HttpStatus.OK);
    }

    @DeleteMapping(value = "/api/v1/{entity}/{entityId}/{property}/documents/{documentId}")
    public ResponseEntity deleteAFile(@PathVariable String entity,
                              @PathVariable String entityId,
                              @PathVariable String property,
                              @PathVariable String documentId,
                              HttpServletRequest httpServletRequest) {
        return fileStorageService.deleteDocument(documentId);
    }

    @GetMapping(value = "/api/v1/{entity}/{entityId}/{property}/documents/{documentId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public byte[] get(@PathVariable String entity,
                              @PathVariable String entityId,
                              @PathVariable String property,
                              @PathVariable String documentId,
                              HttpServletRequest httpServletRequest) {
        return fileStorageService.getDocument(documentId);
    }
}