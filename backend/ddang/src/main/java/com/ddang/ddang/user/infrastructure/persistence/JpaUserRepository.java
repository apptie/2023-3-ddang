package com.ddang.ddang.user.infrastructure.persistence;

import com.ddang.ddang.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserRepository extends JpaRepository<User, Long> {
}
