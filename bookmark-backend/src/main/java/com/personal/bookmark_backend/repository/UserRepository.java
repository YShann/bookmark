package com.personal.bookmark_backend.repository;

import com.personal.bookmark_backend.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select u from User u where u.id = :id")
    Optional<User> lockById(@org.springframework.data.repository.query.Param("id") Long id);

    // 依照 Email 查詢使用者
    // Optional：代表查詢結果可能有資料，也可能沒有資料
    Optional<User> findByEmail(String email);

    @org.springframework.data.jpa.repository.Query("select u.id from User u where u.email = :email")
    Optional<Long> findIdByEmail(@org.springframework.data.repository.query.Param("email") String email);

    Optional<User> findByGoogleSubject(String googleSubject);

    // 檢查 Email 是否已經被註冊
    // 有資料回傳 true，沒有資料回傳 false
    boolean existsByEmail(String email);
}
