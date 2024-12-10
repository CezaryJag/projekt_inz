package com.example.demo.entity;

import jakarta.persistence.*;
import com.example.demo.entity.Car;
@Entity
@Table(name = "maintenance1")
public class Maintenance1 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "maintenance_id")
    private Long maintenanceId;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Car vehicle;

    @Column(name = "maintenance_date", nullable = false)
    private String maintenanceDate;

    @Column(name = "details", nullable = false)
    private String maintenanceDetails;

    // Getters and setters
    public Long getMaintenanceId() {
        return maintenanceId;
    }

    public void setMaintenanceId(Long maintenanceId) {
        this.maintenanceId = maintenanceId;
    }

    public Car getVehicle() {
        return vehicle;
    }

    public void setVehicle(Car vehicle) {
        this.vehicle = vehicle;
    }

    public String getMaintenanceDate() {
        return maintenanceDate;
    }

    public void setMaintenanceDate(String maintenanceDate) {
        this.maintenanceDate = maintenanceDate;
    }

    public String getMaintenanceDetails() {
        return maintenanceDetails;
    }

    public void setMaintenanceDetails(String maintenanceDetails) {
        this.maintenanceDetails = maintenanceDetails;
    }
}