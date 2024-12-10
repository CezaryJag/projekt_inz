package com.example.demo.controller;

import com.example.demo.entity.GearboxType1;
import com.example.demo.service.GearboxTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gearbox1")
public class Gearbox1Controller {

    @Autowired
    private GearboxTypeService gearboxTypeService;

    @GetMapping
    public List<GearboxType1> getAllGearboxTypes() {
        return gearboxTypeService.getAllGearboxTypes();
    }
}