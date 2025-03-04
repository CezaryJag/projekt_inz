package com.example.demo.controller;

import com.example.demo.entity.Car;
import com.example.demo.entity.CarGroup;
import com.example.demo.entity.GroupMember;
import com.example.demo.service.CarGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Map;

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
    public ResponseEntity<?> createCarGroup(@RequestBody CarGroup carGroup) {
        if (carGroupService.existsByGroupName(carGroup.getGroupName())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Grupa o takiej nazwie już istnieje");
        }
        CarGroup createdGroup = carGroupService.saveCarGroup(carGroup);
        return ResponseEntity.ok(createdGroup);
    }
    @GetMapping("/user-groups")
    public ResponseEntity<List<CarGroup>> getUserGroups() {
        List<CarGroup> userGroups = carGroupService.getCarGroupsForLoggedInUser();
        return ResponseEntity.ok(userGroups);
    }

    @PostMapping("/{groupId}/cars")
    public CarGroup addCarsToGroup(@PathVariable Long groupId, @RequestBody Set<Long> carIds) {
        return carGroupService.addCarsToGroup(groupId, carIds);
    }

    @GetMapping("/{groupId}/cars")
    public ResponseEntity<List<Car>> getFilteredCarsByGroup(
            @PathVariable Long groupId,
            @RequestParam(required = false) String yearFrom,
            @RequestParam(required = false) String yearTo,
            @RequestParam(required = false) String milageFrom,
            @RequestParam(required = false) String milageTo,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String gearboxType,
            @RequestParam(required = false) String gearboxCount,
            //@RequestBody(required = false) String fuelType,
            @RequestParam(required = false) String carModel,
            @RequestParam(required = false) String fuelType,
            @RequestParam(required = false) String bodyType,
            @RequestParam(required = false) String seatCount
    ) {
        List<Car> filteredCars = carGroupService.getFilteredCarsByGroup(
                groupId, yearFrom,yearTo,milageFrom,milageTo,color,
                status, gearboxType,gearboxCount,carModel, fuelType, bodyType, seatCount
        );
        return ResponseEntity.ok(filteredCars);
    }

    @DeleteMapping("/{groupId}/cars/{carId}")
    public void removeCarFromGroup(@PathVariable Long groupId, @PathVariable Long carId) {
        carGroupService.removeCarFromGroup(groupId, carId);
    }

    @DeleteMapping("/{groupId}")
    public void deleteCarGroup(@PathVariable Long groupId) {
        carGroupService.deleteCarGroup(groupId);
    }

    @GetMapping("/{groupId}/members")
    public List<GroupMember> getGroupMembers(@PathVariable Long groupId) {
        return carGroupService.getGroupMembers(groupId);
    }

    @PostMapping("/{groupId}/members")
    public GroupMember addUserToGroup(@PathVariable Long groupId, @RequestBody GroupMemberRequest request) {
        return carGroupService.addUserToGroup(groupId, request.getEmail(), request.getRole());
    }

    @DeleteMapping("/{groupId}/members/{userId}")
    public void removeUserFromGroup(@PathVariable Long groupId, @PathVariable Long userId) {
        carGroupService.removeUserFromGroup(groupId, userId);
    }

    @PutMapping("/{groupId}/members/{userId}")
    public ResponseEntity<Void> updateUserRole(@PathVariable Long groupId, @PathVariable Long userId, @RequestBody Map<String, String> requestBody) {
        String newRole = requestBody.get("role");
        System.out.println("groupId = " + groupId + ", userId = " + userId + ", newRole = " + newRole);
        carGroupService.updateUserRole(groupId, userId, newRole);
        return ResponseEntity.ok().build();
    }

}