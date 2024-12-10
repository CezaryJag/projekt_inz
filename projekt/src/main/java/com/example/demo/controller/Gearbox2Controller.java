package com.example.demo.controller;

import com.example.demo.entity.GearboxCount;
import com.example.demo.service.GearCountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gearbox2")
public class Gearbox2Controller {

    @Autowired
    private GearCountService gearCountService;

    @GetMapping
    public List<GearboxCount> getAllGearCounts() {
        return gearCountService.getAllGearCounts();
    }
}