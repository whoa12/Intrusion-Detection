package com.projects.intrustion_detection.repository;

import com.projects.intrustion_detection.Entity.Attack;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttackRepository extends JpaRepository<Attack, Integer> {

}
