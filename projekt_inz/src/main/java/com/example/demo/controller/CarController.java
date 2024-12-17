package com.example.demo.controller;

import com.example.demo.entity.Car;
import com.example.demo.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/cars")
public class CarController {

    @Autowired
    private CarService carService;

    @GetMapping
    public List<Car> getAllCars() {
        return carService.getAllCars();
    }

    @PostMapping
    public ResponseEntity<Car> addCar(@RequestBody Car car) {
        Car newCar = carService.saveCar(car);
        return ResponseEntity.ok(newCar);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Car> getCarById(@PathVariable Long id) {
        Car car = carService.getCarById(id);
        return ResponseEntity.ok(car);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Car> updateCar(@PathVariable Long id, @RequestBody Car car) {
        car.setVehicleId(id);
        Car updatedCar = carService.saveCar(car);
        return ResponseEntity.ok(updatedCar);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Car>> getCarsByFilters(
            @RequestParam(required = false) String yearFrom,
            @RequestParam(required = false) String yearTo,
            @RequestBody(required = false) String milageFrom,
            @RequestBody(required = false) String milageTo,
            @RequestBody(required = false) String color,
            @RequestBody(required = false) String status,
            @RequestBody(required = false) String gearType,
            @RequestBody(required = false) String gearCount,
            //@RequestBody(required = false) String fuelType,
            @RequestBody(required = false) String carModel
    ) {
        // Obsługa null dla parametrów (ustawienie pustego stringa lub domyślnej wartości)
        List<Car> filteredCars = carService.getCarsByFilter(yearFrom,yearTo,milageFrom,milageTo,color,
                status, gearType,gearCount,carModel);//fuelType,carModel);
                //yearFrom != null ? yearFrom : "",
                //yearTo != null ? yearTo : "");
        return ResponseEntity.ok(filteredCars);
    }
}