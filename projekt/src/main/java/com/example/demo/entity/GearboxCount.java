package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "gearbox2")
public class GearboxCount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gearbox_id")
    private Long gearboxId;

    @Column(name = "gear_count", nullable = false)
    private int gearCount;

    // Getters and setters
    public Long getGearboxId() {
        return gearboxId;
    }

    public void setGearboxId(Long gearboxId) {
        this.gearboxId = gearboxId;
    }

    public int getGearCount() {
        return gearCount;
    }

    public void setGearCount(int gearCount) {
        this.gearCount = gearCount;
    }
}