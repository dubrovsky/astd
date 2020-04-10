package com.isc.astd.service.dto;

import java.util.List;

/**
 * @author p.dzeviarylin
 */
public class PageRequestDTO<T> {

    private final Integer totalPages;
    private final long totalElements;
    private final List<T> content;

    public PageRequestDTO(Integer totalPages, long totalElements, List<T> content) {
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.content = content;
    }

    public PageRequestDTO(long totalElements, List<T> content) {
        this(null, totalElements, content);
    }

    public int getTotalPages() {
        return totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public List<T> getContent() {
        return content;
    }
}
