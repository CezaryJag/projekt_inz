package com.example.demo.service;

import com.example.demo.entity.Car;
import com.example.demo.entity.CarModel;
import com.example.demo.entity.Maintenance;
import com.example.demo.repository.CarModelRepository;
import com.example.demo.repository.CarRepository;
import com.example.demo.repository.MaintenanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CarService {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CarModelRepository carModelRepository;

    @Autowired
    private MaintenanceRepository maintenanceRepository;

    public List<Car> getAllCars() {
        return carRepository.findAll();
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

        Maintenance maintenance = car.getMaintenance();
        if (maintenance != null) {
            Maintenance existingMaintenance = maintenanceRepository.findByMaintenanceDateAndCost(
                    maintenance.getMaintenanceDate(), maintenance.getCost());
            if (existingMaintenance == null) {
                maintenance = maintenanceRepository.save(maintenance);
            } else {
                maintenance = existingMaintenance;
            }
            car.setMaintenance(maintenance);
            car.setMaintenanceId(maintenance.getMaintenanceId());
        }

        return carRepository.save(car);
    }

    public Car getCarById(Long vehicleId) {
        return carRepository.findById(vehicleId).orElseThrow(() -> new NoSuchElementException("Car not found"));
    }

    public Car updateCar(Car car) {
        CarModel carModel = carModelRepository.findById(car.getModelId())
                .orElseThrow(() -> new NoSuchElementException("CarModel not found"));
        car.setCarModel(carModel);

        Maintenance maintenance = car.getMaintenance();
        if (maintenance != null) {
            Maintenance existingMaintenance = maintenanceRepository.findByMaintenanceDateAndCost(
                    maintenance.getMaintenanceDate(), maintenance.getCost());
            if (existingMaintenance == null) {
                maintenance = maintenanceRepository.save(maintenance);
            } else {
                maintenance = existingMaintenance;
            }
            car.setMaintenance(existingMaintenance);
        }

        return carRepository.save(car);
    }

    public void deleteCar(Long vehicleId) {
        carRepository.deleteById(vehicleId);
    }
}