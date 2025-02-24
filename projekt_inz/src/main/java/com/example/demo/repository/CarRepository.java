package com.example.demo.repository;

import com.example.demo.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CarRepository extends JpaRepository<Car, Long> {

    @Query("SELECT c FROM Car c WHERE "
            + "(:yearFrom IS NULL OR c.productionYear >= :yearFrom) AND "
            + "(:yearTo IS NULL OR c.productionYear <= :yearTo) AND "
            + "(:mileageFrom IS NULL OR c.milage >= :mileageFrom) AND "
            + "(:mileageTo IS NULL OR c.milage <= :mileageTo) AND "
            + "(:color IS NULL OR :color = '' OR c.color.colorName LIKE %:color%) AND "
            + "(:status IS NULL OR :status = '' OR c.status = :status) AND "
            + "(:gearboxType IS NULL OR :gearboxType = '' OR c.gearType = :gearboxType) AND "
            + "(:gearboxCount IS NULL OR c.gearCount = :gearboxCount) AND "
            + "(:carModel IS NULL OR :carModel = '' OR c.carModel.modelName LIKE %:carModel%) AND "
            + "(:fuelType IS NULL OR :fuelType = '' OR c.fuelType LIKE %:fuelType%) AND "
            + "(:bodyType IS NULL OR :bodyType = '' OR c.bodyType LIKE %:bodyType%) AND "
            + "(:seatCount IS NULL OR c.seatCount = :seatCount)")
    List<Car> findByFilters(
            @Param("yearFrom") Integer yearFrom,
            @Param("yearTo") Integer yearTo,
            @Param("mileageFrom") Integer mileageFrom,
            @Param("mileageTo") Integer mileageTo,
            @Param("color") String color,
            @Param("status") String status,
            @Param("gearboxType") String gearboxType,
            @Param("gearboxCount") Integer gearboxCount,
            @Param("carModel") String carModel,
            @Param("fuelType") String fuelType,
            @Param("bodyType") String bodyType,
            @Param("seatCount") Integer seatCount);
}
