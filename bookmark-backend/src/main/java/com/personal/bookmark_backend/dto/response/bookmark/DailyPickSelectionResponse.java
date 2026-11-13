package com.personal.bookmark_backend.dto.response.bookmark;

import java.time.LocalDate;
import java.util.List;

public record DailyPickSelectionResponse(LocalDate selectionDate, long bookmarkCount,
                                         boolean isSufficient, List<Item> dailyPicks) {
    public record Item(int position, BookmarkResponse bookmark) {
    }
}
