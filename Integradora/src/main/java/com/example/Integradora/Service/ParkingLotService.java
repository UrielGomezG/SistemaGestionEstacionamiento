package com.example.Integradora.Service;

import com.example.Integradora.Model.Car;
import com.example.Integradora.Model.Ticket;
import com.example.Integradora.Structures.ParkingLotList;
import com.example.Integradora.Structures.QueueWaiting;
import com.example.Integradora.Structures.StackHistory;
import com.example.Integradora.Structures.BinaryTree;
import jakarta.annotation.PostConstruct;
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
    private final BinaryTree<Ticket> ticketTree;

    private static final int PARKING_CAPACITY = 10;
    private static final double HOURLY_RATE = 25.0;

    public ParkingLotService() {
        this.parkingLot = new ParkingLotList<>(PARKING_CAPACITY);
        this.waitingQueue = new QueueWaiting<>();
        this.exitHistory = new StackHistory<>();
        this.ticketTree = new BinaryTree<>();
    }

    @PostConstruct
    public void initializeParkingState() {
        List<Car> carsInParking = carService.findCarsInParking();
        if (carsInParking == null || carsInParking.isEmpty()) {
            return;
        }

        for (Car car : carsInParking) {
            if (car == null) {
                continue;
            }
            if (parkingLot.add(car) == null) {
                waitingQueue.enqueue(car);
            }
        }
    }

    public Car registerEntry(Car car) {
        // Validar que el auto no esté ya en el estacionamiento
        if (parkingLot.getData(car) != null) {
            throw new IllegalStateException("El auto con placa " + car.getPlate() + " ya está en el estacionamiento");
        }

        Car existingCar = null;
        if (carService.existsByPlate(car.getPlate())) {
            existingCar = carService.findByPlate(car.getPlate()).orElse(null);
            if (existingCar != null && existingCar.getInParking() != null && existingCar.getInParking()) {
                throw new IllegalStateException(
                        "El auto con placa " + car.getPlate() + " ya está en el estacionamiento");
            }
        }

        if (existingCar != null) {
            car = existingCar;
        }

        car.setEntryTime(LocalDateTime.now());
        car.setExitTime(null);
        car.setInParking(true);

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
        carToExit.setInParking(false);

        Car removedCar = parkingLot.remove(carToExit);

        if (removedCar != null) {
            carToExit = carService.update(carToExit);
            createTicketForExit(carToExit);
            exitHistory.push(carToExit);

            if (!waitingQueue.isEmpty()) {
                Car nextCar = waitingQueue.dequeue();
                if (nextCar != null) {
                    nextCar.setEntryTime(LocalDateTime.now());
                    nextCar.setInParking(true);
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

    public List<Car> getSortedCarsInParking() {
        parkingLot.sort();
        return getAllCarsInParking();
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
                parkingLot.isFull());
    }

    public static class ParkingStats {
        private int occupiedSpots;
        private int totalSpots;
        private int waitingCars;
        private int exitedCars;
        private boolean isEmpty;
        private boolean isFull;

        public ParkingStats(int occupiedSpots, int totalSpots, int waitingCars, int exitedCars, boolean isEmpty,
                boolean isFull) {
            this.occupiedSpots = occupiedSpots;
            this.totalSpots = totalSpots;
            this.waitingCars = waitingCars;
            this.exitedCars = exitedCars;
            this.isEmpty = isEmpty;
            this.isFull = isFull;
        }

        public int getOccupiedSpots() {
            return occupiedSpots;
        }

        public int getTotalSpots() {
            return totalSpots;
        }

        public int getAvailableSpots() {
            return totalSpots - occupiedSpots;
        }

        public int getWaitingCars() {
            return waitingCars;
        }

        public int getExitedCars() {
            return exitedCars;
        }

        public boolean isEmpty() {
            return isEmpty;
        }

        public boolean isFull() {
            return isFull;
        }
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

    private void createTicketForExit(Car car) {
        if (car.getEntryTime() == null || car.getExitTime() == null) {
            return;
        }

        Double total = calculateParkingFee(car.getEntryTime(), car.getExitTime());

        Ticket ticket = new Ticket();
        ticket.setPlate(car.getPlate());
        ticket.setEntryTime(car.getEntryTime());
        ticket.setExitTime(car.getExitTime());
        ticket.setTotal(total);

        ticketService.save(ticket);
        ticketTree.insert(ticket);
    }

    private Double calculateParkingFee(LocalDateTime entryTime, LocalDateTime exitTime) {
        long minutes = Math.max(1, java.time.Duration.between(entryTime, exitTime).toMinutes());
        double hoursRoundedUp = Math.ceil(minutes / 60.0);
        return hoursRoundedUp * HOURLY_RATE;
    }

    public List<Ticket> getSortedTickets() {
        return ticketTree.getInOrder();
    }
}
