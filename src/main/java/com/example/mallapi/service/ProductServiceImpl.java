package com.example.mallapi.service;


import com.example.mallapi.domain.Product;
import com.example.mallapi.domain.ProductImage;
import com.example.mallapi.dto.PageRequestDTO;
import com.example.mallapi.dto.PageResponseDTO;
import com.example.mallapi.dto.ProductDTO;
import com.example.mallapi.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public PageResponseDTO<ProductDTO> getList(PageRequestDTO pageRequestDTO) {

        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(),
                Sort.by("pno").descending());

        Page<Object[]> result = productRepository.selectList(pageable);

        //Object[] 0번째 product, 1번째 productImage
        List<ProductDTO> dtoList = result.get().map(arr -> {
            Product product = (Product) arr[0];
            ProductImage productImage = (ProductImage) arr[1];

            ProductDTO productDTO = ProductDTO.builder()
                                              .pno(product.getPno())
                                              .pName(product.getPName())
                                              .price(product.getPrice())
                                              .pDesc(product.getPDesc())
                                              .build();

            String imageStr = productImage.getFileName();
            productDTO.setUploadedFileNames(List.of(imageStr));

            return productDTO;
        }).toList();

        long totalContent = result.getTotalElements();

        return PageResponseDTO.<ProductDTO>withAll()
                              .dtoList(dtoList)
                              .total(totalContent)
                              .pageRequestDTO(pageRequestDTO)
                              .build();
    }

    @Override
    public Long register(ProductDTO productDTO) {
        Product product = dtoToEntity(productDTO);

        log.info("----------product register----------");
        log.info("product = {}", product);
        log.info("product.getImageList() = {}", product.getImageList());

        return productRepository.save(product).getPno();
    }

    @Override
    public ProductDTO get(Long pno) {
        Optional<Product> result = productRepository.findById(pno);
        Product product = result.orElseThrow();

        return entityToDTO(product);
    }

    @Override
    public void modify(ProductDTO productDTO) {
        //조회
        Optional<Product> result = productRepository.findById(productDTO.getPno());
        Product product = result.orElseThrow();

        //변경 내용 반영
        product.changePrice(productDTO.getPrice());
        product.changeName(productDTO.getPName());
        product.changeDesc(productDTO.getPDesc());
        product.changeDel(productDTO.isDelFlag());

        //이미지 처리
        List<String> uploadedFileNames = productDTO.getUploadedFileNames();
        product.clearImage();

        if (uploadedFileNames != null && !uploadedFileNames.isEmpty()) {
            uploadedFileNames.forEach(product::addImageString);
        }
    }

    private Product dtoToEntity(ProductDTO productDTO) {
        Product product = Product.builder()
                                 .pno(productDTO.getPno())
                                 .pName(productDTO.getPName())
                                 .price(productDTO.getPrice())
                                 .pDesc(productDTO.getPDesc())
                                 .build();
        List<String> uploadedFileNames = productDTO.getUploadedFileNames();
        if (uploadedFileNames == null || uploadedFileNames.isEmpty()) {
            return product;
        }

        uploadedFileNames.forEach(product::addImageString);
        return product;
    }

    private ProductDTO entityToDTO(Product product) {
        ProductDTO productDTO = ProductDTO.builder()
                                          .pno(product.getPno())
                                          .pName(product.getPName())
                                          .pDesc(product.getPDesc())
                                          .price(product.getPrice())
                                          .delFlag(product.isDelFlag())
                                          .build();

        List<ProductImage> imageList = product.getImageList();

        if (imageList == null || imageList.isEmpty()) {
            return productDTO;
        }

        List<String> fileNameList = imageList.stream()
                                             .map(ProductImage::getFileName)
                                             .toList();

        productDTO.setUploadedFileNames(fileNameList);

        return productDTO;
    }

}