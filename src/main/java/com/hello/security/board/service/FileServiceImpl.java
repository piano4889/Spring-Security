package com.hello.security.board.service;

import com.hello.security.board.domain.File;
import com.hello.security.board.mapper.FileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class FileServiceImpl implements FileService {

    private final FileMapper fileMapper;

    @Value("${spring.servlet.multipart.location}")
    private String uploadPath;

    @Override
    public File getOneFile(Long fileIdx) {
        return fileMapper.getOneFile(fileIdx);
    }

    @Override
    public File insertFiles(Long idx, MultipartFile uploadFile) {
        //이미지 파일이 아닐 때 실행 취소
        if (!Objects.requireNonNull(uploadFile.getContentType()).startsWith("image")) {
            return null;
        } else {
            String uuid = UUID.randomUUID().toString();
            String folderPath = makeDir();
            String originalName = uploadFile.getOriginalFilename();
            //String fileName = originalName.substring(originalName.lastIndexOf("\\") + 1);

            File vo = File.builder().
                    idx(idx)
                    .originName(originalName)
                    .saveName(uuid + "_" + originalName)
                    .uploadPath(uploadPath + java.io.File.separator)
                    .folderPath(folderPath + java.io.File.separator)
                    .build();
            Path savePath = Paths.get(vo.getUploadPath()+vo.getFolderPath()+vo.getSaveName());
            try {
                uploadFile.transferTo(savePath);
                int result = fileMapper.insertFiles(vo);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return vo;
        }

    }

    private String makeDir() {
        String str = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String folderPath = str.replace("/", java.io.File.separator);
        java.io.File uploadPathFolder = new java.io.File(uploadPath, folderPath);

        if (!uploadPathFolder.exists()) {
            uploadPathFolder.mkdirs();
        }
        return folderPath;
    }


    @Override
    public ResponseEntity<?> attachDownload(File vo) throws IOException {
        //경로 획득
        Path path = Paths.get(vo.getUploadPath() + vo.getSaveName());
        //URL 확인
        Resource resource = new InputStreamResource((Files.newInputStream(path)));
        //Uri로 변경
        java.io.File file = new java.io.File(path.toUri());

        //헤더 설정(다운로드 가능하게)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(
                ContentDisposition.builder("attachment") //다운로드 가능
                        .filename(file.getName(), StandardCharsets.UTF_8) //UTF-8로 파일 명 인코딩
                        .build()
        );

        return new ResponseEntity<Object>(resource, headers, HttpStatus.OK);
    }

}
