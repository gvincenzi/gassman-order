package org.gassman.order.repository;

import org.gassman.order.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByActiveTrue();
    User findByMail(String mail);
    Optional<User> findByTelegramUserId(Integer telegramUserId);
}
