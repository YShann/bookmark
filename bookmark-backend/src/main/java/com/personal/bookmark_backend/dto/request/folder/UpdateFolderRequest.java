package com.personal.bookmark_backend.dto.request.folder;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateFolderRequest(

        @NotBlank(message = "收藏夾名稱不可為空")
        @Size(max = 50, message = "收藏夾名稱不可超過 50 個字")
        String name

) {
}