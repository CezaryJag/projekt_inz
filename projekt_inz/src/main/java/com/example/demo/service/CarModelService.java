package com.example.demo.service;

import com.example.demo.entity.CarModel;
import com.example.demo.repository.CarModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarModelService {

    @Autowired
    private CarModelRepository carModelRepository;

    public List<CarModel> getAllCarModels() {
        return carModelRepository.findAll();
    }
}