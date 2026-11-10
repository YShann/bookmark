package com.personal.bookmark_backend.controller;
import com.personal.bookmark_backend.service.*;
import com.personal.bookmark_backend.dto.response.bookmark.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@RestController
@RequestMapping("/api/daily-picks")
@RequiredArgsConstructor
public class DailyPickController {
    private final DailyPickSelectionService selections;
    private final TagService tags;
    @GetMapping @Transactional
    public DailyPickSelectionResponse get(Authentication authentication) { return response((Long)authentication.getPrincipal(),false); }
    @PostMapping("/refresh") @Transactional
    public DailyPickSelectionResponse refresh(Authentication authentication) { return response((Long)authentication.getPrincipal(),true); }
    private DailyPickSelectionResponse response(Long userId,boolean refresh) {
        var result=selections.select(userId,refresh,null);
        Map<Long,List<String>> names=new HashMap<>();
        result.picks().stream().map(b->b.getFolderId()).distinct().forEach(folderId -> names.putAll(tags.getBookmarkTagNames(userId,folderId,
            result.picks().stream().filter(b->b.getFolderId().equals(folderId)).map(b->b.getId()).toList())));
        var items=new ArrayList<DailyPickSelectionResponse.Item>();
        for(var b:result.picks()) items.add(new DailyPickSelectionResponse.Item(items.size()+1,new BookmarkResponse(
            b.getId(),b.getFolderId(),b.getUrl(),b.getTitle(),b.getContent(),b.getNote(),b.getSource(),b.getImageUrl(),
            names.getOrDefault(b.getId(),List.of()),b.getCreatedAt(),b.getUpdatedAt())));
        return new DailyPickSelectionResponse(result.date(),result.count(),result.count()>=3,items);
    }
}
