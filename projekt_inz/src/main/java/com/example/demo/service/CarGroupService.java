package com.example.demo.service;

import com.example.demo.entity.Car;
import com.example.demo.entity.CarGroup;
import com.example.demo.repository.CarGroupRepository;
import com.example.demo.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.NoSuchElementException;

@Service
public class CarGroupService {

    @Autowired
    private CarGroupRepository carGroupRepository;

    @Autowired
    private CarRepository carRepository;

    public List<CarGroup> getAllCarGroups() {
        return carGroupRepository.findAll();
    }

    public CarGroup saveCarGroup(CarGroup carGroup) {
        return carGroupRepository.save(carGroup);
    }

    public CarGroup getCarGroupById(Long groupId) {
        return carGroupRepository.findById(groupId).orElseThrow(() -> new NoSuchElementException("CarGroup not found"));
    }

    public void removeCarFromGroup(Long groupId, Long carId) {
        CarGroup carGroup = carGroupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Car not found"));
        carGroup.getCars().remove(car);
        carGroupRepository.save(carGroup);
    }

    public CarGroup addCarsToGroup(Long groupId, Set<Long> carIds) {
        CarGroup carGroup = getCarGroupById(groupId);
        Set<Car> cars = carRepository.findAllById(carIds).stream().collect(Collectors.toSet());
        carGroup.getCars().addAll(cars);
        return carGroupRepository.save(carGroup);
    }
}