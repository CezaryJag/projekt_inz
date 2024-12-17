package com.example.demo.repository;

import com.example.demo.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CarRepository extends JpaRepository<Car, Long> {
    @Query("SELECT c FROM Car c WHERE "
            + "(:yearFrom IS NULL OR c.productionYear >= CAST(:yearFrom AS int)) AND "
            + "(:yearTo IS NULL OR c.productionYear <= CAST(:yearTo AS int)) AND "
            + "(:mileageFrom IS NULL OR c.milage >= CAST(:mileageFrom AS int)) AND "
            + "(:mileageTo IS NULL OR c.milage <= CAST(:mileageTo AS int)) AND "
            + "(:color IS NULL OR :color = '' OR c.color LIKE %:color%) AND "
            + "(:status IS NULL OR :status = '' OR c.status LIKE %:status%) AND "
            + "(:gearboxType IS NULL OR :gearboxType = '' OR c.gearType LIKE %:gearboxType%) AND "
            + "(:gearboxCount IS NULL OR :gearboxCount = '' OR c.gearCount = CAST(:gearboxCount AS int)) AND "
            + "(:carModel IS NULL OR :carModel = '' OR c.carModel.modelName LIKE %:carModel%)")
    List<Car> findByFilters(
            @Param("yearFrom") String yearFrom,
            @Param("yearTo") String yearTo,
            @Param("mileageFrom") String mileageFrom,
            @Param("mileageTo") String mileageTo,
            @Param("color") String color,
            @Param("status") String status,
            @Param("gearboxType") String gearboxType,
            @Param("gearboxCount") String gearboxCount,
            @Param("carModel") String carModel);
}