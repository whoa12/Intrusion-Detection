package com.projects.intrustion_detection.repository;

import com.projects.intrustion_detection.Entity.BlockedIpAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlockedUserRepository extends JpaRepository<BlockedIpAddress, Integer> {
}
