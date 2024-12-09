package com.example.demo.service;

import com.example.demo.entity.Car;
import com.example.demo.entity.CarModel;
import com.example.demo.repository.CarModelRepository;
import com.example.demo.repository.CarRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarService {

    private final CarRepository carRepository;
    private final CarModelRepository carModelRepository;

    public CarService(CarRepository carRepository, CarModelRepository carModelRepository) {
        this.carRepository = carRepository;
        this.carModelRepository = carModelRepository;
    }

    public Car saveCar(Car car) {
        CarModel carModel = car.getCarModel();
        CarModel existingCarModel = carModelRepository.findByModelName(carModel.getModelName());

        if (existingCarModel == null) {
            carModel = carModelRepository.save(carModel);
        } else {
            carModel = existingCarModel;
        }

        car.setCarModel(carModel);
        car.setModelId(carModel.getModelId());
        return carRepository.save(car);
    }

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }
}