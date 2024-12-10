package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "gearbox1")
public class GearboxType1 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gearbox_id")
    private Long gearboxId;

    @Column(name = "gear_type", nullable = false, length = 50)
    private String gearType;

    // Getters and setters
    public Long getGearboxId() {
        return gearboxId;
    }

    public void setGearboxId(Long gearboxId) {
        this.gearboxId = gearboxId;
    }

    public String getGearType() {
        return gearType;
    }

    public void setGearType(String gearType) {
        this.gearType = gearType;
    }
}