package com.sliit.spm.controller;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;

public class Measure {

    private final static int SPACE = 32;
    private final static int LOGICAL_AND = 38;
    private final static int LOGICAL_OR = 124;
    private final static int BRACKET_CLOSE = 41;
    private final static int SLASH= 47;
    private final static int STAR= 42;

    private BufferedReader bufferedReader;
    private String currentLine;
    private String word;
    private String path;
    private String detectedKeyword;
    private String prevDetectedKeyword;
    private int ctc;
    private int count;
    private JSONObject json;
    private JSONObject tempJSON;
    private JSONArray tokenArray;
    private JSONArray tempArr;
    private boolean isMultiLineComment;
    private boolean isSingleLineComment;
    private int prevChar;
    private int currentChar;

    public Measure(String path) {
        this.path = path;
        ctc = 0;
        count = 0;
        word = "";
        isMultiLineComment = false;
        isSingleLineComment = false;
        detectedKeyword = "";
        prevDetectedKeyword = "";
        json = new JSONObject();
        tempJSON = new JSONObject();
        tokenArray = new JSONArray();
        tempArr = new JSONArray();
    }

    public void detectConditionalControlStructure(){
        if (word.startsWith("if(") || word.startsWith("if (")){
            ctc++;
            word = "";
            detectedKeyword = "if";
            tokenArray.put("if");
        }
    }

    public void detectBracketClose(){
        if(currentChar==BRACKET_CLOSE){
            prevDetectedKeyword = detectedKeyword;
            detectedKeyword = "";
            word="";
        }
    }
    public void detectLogicalOperator(int increment){
        if (currentChar == LOGICAL_AND) {
            if (prevChar != LOGICAL_AND) {
                word = "";
                ctc += increment;
                tokenArray.put("AND");
            } else {
                word = "";
            }
        }else if (currentChar == LOGICAL_OR) {
            if (prevChar != LOGICAL_OR) {
                word = "";
                ctc+=increment;
                tokenArray.put("OR");
            } else {
                word = "";
            }
        }
    }

    public void detectIterativeControlStructure(){
        /*
        detect for loop
         */
        if(word.startsWith("for(") || word.startsWith("for (")){
            ctc += 2;
            word = "";
            detectedKeyword = "for";
            tokenArray.put("for");
        }
        if(word.startsWith("do{") || word.startsWith("do {")){
            ctc += 2;
            word = "";
            detectedKeyword = "do";
            tokenArray.put("do-while");
        }
        /*
        detect while loop
         */
        if(word.startsWith("while (") || word.startsWith("while(")){
            if(!detectedKeyword.contains("do") && !prevDetectedKeyword.contains("do")){
                detectedKeyword = "while";
                word = "";
            }else{
                ctc += 2;
                word = "";
                detectedKeyword = "while";
                tokenArray.put("while");
            }
        }
    }

    public void detectCatchStatement(){
        if(word.contains("catch")){
            ctc += 1;
            word = "";
            detectedKeyword = "catch";
            tokenArray.put("catch");
        }
    }

    public void detectSwitch(){
        if(word.contains("switch")){
            detectedKeyword="switch";
            word = "";
        }
    }

    public void detectCase(){
        if(prevDetectedKeyword.equals("switch") && word.startsWith("case")) {
            word = "";
            ctc++;
            tokenArray.put("case");
        }
        if(prevDetectedKeyword.equals("switch") && word.startsWith("default")) {
            word = "";
            prevDetectedKeyword="";
        }
    }

    public void commentsDetector(){
        if(prevChar == SLASH && currentChar == STAR) {
            isMultiLineComment = true;
        }
        if(prevChar==STAR && currentChar == SLASH) {
            isMultiLineComment=false;
        }
        if(prevChar==SLASH && currentChar == SLASH) {
            isSingleLineComment = true;
        }
    }
    public void mesaureCtC() {
        try{
            bufferedReader = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/temp/" + this.path));

            char[] temp;
            while ((currentLine= bufferedReader.readLine()) != null)
            {
                isSingleLineComment = false;
                count++;
                currentLine = currentLine.trim();
                word = "";
                temp = currentLine.toCharArray();
                tempJSON = new JSONObject();
                tokenArray = new JSONArray();

                for (int ch: temp) {
                    currentChar = ch;
                    commentsDetector();

                    if(isSingleLineComment || isMultiLineComment){
                        prevChar = currentChar;
                        continue;
                    }
                    if((ch == SPACE) || (prevChar==STAR && currentChar == SLASH)) {
                        word = "";
                    }else{
                        word = word.concat(Character.toString((char) ch));
                        detectBracketClose();
                        detectConditionalControlStructure();
                        if(detectedKeyword.equals("if")){
                            detectLogicalOperator(1);
                        }
                        detectIterativeControlStructure();
                        if(detectedKeyword.equals("while") || detectedKeyword.equals("for") || detectedKeyword.equals("do")) {
                            detectLogicalOperator(2);
                        }

                        detectCatchStatement();

                        detectSwitch();
                        detectCase();
                    }
                    prevChar = ch;
                }

                tempJSON.put("no",count);
                tempJSON.put("line",currentLine);
                tempJSON.put("token",tokenArray);
                tempArr.put(tempJSON);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getWeight() {
        return ctc;
    }
    public String get() {
        json = new JSONObject();
        json.put("code",tempArr);
        json.put("weight",getWeight());
        return json.toString();
    }
}
