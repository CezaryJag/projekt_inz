package com.example.demo.service;

import com.example.demo.entity.GearboxCount;
import com.example.demo.repository.GearCountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GearCountService {

    @Autowired
    private GearCountRepository gearCountRepository;

    public List<GearboxCount> getAllGearCounts() {
        return gearCountRepository.findAll();
    }
}