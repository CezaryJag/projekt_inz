package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "group_vehicles")
public class GroupVehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private CarGroup carGroup;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Car car;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CarGroup getCarGroup() {
        return carGroup;
    }

    public void setCarGroup(CarGroup carGroup) {
        this.carGroup = carGroup;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }
}