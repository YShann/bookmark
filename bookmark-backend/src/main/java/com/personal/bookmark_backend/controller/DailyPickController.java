package com.personal.bookmark_backend.controller;

import com.personal.bookmark_backend.dto.response.bookmark.DailyPickSelectionResponse;
import com.personal.bookmark_backend.service.BookmarkResponseAssembler;
import com.personal.bookmark_backend.service.DailyPickSelectionService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/daily-picks")
@RequiredArgsConstructor
public class DailyPickController {
    private final DailyPickSelectionService selections;
    private final BookmarkResponseAssembler bookmarkResponses;

    @GetMapping
    @Transactional
    public DailyPickSelectionResponse get(
            Authentication authentication
    ) {
        return response((Long) authentication.getPrincipal(), false);
    }

    @PostMapping("/refresh")
    @Transactional
    public DailyPickSelectionResponse refresh(
            Authentication authentication
    ) {
        return response((Long) authentication.getPrincipal(), true);
    }

    private DailyPickSelectionResponse response(
            Long userId,
            boolean refresh
    ) {
        var result = selections.select(userId, refresh, null);
        var items = new ArrayList<DailyPickSelectionResponse.Item>();
        for (var response : bookmarkResponses.toResponses(userId, result.picks())) {
            items.add(
                    new DailyPickSelectionResponse.Item(items.size() + 1, response)
            );
        }
        return new DailyPickSelectionResponse(
                result.date(),
                result.count(),
                result.count() >= 3, items
        );
    }
}
