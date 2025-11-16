package com.example.Integradora.Repositories;

import com.example.Integradora.Model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket,Long> {}
