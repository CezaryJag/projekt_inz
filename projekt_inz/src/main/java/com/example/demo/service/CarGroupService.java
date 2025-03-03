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

    public List<CarGroup> getCarGroupsForLoggedInUser() {
        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new NoSuchElementException("User not found"));
        return groupMemberRepository.findByUser(user).stream()
                .map(GroupMember::getCarGroup)
                .collect(Collectors.toList());
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
        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new NoSuchElementException("User not found"));
        long userId = user.getUserId();
        List<GroupMember> members = groupMemberRepository.findByCarGroup_GroupId(groupId);
        GroupMember groupMember = members.stream()
                .filter(m -> m.getUser().getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("User not in group"));

        CarGroup carGroup = getCarGroupById(groupId);
        User user1 = userRepository.findByEmail(email).orElseThrow(() -> new NoSuchElementException("User not found"));
        GroupMember groupMember1 = new GroupMember();
        groupMember1.setCarGroup(carGroup);
        groupMember1.setUser(user1);
        groupMember1.setRole(role);

        if ("superadmin".equals(groupMember1.getRole())) {
            throw new IllegalArgumentException("Cannot add superadmin to the group");
        }
        if ("admin".equals(groupMember.getRole()) && "admin".equals(groupMember1.getRole())) {
            throw new IllegalArgumentException("Admin cannot add admin to the group");
        }
        if ("user".equals(groupMember.getRole())) {
            throw new IllegalArgumentException("User cannot add other people to the group");
        }
        return groupMemberRepository.save(groupMember1);
    }

    public void removeUserFromGroup(Long groupId, Long userId) {
        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new NoSuchElementException("User not found"));
        long userIdL = user.getUserId();
        List<GroupMember> members = groupMemberRepository.findByCarGroup_GroupId(groupId);
        GroupMember groupMember = members.stream()
                .filter(m -> m.getUser().getUserId().equals(userIdL))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("User not in group"));

        List<GroupMember> members1 = groupMemberRepository.findByCarGroup_GroupId(groupId);
        GroupMember groupMember1 = members1.stream()
                .filter(m -> m.getUser().getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("User not in group"));

        if ("superadmin".equals(groupMember1.getRole())) {
            throw new IllegalArgumentException("Cannot remove superadmin from the group");
        }
        if ("admin".equals(groupMember.getRole()) && "admin".equals(groupMember1.getRole())) {
            throw new IllegalArgumentException("Admin cannot remove admin to the group");
        }
        if ("user".equals(groupMember.getRole())) {
            throw new IllegalArgumentException("User cannot remove other people to the group");
        }
        groupMemberRepository.delete(groupMember);
    }

    public void updateUserRole(Long groupId, Long userId, String newRole) {
        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new NoSuchElementException("User not found"));
        long userIdL = user.getUserId();
        List<GroupMember> members = groupMemberRepository.findByCarGroup_GroupId(groupId);
        GroupMember groupMember = members.stream()
                .filter(m -> m.getUser().getUserId().equals(userIdL))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("User not in group"));

        System.out.println("groupId = " + groupId + ", userId = " + userId + ", newRole = " + newRole);
        GroupMember groupMember1 = groupMemberRepository.findByCarGroup_GroupId(groupId).stream()
                .filter(member -> member.getUser().getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> {
                    System.err.println("GroupMember not found for groupId = " + groupId + ", userId = " + userId);
                    return new NoSuchElementException("GroupMember not found");
                });
        if ("superadmin".equals(groupMember1.getRole())) {
            throw new IllegalArgumentException("Cannot change role of superadmin");
        }
        if ("user".equals(groupMember.getRole()) || "admin".equals(groupMember.getRole())) {
            throw new IllegalArgumentException("User cannot remove other people to the group");
        }
        groupMember.setRole(newRole);
        groupMemberRepository.save(groupMember);
    }

    public boolean existsByGroupName(String groupName) {
        return carGroupRepository.existsByGroupName(groupName);
    }
}