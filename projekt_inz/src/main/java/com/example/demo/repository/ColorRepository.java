package com.example.demo.repository;

import com.example.demo.entity.CarModel;
import com.example.demo.entity.Color;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ColorRepository extends JpaRepository<Color, Integer> {
    Color findByColorName(String colorName);
}