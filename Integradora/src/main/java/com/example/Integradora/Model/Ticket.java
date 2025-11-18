package com.example.Integradora.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table (name = "Tickets")
public class Ticket {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String plate;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private Double total;

    public Ticket() {}

    public Ticket(String plate, LocalDateTime entryTime, LocalDateTime exitTime, Double total) {
        this.plate = plate;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.total = total;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(LocalDateTime entryTime) {
        this.entryTime = entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
}
