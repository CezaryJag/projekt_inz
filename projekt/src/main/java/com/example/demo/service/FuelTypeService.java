package com.example.demo.service;

import com.example.demo.entity.FuelType;
import com.example.demo.repository.FuelTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FuelTypeService {

    @Autowired
    private FuelTypeRepository fuelTypeRepository;

    public List<FuelType> getAllFuelTypes() {
        return fuelTypeRepository.findAll();
    }
}