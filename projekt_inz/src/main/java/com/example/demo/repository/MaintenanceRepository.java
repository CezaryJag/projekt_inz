package com.example.demo.repository;

import com.example.demo.entity.Maintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface MaintenanceRepository extends JpaRepository<Maintenance, Integer> {
    Maintenance findByMaintenanceDateAndCost(Date maintenanceDate, Integer cost);
}