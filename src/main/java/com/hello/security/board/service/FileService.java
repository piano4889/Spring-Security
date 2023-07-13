package com.hello.security.board.service;

import com.hello.security.board.domain.File;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    File getOneFile(Long fileIdx);

    File insertFiles(Long idx, MultipartFile file);

    ResponseEntity<?> attachDownload(File vo) throws IOException;
}
