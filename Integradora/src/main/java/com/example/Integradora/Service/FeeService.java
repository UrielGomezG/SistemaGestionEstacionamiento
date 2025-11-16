package com.example.Integradora.Service;

import com.example.Integradora.Model.Fee;
import com.example.Integradora.Repositories.FeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FeeService {
    @Autowired
    private FeeRepository feeRepository;

    public Fee save(Fee fee){
        return feeRepository.save(fee);
    }

    public Optional<Fee> findById(Long id){
        return feeRepository.findById(id);
    }

    public List<Fee> findAll(){
        return feeRepository.findAll();
    }

    public Fee update(Fee fee){
        return feeRepository.save(fee);
    }

    public void deleteById(Long id){
        feeRepository.deleteById(id);
    }

    public void delete(Fee fee){
        feeRepository.delete(fee);
    }

    public boolean existsById(Long id){
        return feeRepository.existsById(id);
    }
}
