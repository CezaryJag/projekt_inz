package com.example.demo.service;

import com.example.demo.entity.CarGroup;
import com.example.demo.repository.CarGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CarGroupService {

    @Autowired
    private CarGroupRepository carGroupRepository;

    public List<CarGroup> getAllCarGroups() {
        return carGroupRepository.findAll();
    }

    public CarGroup saveCarGroup(CarGroup carGroup) {
        return carGroupRepository.save(carGroup);
    }

    public CarGroup getCarGroupById(Long groupId) {
        return carGroupRepository.findById(groupId).orElseThrow(() -> new NoSuchElementException("CarGroup not found"));
    }

    public void deleteCarGroup(Long groupId) {
        carGroupRepository.deleteById(groupId);
    }
}