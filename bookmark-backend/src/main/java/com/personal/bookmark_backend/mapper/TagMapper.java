package com.personal.bookmark_backend.mapper;

import com.personal.bookmark_backend.dto.response.tag.TagResponse;
import com.personal.bookmark_backend.entity.Tag;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TagMapper {
    TagResponse toResponse(Tag tag);
}
