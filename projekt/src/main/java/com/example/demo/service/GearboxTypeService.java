package com.example.demo.service;

import com.example.demo.entity.GearboxType1;
import com.example.demo.repository.GearboxTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GearboxTypeService {

    @Autowired
    private GearboxTypeRepository gearboxTypeRepository;

    public List<GearboxType1> getAllGearboxTypes() {
        return gearboxTypeRepository.findAll();
    }
}