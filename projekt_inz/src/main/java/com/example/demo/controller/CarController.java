package com.example.demo.controller;

import com.example.demo.entity.Car;
import com.example.demo.entity.CarGroup;
import com.example.demo.service.CarGroupService;
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

    @Autowired
    private CarGroupService carGroupService;


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
            @RequestParam(required = false) String milageFrom,
            @RequestParam(required = false) String milageTo,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String gearboxType,
            @RequestParam(required = false) String gearboxCount,
            //@RequestBody(required = false) String fuelType,
            @RequestParam(required = false) String carModel
    ) {
        // Obsługa null dla parametrów (ustawienie pustego stringa lub domyślnej wartości)
        List<Car> filteredCars = carService.getCarsByFilter(yearFrom,yearTo,milageFrom,milageTo,color,
                status, gearboxType,gearboxCount,carModel);//fuelType,carModel);
                //yearFrom != null ? yearFrom : "",
                //yearTo != null ? yearTo : "");
        return ResponseEntity.ok(filteredCars);
    }
    @PostMapping("/groups")
    public ResponseEntity<CarGroup> createCarGroup(@RequestBody CarGroup carGroup) {
        CarGroup newGroup = carGroupService.saveCarGroup(carGroup);
        return ResponseEntity.ok(newGroup);
    }

    @PutMapping("/groups/{groupId}/add-cars")
    public ResponseEntity<Void> addCarsToGroup(@PathVariable Long groupId, @RequestBody List<Long> carIds) {
        CarGroup carGroup = carGroupService.getCarGroupById(groupId);
        for (Long carId : carIds) {
            Car car = carService.getCarById(carId);
            car.setCarGroup(carGroup);
            car.setGroupId(groupId);
            carService.saveCar(car);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/groups")
    public List<CarGroup> getAllCarGroups() {
        return carGroupService.getAllCarGroups();
    }

    @GetMapping("/groups/{groupId}")
    public ResponseEntity<CarGroup> getCarGroupById(@PathVariable Long groupId) {
        CarGroup carGroup = carGroupService.getCarGroupById(groupId);
        return ResponseEntity.ok(carGroup);
    }

    @DeleteMapping("/groups/{groupId}")
    public ResponseEntity<Void> deleteCarGroup(@PathVariable Long groupId) {
        carGroupService.deleteCarGroup(groupId);
        return ResponseEntity.noContent().build();
    }
}


