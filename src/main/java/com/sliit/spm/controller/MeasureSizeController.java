package com.sliit.spm.controller;

import java.io.File;
import java.io.FileNotFoundException;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sliit.spm.complexity.SizeComplexity;

// 5d498610ec688a47dce6a2d3 - CoinChange.java

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/complexity")
public class MeasureSizeController {
	
	@GetMapping("/size")
	public int calculateSizeComplexity() throws FileNotFoundException {
		
		File file = new File("C:\\Users\\pasan\\Desktop\\CoinChange.java");
		SizeComplexity sizeComplexity = new SizeComplexity(file);

		return sizeComplexity.calculateTotalSizeComplexity();
	}

}
