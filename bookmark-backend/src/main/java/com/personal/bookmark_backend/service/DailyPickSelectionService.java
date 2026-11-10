package com.personal.bookmark_backend.service;

import com.personal.bookmark_backend.entity.*;
import com.personal.bookmark_backend.repository.*;
import com.personal.bookmark_backend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class DailyPickSelectionService {
    private final BookmarkRepository bookmarks;
    private final DailyPickSelectionRepository selections;
    private final UserRepository users;
    public record Selection(LocalDate date, long count, List<Bookmark> picks) {}

    @Transactional
    public void lockUser(Long userId) {
        users.lockById(userId).orElseThrow(() -> new ResourceNotFoundException("找不到使用者"));
    }

    @Transactional
    public Selection select(Long userId, boolean refresh, Long includeId) {
        lockUser(userId);
        LocalDate day = LocalDate.now(ZoneId.of("Asia/Taipei"));
        var rows = selections.findAllByUserIdOrderByPositionAsc(userId);
        var ids = rows.stream().map(DailyPickSelection::getBookmarkId).distinct().toList();
        var owned = ids.isEmpty() ? List.<Bookmark>of() : bookmarks.findAllByUserIdAndIdInOrderByCreatedAtDesc(userId, ids);
        Map<Integer, Bookmark> slots = new TreeMap<>();
        Set<Long> used = new HashSet<>();
        List<DailyPickSelection> invalid = new ArrayList<>();
        for (var row : rows) {
            var item = owned.stream().filter(b -> b.getId().equals(row.getBookmarkId())).findFirst();
            if (!day.equals(row.getSelectionDate()) || row.getPosition() == null || row.getPosition()<1 || row.getPosition()>3 ||
                slots.containsKey(row.getPosition()) || item.isEmpty() || !used.add(row.getBookmarkId())) invalid.add(row);
            else slots.put(row.getPosition(), item.get());
        }
        selections.deleteAll(invalid);
        selections.flush();
        long count = bookmarks.countByUserId(userId);
        if (count < 3) {
            selections.deleteAll(rows.stream().filter(row -> !invalid.contains(row)).toList());
            selections.flush();
            return new Selection(day, count, List.of());
        }
        if (refresh && count > 3) {
            var previous = new ArrayList<>(slots.values());
            var candidates = random(userId, previous.stream().map(Bookmark::getId).toList(), 3);
            Collections.shuffle(previous);
            for (var old : previous) if (candidates.size()<3) candidates.add(old);
            slots.clear();
            for (var b : candidates) slots.put(slots.size()+1, b);
        }
        if (includeId != null && slots.values().stream().noneMatch(b -> b.getId().equals(includeId))) {
            Bookmark item = bookmarks.findByIdAndUserId(includeId,userId).orElseThrow(() -> new ResourceNotFoundException("找不到此收藏"));
            int position = slots.size()==3 ? ThreadLocalRandom.current().nextInt(1,4) : emptyPosition(slots);
            slots.put(position,item);
        }
        if (slots.size()<3) {
            for (var b : random(userId, slots.values().stream().map(Bookmark::getId).toList(), 3-slots.size()))
                slots.put(emptyPosition(slots),b);
        }
        // Flush removals before inserts so unique position/bookmark constraints never collide.
        var retained = selections.findAllByUserIdOrderByPositionAsc(userId);
        for (var row : retained) {
            Bookmark desired = slots.get(row.getPosition());
            if (desired != null && desired.getId().equals(row.getBookmarkId())) continue;
            selections.delete(row);
        }
        selections.flush();
        for (var entry : slots.entrySet()) {
            boolean exists = retained.stream().anyMatch(row -> row.getPosition().equals(entry.getKey()) && row.getBookmarkId().equals(entry.getValue().getId()));
            if (!exists) {
                var row = new DailyPickSelection(); row.setUserId(userId); row.setBookmarkId(entry.getValue().getId());
                row.setPosition(entry.getKey()); row.setSelectionDate(day); selections.save(row);
            }
        }
        selections.flush();
        return new Selection(day,count,new ArrayList<>(slots.values()));
    }
    private int emptyPosition(Map<Integer,Bookmark> slots) {
        for(int i=1;i<=3;i++) if(!slots.containsKey(i)) return i;
        throw new IllegalStateException("今日拾藏位置已滿");
    }
    private List<Bookmark> random(Long userId,List<Long> excluded,int size) {
        long seed=ThreadLocalRandom.current().nextLong(1,Integer.MAX_VALUE);
        return new ArrayList<>(excluded.isEmpty() ? bookmarks.findDailyPicks(userId,seed,PageRequest.of(0,size)) :
            bookmarks.findDailyPicksExcluding(userId,excluded,seed,PageRequest.of(0,size)));
    }
    @Transactional public List<Bookmark> getPicks(Long userId,int rotation,List<Long> excluded) {
        return select(userId,excluded!=null && !excluded.isEmpty(),null).picks();
    }
    @Transactional public void includeBookmark(Long userId,Bookmark bookmark) { select(userId,false,bookmark.getId()); }
    @Transactional public void removeBookmarks(Long userId,List<Long> ids) {
        lockUser(userId);
        if(!ids.isEmpty()) { selections.deleteAllByUserIdAndBookmarkIdIn(userId,ids); selections.flush(); }
    }
}
