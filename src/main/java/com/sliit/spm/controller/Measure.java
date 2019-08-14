package com.sliit.spm.controller;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
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
	private String line;
	private String detectedKeyword;
	private String prevDetectedKeyword;
	private int count;
	private int weight;
	private String file;
	private FileReader fileReader;
	private JSONObject json;
	private JSONObject tempJSON;
	private JSONArray jsonArray;
	private JSONArray tempArr;
	private JSONArray inheritanceObjArr;
	private JSONObject inheritanceObj;

	public Measure(String file) {
		this.file = file;
		prevChar = 0;
		word = "";
		line = "";
		detectedKeyword = "";
		prevDetectedKeyword = "";
		count = 1;
		weight = 0;
		json = new JSONObject();
		tempJSON = new JSONObject();
		jsonArray = new JSONArray();
		tempArr = new JSONArray();
		inheritanceObj = new JSONObject();
		inheritanceObjArr = new JSONArray();

		try {
			fileReader = new FileReader(System.getProperty("user.dir") + "/temp/" + this.file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private int readChar() {
		try {
			return fileReader.read();
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public void detectConditionalControlStructure() {
		if (word.contains("if")) {
			weight++;
			word = "";
			detectedKeyword = "if";
			jsonArray.put("if");
		}
	}

	public void detectLogicalOperator(int increment) {
		if (currentChar == LOGICAL_AND) {
			if (prevChar != LOGICAL_AND) {
				word = "";
				weight += increment;
				jsonArray.put("AND");
			} else {
				word = "";
			}
		} else if (currentChar == LOGICAL_OR) {
			if (prevChar != LOGICAL_OR) {
				word = "";
				weight += increment;
				jsonArray.put("OR");
			} else {
				word = "";
			}
		}
	}

	public void detectBracketClose() {
		if (currentChar == BRACKET_CLOSE) {
			prevDetectedKeyword = detectedKeyword;
			detectedKeyword = "";
//            if(prevDetectLogic != null && !prevDetectLogic.equals("")){
//                jsonArray.put(prevDetectLogic);
//                prevDetectLogic=null;
//            }
		}
	}

	public void detectIterativeControlStructure() {
		/*
		 * detect for loop
		 */
		if (word.contains("for")) {
			weight += 2;
			word = "";
			detectedKeyword = "for";
			jsonArray.put("for");
		}
		if (word.contains("do")) {
			weight += 2;
			word = "";
			detectedKeyword = "do";
			jsonArray.put("do-while");
		}
		/*
		 * detect while loop
		 */
		if (word.contains("while") && (!detectedKeyword.contains("do") && (!prevDetectedKeyword.contains("do")))) {
			weight += 2;
			word = "";
			detectedKeyword = "while";
			jsonArray.put("while");
		}
	}

	public void detectCatchStatement() {
		if (word.contains("catch")) {
			weight += 1;
			word = "";
			detectedKeyword = "catch";
			jsonArray.put("catch");
		}
	}

	public void detectSwitch() {
		if (word.contains("switch")) {
			detectedKeyword = "switch";
			word = "";
		}
	}

	public void measureCtC() {
		while ((currentChar = readChar()) != -1) {

			line = line.concat(Character.toString((char) currentChar));
			if (currentChar == 10) {
				tempJSON.put("no", count);
				tempJSON.put("line", line.replace("  ", ""));
				line = "";
				tempJSON.put("tokens", jsonArray);
				tempArr.put(tempJSON);
				tempJSON = new JSONObject();
				jsonArray = new JSONArray();
				count++;
			}

			if (this.currentChar != SPACE) {
				word = word.concat(Character.toString((char) currentChar));
				detectConditionalControlStructure();
				detectBracketClose();
				if (detectedKeyword.equals("if")) {
					detectLogicalOperator(1);
				}
				detectIterativeControlStructure();

				if (detectedKeyword.equals("while") || detectedKeyword.equals("for") || detectedKeyword.equals("do"))
					detectLogicalOperator(2);
				detectCatchStatement();
				detectSwitch();
				if (prevDetectedKeyword.equals("switch") && word.contains("case")) {
					jsonArray.put("case");
					weight++;
				}

			} else
				word = "";
			prevChar = currentChar;
		}
	}

	public String getCodeInheritance() {

		BufferedReader reader;
		int ci = 0;
		int totalCi = 0;
		String comment = " ";

		try {
			reader = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/temp/" + this.file));

			String line = reader.readLine();

			int count = 1;
			inheritanceObj = new JSONObject();

			while (line != null) {

				if (this.file.contains(".java")) {
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
						if(line.contains(" extends ") || line.contains(" implements ") ) {
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

					count++;

				} else if (this.file.contains(".cpp")) {
					return line;
				}

			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		inheritanceObj.put("totalCi", totalCi);
		inheritanceObjArr.put(inheritanceObj);
		return inheritanceObjArr.toString();
	}

	public static boolean containsIgnoreCase(String str, String subString) {
		return str.toLowerCase().contains(subString.toLowerCase());
	}

	public String get() {
		json = new JSONObject();
		json.put("code", tempArr);
		json.put("weight", weight);
		return json.toString();
	}

}
