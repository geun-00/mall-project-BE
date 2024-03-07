package com.example.mallapi.util;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
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

@Component
@Log4j2
@RequiredArgsConstructor
public class CustomFileUtil {

    @Value("${org.update.path}")
    private String uploadPath;

    @PostConstruct
    public void init() {
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
            String savedNames = getSavedNames(file);

            Path savePath = Paths.get(uploadPath, savedNames);

            try {
                Files.copy(file.getInputStream(), savePath);
                uploadNames.add(savedNames);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return uploadNames;
    }

    private String getSavedNames(MultipartFile file) {
        return UUID.randomUUID().toString().substring(0, 8) + "_" + file.getOriginalFilename();
    }
}
