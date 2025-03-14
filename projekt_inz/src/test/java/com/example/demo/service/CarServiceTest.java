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
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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
    public void testGetAllCars() {
        List<Car> cars = List.of(new Car(), new Car());
        when(carRepository.findAll()).thenReturn(cars);

        List<Car> result = carService.getAllCars();

        assertEquals(2, result.size());
        verify(carRepository, times(1)).findAll();
    }



    @Test
    public void testSaveCar_NewModelColorMaintenance() throws ParseException {
        Car car = new Car();
        car.setRegistrationNumber("XYZ789");
        car.setProductionYear(2022);

        CarModel carModel = new CarModel();
        carModel.setModelName("Model X");
        car.setCarModel(carModel);

        Color color = new Color();
        color.setColorName("Blue");
        car.setColor(color);

        Maintenance maintenance = new Maintenance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date maintenanceDate = dateFormat.parse("2023-06-15");
        maintenance.setMaintenanceDate(maintenanceDate);
        maintenance.setCost(200);
        car.setMaintenance(maintenance);

        when(carModelRepository.findByModelName("Model X")).thenReturn(null);
        when(carModelRepository.save(any(CarModel.class))).thenReturn(carModel);
        when(colorRepository.findByColorName("Blue")).thenReturn(null);
        when(colorRepository.save(any(Color.class))).thenReturn(color);
        when(maintenanceRepository.findByMaintenanceDateAndCost(maintenanceDate, 200)).thenReturn(null);
        when(maintenanceRepository.save(any(Maintenance.class))).thenReturn(maintenance);
        when(carRepository.save(any(Car.class))).thenReturn(car);

        Car savedCar = carService.saveCar(car);

        assertNotNull(savedCar);
        assertEquals("XYZ789", savedCar.getRegistrationNumber());
        assertEquals(2022, savedCar.getProductionYear());
        assertEquals("Model X", savedCar.getCarModel().getModelName());
        assertEquals("Blue", savedCar.getColor().getColorName());
        assertEquals(maintenanceDate, savedCar.getMaintenance().getMaintenanceDate());
        assertEquals(200, savedCar.getMaintenance().getCost());

        verify(carModelRepository, times(1)).findByModelName("Model X");
        verify(carModelRepository, times(1)).save(carModel);
        verify(colorRepository, times(1)).findByColorName("Blue");
        verify(colorRepository, times(1)).save(color);
        verify(maintenanceRepository, times(1)).findByMaintenanceDateAndCost(maintenanceDate, 200);
        verify(maintenanceRepository, times(1)).save(maintenance);
        verify(carRepository, times(1)).save(car);
    }

    @Test
    public void testGetCarById_NotFound() {
        when(carRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> carService.getCarById(1L));
        verify(carRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetCarById() {
        Car car = new Car();
        when(carRepository.findById(anyLong())).thenReturn(Optional.of(car));

        Car result = carService.getCarById(1L);

        assertNotNull(result);
        verify(carRepository, times(1)).findById(1L);
    }

    @Test
    public void testUpdateCar() throws ParseException {
        Car car = new Car();
        car.setModelId(1);

        Color color = new Color();
        color.setColorName("Red");
        car.setColor(color);

        Maintenance maintenance = new Maintenance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date maintenanceDate = dateFormat.parse("2023-01-01");
        maintenance.setMaintenanceDate(maintenanceDate);
        maintenance.setCost(100);
        car.setMaintenance(maintenance);

        CarModel carModel = new CarModel();
        when(carModelRepository.findById(anyInt())).thenReturn(Optional.of(carModel));
        when(colorRepository.findByColorName(anyString())).thenReturn(color);
        when(maintenanceRepository.findByMaintenanceDateAndCost(any(Date.class), anyInt())).thenReturn(maintenance);
        when(carRepository.save(any(Car.class))).thenReturn(car);

        Car result = carService.updateCar(car);

        assertNotNull(result);
        verify(carModelRepository, times(1)).findById(1);
        verify(colorRepository, times(1)).findByColorName("Red");
        verify(maintenanceRepository, times(1)).findByMaintenanceDateAndCost(maintenanceDate, 100);
        verify(carRepository, times(1)).save(car);
    }
    @Test
    public void testUpdateCar_NotFoundModel() {
        Car car = new Car();
        car.setModelId(99);

        when(carModelRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> carService.updateCar(car));
        verify(carModelRepository, times(1)).findById(99);
    }

    @Test
    public void testDeleteCar() {
        doNothing().when(carRepository).deleteById(anyLong());

        carService.deleteCar(1L);

        verify(carRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testParseInteger() {
        assertEquals(123, carService.parseInteger("123"));
        assertNull(carService.parseInteger(null));
        assertNull(carService.parseInteger(""));
        assertNull(carService.parseInteger("xyz"));
    }

    @Test
    public void testSaveCar_WithExistingModelColorMaintenance() {
        Car car = new Car();
        car.setRegistrationNumber("XYZ789");
        car.setProductionYear(2022);

        CarModel carModel = new CarModel();
        carModel.setModelName("Model X");
        car.setCarModel(carModel);

        Color color = new Color();
        color.setColorName("Blue");
        car.setColor(color);

        Maintenance maintenance = new Maintenance();
        maintenance.setMaintenanceDate(new Date());
        maintenance.setCost(200);
        car.setMaintenance(maintenance);

        when(carModelRepository.findByModelName("Model X")).thenReturn(carModel);
        when(colorRepository.findByColorName("Blue")).thenReturn(color);
        when(maintenanceRepository.findByMaintenanceDateAndCost(any(Date.class), anyInt())).thenReturn(maintenance);
        when(carRepository.save(any(Car.class))).thenReturn(car);

        Car savedCar = carService.saveCar(car);

        assertNotNull(savedCar);
        assertEquals("XYZ789", savedCar.getRegistrationNumber());
        assertEquals(2022, savedCar.getProductionYear());
        assertEquals("Model X", savedCar.getCarModel().getModelName());
        assertEquals("Blue", savedCar.getColor().getColorName());
        assertEquals(200, savedCar.getMaintenance().getCost());

        verify(carModelRepository, times(1)).findByModelName("Model X");
        verify(carModelRepository, never()).save(any(CarModel.class));
        verify(colorRepository, times(1)).findByColorName("Blue");
        verify(colorRepository, never()).save(any(Color.class));
        verify(maintenanceRepository, times(1)).findByMaintenanceDateAndCost(any(Date.class), anyInt());
        verify(maintenanceRepository, never()).save(any(Maintenance.class));
        verify(carRepository, times(1)).save(car);
    }

    @Test
    public void testSaveCar_WithNewModelColorMaintenance() {
        Car car = new Car();
        car.setRegistrationNumber("XYZ123");
        car.setProductionYear(2023);

        CarModel carModel = new CarModel();
        carModel.setModelName("Model Z");
        car.setCarModel(carModel);

        Color color = new Color();
        color.setColorName("Green");
        car.setColor(color);

        Maintenance maintenance = new Maintenance();
        maintenance.setMaintenanceDate(new Date());
        maintenance.setCost(300);
        car.setMaintenance(maintenance);

        when(carModelRepository.findByModelName("Model Z")).thenReturn(null);
        when(carModelRepository.save(any(CarModel.class))).thenReturn(carModel);
        when(colorRepository.findByColorName("Green")).thenReturn(null);
        when(colorRepository.save(any(Color.class))).thenReturn(color);
        when(maintenanceRepository.findByMaintenanceDateAndCost(any(Date.class), anyInt())).thenReturn(null);
        when(maintenanceRepository.save(any(Maintenance.class))).thenReturn(maintenance);
        when(carRepository.save(any(Car.class))).thenReturn(car);

        Car savedCar = carService.saveCar(car);

        assertNotNull(savedCar);
        assertEquals("XYZ123", savedCar.getRegistrationNumber());
        assertEquals(2023, savedCar.getProductionYear());
        assertEquals("Model Z", savedCar.getCarModel().getModelName());
        assertEquals("Green", savedCar.getColor().getColorName());
        assertEquals(300, savedCar.getMaintenance().getCost());

        verify(carModelRepository, times(1)).findByModelName("Model Z");
        verify(carModelRepository, times(1)).save(carModel);
        verify(colorRepository, times(1)).findByColorName("Green");
        verify(colorRepository, times(1)).save(color);
        verify(maintenanceRepository, times(1)).findByMaintenanceDateAndCost(any(Date.class), anyInt());
        verify(maintenanceRepository, times(1)).save(maintenance);
        verify(carRepository, times(1)).save(car);
    }

    @Test
    public void testUpdateCar_SuccessfulUpdate() throws ParseException {
        Car existingCar = new Car();
        existingCar.setVehicleId(1L);
        existingCar.setRegistrationNumber("OLD123");
        existingCar.setProductionYear(2018);

        Car updatedCar = new Car();
        updatedCar.setVehicleId(1L);
        updatedCar.setRegistrationNumber("NEW123");
        updatedCar.setProductionYear(2022);
        updatedCar.setModelId(2);

        CarModel carModel = new CarModel();
        carModel.setModelId(2);
        carModel.setModelName("Updated Model");

        Color color = new Color();
        color.setColorName("Black");

        Maintenance maintenance = new Maintenance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date maintenanceDate = dateFormat.parse("2023-01-01");
        maintenance.setMaintenanceDate(maintenanceDate);
        maintenance.setCost(400);

        updatedCar.setCarModel(carModel);
        updatedCar.setColor(color);
        updatedCar.setMaintenance(maintenance);

        when(carRepository.findById(1L)).thenReturn(Optional.of(existingCar));
        when(carModelRepository.findById(2)).thenReturn(Optional.of(carModel));
        when(colorRepository.findByColorName("Black")).thenReturn(color);
        when(maintenanceRepository.findByMaintenanceDateAndCost(maintenanceDate, 400)).thenReturn(maintenance);
        when(carRepository.save(any(Car.class))).thenReturn(updatedCar);

        Car result = carService.updateCar(updatedCar);

        assertNotNull(result);
        assertEquals("NEW123", result.getRegistrationNumber());
        assertEquals(2022, result.getProductionYear());
        assertEquals("Updated Model", result.getCarModel().getModelName());
        assertEquals("Black", result.getColor().getColorName());
        assertEquals(400, result.getMaintenance().getCost());

        verify(carModelRepository, times(1)).findById(2);
        verify(colorRepository, times(1)).findByColorName("Black");
        verify(maintenanceRepository, times(1)).findByMaintenanceDateAndCost(maintenanceDate, 400);
        verify(carRepository, times(1)).save(updatedCar);
    }

    @Test
    public void testUpdateCar_MissingModel_ThrowsException() {
        Car updatedCar = new Car();
        updatedCar.setModelId(10); // Non-existing model

        when(carModelRepository.findById(10)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> carService.updateCar(updatedCar));
        verify(carModelRepository, times(1)).findById(10);
        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    public void testSaveCar_NoMaintenance() {
        Car car = new Car();
        car.setRegistrationNumber("XYZ789");
        car.setProductionYear(2022);

        CarModel carModel = new CarModel();
        carModel.setModelName("Model X");
        car.setCarModel(carModel);

        Color color = new Color();
        color.setColorName("Blue");
        car.setColor(color);

        when(carModelRepository.findByModelName("Model X")).thenReturn(null);
        when(carModelRepository.save(any(CarModel.class))).thenReturn(carModel);
        when(colorRepository.findByColorName("Blue")).thenReturn(null);
        when(colorRepository.save(any(Color.class))).thenReturn(color);
        when(carRepository.save(any(Car.class))).thenReturn(car);

        Car savedCar = carService.saveCar(car);

        assertNotNull(savedCar);
        assertEquals("XYZ789", savedCar.getRegistrationNumber());
        assertEquals(2022, savedCar.getProductionYear());
        assertEquals("Model X", savedCar.getCarModel().getModelName());
        assertEquals("Blue", savedCar.getColor().getColorName());

        verify(carModelRepository, times(1)).findByModelName("Model X");
        verify(carModelRepository, times(1)).save(carModel);
        verify(colorRepository, times(1)).findByColorName("Blue");
        verify(colorRepository, times(1)).save(color);
        verify(carRepository, times(1)).save(car);
    }


    @Test
    public void testUpdateCar_ColorIsNull() throws ParseException {
        Car car = new Car();
        car.setModelId(1);
        car.setColor(null);

        CarModel carModel = new CarModel();
        when(carModelRepository.findById(anyInt())).thenReturn(Optional.of(carModel));
        when(carRepository.save(any(Car.class))).thenReturn(car);

        Car result = carService.updateCar(car);

        assertNotNull(result);
        verify(carModelRepository, times(1)).findById(1);
        verify(carRepository, times(1)).save(car);
    }

    @Test
    public void testUpdateCar_NewColor() throws ParseException {
        Car car = new Car();
        car.setModelId(1);

        Color color = new Color();
        color.setColorName("Red");
        car.setColor(color);

        CarModel carModel = new CarModel();
        when(carModelRepository.findById(anyInt())).thenReturn(Optional.of(carModel));
        when(colorRepository.findByColorName("Red")).thenReturn(null);
        when(colorRepository.save(any(Color.class))).thenReturn(color);
        when(carRepository.save(any(Car.class))).thenReturn(car);

        Car result = carService.updateCar(car);

        assertNotNull(result);
        verify(carModelRepository, times(1)).findById(1);
        verify(colorRepository, times(1)).findByColorName("Red");
        verify(colorRepository, times(1)).save(color);
        verify(carRepository, times(1)).save(car);
    }

    @Test
    public void testUpdateCar_ExistingColor() throws ParseException {
        Car car = new Car();
        car.setModelId(1);

        Color color = new Color();
        color.setColorName("Red");
        car.setColor(color);

        CarModel carModel = new CarModel();
        when(carModelRepository.findById(anyInt())).thenReturn(Optional.of(carModel));
        when(colorRepository.findByColorName("Red")).thenReturn(color);
        when(carRepository.save(any(Car.class))).thenReturn(car);

        Car result = carService.updateCar(car);

        assertNotNull(result);
        verify(carModelRepository, times(1)).findById(1);
        verify(colorRepository, times(1)).findByColorName("Red");
        verify(colorRepository, never()).save(any(Color.class));
        verify(carRepository, times(1)).save(car);
    }

    @Test
    public void testUpdateCar_NoMaintenance() {
        Car car = new Car();
        car.setModelId(1);

        Color color = new Color();
        color.setColorName("Red");
        car.setColor(color);
        car.setMaintenance(null);

        CarModel carModel = new CarModel();
        when(carModelRepository.findById(anyInt())).thenReturn(Optional.of(carModel));
        when(colorRepository.findByColorName("Red")).thenReturn(color);
        when(carRepository.save(any(Car.class))).thenReturn(car);

        Car result = carService.updateCar(car);

        assertNotNull(result);
        verify(carModelRepository, times(1)).findById(1);
        verify(colorRepository, times(1)).findByColorName("Red");
        verify(carRepository, times(1)).save(car);
    }

    @Test
    public void testUpdateCar_NewMaintenance() throws ParseException {
        Car car = new Car();
        car.setModelId(1);

        Color color = new Color();
        color.setColorName("Red");
        car.setColor(color);

        Maintenance maintenance = new Maintenance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date maintenanceDate = dateFormat.parse("2023-01-01");
        maintenance.setMaintenanceDate(maintenanceDate);
        maintenance.setCost(100);
        car.setMaintenance(maintenance);

        CarModel carModel = new CarModel();
        when(carModelRepository.findById(anyInt())).thenReturn(Optional.of(carModel));
        when(colorRepository.findByColorName(anyString())).thenReturn(color);
        when(maintenanceRepository.findByMaintenanceDateAndCost(any(Date.class), anyInt())).thenReturn(null);
        when(maintenanceRepository.save(any(Maintenance.class))).thenReturn(maintenance);
        when(carRepository.save(any(Car.class))).thenReturn(car);

        Car result = carService.updateCar(car);

        assertNotNull(result);
        verify(carModelRepository, times(1)).findById(1);
        verify(colorRepository, times(1)).findByColorName("Red");
        verify(maintenanceRepository, times(1)).findByMaintenanceDateAndCost(maintenanceDate, 100);
        verify(maintenanceRepository, times(1)).save(maintenance);
        verify(carRepository, times(1)).save(car);
    }

    @Test
    public void testGetCarsByFilter_WithNullValues() {
        List<Car> cars = List.of(new Car(), new Car());
        when(carRepository.findByFilters(anyInt(), anyInt(), anyInt(), anyInt(),
                isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull()))
                .thenReturn(cars);

        List<Car> result = carService.getCarsByFilter(null, null, null, null, null, null, null, null, null, null, null, null);

        assertEquals(2, result.size());
        verify(carRepository, times(1)).findByFilters(
                eq(0), eq(10000), eq(0), eq(1000000),
                isNull(), isNull(), isNull(),
                isNull(), isNull(), isNull(), isNull(), isNull()
        );
    }

    @Test
    public void testGetCarsByFilter_WithEmptyValues() {
        List<Car> cars = List.of(new Car(), new Car());
        when(carRepository.findByFilters(anyInt(), anyInt(), anyInt(), anyInt(),
                isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull()))
                .thenReturn(cars);

        List<Car> result = carService.getCarsByFilter("", "", "", "", "", "", "", "", "", "", "", "");

        assertEquals(2, result.size());
        verify(carRepository, times(1)).findByFilters(
                eq(0), eq(10000), eq(0), eq(1000000),
                isNull(), isNull(), isNull(),
                isNull(), isNull(), isNull(), isNull(), isNull()
        );
    }

    @Test
    public void testGetCarsByFilter_WithValidValues() {
        List<Car> cars = List.of(new Car(), new Car(), new Car());
        when(carRepository.findByFilters(eq(2015), eq(2022), eq(50000), eq(200000),
                eq("Red"), eq("Available"), eq("Automatic"),
                eq(6), eq("Toyota"), eq("Diesel"), eq("SUV"), eq(5)))
                .thenReturn(cars);

        List<Car> result = carService.getCarsByFilter("2015", "2022", "50000", "200000", "Red", "Available", "Automatic", "6", "Toyota", "Diesel", "SUV", "5");

        assertEquals(3, result.size());
        verify(carRepository, times(1)).findByFilters(
                eq(2015), eq(2022), eq(50000), eq(200000),
                eq("Red"), eq("Available"), eq("Automatic"),
                eq(6), eq("Toyota"), eq("Diesel"), eq("SUV"), eq(5)
        );
    }

    @Test
    public void testGetCarsByFilter_WithInvalidNumbers() {
        List<Car> cars = List.of();
        when(carRepository.findByFilters(any(), any(), any(), any(),
                any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(cars);

        List<Car> result = carService.getCarsByFilter("abcd", "-1", "xyz", "", "Blue", null, "Manual", "4", "Ford", "Petrol", "Sedan", "three");

        assertEquals(0, result.size());
        verify(carRepository, times(1)).findByFilters(
                isNull(),  // "abcd" -> null
                eq(-1),  // "-1" -> null (zmiana w teście)
                isNull(),  // "xyz" -> null
                eq(1000000), // "" -> domyślna wartość
                eq("Blue"),
                isNull(),   // null pozostaje null
                eq("Manual"),
                eq(4),
                eq("Ford"),
                eq("Petrol"),
                eq("Sedan"),
                isNull()    // "three" -> null
        );
    }

    @Test
    public void testGetCarsByFilter_WithNullAndEmptySeatCount() {
        List<Car> cars = List.of(new Car());
        when(carRepository.findByFilters(anyInt(), anyInt(), anyInt(), anyInt(),
                any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(cars);

        List<Car> result1 = carService.getCarsByFilter("2010", "2020", "40000", "150000", "Black", "Used", "Manual", "5", "Honda", "Electric", "Hatchback", null);
        List<Car> result2 = carService.getCarsByFilter("2010", "2020", "40000", "150000", "Black", "Used", "Manual", "5", "Honda", "Electric", "Hatchback", "");

        assertEquals(1, result1.size());
        assertEquals(1, result2.size());

        verify(carRepository, times(2)).findByFilters(
                eq(2010), eq(2020), eq(40000), eq(150000),
                eq("Black"), eq("Used"), eq("Manual"),
                eq(5), eq("Honda"), eq("Electric"), eq("Hatchback"), isNull()
        );
    }

}
