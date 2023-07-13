package com.hello.security.board.controller;

import com.hello.security.board.domain.File;
import com.hello.security.board.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Log4j2
@RequiredArgsConstructor
@RestController
@Tag(name = "File Up/Down API", description = "파일 업/다운로드 API")
public class FileController {

    private final FileService fileService;

    //TODO 컨트롤러 역할 분리

    @PostMapping("/file/upload")
    @Operation(summary = "File Upload", description = "파일 업로드")
    public ResponseEntity<?> upload(
            @RequestParam("filename") MultipartFile uploadFile, @PathVariable String idx) throws IOException {

        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        File vo = fileService.insertFiles(Long.parseLong(idx), uploadFile);
        return new ResponseEntity<>(vo, header, HttpStatus.OK);
    }

    @GetMapping("/file/attach/{fileIdx}")
    @Operation(summary = "File Download", description = "파일 다운로드")
    public ResponseEntity<?> download(@PathVariable Long fileIdx) throws IOException {

        return fileService.attachDownload(fileService.getOneFile(fileIdx));
    }

}
