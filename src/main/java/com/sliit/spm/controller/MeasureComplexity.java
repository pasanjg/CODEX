package com.sliit.spm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

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
        FileReader fileReader = null;

        try {
            fileReader = new FileReader(System.getProperty("user.dir")+"/temp/" + fileName);

            int currentChar;
            int prevChar=0;
            String word = "";
            String prevDetect = "";
            int count =0;
            int weight = 0;
            while ((currentChar=fileReader.read())!=-1){
                if(currentChar != 32) {
                    word = word.concat(Character.toString((char) currentChar));


                    if (word.contains("if")){
                        weight++;
                        word = "";
                        prevDetect = "if";
                    }


                    if(currentChar == 38){
                        if(prevChar != 38){
                            prevDetect = "&";
                            word ="";
                            weight++;
                        }else{
                            word="";
                            prevDetect = "&&";
                        }
                    }

                    if(currentChar == 124){
                        if(prevChar != 124){
                            prevDetect = "|";
                            word ="";
                            weight++;
                        }else{
                            word="";
                            prevDetect = "||";
                        }
                    }

                    prevChar = currentChar;

                }else{
                    word = "";
                }
                count++;
            }
            return String.valueOf(weight);
//            return word;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

