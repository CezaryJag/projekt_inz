package com.example.demo.controller;

import com.example.demo.entity.Car;
import com.example.demo.entity.CarGroup;
import com.example.demo.entity.GroupMember;
import com.example.demo.service.CarGroupService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public CarGroup createCarGroup(@RequestBody CarGroup carGroup) {
        return carGroupService.saveCarGroup(carGroup);
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
    public List<Car> getCarsByGroupId(@PathVariable Long groupId) {
        CarGroup carGroup = carGroupService.getCarGroupById(groupId);
        return new ArrayList<>(carGroup.getCars());
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