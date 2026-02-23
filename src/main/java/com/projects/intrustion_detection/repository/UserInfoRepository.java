package com.projects.intrustion_detection.repository;

import com.projects.intrustion_detection.Entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserInfoRepository extends JpaRepository<UserInfo, Integer > {
    Optional<UserInfo> findByEmail(String username);
}
