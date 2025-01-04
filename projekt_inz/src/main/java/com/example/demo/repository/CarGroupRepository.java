package com.example.demo.repository;

import com.example.demo.entity.CarGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarGroupRepository extends JpaRepository<CarGroup, Long> {
}