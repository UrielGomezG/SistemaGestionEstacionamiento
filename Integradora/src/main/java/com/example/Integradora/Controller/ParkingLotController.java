package com.example.Integradora.Controller;

import com.example.Integradora.Model.Car;
import com.example.Integradora.Service.ParkingLotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cars")
@CrossOrigin(origins = "*")
public class ParkingLotController {

    @Autowired
    private ParkingLotService parkingLotService;
    /**
     * Registra la entrada de un auto al estacionamiento
     * POST /cars/entry
     */
    @PostMapping("/entry")
    public ResponseEntity<?> registerEntry(@RequestBody Car car) {
        try {
            Car registeredCar = parkingLotService.registerEntry(car);

            if (registeredCar != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Auto ingresado exitosamente al estacionamiento");
                response.put("car", registeredCar);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Estacionamiento lleno. Auto agregado a la cola de espera");
                response.put("car", car);
                response.put("inQueue", true);
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
            }
        } catch (IllegalStateException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error al registrar entrada: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Registra la salida de un auto del estacionamiento
     * POST /cars/exit/{plate}
     */
    @PostMapping("/exit/{plate}")
    public ResponseEntity<?> registerExit(@PathVariable String plate) {
        try {
            Car exitedCar = parkingLotService.registerExit(plate);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Auto salió exitosamente del estacionamiento");
            response.put("car", exitedCar);

            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error al registrar salida: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Busca un auto por su placa en el estacionamiento
     * GET /cars/parking/{plate}
     */
    @GetMapping("/parking/{plate}")
    public ResponseEntity<?> findCarInParking(@PathVariable String plate) {
        try {
            Car car = parkingLotService.findCarInParking(plate);

            if (car != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("car", car);
                response.put("found", true);
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Auto no encontrado en el estacionamiento");
                response.put("found", false);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error al buscar auto: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Obtiene todos los autos actualmente en el estacionamiento
     * GET /cars/parking
     */
    @GetMapping("/parking")
    public ResponseEntity<?> getAllCarsInParking() {
        try {
            List<Car> cars = parkingLotService.getAllCarsInParking();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("cars", cars);
            response.put("count", cars.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error al obtener autos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Obtiene los autos en la cola de espera
     * GET /cars/waiting
     */
    @GetMapping("/waiting")
    public ResponseEntity<?> getWaitingCars() {
        try {
            List<Car> cars = parkingLotService.getWaitingCars();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("cars", cars);
            response.put("count", cars.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error al obtener autos en espera: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Obtiene el historial de autos que salieron (últimos primero - LIFO)
     * GET /cars/history
     */
    @GetMapping("/history")
    public ResponseEntity<?> getExitHistory() {
        try {
            List<Car> history = parkingLotService.getExitHistoryList();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("history", history);
            response.put("count", history.size());
            response.put("message", "Historial ordenado del más reciente al más antiguo (LIFO)");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error al obtener historial: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /cars/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        try {
            ParkingLotService.ParkingStats stats = parkingLotService.getStats();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("stats", stats);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error al obtener estadísticas: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}

