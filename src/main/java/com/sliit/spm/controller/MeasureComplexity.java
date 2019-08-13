package com.sliit.spm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/measure")
public class MeasureComplexity {
    @Autowired
    private ApplicationContext ctx;

//    5d4822e8ada9aa1a243d5d80 FibonacciMain
//    5d482360ada9aa1a243d5d82 myException file
    @GetMapping("/controlStructure/{fileName}")
    public String calculateCnC(@PathVariable String fileName){

        Measure measure = new Measure(fileName);
        measure.measureCtC();
//        return measure.get();
        return measure.getCodeInheritance();
    }
}

