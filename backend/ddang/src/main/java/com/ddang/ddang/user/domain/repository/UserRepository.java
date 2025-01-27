package com.ddang.ddang.user.domain.repository;

import com.ddang.ddang.user.domain.User;

import java.util.Optional;

public interface UserRepository {

    User save(final User user);

    User getByIdOrThrow(final Long id);

    Optional<User> findById(final Long id);

    Optional<User> findByOauthId(final String oauthId);

    boolean existsByIdAndDeletedIsTrue(final Long id);

    boolean existsByNameEndingWith(final String name);

    boolean existsByName(final String name);
}
