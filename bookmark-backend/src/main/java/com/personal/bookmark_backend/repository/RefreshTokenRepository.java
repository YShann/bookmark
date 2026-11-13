package com.personal.bookmark_backend.repository;

import com.personal.bookmark_backend.entity.RefreshToken;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, Long> {

    // 查詢指定使用者的所有 Refresh Token
    List<RefreshToken> findAllByUserId(Long userId);

    @Query("""
            select case when count(t) > 0 then true else false end from RefreshToken t
            where t.id = :id and t.userId = :userId and t.revokedAt is null
              and t.expiresAt > current_timestamp and t.absoluteExpiresAt > current_timestamp
            """)
    boolean existsActiveSession(@Param("id") Long id, @Param("userId") Long userId);

    // 查詢指定使用者目前尚未撤銷的 Refresh Token
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    List<RefreshToken> findAllByUserIdAndRevokedAtIsNull(Long userId);

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from RefreshToken t where t.id = :id")
    Optional<RefreshToken> lockById(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select t from RefreshToken t where t.userId = :userId and t.revokedAt is null
              and (t.familyId = :familyId or t.id = :familyId)
            """)
    List<RefreshToken> lockActiveFamily(@Param("userId") Long userId, @Param("familyId") Long familyId);
}
