package com.example.demo.repository;

import com.example.demo.entity.GearboxCount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GearCountRepository extends JpaRepository<GearboxCount, Long> {
}