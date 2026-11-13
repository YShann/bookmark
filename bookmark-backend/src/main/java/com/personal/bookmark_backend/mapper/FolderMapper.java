package com.personal.bookmark_backend.mapper;

import com.personal.bookmark_backend.dto.response.folder.FolderResponse;
import com.personal.bookmark_backend.entity.Folder;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface FolderMapper {
    @Mapping(target = "bookmarkCount", source = "bookmarkCount")
    FolderResponse toResponse(Folder folder, long bookmarkCount);
}
