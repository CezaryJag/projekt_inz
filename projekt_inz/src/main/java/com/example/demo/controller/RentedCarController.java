package com.example.demo.controller;

import com.example.demo.entity.Car;
import com.example.demo.entity.RentedCar;
import com.example.demo.entity.User;
import com.example.demo.service.RentedCarService;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import com.example.demo.service.CarService;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/rented-cars")
public class RentedCarController {

    @Autowired
    private RentedCarService rentedCarService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CarService carService;

    @GetMapping
    public ResponseEntity<List<RentedCar>> getRentedCars(@RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);
        List<RentedCar> rentedCars = rentedCarService.getRentedCarsByUserId(userId);
        return ResponseEntity.ok(rentedCars);
    }

    @PostMapping("/{vehicleId}/extend")
    public ResponseEntity<Void> extendRent(@PathVariable Long vehicleId, @RequestHeader("Authorization") String token) {
        // Implement logic to extend rent
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{vehicleId}/cancel")
    public ResponseEntity<Void> cancelRent(@PathVariable Long vehicleId, @RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);
        List<RentedCar> rentedCars = rentedCarService.getRentedCarsByUserId(userId);
        for (RentedCar rentedCar : rentedCars) {
            if (rentedCar.getVehicleId().equals(vehicleId)) {
                rentedCarService.cancelRent(rentedCar.getRentId());
                Car car = carService.getCarById(vehicleId);
                car.setStatus("dostępny");
                carService.saveCar(car);
                break;
            }
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{vehicleId}/rent")
    public ResponseEntity<String> rentCar(@PathVariable Long vehicleId, @RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);
        Car car = carService.getCarById(vehicleId);

        if ("niedostępny".equals(car.getStatus())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Samochód niedostępny");
        }

        car.setStatus("niedostępny");
        carService.saveCar(car);

        RentedCar rentedCar = new RentedCar();
        rentedCar.setUserId(userId);
        rentedCar.setVehicleId(vehicleId);
        rentedCar.setRentDate(LocalDateTime.now());
        rentedCarService.rentCar(rentedCar);

        return ResponseEntity.ok("Wypożyczono samochód");
    }

    private Long getUserIdFromToken(String token) {
        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new NoSuchElementException("User not found"));
        return user.getUserId();
    }
}