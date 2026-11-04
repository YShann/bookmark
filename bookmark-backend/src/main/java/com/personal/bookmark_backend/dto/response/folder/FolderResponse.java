package com.personal.bookmark_backend.dto.response.folder;

public record FolderResponse(

        Long id,

        String name,

        String imageUrl,

        Boolean isDefault

) {
}
