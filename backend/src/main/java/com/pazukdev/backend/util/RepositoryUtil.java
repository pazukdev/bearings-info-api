package com.pazukdev.backend.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class RepositoryUtil {

    public static final int LAST_TEN = 10;

    public static Pageable getPageRequest(int size) {
        return PageRequest.of(0, size, Sort.Direction.DESC, "id");
    }

}
