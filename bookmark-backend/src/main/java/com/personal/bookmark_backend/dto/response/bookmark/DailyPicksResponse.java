package com.personal.bookmark_backend.dto.response.bookmark;

import java.util.List;

public record DailyPicksResponse(
        long totalBookmarkCount,
        List<BookmarkResponse> picks
) {}
