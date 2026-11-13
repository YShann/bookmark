package com.personal.bookmark_backend.service;

import com.personal.bookmark_backend.entity.*;
import com.personal.bookmark_backend.repository.*;

import org.junit.jupiter.api.*;

import java.time.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

class DailyPickSelectionServiceTest {
    BookmarkRepository books = mock(BookmarkRepository.class);
    DailyPickSelectionRepository rows = mock(DailyPickSelectionRepository.class);
    UserRepository users = mock(UserRepository.class);
    DailyPickSelectionService service = new DailyPickSelectionService(books, rows, users);
    List<DailyPickSelection> data = new ArrayList<>();
    List<Bookmark> all = new ArrayList<>();

    @BeforeEach
    void setup() {
        for (long i = 1; i <= 8; i++) {
            var b = new Bookmark();
            b.setId(i);
            b.setUserId(7L);
            all.add(b);
        }
        when(users.lockById(7L)).thenReturn(Optional.of(new User()));
        when(rows.findAllByUserIdOrderByPositionAsc(7L)).thenAnswer(a -> new ArrayList<>(data));
        when(books.countByUserId(7L)).thenAnswer(a -> (long) all.size());
        when(books.findAllByUserIdAndIdInOrderByCreatedAtDesc(eq(7L), anyList())).thenAnswer(a -> all.stream()
                .filter(b -> ((List<?>) a.getArgument(1)).contains(b.getId()))
                .toList());
        when(books.findByIdAndUserId(anyLong(), eq(7L))).thenAnswer(a -> all.stream()
                .filter(b -> b.getId().equals(a.getArgument(0)))
                .findFirst());
        when(books.findDailyPicks(eq(7L), anyLong(), any())).thenAnswer(a -> all.stream()
                .limit(((org.springframework.data.domain.Pageable) a.getArgument(2)).getPageSize())
                .toList());
        when(books.findDailyPicksExcluding(eq(7L), anyList(), anyLong(), any())).thenAnswer(a -> all.stream()
                .filter(b -> !((List<?>) a.getArgument(1)).contains(b.getId()))
                .limit(((org.springframework.data.domain.Pageable) a.getArgument(3)).getPageSize())
                .toList());
        doAnswer(a -> {
            data.removeAll(a.getArgument(0));
            return null;
        }).when(rows).deleteAll(anyList());
        doAnswer(a -> {
            data.remove(a.getArgument(0));
            return null;
        }).when(rows).delete(any());
        when(rows.save(any())).thenAnswer(a -> {
            DailyPickSelection row = a.getArgument(0);
            data.add(row);
            return row;
        });
    }

    List<Long> get() {
        return service.select(7L, false, null).picks().stream().map(Bookmark::getId).toList();
    }

    @Test
    void firstGet() {
        assertEquals(3, get().size());
        assertEquals(3, data.size());
    }

    @Test
    void stable() {
        var old = get();
        assertEquals(old, get());
        verify(books, times(1)).findDailyPicks(anyLong(), anyLong(), any());
    }

    @Test
    void dailyBoundaryUsesSixInTheMorningInTaipei() {
        assertEquals(
                LocalDate.of(2026, 11, 12),
                DailyPickSelectionService.selectionDateAt(Instant.parse("2026-11-12T21:59:59Z"))
        );
        assertEquals(
                LocalDate.of(2026, 11, 13),
                DailyPickSelectionService.selectionDateAt(Instant.parse("2026-11-12T22:00:00Z"))
        );
    }

    @Test
    void nextDayReplacesPreviousPicksAndKeepsTheNewSelection() {
        var old = get();
        var today = DailyPickSelectionService.selectionDateAt(Instant.now());
        data.forEach(row -> row.setSelectionDate(today.minusDays(1)));
        var next = get();
        assertEquals(3, next.size());
        assertTrue(Collections.disjoint(old, next));
        assertEquals(next, get());
        assertTrue(data.stream().allMatch(row -> row.getSelectionDate().equals(today)));
    }

    @Test
    void refreshAvoidsPrevious() {
        var old = get();
        var next = service.select(7L, true, null);
        assertEquals(3, next.picks().size());
        assertTrue(next.picks().stream().noneMatch(b -> old.contains(b.getId())));
    }

    @Test
    void onlyThree() {
        all.subList(3, all.size()).clear();
        var old = get();
        assertEquals(old, service.select(7L, true, null).picks().stream().map(Bookmark::getId).toList());
    }

    @Test
    void fourBooks() {
        all.subList(4, all.size()).clear();
        get();
        assertTrue(service.select(7L, true, null).picks().stream().anyMatch(b -> b.getId() == 4));
    }

    @Test
    void includeReplacesOne() {
        var old = get();
        var next = service.select(7L, false, 8L).picks();
        assertEquals(2, next.stream().filter(b -> old.contains(b.getId())).count());
        assertTrue(next.stream().anyMatch(b -> b.getId() == 8));
        assertEquals(3, data.size());
    }

    @Test
    void includeInitially() {
        assertTrue(service.select(7L, false, 8L).picks().stream().anyMatch(b -> b.getId() == 8));
    }

    @Test
    void insufficient() {
        all.subList(2, all.size()).clear();
        assertTrue(service.select(7L, false, 2L).picks().isEmpty());
        assertTrue(data.isEmpty());
    }

    @Test
    void deletedBookmarkFilled() {
        get();
        all.removeIf(b -> b.getId() == 2);
        assertEquals(List.of(1L, 4L, 3L), get());
    }

    @Test
    void partialIncludeRetainsValid() {
        get();
        all.removeIf(b -> b.getId() == 2);
        assertEquals(
                List.of(1L, 8L, 3L),
                service.select(7L, false, 8L).picks().stream().map(Bookmark::getId).toList()
        );
    }

    @Test
    void includeDuplicate() {
        get();
        service.select(7L, false, 1L);
        assertEquals(3, data.size());
    }

    @Test
    void editsAndMovesKeepIds() {
        var old = get();
        all.get(0).setTitle("changed");
        all.get(0).setFolderId(42L);
        assertEquals(old, get());
    }

    @Test
    void otherUserCannotAccess() {
        assertThrows(RuntimeException.class, () -> service.select(99L, false, null));
        verify(rows, never()).findAllByUserIdOrderByPositionAsc(99L);
    }

    @Test
    void usesUserLockBeforeRead() {
        get();
        var order = inOrder(users, rows);
        order.verify(users).lockById(7L);
        order.verify(rows).findAllByUserIdOrderByPositionAsc(7L);
    }

    @Test
    void databaseFailurePropagates() {
        doThrow(new IllegalStateException("db")).when(rows).flush();
        assertThrows(IllegalStateException.class, () -> get());
    }
}
