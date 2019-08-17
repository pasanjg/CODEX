package com.sliit.spm.controller;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

public class Measure {

    private final static int SPACE = 32;
    private final static int LOGICAL_AND = 38;
    private final static int LOGICAL_OR = 124;
    private final static int BRACKET_CLOSE = 41;
    private final static int SLASH= 47;
    private final static int STAR= 42;
    
    HashMap<Integer, Integer> range = new HashMap<Integer, Integer>();
	LinkedList<String> list = new LinkedList<String>();

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
	int cnc = 0;
	int cr = 0;
	File file;
	int n_count = 0;
	int b_count = 0;
	int arrCount = 0;
	boolean status = false;
	boolean _if = false;
	boolean _for = false;
	boolean _while = false;
	boolean inMethod = false;
	boolean preLine = false;
	boolean isFirst = false;
	String val;
    String _methodName = "";
    String methodName = "";
    String r_word = "";
    int startLine = 0;
    int endLine = 0;
    int startNo = 0;
    int endNo = 0;
    int arrIndex = 0;
    int currentStartLine = 0;
    int currentEndLine = 0;
    boolean isRec = false;
    int bracketCount = 0;
    int _startLineArray[] = new int[100];
    int _endLineArray[] = new int[100];
    int startLineArray[];
    int endLineArray[];
    int lineNo = 1;

    public Measure(String path) {
        this.path = path;
        ctc = 0;
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
    }

    public void detectConditionalControlStructure(){
        if (r_word.startsWith("if(") || r_word.startsWith("if (")){
            ctc++;
            r_word = "";
            detectedKeyword = "if";
            tokenArray.put("if");
        }
    }

    public void detectBracketClose(){
        if(currentChar==BRACKET_CLOSE){
            prevDetectedKeyword = detectedKeyword;
            detectedKeyword = "";
            r_word="";
        }
    }
    public void detectLogicalOperator(int increment){
        if (currentChar == LOGICAL_AND) {
            if (prevChar != LOGICAL_AND) {
                r_word = "";
                ctc += increment;
                tokenArray.put("AND");
            } else {
                r_word = "";
            }
        }else if (currentChar == LOGICAL_OR) {
            if (prevChar != LOGICAL_OR) {
                r_word = "";
                ctc+=increment;
                tokenArray.put("OR");
            } else {
                r_word = "";
            }
        }
    }

    public void detectIterativeControlStructure(){
        /*
        detect for loop
         */
        if(r_word.startsWith("for(") || r_word.startsWith("for (")){
            ctc += 2;
            r_word = "";
            detectedKeyword = "for";
            tokenArray.put("for");
        }
        if(r_word.startsWith("do{") || r_word.startsWith("do {")){
            ctc += 2;
            r_word = "";
            detectedKeyword = "do";
            tokenArray.put("do-while");
        }
        /*
        detect while loop
         */
        if(r_word.startsWith("while (") || r_word.startsWith("while(")){
            if(!detectedKeyword.contains("do") && !prevDetectedKeyword.contains("do")){
                detectedKeyword = "while";
                r_word = "";
            }else{
                ctc += 2;
                r_word = "";
                detectedKeyword = "while";
                tokenArray.put("while");
            }
        }
    }

    public void detectCatchStatement(){
        if(r_word.contains("catch")){
            ctc += 1;
            r_word = "";
            detectedKeyword = "catch";
            tokenArray.put("catch");
        }
    }

    public void detectSwitch(){
        if(r_word.contains("switch")){
            detectedKeyword="switch";
            r_word = "";
        }
    }

    public void detectCase(){
        if(prevDetectedKeyword.equals("switch") && r_word.startsWith("case")) {
            r_word = "";
            ctc++;
            tokenArray.put("case");
        }
        if(prevDetectedKeyword.equals("switch") && r_word.startsWith("default")) {
            r_word = "";
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
			        			word = lineWords[3];
		            			startLine = lineNo;
		            			endLine = lineNo;
		            			b_count++;
		            			temp = b_count;
		            			System.out.println(word);
			        		}		
			        	} else {
			        		inMethod = true;
			        		preLine = true;
			        		isFirst = true;
			        		word = lineWords[2];
		            		startLine = lineNo;
		            		endLine = lineNo;
		            		b_count++;
		            		temp = b_count;
		            		System.out.println(word);
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
		
		char[] charLineArr = word.toCharArray();
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
            bufferedReader = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/temp/" + this.path));

            char[] temp;
            while ((currentLine= bufferedReader.readLine()) != null)
            {
                isSingleLineComment = false;
                count++;
                currentLine = currentLine.trim();
                r_word = "";
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
                        r_word = "";
                    }else{
                        r_word = r_word.concat(Character.toString((char) ch));
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

    public int getCtc() {
        return ctc;
    }
    public String get() {
        json = new JSONObject();
        json.put("code",tempArr);
        json.put("ctc",getCtc());
        return json.toString();
    }
}



//public String CalcNestingControlStructures() throws FileNotFoundException{
//	Scanner recScanner = new Scanner(this.getFile());
//	Scanner scanner = new Scanner(this.getFile());
//    tempArr = new JSONArray();
//    
//    while (recScanner.hasNext()) {
//
//		String currentLine = recScanner.nextLine().trim();
//		String[] skipKeys = { "/", "*" };
//		String fileExt = file.getName().substring(file.getName().lastIndexOf("."));
//
//
//		if (canSkip(currentLine, skipKeys)) {
//			continue;
//		}
//		
//		String[] words = currentLine.split(" ");
//		String[] keys = {"if", "for", "while", "else if","} else if", "}else if", "do"};
//		System.out.println(currentLine+"---->"+trackRec(currentLine, keys));
//		
//		this.lineNo++;
//
//	}
//    
//    this.lineNo = 1;
//    if(arrCount != 0) {
//    	startLineArray = new int[arrCount];
//        endLineArray = new int[arrCount];
//        for(int x=0; x < arrCount; x++) {
//        	startLineArray[x] = _startLineArray[x];
//        	endLineArray[x] = _endLineArray[x];
//        }
//    }
//    System.out.println(startLineArray[arrIndex]);
//    System.out.println(endLineArray[arrIndex]);
//
//	while (scanner.hasNext()) {
//
//		String currentLine = scanner.nextLine().trim();
//		String[] skipKeys = { "/", "*" };
//		String fileExt = file.getName().substring(file.getName().lastIndexOf("."));
//		tempJSON = new JSONObject();
//
//		if (canSkip(currentLine, skipKeys)) {
//			continue;
//		}
//		String[] words = currentLine.split(" ");
//		String[] keys = {"if", "for", "while", "else if","} else if", "}else if", "do"};
//		this.cnc = countCncValue(currentLine, keys);
//		if(arrCount != 0) {
//			startNo = startLineArray[arrIndex];
//			endNo = endLineArray[arrIndex];
//			if((startNo <= lineNo) && (endNo >= lineNo)) {
//				this.cr = this.cnc * 2;
//				
//				if(endNo == lineNo) {
//					if(!(arrIndex == arrCount-1)) {
//						arrIndex++;
//					}
//				}
//			} else {
//				this.cr = 0;
//			}
//		}
//		tempJSON.put("lineNo", this.lineNo);
//		tempJSON.put("statement", currentLine);
//		tempJSON.put("cnc value", cnc);
//		tempJSON.put("cr value", cr);
//		tempArr.put(tempJSON);
//		this.lineNo++;
//
//	}
//	json = new JSONObject();
//    json.put("code",tempArr);
//    return json.toString();
//
//	
//}
