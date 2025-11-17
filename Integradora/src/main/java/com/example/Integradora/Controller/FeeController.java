package com.example.Integradora.Controller;

import com.example.Integradora.Model.Fee;
import com.example.Integradora.Service.FeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/fees")
@CrossOrigin(origins = "*")
public class FeeController {
    @Autowired
    private FeeService feeService;

    /**
     * Obtiene todas las tarifas de la bd
     * GET /fees
     */
    @GetMapping
    public ResponseEntity<?> getAllFees() {
        Map<String, Object> response = new HashMap<>();
        try{
            List<Fee> fees = feeService.findAll();
            response.put("success", true);
            response.put("fees", fees);
            response.put("count",  fees.size());
            return ResponseEntity.ok(response);
        } catch (Exception e){
            response.put("success", false);
            response.put("message", "Error al obtener tarifas" +  e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
