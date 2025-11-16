package com.example.Integradora.Controller;

import com.example.Integradora.Model.Car;
import com.example.Integradora.Service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/cars")
@CrossOrigin(origins = "*")
public class CarController {
    
    @Autowired
    private CarService carService;

    /**
     * Obtiene todos los autos de la base de datos
     * GET /cars
     */
    @GetMapping
    public ResponseEntity<?> getAllCars() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Car> cars = carService.findAll();
            response.put("success", true);
            response.put("cars", cars);
            response.put("count", cars.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al obtener autos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Obtiene un auto por su ID
     * GET /cars/id/{id}
     */
    @GetMapping("/id/{id}")
    public ResponseEntity<?> getCarById(@PathVariable Long id) {
        try {
            Optional<Car> car = carService.findById(id);
            if (car.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("car", car.get());
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Auto con ID " + id + " no encontrado");
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
     * Crea un nuevo auto en la base de datos
     * POST /cars
     */
    @PostMapping
    public ResponseEntity<?> createCar(@RequestBody Car car) {
        try {
            Car savedCar = carService.save(car);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Auto creado exitosamente");
            response.put("car", savedCar);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error al crear auto: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Actualiza un auto existente
     * PUT /cars/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCar(@PathVariable Long id, @RequestBody Car car) {
        try {
            if (carService.existsById(id)) {
                car.setId(id);
                Car updatedCar = carService.update(car);
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Auto actualizado exitosamente");
                response.put("car", updatedCar);
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Auto con ID " + id + " no encontrado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error al actualizar auto: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Elimina un auto por su ID
     * DELETE /cars/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCar(@PathVariable Long id) {
        try {
            if (carService.existsById(id)) {
                carService.deleteById(id);
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Auto eliminado exitosamente");
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Auto con ID " + id + " no encontrado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error al eliminar auto: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
