package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class Pagination extends PageRequest {
    public Pagination(int page, int size) {
        super(page / size, size, Sort.by(Sort.Direction.DESC, "start"));
    }

    public Pagination(int page, int size, Sort sort) {
        super(page / size, size, sort);
    }

    public Pageable getPageable() {
        return of(getPageNumber(), getPageSize(), getSort());
    }
}