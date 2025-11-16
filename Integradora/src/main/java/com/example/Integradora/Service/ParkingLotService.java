package com.example.Integradora.Service;

import com.example.Integradora.Model.Car;
import com.example.Integradora.Structures.ParkingLotList;
import com.example.Integradora.Structures.QueueWaiting;
import com.example.Integradora.Structures.StackHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ParkingLotService {
    
    @Autowired
    private CarService carService;
    
    @Autowired
    private TicketService ticketService;
    
    private final ParkingLotList<Car> parkingLot;
    private final QueueWaiting<Car> waitingQueue;
    private final StackHistory<Car> exitHistory;
    
    private static final int PARKING_CAPACITY = 10;
    
    public ParkingLotService() {
        this.parkingLot = new ParkingLotList<>(PARKING_CAPACITY);
        this.waitingQueue = new QueueWaiting<>();
        this.exitHistory = new StackHistory<>();
    }

    public Car registerEntry(Car car) {
        if (parkingLot.getData(car) != null) {
            throw new IllegalStateException("El auto con placa " + car.getPlate() + " ya está en el estacionamiento");
        }
        
        if (carService.existsByPlate(car.getPlate())) {
            Car existingCar = carService.findByPlate(car.getPlate()).orElse(null);
            if (existingCar != null && existingCar.getExitTime() == null) {
                throw new IllegalStateException("El auto con placa " + car.getPlate() + " ya está registrado y no ha salido");
            }
        }
        
        car.setEntryTime(LocalDateTime.now());
        car.setExitTime(null);
        
        car = carService.save(car);
        
        Car addedCar = parkingLot.add(car);
        
        if (addedCar != null) {
            return car;
        } else {
            waitingQueue.enqueue(car);
            return null;
        }
    }

    public Car registerExit(String plate) {
        Car carToExit = findCarInParking(plate);
        
        if (carToExit == null) {
            throw new IllegalStateException("El auto con placa " + plate + " no está en el estacionamiento");
        }
        
        carToExit.setExitTime(LocalDateTime.now());
        
        Car removedCar = parkingLot.remove(carToExit);
        
        if (removedCar != null) {
            carToExit = carService.update(carToExit);
            
            exitHistory.push(carToExit);
            
            if (!waitingQueue.isEmpty()) {
                Car nextCar = waitingQueue.dequeue();
                if (nextCar != null) {
                    nextCar.setEntryTime(LocalDateTime.now());
                    nextCar = carService.update(nextCar);
                    parkingLot.add(nextCar);
                }
            }
            
            return carToExit;
        }
        
        throw new IllegalStateException("No se pudo remover el auto del estacionamiento");
    }

    public Car findCarInParking(String plate) {
        Car searchCar = new Car();
        searchCar.setPlate(plate);
        
        return parkingLot.getData(searchCar);
    }

    public List<Car> getAllCarsInParking() {
        List<Car> cars = new ArrayList<>();
        com.example.Integradora.Model.Node<Car> current = parkingLot.getHead();
        
        while (current != null) {
            cars.add(current.getData());
            current = current.getNext();
        }
        
        return cars;
    }

    public List<Car> getWaitingCars() {
        List<Car> cars = new ArrayList<>();
        com.example.Integradora.Model.Node<Car> current = waitingQueue.getHead();
        
        while (current != null) {
            cars.add(current.getData());
            current = current.getNext();
        }
        
        return cars;
    }

    public List<Car> getExitHistoryList() {
        return exitHistory.showAll();
    }

    public ParkingStats getStats() {
        return new ParkingStats(
            parkingLot.getSize(),
            parkingLot.getSizeLimit(),
            waitingQueue.getSize(),
            exitHistory.size(),
            parkingLot.isEmpty(),
            parkingLot.isFull()
        );
    }

    public static class ParkingStats {
        private int occupiedSpots;
        private int totalSpots;
        private int waitingCars;
        private int exitedCars;
        private boolean isEmpty;
        private boolean isFull;
        
        public ParkingStats(int occupiedSpots, int totalSpots, int waitingCars, int exitedCars, boolean isEmpty, boolean isFull) {
            this.occupiedSpots = occupiedSpots;
            this.totalSpots = totalSpots;
            this.waitingCars = waitingCars;
            this.exitedCars = exitedCars;
            this.isEmpty = isEmpty;
            this.isFull = isFull;
        }
        
        public int getOccupiedSpots() { return occupiedSpots; }
        public int getTotalSpots() { return totalSpots; }
        public int getAvailableSpots() { return totalSpots - occupiedSpots; }
        public int getWaitingCars() { return waitingCars; }
        public int getExitedCars() { return exitedCars; }
        public boolean isEmpty() { return isEmpty; }
        public boolean isFull() { return isFull; }
    }
    
    public ParkingLotList<Car> getParkingLot() {
        return parkingLot;
    }
    
    public QueueWaiting<Car> getWaitingQueue() {
        return waitingQueue;
    }
    
    public StackHistory<Car> getExitHistory() {
        return exitHistory;
    }
}
