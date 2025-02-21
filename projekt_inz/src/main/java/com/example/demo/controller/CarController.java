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
        //List<Car> cars = carService.getAllCars();
        //System.out.println("Returned cars: " + cars);
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
            @RequestParam(required = false) String milageFrom,
            @RequestParam(required = false) String milageTo,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String gearboxType,
            @RequestParam(required = false) String gearboxCount,
            //@RequestBody(required = false) String fuelType,
            @RequestParam(required = false) String carModel,
            @RequestParam(required = false) String fuelType,
            @RequestParam(required = false) String bodyType,
            @RequestParam(required = false) String seatCount
    ) {
        // Obsługa null dla parametrów (ustawienie pustego stringa lub domyślnej wartości)
        List<Car> filteredCars = carService.getCarsByFilter(yearFrom,yearTo,milageFrom,milageTo,color,
                status, gearboxType,gearboxCount,carModel, fuelType, bodyType, seatCount);//fuelType,carModel);
                //yearFrom != null ? yearFrom : "",
                //yearTo != null ? yearTo : "");
        //System.out.println("Returned cars: " + filteredCars);
        return ResponseEntity.ok(filteredCars);
    }
}