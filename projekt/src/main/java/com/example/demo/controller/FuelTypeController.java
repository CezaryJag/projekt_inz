package com.example.demo.controller;

import com.example.demo.entity.FuelType;
import com.example.demo.service.FuelTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fuel_types")
public class FuelTypeController {

    @Autowired
    private FuelTypeService fuelTypeService;

    @GetMapping
    public List<FuelType> getAllFuelTypes() {
        return fuelTypeService.getAllFuelTypes();
    }
}