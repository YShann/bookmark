package com.personal.bookmark_backend.repository;

import com.personal.bookmark_backend.entity.BookmarkSource;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookmarkSourceRepository extends JpaRepository<BookmarkSource, String> {
    List<BookmarkSource> findAllByOrderBySortOrderAscCodeAsc();
}
