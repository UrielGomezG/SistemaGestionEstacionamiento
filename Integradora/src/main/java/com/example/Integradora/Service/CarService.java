package com.example.Integradora.Service;

import com.example.Integradora.Model.Car;
import com.example.Integradora.Repositories.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CarService {
    @Autowired
    private CarRepository carRepository;

    public Car save(Car car) {
        return carRepository.save(car);
    }

    public Optional<Car> findById(Long id) {
        return carRepository.findById(id);
    }

    public Optional<Car> findByPlate(String plate) {
        return carRepository.findByPlate(plate);
    }

    public List<Car> findAll() {
        return carRepository.findAll();
    }

    public Car update(Car car) {
        return carRepository.save(car);
    }
    

    public void deleteById(Long id) {
        carRepository.deleteById(id);
    }

    public void delete(Car car) {
        carRepository.delete(car);
    }

    public boolean existsById(Long id) {
        return carRepository.existsById(id);
    }

    public boolean existsByPlate(String plate) {
        return carRepository.findByPlate(plate).isPresent();
    }
}
