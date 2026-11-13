package com.personal.bookmark_backend.mapper;

import com.personal.bookmark_backend.dto.response.auth.AuthResponse;
import com.personal.bookmark_backend.service.AuthService;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface AuthMapper {
    AuthResponse toResponse(AuthService.AuthResponse result);
}
