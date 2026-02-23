package com.projects.intrustion_detection.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BlockedUserRepository extends JpaRepository<BlockedUserRepository, Long> {
}
