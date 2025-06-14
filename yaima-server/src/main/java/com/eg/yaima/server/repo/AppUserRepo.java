package com.eg.yaima.server.repo;

import com.eg.yaima.server.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepo extends JpaRepository<AppUser, Long> {
}
