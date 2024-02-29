package com.example.mallapi.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.IntStream;

@Data
public class PageResponseDTO<E> {

    private List<E> dtoList;
    private List<Integer> pageNumList;
    private PageRequestDTO pageRequestDTO;
    private boolean prev, next;
    private int totalCount, prevPage, nextPage, totalPage, current;

    @Builder(builderMethodName = "withAll")
    public PageResponseDTO(List<E> dtoList, PageRequestDTO pageRequestDTO, long total) {
        this.dtoList = dtoList;
        this.pageRequestDTO = pageRequestDTO;
        totalCount = (int) total;

        //끝 페이지
        int end = (int) (Math.ceil(pageRequestDTO.getPage() / 10.0)) * 10;

        int start = end - 9;

        //진짜 마지막
        int last = (int) (Math.ceil(totalCount / (double) pageRequestDTO.getSize()));

        end = Math.min(end, last);

        prev = start > 1;

        next = totalCount > end * pageRequestDTO.getSize();

        pageNumList =  IntStream.rangeClosed(start,end).boxed().toList();

        prevPage = prev ? start - 1 : 0;
        nextPage = next ? end + 1 : 0;
    }
}
