package com.example.demo.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.entity.Car;
import com.example.demo.entity.CarModel;
import com.example.demo.entity.Color;
import com.example.demo.entity.Maintenance;
import com.example.demo.repository.CarModelRepository;
import com.example.demo.repository.CarRepository;
import com.example.demo.repository.ColorRepository;
import com.example.demo.repository.MaintenanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CarServiceTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private CarModelRepository carModelRepository;

    @Mock
    private ColorRepository colorRepository;

    @Mock
    private MaintenanceRepository maintenanceRepository;

    @InjectMocks
    private CarService carService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveCar() throws ParseException {
        // Arrange
        Car car = new Car();
        car.setRegistrationNumber("ABC123");
        car.setProductionYear(2020);

        CarModel carModel = new CarModel();
        carModel.setModelName("Model S");
        car.setCarModel(carModel);

        Color color = new Color();
        color.setColorName("Red");
        car.setColor(color);

        Maintenance maintenance = new Maintenance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date maintenanceDate = dateFormat.parse("2023-01-01");
        maintenance.setMaintenanceDate(maintenanceDate);
        maintenance.setCost(100);
        car.setMaintenance(maintenance);

        when(carModelRepository.findByModelName(anyString())).thenReturn(null);
        when(carModelRepository.save(any(CarModel.class))).thenReturn(carModel);
        when(colorRepository.findByColorName(anyString())).thenReturn(null);
        when(colorRepository.save(any(Color.class))).thenReturn(color);
        when(maintenanceRepository.findByMaintenanceDateAndCost(any(Date.class), anyInt())).thenReturn(null);
        when(maintenanceRepository.save(any(Maintenance.class))).thenReturn(maintenance);
        when(carRepository.save(any(Car.class))).thenReturn(car);

        // Act
        Car savedCar = carService.saveCar(car);

        // Assert
        assertNotNull(savedCar);
        assertEquals("ABC123", savedCar.getRegistrationNumber());
        assertEquals(2020, savedCar.getProductionYear());
        assertEquals("Model S", savedCar.getCarModel().getModelName());
        assertEquals("Red", savedCar.getColor().getColorName());
        assertEquals(maintenanceDate, savedCar.getMaintenance().getMaintenanceDate());
        assertEquals(100, savedCar.getMaintenance().getCost());

        verify(carModelRepository, times(1)).findByModelName("Model S");
        verify(carModelRepository, times(1)).save(carModel);
        verify(colorRepository, times(1)).findByColorName("Red");
        verify(colorRepository, times(1)).save(color);
        verify(maintenanceRepository, times(1)).findByMaintenanceDateAndCost(maintenanceDate, 100);
        verify(maintenanceRepository, times(1)).save(maintenance);
        verify(carRepository, times(1)).save(car);
    }

    @Test
    public void testSaveCarWithExistingModelAndColor() throws ParseException {
        // Arrange
        Car car = new Car();
        car.setRegistrationNumber("XYZ789");
        car.setProductionYear(2021);

        CarModel carModel = new CarModel();
        carModel.setModelName("Model X");
        car.setCarModel(carModel);

        Color color = new Color();
        color.setColorName("Blue");
        car.setColor(color);

        Maintenance maintenance = new Maintenance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date maintenanceDate = dateFormat.parse("2023-06-01");
        maintenance.setMaintenanceDate(maintenanceDate);
        maintenance.setCost(200);
        car.setMaintenance(maintenance);

        when(carModelRepository.findByModelName("Model X")).thenReturn(carModel);
        when(colorRepository.findByColorName("Blue")).thenReturn(color);
        when(maintenanceRepository.findByMaintenanceDateAndCost(maintenanceDate, 200)).thenReturn(null);
        when(maintenanceRepository.save(any(Maintenance.class))).thenReturn(maintenance);
        when(carRepository.save(any(Car.class))).thenReturn(car);

        // Act
        Car savedCar = carService.saveCar(car);

        // Assert
        assertNotNull(savedCar);
        assertEquals("XYZ789", savedCar.getRegistrationNumber());
        assertEquals(2021, savedCar.getProductionYear());
        assertEquals("Model X", savedCar.getCarModel().getModelName());
        assertEquals("Blue", savedCar.getColor().getColorName());
        assertEquals(maintenanceDate, savedCar.getMaintenance().getMaintenanceDate());
        assertEquals(200, savedCar.getMaintenance().getCost());

        verify(carModelRepository, times(1)).findByModelName("Model X");
        verify(carModelRepository, times(0)).save(carModel);
        verify(colorRepository, times(1)).findByColorName("Blue");
        verify(colorRepository, times(0)).save(color);
        verify(maintenanceRepository, times(1)).findByMaintenanceDateAndCost(maintenanceDate, 200);
        verify(maintenanceRepository, times(1)).save(maintenance);
        verify(carRepository, times(1)).save(car);
    }

    @Test
    public void testSaveCarWithExistingMaintenance() throws ParseException {
        // Arrange
        Car car = new Car();
        car.setRegistrationNumber("LMN456");
        car.setProductionYear(2019);

        CarModel carModel = new CarModel();
        carModel.setModelName("Model 3");
        car.setCarModel(carModel);

        Color color = new Color();
        color.setColorName("Green");
        car.setColor(color);

        Maintenance maintenance = new Maintenance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date maintenanceDate = dateFormat.parse("2022-12-01");
        maintenance.setMaintenanceDate(maintenanceDate);
        maintenance.setCost(150);
        car.setMaintenance(maintenance);

        when(carModelRepository.findByModelName("Model 3")).thenReturn(carModel);
        when(colorRepository.findByColorName("Green")).thenReturn(color);
        when(maintenanceRepository.findByMaintenanceDateAndCost(maintenanceDate, 150)).thenReturn(maintenance);
        when(carRepository.save(any(Car.class))).thenReturn(car);

        // Act
        Car savedCar = carService.saveCar(car);

        // Assert
        assertNotNull(savedCar);
        assertEquals("LMN456", savedCar.getRegistrationNumber());
        assertEquals(2019, savedCar.getProductionYear());
        assertEquals("Model 3", savedCar.getCarModel().getModelName());
        assertEquals("Green", savedCar.getColor().getColorName());
        assertEquals(maintenanceDate, savedCar.getMaintenance().getMaintenanceDate());
        assertEquals(150, savedCar.getMaintenance().getCost());

        verify(carModelRepository, times(1)).findByModelName("Model 3");
        verify(carModelRepository, times(0)).save(carModel);
        verify(colorRepository, times(1)).findByColorName("Green");
        verify(colorRepository, times(0)).save(color);
        verify(maintenanceRepository, times(1)).findByMaintenanceDateAndCost(maintenanceDate, 150);
        verify(maintenanceRepository, times(0)).save(maintenance);
        verify(carRepository, times(1)).save(car);
    }
}