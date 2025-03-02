package com.example.demo.controller;

import com.example.demo.entity.Car;
import com.example.demo.entity.RentedCar;
import com.example.demo.entity.User;
import com.example.demo.service.RentedCarService;
import com.example.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import com.example.demo.service.CarService;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/rented-cars")
public class RentedCarController {

    private static final Logger log = LoggerFactory.getLogger(RentedCarController.class);
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
    public ResponseEntity<?> extendRent(@PathVariable Long vehicleId, @RequestBody Map<String, String> requestBody) {
        try {
            String newEndDateStr = requestBody.get("newEndDate");
            LocalDate newEndDate = LocalDate.parse(newEndDateStr);
            rentedCarService.extendRent(vehicleId, newEndDate.atStartOfDay());
            return ResponseEntity.ok("Wypożyczenie przedłużone.");
        } catch (Exception e) {
            log.error("Error extending rent for vehicleId {}: {}", vehicleId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while extending the rent");
        }
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
    public ResponseEntity<String> rentCar(@PathVariable Long vehicleId, @RequestHeader("Authorization") String token, @RequestBody Map<String, String> rentRequest) {
        Long userId = getUserIdFromToken(token);
        Car car = carService.getCarById(vehicleId);

        if ("niedostępny".equals(car.getStatus())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Samochód niedostępny");
        }

        car.setStatus("niedostępny");
        carService.saveCar(car);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate rentDate = LocalDate.parse(rentRequest.get("rentDate"), formatter);
        LocalDate rentalEndDate = LocalDate.parse(rentRequest.get("rentalEndDate"), formatter);

        RentedCar rentedCar = new RentedCar();
        rentedCar.setUserId(userId);
        rentedCar.setVehicleId(vehicleId);
        rentedCar.setRentDate(rentDate.atStartOfDay());  //konwersja na date
        rentedCar.setRentEndDate(rentalEndDate.atStartOfDay());

        rentedCarService.rentCar(rentedCar);

        return ResponseEntity.ok("Wypożyczono samochód od " + rentedCar.getRentDate() + " do " + rentedCar.getRentEndDate());
    }

    private Long getUserIdFromToken(String token) {
        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new NoSuchElementException("User not found"));
        return user.getUserId();
    }
}