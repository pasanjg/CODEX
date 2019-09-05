package com.sliit.spm.complexity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class Measure {

    private final static Logger LOGGER =
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final static String regex_comment = "\\/\\/.*|\\/\\*.*|\\*.*";
    private boolean isMultiLineComment;
    LinkedList<String> list = new LinkedList<String>();
    private ControlStructureComplexity controlStructureComplexity;
    private Inheritance inheritance;
    private String fileName;
    private JSONArray tempArray;
    private JSONObject jsonObject;
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
    private String word = "";
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
    private int TW = 0;
    private int _ci = 0;
    private int cps = 0;
    private int cs = 0;
    private int cp = 0;
    private int ctc = 0;
    private int lineNo = 1;
    //TODO: define complexity calculation objects
    public Measure(String fileName){
        this.fileName = fileName;
        tempArray = new JSONArray();
        isMultiLineComment =false;
        controlStructureComplexity = new ControlStructureComplexity();
        inheritance = new Inheritance();
        //TODO:
    }

    public void measure(){
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/temp/" + this.fileName));
            BufferedReader bufferedReader_rec = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/temp/" + this.fileName));
            String currentLine;
            int count = 1;
            lineNo = 1;
            
            while ((currentLine= bufferedReader_rec.readLine()) != null)
            {
                currentLine = currentLine.trim();
                String[] skipKeys = { "/", "*" };
                
                if (canSkip(currentLine, skipKeys)) {
					continue;
				}
				String[] keys = {"if", "for", "while", "else if","} else if", "}else if", "do"};
				trackRec(currentLine, keys);
				lineNo++;
            }
            
            lineNo = 1;
		    if(arrCount != 0) {
		    	startLineArray = new int[arrCount];
		        endLineArray = new int[arrCount];
		        for(int x=0; x < arrCount; x++) {
		        	startLineArray[x] = _startLineArray[x];
		        	endLineArray[x] = _endLineArray[x];
		        }
		    }

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
                    jsonObject.put("cnc",0);
                    jsonObject.put("code",currentLine);
                    jsonObject.put("TW", 0);
    				jsonObject.put("cps", 0);
    				jsonObject.put("cr", 0);
    				jsonObject.put("cs",1);
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
                    jsonObject.put("cnc", 0);
                    jsonObject.put("code",currentLine);
                    jsonObject.put("TW", 0);
    				jsonObject.put("cps", 0);
    				jsonObject.put("cr", 0);
    				jsonObject.put("cs",1);
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
                String[] keys = {"if", "for", "while", "else if","} else if", "}else if", "do"};
                cnc = countCncValue(currentLine, keys);
                jsonObject.put("cnc",cnc);
                jsonObject.put("ctcTokens",controlStructureComplexity.getTokens());
                jsonObject.put("line",count);
                jsonObject.put("ctc",controlStructureComplexity.getCtc());
                jsonObject.put("code",currentLine);

                //Final CR-CP value calculation
                //cs = sizeComplexity.getComplexity()-totalCs;    cs value calculation
                cs = 1;
                ctc = controlStructureComplexity.getCtc();
				TW = ctc + cnc + _ci;
				cps = cs * TW;
				cps = 1;
				
				if(arrCount != 0) {
					startNo = startLineArray[arrIndex];
					endNo = endLineArray[arrIndex];
					if((startNo <= lineNo) && (endNo >= lineNo)) {
						cr = cps * 2;
						cp = cp + cr;
						System.out.println("in rec"+lineNo+" cps:"+cps+" cr:"+cr);
						if(endNo == lineNo) {
							if(!(arrIndex == arrCount-1)) {
								arrIndex++;
							}
						}
					} else {
						cr = 0;
						System.out.println("Out rec"+lineNo+" cps:"+cps+" cr:"+cr);
					}
				}
				if(cr == 0) {
					cp = cp + cps;
				}
				
				jsonObject.put("TW", TW);
				jsonObject.put("cps",cps);
				jsonObject.put("cr", cr);
				jsonObject.put("cs",1);
                tempArray.put(jsonObject);
                cr = 0;
                count++;
                lineNo++;
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
        jsonObject.put("totalCp", cp);
        return String.valueOf(jsonObject.toString());
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
    public boolean canSkip(String currentLine, String[] skipKey) {
		for (int i = 0; i < skipKey.length; i++) {
			if (currentLine.startsWith(skipKey[i])) {
				lineNo++;
				return true;
			}
		}

		return false;
	}
    
    public void trackRec(String currentLine, String[] keys) {
		int temp = 0;
		int c_count = 0;
		
		currentLine = currentLine.trim();
		String[] lineWords = currentLine.split(" ");
		
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
	            		c_count++;
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
	}
	
	
}
