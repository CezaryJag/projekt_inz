package com.example.demo.repository;

import com.example.demo.entity.AccessKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccessKeyRepository extends JpaRepository<AccessKey, Long> {
    Optional<AccessKey> findByKeyValueAndIsUsedFalse(String keyValue);
}