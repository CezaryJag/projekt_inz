package com.example.demo.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "vehicles")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vehicle_id")
    private Long vehicleId;

    @ManyToOne
    @JoinColumn(name = "model_id", nullable = false)
    private CarModel carModel;

    @Column(name = "production_year", nullable = false)
    private int productionYear;

    @Column(name = "registration_number", nullable = false)
    private String registrationNumber;

    @ManyToOne
    @JoinColumn(name = "gearbox_type_id", nullable = false)
    private GearboxType1 gearboxType;

    @ManyToOne
    @JoinColumn(name = "gear_count_id", nullable = false)
    private GearboxCount gearCount;

    @OneToMany(mappedBy = "vehicle")
    private List<Maintenance1> maintenances;

    // Getters and setters
    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public CarModel getCarModel() {
        return carModel;
    }

    public void setCarModel(CarModel carModel) {
        this.carModel = carModel;
    }

    public int getProductionYear() {
        return productionYear;
    }

    public void setProductionYear(int productionYear) {
        this.productionYear = productionYear;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public GearboxType1 getGearboxType() {
        return gearboxType;
    }

    public void setGearboxType(GearboxType1 gearboxType) {
        this.gearboxType = gearboxType;
    }

    public GearboxCount getGearCount() {
        return gearCount;
    }

    public void setGearCount(GearboxCount gearCount) {
        this.gearCount = gearCount;
    }

    public List<Maintenance1> getMaintenances() {
        return maintenances;
    }

    public void setMaintenances(List<Maintenance1> maintenances) {
        this.maintenances = maintenances;
    }
}