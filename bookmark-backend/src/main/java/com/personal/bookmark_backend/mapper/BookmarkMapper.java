package com.personal.bookmark_backend.mapper;

import com.personal.bookmark_backend.dto.response.bookmark.BookmarkResponse;
import com.personal.bookmark_backend.entity.Bookmark;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface BookmarkMapper {
    // 標籤由 Controller 查好後傳入，其餘欄位依名稱自動對應。
    @Mapping(target = "tags", source = "tagNames")
    BookmarkResponse toResponse(Bookmark bookmark, List<String> tagNames);
}
