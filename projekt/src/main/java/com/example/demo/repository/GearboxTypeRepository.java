package com.example.demo.repository;

import com.example.demo.entity.GearboxType1;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GearboxTypeRepository extends JpaRepository<GearboxType1, Long> {
}