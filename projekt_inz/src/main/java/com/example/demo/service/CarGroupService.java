package com.example.demo.service;

import com.example.demo.entity.Car;
import com.example.demo.entity.CarGroup;
import com.example.demo.entity.GroupMember;
import com.example.demo.entity.User;
import com.example.demo.repository.CarGroupRepository;
import com.example.demo.repository.CarRepository;
import com.example.demo.repository.GroupMemberRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private UserRepository userRepository;

    public List<CarGroup> getAllCarGroups() {
        return carGroupRepository.findAll();
    }

    
    public CarGroup saveCarGroup(CarGroup carGroup) {
        CarGroup savedGroup = carGroupRepository.save(carGroup);

        // Get the currently logged-in user
        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new NoSuchElementException("User not found"));

        // Create a new GroupMember and set the user as admin
        GroupMember groupMember = new GroupMember();
        groupMember.setCarGroup(savedGroup);
        groupMember.setUser(user);
        groupMember.setRole("superadmin");

        // Save the GroupMember
        groupMemberRepository.save(groupMember);

        return savedGroup;
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

    public void deleteCarGroup(Long groupId) {
        carGroupRepository.deleteById(groupId);
    }

    public List<GroupMember> getGroupMembers(Long groupId) {
        return groupMemberRepository.findByCarGroup_GroupId(groupId);
    }
    public GroupMember addUserToGroup(Long groupId, String email, String role) {
        CarGroup carGroup = getCarGroupById(groupId);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NoSuchElementException("User not found"));
        GroupMember groupMember = new GroupMember();
        groupMember.setCarGroup(carGroup);
        groupMember.setUser(user);
        groupMember.setRole(role);
        return groupMemberRepository.save(groupMember);
    }

    public void removeUserFromGroup(Long groupId, Long userId) {
        List<GroupMember> members = groupMemberRepository.findByCarGroup_GroupId(groupId);
        GroupMember groupMember = members.stream()
                .filter(m -> m.getUser().getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("User not in group"));
        if ("superadmin".equals(groupMember.getRole())) {
            throw new IllegalArgumentException("Cannot remove superadmin from the group");
        }

        groupMemberRepository.delete(groupMember);
    }

    public void updateUserRole(Long groupId, Long userId, String newRole) {
        GroupMember groupMember = groupMemberRepository.findByCarGroup_GroupId(groupId).stream()
                .filter(member -> member.getUser().getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("GroupMember not found"));

        if ("superadmin".equals(groupMember.getRole())) {
            throw new IllegalArgumentException("Cannot change role of superadmin");
        }

        groupMember.setRole(newRole);
        groupMemberRepository.save(groupMember);
    }
}