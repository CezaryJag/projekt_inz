package com.example.demo.controller;

import com.example.demo.entity.CarModel;
import com.example.demo.service.CarModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/car-models")
public class CarModelController {

    @Autowired
    private CarModelService carModelService;

    @GetMapping
    public List<CarModel> getAllCarModels() {
        return carModelService.getAllCarModels();
    }
}