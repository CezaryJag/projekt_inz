package com.example.demo.controller;

import com.example.demo.entity.Car;
import com.example.demo.entity.CarGroup;
import com.example.demo.service.CarGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/car-groups")
public class CarGroupController {

    @Autowired
    private CarGroupService carGroupService;

    @GetMapping
    public List<CarGroup> getAllCarGroups() {
        return carGroupService.getAllCarGroups();
    }

    @PostMapping
    public CarGroup createCarGroup(@RequestBody CarGroup carGroup) {
        return carGroupService.saveCarGroup(carGroup);
    }

    @PostMapping("/{groupId}/cars")
    public CarGroup addCarsToGroup(@PathVariable Long groupId, @RequestBody Set<Long> carIds) {
        return carGroupService.addCarsToGroup(groupId, carIds);
    }

    @GetMapping("/{groupId}/cars")
    public List<Car> getCarsByGroupId(@PathVariable Long groupId) {
        CarGroup carGroup = carGroupService.getCarGroupById(groupId);
        return new ArrayList<>(carGroup.getCars());
    }

    @DeleteMapping("/{groupId}")
    public void deleteCarGroup(@PathVariable Long groupId) {
        carGroupService.deleteCarGroup(groupId);
    }
}