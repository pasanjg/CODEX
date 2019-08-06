package com.sliit.spm.controller;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Measure {

    private final static int SPACE = 32;
    private final static int LOGICAL_AND = 38;
    private final static int LOGICAL_OR = 124;
    private final static int BRACKET_CLOSE = 41;

    private int currentChar;
    private int prevChar;
    private String word;
    private String prevDetectLogic;
    private String prevDetectControlStruc;
    private int count;
    private int weight;
    private String file;
    private FileReader fileReader;

    public Measure(String file){
        this.file = file;
        prevChar=0;
        word = "";
        prevDetectLogic = "";
        prevDetectControlStruc = "";
        count =0;
        weight = 0;
        try {
            fileReader = new FileReader(System.getProperty("user.dir")+"/temp/" + this.file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private int readChar(){
        try {
            return fileReader.read();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void detectConditionalControlStructure(){
        if (word.contains("if")){
            weight++;
            word = "";
            prevDetectControlStruc = "if";
        }
    }

    public void detectLogicalOperator(int increment){
        if (currentChar == LOGICAL_AND) {
            if (prevChar != LOGICAL_AND) {
                prevDetectLogic = "&";
                word = "";
                weight += increment;
            } else {
                word = "";
                prevDetectLogic = "&&";
            }
        }

        if (currentChar == LOGICAL_OR) {
            if (prevChar != LOGICAL_OR) {
                prevDetectLogic = "|";
                word = "";
                weight+=increment;
            } else {
                word = "";
                prevDetectLogic = "||";
            }
        }
    }

    public void detectBracketClose(){
        if(currentChar==BRACKET_CLOSE){
            prevDetectControlStruc = "";
            prevDetectLogic = "";
        }
    }

    public void measureCtC(){
        while ((currentChar=readChar())!=-1){
            if(this.currentChar != SPACE){
                word = word.concat(Character.toString((char) currentChar));
                detectConditionalControlStructure();
                detectBracketClose();
                if(prevDetectControlStruc.equals("if")){
                    detectLogicalOperator(1);
                }

            }
            else
                word = "";
            prevChar = currentChar;
        }
    }

    public int getWeight() {
        return weight;
    }
}
