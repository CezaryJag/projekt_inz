package com.example.demo.repository;

import com.example.demo.entity.CarModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarModelRepository extends JpaRepository<CarModel, Integer> {
    CarModel findByModelName(String modelName);
}