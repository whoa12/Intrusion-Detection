package com.projects.intrustion_detection.repository;

import com.projects.intrustion_detection.Entity.BlockedIpAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlockedIpAddressRepository extends JpaRepository<BlockedIpAddress, Integer> {
    Optional<BlockedIpAddress> findByIpAddress(String ipAddress);

    boolean existsByIpAddress(String ip);
}
