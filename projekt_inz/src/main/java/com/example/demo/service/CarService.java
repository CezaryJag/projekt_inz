package com.example.demo.service;

import com.example.demo.entity.Car;
import com.example.demo.entity.CarModel;
import com.example.demo.entity.Color;
import com.example.demo.entity.Maintenance;
import com.example.demo.repository.CarModelRepository;
import com.example.demo.repository.CarRepository;
import com.example.demo.repository.ColorRepository;
import com.example.demo.repository.MaintenanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
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
    private ColorRepository colorRepository;

    @Autowired
    private MaintenanceRepository maintenanceRepository;

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    public List<Car> getCarsByFilter(
            String yearFrom,
            String yearTo,
            String milageFrom,
            String milageTo,
            String color,
            String status,
            String gearType,
            String gearCount,
            String carModel,
            String fuelType,
            String bodyType,
            String seatCount) {
        if (yearFrom == null || yearFrom.isEmpty()) {
            yearFrom = "0";
        }
        if (yearTo == null || yearTo.isEmpty()) {
            yearTo = "10000";
        }
        if (milageFrom == null || milageFrom.isEmpty()) {
            milageFrom = "0";
        }
        if (milageTo == null || milageTo.isEmpty()) {
            milageTo = "1000000";
        }
        if (color == null || color.isEmpty()) {
            color = null;
        }
        if (status == null || status.isEmpty()) {
            status = null;
        }
        if (gearType == null || gearType.isEmpty()) {
            gearType = null;
        }
        if (gearCount == null || gearCount.isEmpty()) {
            gearCount = null;
        }
        if (carModel == null || carModel.isEmpty()) {
            carModel = null;
        }
        if (fuelType == null || fuelType.isEmpty()) {
            fuelType = null;
        }
        if (bodyType == null || bodyType.isEmpty()) {
            bodyType = null;
        }
        /*if (seatCount == null || seatCount.isEmpty()) {
            seatCount = "0";
        }*/
        return carRepository.findByFilters(
                parseInteger(yearFrom),
                parseInteger(yearTo),
                parseInteger(milageFrom),
                parseInteger(milageTo),
                color,
                status,
                gearType,
                parseInteger(gearCount),
                carModel,
                fuelType,
                bodyType,
                parseInteger(seatCount)
        );

    }
    Integer parseInteger(String value) {
        try {
            return (value != null && !value.isEmpty()) ? Integer.parseInt(value) : null;
        } catch (NumberFormatException e) {
            return null;
        }
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

        Color color = car.getColor();
        Color existingColor = colorRepository.findByColorName(color.getColorName());

        if (existingColor == null) {
            color = colorRepository.save(color);
        } else {
            color = existingColor;
        }

        car.setColor(color);
        car.setColorId(color.getColorId());

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

        Color color = car.getColor();
        if (color != null) {
            Color existingColor = colorRepository.findByColorName(color.getColorName());
            if (existingColor == null) {
                color = colorRepository.save(color);
            } else {
                color = existingColor;
            }
            car.setColor(color);
        }

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

    public void deleteCar(Long vehicleId) {
        carRepository.deleteById(vehicleId);
    }
}