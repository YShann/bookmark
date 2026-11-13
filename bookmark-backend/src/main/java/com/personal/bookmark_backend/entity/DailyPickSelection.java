package com.personal.bookmark_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_pick_selections", uniqueConstraints = {
        @UniqueConstraint(name = "uk_daily_pick_user_position", columnNames = {"user_id", "position"}),
        @UniqueConstraint(name = "uk_daily_pick_user_bookmark", columnNames = {"user_id", "bookmark_id"})})
@Getter
@Setter
public class DailyPickSelection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Column(name = "bookmark_id", nullable = false)
    private Long bookmarkId;
    @Column(name = "selection_date", nullable = false)
    private LocalDate selectionDate;
    @Min(1)
    @Max(3)
    @Column(nullable = false, columnDefinition = "TINYINT")
    private Integer position;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void created() {
        if (createdAt == null) createdAt = LocalDateTime.now(java.time.ZoneId.of("Asia/Taipei"));
    }
}
