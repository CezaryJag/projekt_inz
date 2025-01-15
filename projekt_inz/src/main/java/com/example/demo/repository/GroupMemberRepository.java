package com.example.demo.repository;

import com.example.demo.entity.GroupMember;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    List<GroupMember> findByCarGroup_GroupId(Long groupId);
    List<GroupMember> findByUser_UserId(Long userId);
    List<GroupMember> findByUser(User user);
}