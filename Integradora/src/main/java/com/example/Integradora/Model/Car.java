package com.example.Integradora.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cars")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String plate;
    private String make;
    private String model;
    private String color;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    @Column(nullable = false)
    private Boolean inParking = false;

    public Car() {
        this.inParking = false;
    }

    public Car(String plate, String make, String model, String color, LocalDateTime entryTime) {
        this.plate = plate;
        this.make = make;
        this.model = model;
        this.color = color;
        this.entryTime = entryTime;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getPlate() { return plate; }

    public void setPlate(String plate) { this.plate = plate; }

    public String getMake() { return make; }

    public void setMake(String make) { this.make = make; }

    public String getModel() { return model; }

    public void setModel(String model) { this.model = model; }

    public String getColor() { return color; }

    public void setColor(String color) { this.color = color; }

    public LocalDateTime getEntryTime() { return entryTime; }

    public void setEntryTime(LocalDateTime entryTime) { this.entryTime = entryTime; }

    public LocalDateTime getExitTime() { return exitTime; }

    public void setExitTime(LocalDateTime exitTime) { this.exitTime = exitTime; }

    public Boolean getInParking() { return inParking; }

    public void setInParking(Boolean inParking) { this.inParking = inParking; }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Car car = (Car) obj;
        return plate != null && plate.equals(car.plate);
    }
    
    @Override
    public int hashCode() {
        return plate != null ? plate.hashCode() : 0;
    }
}
