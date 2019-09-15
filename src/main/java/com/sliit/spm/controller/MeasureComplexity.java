package com.sliit.spm.controller;

import com.sliit.spm.complexity.SizeComplexity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileNotFoundException;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/measure")
public class MeasureComplexity {
    @Autowired
    private ApplicationContext ctx;

    // 5d4822e8ada9aa1a243d5d80 FibonacciMain
// 5d482360ada9aa1a243d5d82 myException file
// 5d498610ec688a47dce6a2d3 - CoinChange.java
    @GetMapping("/{fileName}")
    public String calculate(@PathVariable String fileName) {
        com.sliit.spm.complexity.Measure measure = new com.sliit.spm.complexity.Measure(fileName);
        measure.measure();
        return measure.getComplexity();
    }

}

