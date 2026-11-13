package com.personal.bookmark_backend.repository;

import com.personal.bookmark_backend.entity.DailyPickSelection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface DailyPickSelectionRepository extends JpaRepository<DailyPickSelection, Long> {
    List<DailyPickSelection> findAllByUserIdOrderByPositionAsc(Long userId);

    @Query("select distinct s.userId from DailyPickSelection s where s.selectionDate < :date")
    List<Long> findUsersWithOlderSelections(LocalDate date);

    void deleteAllByUserIdAndBookmarkIdIn(Long userId, List<Long> bookmarkIds);
}
