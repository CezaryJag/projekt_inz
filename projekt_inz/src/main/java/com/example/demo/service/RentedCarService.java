package com.example.demo.service;

import com.example.demo.entity.RentedCar;
import com.example.demo.repository.RentedCarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RentedCarService {

    @Autowired
    private RentedCarRepository rentedCarRepository;

    public List<RentedCar> getRentedCarsByUserId(Long userId) {
        return rentedCarRepository.findByUserId(userId);
    }

    public RentedCar rentCar(RentedCar rentedCar) {
        return rentedCarRepository.save(rentedCar);
    }

    public void cancelRent(Long rentId) {
        rentedCarRepository.deleteById(rentId);
    }
}