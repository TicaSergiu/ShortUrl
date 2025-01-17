package com.shorturl.Repositories;

import com.shorturl.Models.UserPO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserPO, Long> {
    UserPO findByUsername(String username);

    boolean existsByUsername(String username);
}
