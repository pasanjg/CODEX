package com.sliit.spm.complexity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class Measure {

    private final static Logger LOGGER =
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final static String regex_comment = "\\/\\/.*|\\/\\*.*|\\*.*";
    private boolean isMultiLineComment;

    private ControlStructureComplexity controlStructureComplexity;
    private Inheritance inheritance;
    private String fileName;
    private JSONArray tempArray;
    private JSONObject jsonObject;
    //TODO: define complexity calculation objects
    public Measure(String fileName){
        this.fileName = fileName;
        tempArray = new JSONArray();
        isMultiLineComment =false;
        controlStructureComplexity = new ControlStructureComplexity();
        //TODO:
    }

    public void measure(){
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/temp/" + this.fileName));
            String currentLine;
            int count = 0;

            /*
             * check type
             * */
            inheritance.setJava(this.fileName.contains(".java"));
            inheritance.setCpp(this.fileName.contains(".cpp"));

            while ((currentLine= bufferedReader.readLine()) != null){
                //TODO: skip lines until detect class
                currentLine = currentLine.trim();
                jsonObject = new JSONObject();
                //skip multiline comments
                if(isMultiLineComment){
                    jsonObject.put("line",count++);
                    jsonObject.put("ctc",0);
                    jsonObject.put("code",currentLine);
                    tempArray.put(jsonObject);
                    if(currentLine.contains("*/")){
                        isMultiLineComment = false;
                        jsonObject.put("ctcTokens","multiline close");
                    }else{
                        jsonObject.put("ctcTokens","multiline");
                    }
                    continue;
                }

                //pattern match for comments
                if(Pattern.matches(regex_comment,currentLine)){
                    jsonObject.put("line",count++);
                    jsonObject.put("ctc",0);
                    jsonObject.put("code",currentLine);
                    tempArray.put(jsonObject);
                    if(currentLine.contains("/*")){
                        jsonObject.put("ctcTokens","multilineStart");
                        isMultiLineComment = true;
                    }else {
                        jsonObject.put("ctcTokens","comment");
                    }
                    continue;
                }
                controlStructureComplexity.measureCtc(currentLine);
                //TODO: measure lanka's
                //TODO: measure pasan's
                //TODO: measure chathura's
                jsonObject.put("ctcTokens",controlStructureComplexity.getTokens());
                jsonObject.put("line",count);
                jsonObject.put("ctc",controlStructureComplexity.getCtc());
                jsonObject.put("code",currentLine);

                tempArray.put(jsonObject);
                count++;
            }
        } catch (IOException e) {
            LOGGER.log(Level.INFO, e.getMessage());
        }

    }


    public String getComplexity(){
        //TODO:return complexity JSON
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("measure",tempArray);
        jsonObject.put("totalCtc",controlStructureComplexity.getTotalCtc());
        return String.valueOf(jsonObject.toString());
    }
}
