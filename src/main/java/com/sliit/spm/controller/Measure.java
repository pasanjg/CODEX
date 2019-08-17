package com.sliit.spm.controller;

import com.sliit.spm.complexity.SizeComplexity;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Measure {

    private final static int SPACE = 32;
    private final static int LOGICAL_AND = 38;
    private final static int LOGICAL_OR = 124;
    private final static int BRACKET_CLOSE = 41;
    private final static int SLASH= 47;
    private final static int STAR= 42;
    private final static Logger LOGGER =
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    HashMap<Integer, Integer> range = new HashMap<Integer, Integer>();
	LinkedList<String> list = new LinkedList<String>();

    private BufferedReader bufferedReader;
    private BufferedReader bufferedReader_rec;
    private String currentLine;
    private String word;
    private String path;
    private String detectedKeyword;
    private String prevDetectedKeyword;
    private int ctc;
    private int totalCtc;
    private int totalCs;
    private int count;
    private JSONObject json;
    private JSONObject tempJSON;
    private JSONArray tokenArray;
    private JSONArray tempArr;
    private boolean isMultiLineComment;
    private boolean isSingleLineComment;
    private int prevChar;
    private int currentChar;
    private JSONArray inheritanceObjArr;
    private JSONObject inheritanceObj;
    private SizeComplexity sizeComplexity;
    private int cnc = 0;
    private int cr = 0;
    private File file;
    private int n_count = 0;
    private int b_count = 0;
    private int arrCount = 0;
    private boolean status = false;
    private boolean _if = false;
    private boolean _for = false;
    private boolean _while = false;
    private boolean inMethod = false;
    private boolean preLine = false;
    private boolean isFirst = false;
    private String val;
    private String _methodName = "";
    private String methodName = "";
    private String r_word = "";
    private int startLine = 0;
    private int endLine = 0;
    private int startNo = 0;
    private int endNo = 0;
    private int arrIndex = 0;
    private int currentStartLine = 0;
    private int currentEndLine = 0;
    private boolean isRec = false;
    private int bracketCount = 0;
    private int _startLineArray[] = new int[100];
    private int _endLineArray[] = new int[100];
    private int startLineArray[];
    private int endLineArray[];
    private int lineNo = 1;
    private int ci = 0;
    private int totalCi = 0;
    private String comment = " ";

    public Measure(String path) {
        this.path = path;
        ctc = 0;
        totalCtc = 0;
        totalCs = 0;
        count = 0;
        r_word = "";
        isMultiLineComment = false;
        isSingleLineComment = false;
        detectedKeyword = "";
        prevDetectedKeyword = "";
        json = new JSONObject();
        tempJSON = new JSONObject();
        tokenArray = new JSONArray();
        tempArr = new JSONArray();
        inheritanceObj = new JSONObject();
        inheritanceObjArr = new JSONArray();
        sizeComplexity = new SizeComplexity(new File(System.getProperty("user.dir") + "/temp/" + this.path));
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

    public boolean canSkip(String currentLine, String[] skipKey) {
		for (int i = 0; i < skipKey.length; i++) {
			if (currentLine.startsWith(skipKey[i])) {
				this.lineNo++;
				return true;
			}
		}

		return false;
	}

    public int countCncValue(String currentLine, String[] keys) {

		currentLine = currentLine.trim();

		if(status) {
			if(currentLine.startsWith("}")) {
				n_count--;
				if(n_count==0) {
					status = false;
					val = null;
					list.pop();
					val = list.peek();
				}
			}
		}

		for (int i = 0; i < keys.length; i++) {
			if(currentLine.startsWith(keys[i])) {
				if(val == null) {
					val = keys[i];
					list.add(keys[i]);
				}
				if(val != keys[i]) {
					val = keys[i];
					list.add(keys[i]);
					//count = 0;
				}
				if(val == keys[i]) {
					status = true;
					n_count++;

				}
				val = keys[i];
			}
		}

		if(currentLine.equals("{") || currentLine.equals("}")) {
			return 0;
		} else {
			return n_count;
		}
	}

    public int trackRec(String currentLine, String[] keys) {
		int temp = 0;
		int count = 0;
		int c_count = 0;

		currentLine = currentLine.trim();
		String[] lineWords = currentLine.split(" ");
		char[] charLine = currentLine.toCharArray();

		if(lineWords.length >= 3) {
			String keyword = lineWords[1].trim();
			if(currentLine != null || currentLine != "" ) {
				if(!lineWords[0].equals("class") || !lineWords[1].equals("class")) {
					if(keyword.equals("static") || keyword.equals("void") || keyword.equals("String") || keyword.equals("int") || keyword.equals("double") || keyword.equals("float") || keyword.equals("long")) {
			        	if(keyword.equals("static")) {
			        		if(!lineWords[3].equals("main(String[]") || !lineWords[3].equals("main(String")) {
			        			inMethod = true;
			        			preLine = true;
			        			isFirst = true;
			        			r_word = lineWords[3];
		            			startLine = lineNo;
		            			endLine = lineNo;
		            			b_count++;
		            			temp = b_count;
		            			System.out.println(r_word);
			        		}
			        	} else {
			        		inMethod = true;
			        		preLine = true;
			        		isFirst = true;
			        		r_word = lineWords[2];
		            		startLine = lineNo;
		            		endLine = lineNo;
		            		b_count++;
		            		temp = b_count;
		            		System.out.println(r_word);
			        	}
			        }
				}
			}
		}
		if(temp == 1) {
			temp++;
		} else {
			if(inMethod) {
				if(preLine) {
					preLine = false;
				} else {
					if(currentLine.equals("{") || currentLine.endsWith("{")) {
						b_count++;
					}
					if(currentLine.equals("}")) {
						b_count--;
					}
				}
			}
		}
		if(b_count == 0) {
			endLine = lineNo-1;
			inMethod = false;
			if(isRec) {
				_startLineArray[arrCount] = startLine;
				_endLineArray[arrCount] = endLine;
				arrCount++;
				isRec = false;
			}
		}

		char[] charLineArr = r_word.toCharArray();
        if(isFirst) {
        	boolean status = true;
        	for( int ch: charLineArr) {
	        	if(status) {
	        		if(ch == 40) {
	            		status = false;
	            	} else {
	            		c_count++;
	            	}
	        	}
	        }

	        for(int x=0; x<c_count;x++) {
	        	_methodName = _methodName+charLineArr[x];
	        }
	        methodName = _methodName;
	        _methodName = "";
	        System.out.println(methodName);
	        isFirst = false;
        }

        if(!(lineNo == startLine)) {
        	if(b_count > 0) {
        		if(!isFirst) {
	        		for(int x=0; x <lineWords.length; x++) {
		            	if(lineWords[x].startsWith(methodName)) {
		            		isRec = true;
		            		System.out.println(currentLine+"---->inside the core");
		            	}
		            }
	        	}
        	}
        }
		return b_count;
	}
    public void mesaureCtC() {
        try{

            bufferedReader_rec = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/temp/" + this.path));
            bufferedReader = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/temp/" + this.path));
            File file = new File(System.getProperty("user.dir")+ "/temp/" + this.path);
            String fileExt = file.getName().substring(file.getName().lastIndexOf("."));
            String line = bufferedReader.readLine();

            while ((currentLine= bufferedReader_rec.readLine()) != null)
            {
                currentLine = currentLine.trim();
                String[] skipKeys = { "/", "*" };
                
                if (canSkip(currentLine, skipKeys)) {
					continue;
				}

				String[] keys = {"if", "for", "while", "else if","} else if", "}else if", "do"};
				System.out.println(trackRec(currentLine, keys));
				lineNo++;
            }
            System.out.println("arr count---------------------"+arrCount);
            if(arrCount != 0) {
		    	startLineArray = new int[arrCount];
		        endLineArray = new int[arrCount];
		        for(int x=0; x < arrCount; x++) {
		        	startLineArray[x] = _startLineArray[x];
		        	endLineArray[x] = _endLineArray[x];
		        }
		        System.out.println("starrrt"+startLineArray[0]);
		        System.out.println("enddddd"+endLineArray[0]);
		    }
            
            char[] temp;
            lineNo = 1;
            while ((currentLine= bufferedReader.readLine()) != null)
            {
            	//lanka
                if (this.path.contains(".java")) {
                	System.out.println("test#############################");
                    line = currentLine;
                    System.out.println(line);
                    if (line == null) {
                        break;
                    }

                    comment = "comment";
                    if (line.contains("//")) {
                        System.out.println(line.charAt(0));
                        comment = line.substring(0, line.indexOf("//"));
                    }

                    inheritanceObj.put("line", line);

                    if (line.matches(".*[a-zA-Z].*") ) {
                        if (line.contains(" class ")) {
                            ci = 0;
                        }
                        totalCi += ci;
                        tempJSON.put("ci",ci);
                        System.out.println("Shalika------------"+totalCi);
                    }
//                    inheritanceObj.put("number", count);
//                    inheritanceObjArr.put(inheritanceObj);
//                    inheritanceObj = new JSONObject();

//                    if (line != null) {
                        if (containsIgnoreCase(line, " class ")) {
                            ci = 1;
                            System.out.println("test---------------------------------------------------");
                        }

                        if (containsIgnoreCase(line, " extends ")) {
                            ci = 2;
                        }

                        if (containsIgnoreCase(line, " implements ")) {
                            int ciCount = 2;

                            for (int i = 0; i < line.length(); i++) {
                                if (line.charAt(i) == ',') {
                                    ciCount++;
                                }
                            }

                            ci = ciCount;
                        }

                   // }

//                    count++;

                } else if (this.path.contains(".cpp")) {
                    line = bufferedReader.readLine();

                    if (line == null) {
                        break;
                    }

                    if(containsIgnoreCase(line, " main()")) {
                        ci = 0;
                    }

                    comment = "comment";

                    if (line.contains("//")) {
                        System.out.println(line.charAt(0));
                        comment = line.substring(0, line.indexOf("//"));
                    }

                    inheritanceObj.put("line", line);

                    if(!comment.matches(".*[a-zA-Z].*")) {
                        ci = 0;
                    }


                    if (line != null && line.matches(".*[a-zA-Z].*") && comment.matches(".*[a-zA-Z].*")) {
                        if (containsIgnoreCase(line, " class ") ) {
                            ci = 0;
                        }
                        totalCi += ci;
                        tempJSON.put("ci",ci);
                        System.out.println(totalCi);
                    }

//                    inheritanceObj.put("number", count);
//                    inheritanceObjArr.put(inheritanceObj);
//                    inheritanceObj = new JSONObject();

                    if (line != null && line.matches(".*[a-zA-Z].*") && comment.matches(".*[a-zA-Z].*")) {

                        if (containsIgnoreCase(line, " class ")) {
                            int ciCount = 1;

                            for (int i = 0; i < line.length(); i++) {
                                if (line.charAt(i) == ':') {
                                    ciCount++;
                                }
                            }

                            ci = ciCount;
                        }

                    }

                }
                
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
                    if((prevChar==STAR && currentChar == SLASH)) {
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

                String[] skipKeys = { "/", "*" };
                if (sizeComplexity.canSkip(currentLine, skipKeys)) {
                    continue;
                }

                if (fileExt.equals(".cpp")) {
                    sizeComplexity.sizeOfRefAndDeref(currentLine);
                }

                sizeComplexity.weightTwoKeywords(currentLine);
                sizeComplexity.sizeOfArithmeticOperators(currentLine);
                sizeComplexity.sizeOfRelationOperators(currentLine);
                sizeComplexity.sizeOfLogicalOperators(currentLine);
                sizeComplexity.sizeOfBitwiseOperators(currentLine);
                sizeComplexity.sizeOfMiscellaneousOperators(currentLine);
                sizeComplexity.sizeOfAssignmentOperators(currentLine);
                sizeComplexity.sizeOfKeywords(currentLine);
                sizeComplexity.sizeOfManipulators(currentLine);
                sizeComplexity.sizeOfQuotes(currentLine);
                sizeComplexity.sizeOfClasses(currentLine);
                sizeComplexity.sizeOfMethods(currentLine);
                sizeComplexity.sizeOfObjects(currentLine);
                sizeComplexity.sizeOfVariables(currentLine);
                sizeComplexity.sizeOfArrays(currentLine);

                totalCtc += ctc;
                
                
                String[] keys = {"if", "for", "while", "else if","} else if", "}else if", "do"};
				cnc = countCncValue(currentLine, keys);
				
				if(arrCount != 0) {
					startNo = startLineArray[arrIndex];
					endNo = endLineArray[arrIndex];
					if((startNo <= lineNo) && (endNo >= lineNo)) {
						cr = this.cnc * 2;
						
						if(endNo == lineNo) {
							if(!(arrIndex == arrCount-1)) {
								arrIndex++;
							}
						}
					} else {
						cr = 0;
					}
				}
                tempJSON.put("no",count);
                tempJSON.put("line",currentLine);
                tempJSON.put("cscTokens",sizeComplexity.getTempToken());
                tempJSON.put("tokens",tokenArray);
                tempJSON.put("cr",cr);
                tempJSON.put("cnc value", cnc);
                tempJSON.put("ctc",ctc);
                tempJSON.put("cs",sizeComplexity.getComplexity()-totalCs);
                tempArr.put(tempJSON);
                totalCs = sizeComplexity.getComplexity();
                ctc = 0;
                lineNo++;
            }
        } catch (IOException e) {
            LOGGER.log(Level.INFO, e.getMessage());
        }
    }

    public String getCodeInheritance() {

        BufferedReader reader;
        int ci = 0;
        int totalCi = 0;
        String comment = " ";

        try {
            reader = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/temp/" + this.path));

            String line = reader.readLine();

            int count = 1;
            inheritanceObj = new JSONObject();

            while (line != null) {

                if (this.path.contains(".java")) {
                    line = reader.readLine();

                    if (line == null) {
                        break;
                    }

                    comment = "comment";
                    if (line.contains("//")) {
                        System.out.println(line.charAt(0));
                        comment = line.substring(0, line.indexOf("//"));
                    }

                    inheritanceObj.put("line", line);

                    if (line != null && line.matches(".*[a-zA-Z].*") && comment.matches(".*[a-zA-Z].*")) {
                        if (line.contains(" class ")) {
                            ci = 0;
                        }
                        totalCi += ci;
                        inheritanceObj.put("Ci", ci);
                        System.out.println(totalCi);
                    }
                    inheritanceObj.put("number", count);
                    inheritanceObjArr.put(inheritanceObj);
                    inheritanceObj = new JSONObject();

                    if (line != null) {
                        if (containsIgnoreCase(line, " class ")) {
                            ci = 2;
                        }

                        if (containsIgnoreCase(line, " extends ")) {
                            ci = 3;
                        }

                        if (containsIgnoreCase(line, " implements ")) {
                            int ciCount = 3;

                            for (int i = 0; i < line.length(); i++) {
                                if (line.charAt(i) == ',') {
                                    ciCount++;
                                }
                            }

                            ci = ciCount;
                        }

                    }

//                    count++;

                } else if (this.path.contains(".cpp")) {
                    line = reader.readLine();

                    if (line == null) {
                        break;
                    }

                    if(containsIgnoreCase(line, " main()")) {
                        ci = 0;
                    }

                    comment = "comment";

                    if (line.contains("//")) {
                        System.out.println(line.charAt(0));
                        comment = line.substring(0, line.indexOf("//"));
                    }

                    inheritanceObj.put("line", line);

                    if(!comment.matches(".*[a-zA-Z].*")) {
                        ci = 0;
                    }


                    if (line != null && line.matches(".*[a-zA-Z].*") && comment.matches(".*[a-zA-Z].*")) {
                        if (containsIgnoreCase(line, "class ") ) {
                            ci = 0;
                        }
                        totalCi += ci;
                        inheritanceObj.put("Ci", ci);
                        System.out.println(totalCi);
                    }

                    inheritanceObj.put("number", count);
                    inheritanceObjArr.put(inheritanceObj);
                    inheritanceObj = new JSONObject();

                    if (line != null && line.matches(".*[a-zA-Z].*") && comment.matches(".*[a-zA-Z].*")) {

                        if (containsIgnoreCase(line, "class ")) {
                            int ciCount = 2;

                            for (int i = 0; i < line.length(); i++) {
                                if (line.charAt(i) == ':') {
                                    ciCount++;
                                }
                            }

                            ci = ciCount;
                        }

                    }

                }

                count++;

            }

            reader.close();
        } catch (IOException e) {
            LOGGER.log(Level.INFO, e.getMessage());
        }
        inheritanceObj.put("totalCi", totalCi);
        inheritanceObjArr.put(inheritanceObj);
        return inheritanceObjArr.toString();
    }

    public static boolean containsIgnoreCase(String str, String subString) {
        return str.toLowerCase().contains(subString.toLowerCase());
    }



    public int getTotalCtc() {
        return totalCtc;
    }
    public String get() {
        json = new JSONObject();
        json.put("code",tempArr);
        json.put("totalCtc",getTotalCtc());
        json.put("totalCi", totalCi);
        json.put("totalCs",sizeComplexity.getComplexity());
        return json.toString();
    }
}