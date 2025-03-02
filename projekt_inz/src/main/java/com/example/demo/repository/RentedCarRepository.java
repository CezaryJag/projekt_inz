package com.example.demo.repository;

import com.example.demo.entity.RentedCar;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RentedCarRepository extends JpaRepository<RentedCar, Long> {
    List<RentedCar> findByUserId(Long userId);

    RentedCar findByVehicleId(Long vehicleId);
}