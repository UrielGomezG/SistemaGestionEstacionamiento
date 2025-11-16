package com.example.Integradora.Service;

import com.example.Integradora.Repositories.FeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FeeService {
    @Autowired
    private FeeRepository feeRepository;
}
