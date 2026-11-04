package com.personal.bookmark_backend.repository;

import com.personal.bookmark_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 依照 Email 查詢使用者
    // Optional：代表查詢結果可能有資料，也可能沒有資料
    Optional<User> findByEmail(String email);

    // 檢查 Email 是否已經被註冊
    // 有資料回傳 true，沒有資料回傳 false
    boolean existsByEmail(String email);
}
