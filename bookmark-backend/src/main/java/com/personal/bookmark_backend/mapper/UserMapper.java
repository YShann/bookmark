package com.personal.bookmark_backend.mapper;

import com.personal.bookmark_backend.dto.response.user.UserProfileResponse;
import com.personal.bookmark_backend.entity.User;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UserMapper {
    @Mapping(target = "avatarImageUrl", source = "avatarUrl")
    @Mapping(target = "googleLinked", source = "googleSubject", qualifiedByName = "hasGoogleAccount")
    UserProfileResponse toResponse(User user);

    @Named("hasGoogleAccount")
    default boolean hasGoogleAccount(String googleSubject) {
        return googleSubject != null;
    }
}
