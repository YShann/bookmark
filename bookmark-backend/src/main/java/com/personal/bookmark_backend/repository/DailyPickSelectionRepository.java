package com.personal.bookmark_backend.repository;
import com.personal.bookmark_backend.entity.DailyPickSelection;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface DailyPickSelectionRepository extends JpaRepository<DailyPickSelection, Long> {
    List<DailyPickSelection> findAllByUserIdOrderByPositionAsc(Long userId);
    void deleteAllByUserIdAndBookmarkIdIn(Long userId, List<Long> bookmarkIds);
}
