package com.example.mallapi.util;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomFileUpload {

    @Value("${my.upload.path}")
    private String uploadPath;

    @PostConstruct
    public void init() {
        log.info("uploadPath = {}", uploadPath);
        File tempFolder = new File(uploadPath);
        if (!tempFolder.exists()) {
            tempFolder.mkdir();
        }

        uploadPath = tempFolder.getAbsolutePath();

        log.info("-----------uploadPath-----------");
        log.info(uploadPath);
    }


    public List<String> saveFiles(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return null;
        }

        List<String> uploadNames = new ArrayList<>();

        for (MultipartFile file : files) {
            String savedName = getSavedNames(file);

            Path savePath = Paths.get(uploadPath, savedName);

            try {
                Files.copy(file.getInputStream(), savePath); //원본 파일 업로드

                String contentType = file.getContentType();

                if (contentType != null && contentType.startsWith("image")) {
                    //이미지 파일이면 썸네일 대상
                    Path thumbnailPath = Paths.get(uploadPath, "th_" + savedName);

                    Thumbnails.of(savePath.toFile())
                              .size(200, 200)
                              .toFile(thumbnailPath.toFile());

                }
                uploadNames.add(savedName);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return uploadNames;
    }

    public ResponseEntity<Resource> getFile(String fileName) {
        Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);

        if (!resource.isReadable()) {
            resource = new FileSystemResource(uploadPath + File.separator + "default.jpg");
        }
        HttpHeaders headers = new HttpHeaders();
        try {
            headers.add("Content-Type", Files.probeContentType(resource.getFile().toPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok().headers(headers).body(resource);
    }

    public void deleteFiles(List<String> fileNames) {
        if (fileNames == null || fileNames.isEmpty()) {
            return;
        }

        fileNames.forEach(fileName -> {
            //썸네일 삭제
            String thumbNameFileName = "th_" + fileName;

            Path thumbnailPath = Paths.get(uploadPath, thumbNameFileName); //썸네일 파일
            Path filePath = Paths.get(uploadPath, fileName); //원본 파일

            try {
                Files.deleteIfExists(thumbnailPath);
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        });

    }

    private String getSavedNames(MultipartFile file) {
        return UUID.randomUUID().toString().substring(0, 8) + "_" + file.getOriginalFilename();
    }
}
