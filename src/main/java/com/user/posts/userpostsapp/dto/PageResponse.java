package com.user.posts.userpostsapp.dto;

import org.springframework.data.domain.Page;
import java.util.List;

public class PageResponse<T> {
    private final int pageNumber;
    private final int pageSize;
    private final long totalElements;
    private final int totalPages;
    private final boolean first;
    private final boolean last;
    private final List<T> content;

    public PageResponse(Page<T> page) {
        this.pageNumber = page.getNumber();
        this.pageSize = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.first = page.isFirst();
        this.last = page.isLast();
        this.content = page.getContent();
    }

    // Getters only - immutable response
    public int getPageNumber() { return pageNumber; }
    public int getPageSize() { return pageSize; }
    public long getTotalElements() { return totalElements; }
    public int getTotalPages() { return totalPages; }
    public boolean isFirst() { return first; }
    public boolean isLast() { return last; }
    public List<T> getContent() { return content; }
}