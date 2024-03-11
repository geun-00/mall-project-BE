package com.example.mallapi.controller;

import com.example.mallapi.dto.PageRequestDTO;
import com.example.mallapi.dto.PageResponseDTO;
import com.example.mallapi.dto.ProductDTO;
import com.example.mallapi.service.ProductService;
import com.example.mallapi.util.CustomFileUpload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final CustomFileUpload customFileUpload;
    private final ProductService productService;

/*
    @PostMapping("/")
    public Map<String, String> register(ProductDTO productDTO) {
        log.info("register: {}", productDTO);

        List<MultipartFile> files = productDTO.getFiles();

        List<String> uploadedFileNames = customFileUpload.saveFiles(files);
        productDTO.setUploadedFileNames(uploadedFileNames);

        log.info("uploadedFileNames = {}", uploadedFileNames);

        return Map.of("RESULT", "SUCCESS");
    }
*/

    @GetMapping("/view/{fileName}")
    public ResponseEntity<Resource> viewFile(@PathVariable("fileName") String fileName) {
        log.info("fileName = {}", fileName);
        return customFileUpload.getFile(fileName);
    }

    @GetMapping("/list")
    public PageResponseDTO<ProductDTO> list(PageRequestDTO pageRequestDTO) {
        return productService.getList(pageRequestDTO);
    }

    @PostMapping("/")
    public Map<String, Long> register(ProductDTO productDTO) {
        List<MultipartFile> files = productDTO.getFiles();

        List<String> uploadFileNames = customFileUpload.saveFiles(files);
        productDTO.setUploadedFileNames(uploadFileNames);

        log.info("uploadFileNames = {}", uploadFileNames);
        Long pno = productService.register(productDTO);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return Map.of("result", pno);
    }

    @GetMapping("/{pno}")
    public ProductDTO read(@PathVariable("pno") Long pno) {
        return productService.get(pno);
    }

    @PutMapping("/{pno}")
    public Map<String, String> modify(@PathVariable("pno") Long pno, ProductDTO productDTO) {
        productDTO.setPno(pno);

        log.info("productDTO = {}", productDTO);

        //old product, Database saved Product
        ProductDTO oldProductDTO = productService.get(pno);

        //file upload
        List<MultipartFile> files = productDTO.getFiles();
        List<String> currentUploadFileNames = customFileUpload.saveFiles(files);

        //keep files String
        List<String> uploadedFileNames = productDTO.getUploadedFileNames();

        if (currentUploadFileNames != null && !currentUploadFileNames.isEmpty()) {
            uploadedFileNames.addAll(currentUploadFileNames);
        }

        productService.modify(productDTO);

        //예전 파일 삭제
        List<String> oldFileNames = oldProductDTO.getUploadedFileNames();
        if (oldFileNames != null && !oldFileNames.isEmpty()) {
            List<String> removeFiles = oldFileNames.stream()
                    .filter(fileName -> !uploadedFileNames.contains(fileName))
                    .toList();

            customFileUpload.deleteFiles(removeFiles);
        }

        return Map.of("RESULT", "SUCCESS");
    }

    @DeleteMapping("/{pno}")
    public Map<String, String> remove(@PathVariable("pno") Long pno) {
        List<String> oldFileNames = productService.get(pno).getUploadedFileNames();
        productService.remove(pno);
        customFileUpload.deleteFiles(oldFileNames);

        return Map.of("RESULT", "SUCCESS");
    }

}
