package com.sliit.spm.complexity;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

public class SizeComplexity {

	private static String WEIGHT_TWO_KEYWORDS[] = { "new", "delete", "throws", "throw" };
	private static String WEIGHT_ONE_KEYWORDS[] = { "void", "double", "int", "float", "String", "string", "long",
			"short", "char", "boolean", "byte", "printf", "println", "cout", "cin", "if", "for", "while", "do",
			"switch", "case" };
	private static String MANIPULATORS[] = { "endl", "\\\\n" };

	private static String MISCELLANEOUS_OPERATORS[] = { ",", "->", ".", "::" };
	private static String LOGICAL_OPERATORS[] = { "&&", "||", "!" };
	private static String BITWISE_OPERATORS[] = { "|", "^", "~", "<<<", ">>>", ">>", "<<" };
	private static String RELATION_OPERATORS[] = { "==", "!=", ">", "<", ">=", "<=" };
	private static String ASSIGNMENT_OPERATORS[] = { "+=", "-=", "*=", "/=", "=", "|=", "&=", "%=", "<<=", ">>=",
			">>>=", "^=" };
	private static String ARITHMETIC_OPERATORS[] = { "++", "--", "/", "*", "%", "+", "-" };

	private static String DATATYPES[] = { "String", "string", "int", "double", "float", "long" };
	private static ArrayList<String> DECLARED_REFERENCES = new ArrayList<String>();
	private static ArrayList<String> DECLARED_DEREFERENCES = new ArrayList<String>();
	private static ArrayList<String> DECLARED_VARIABLES = new ArrayList<String>();
	private static ArrayList<String> DECLARED_ARRAYS = new ArrayList<String>();
	private static ArrayList<String> DECLARED_CLASSES = new ArrayList<String>();
	private static ArrayList<String> DECLARED_OBJECTS = new ArrayList<String>();

	private int cs = 0;
	private File file;
	private String readLine;

	private JSONObject json;
	private JSONObject tempJSON;
	private JSONArray tempArray;
	private JSONArray tokenArray;

	int lineNo = 1;

	public SizeComplexity() {
		this.tempArray = new JSONArray();
		this.tokenArray = new JSONArray();
	}

	public void setFile(File file) {
		this.file = file;
	}

	public File getFile() {
		return this.file;
	}

	public JSONArray getSizeComplexity() {
//		JSONObject json = new JSONObject();
//		json.put("tokens", this.tokenArray);
//		json.put("cs", this.cs);
		return this.tokenArray;
	}

	public int getComplexity() {
		return this.cs;
	}

	public String getReadLine() {
		return readLine;
	}

	public void setReadLine(String readline) {
		this.readLine = readline;
	}

	public int calculateTotalSizeComplexity(String currentLine, File file) throws FileNotFoundException {

		String fileExt = file.getName().substring(file.getName().lastIndexOf("."));

		json = new JSONObject();

		this.tokenArray = new JSONArray();
		this.tempJSON = new JSONObject();

//		this.tempJSON.put("line", currentLine);

		currentLine = currentLine.trim();

		String[] skipKeys = { "/", "*" };

		if (canSkip(currentLine, skipKeys)) {
			return 0;
		}

		if (fileExt.equals(".cpp")) {
			this.sizeOfRefAndDeref(currentLine);
			currentLine = removeRefAndDeref(currentLine);
		}

		this.sizeOfQuotes(currentLine);
		currentLine = this.removeTextInQuotes(currentLine);
		this.sizeOfMethods(currentLine);
		currentLine = this.removeMethods(currentLine);
		this.sizeOfVariables(currentLine);
		this.sizeOfArrays(currentLine);
		this.weightTwoKeywords(currentLine);
		this.sizeOfAllOperators(currentLine);
		this.sizeOfKeywords(currentLine);
		this.sizeOfManipulators(currentLine);
		this.sizeOfNumbers(currentLine);

		this.sizeOfClasses(currentLine);
		this.sizeOfObjects(currentLine);

		this.tempJSON.put("cs", this.cs);
		this.tempJSON.put("tokens", this.tokenArray);
		this.tempJSON.put("no", this.lineNo);

		this.tempArray.put(this.tempJSON);

		this.lineNo++;

		return this.cs;
	}

	public JSONArray getTempToken() {
		JSONArray array = this.tokenArray;
		this.tokenArray = new JSONArray();
		return array;
	}

	public void sizeOfRefAndDeref(String currentLine) {
		String regex[] = { "&[a-zA-Z]+\\w+", "[*][a-zA-Z]+\\w+" };
		this.cs += this.countRefAndDeref(currentLine, regex);
	}

	public void weightTwoKeywords(String currentLine) {
		this.cs += this.countDuplicateKeywords(currentLine, reArrangeKeywords(WEIGHT_TWO_KEYWORDS)) * 2;
	}

	public void sizeOfKeywords(String currentLine) {
		this.cs += this.countDuplicateKeywords(currentLine, reArrangeKeywords(WEIGHT_ONE_KEYWORDS));
	}

	public void sizeOfAllOperators(String currentLine) {
		String[] keys = this.allOperatorsConcatenated();
		this.cs += this.countSizeOfOperators(currentLine, keys);
	}

	public void sizeOfManipulators(String currentLine) {
		this.cs += this.countDuplicateKeywords(currentLine, MANIPULATORS);
	}

	public void sizeOfQuotes(String currentLine) {
		this.cs += this.countTextInQuotes(currentLine);
	}

	public void sizeOfMethods(String currentLine) {
		this.cs += this.countMethods(currentLine);
	}

	public void sizeOfVariables(String currentLine) {
		this.findDeclaredVariables(currentLine);
		this.cs += this.countVariables(currentLine);
	}

	public void sizeOfArrays(String currentLine) {
		this.findDeclaredArrays(currentLine);
		this.cs += this.countArrays(currentLine);
	}

	public void sizeOfNumbers(String currentLine) {
		this.cs += this.countNumbers(currentLine);
	}

	public void sizeOfClasses(String currentLine) {
		this.findDeclaredClassesAndObjects(currentLine);
		this.findCalledStaticClasses(currentLine);
		this.cs += this.countClasses(currentLine);
	}

	public void sizeOfObjects(String currentLine) {
		this.findCalledObjects(currentLine);
		this.cs += this.countObjects(currentLine);
	}

	public int countRefAndDeref(String currentLine, String[] regex) {

		String[] words = currentLine.split(" ");

		int count = 0;

		for (String word : words) {
			for (String exp : regex) {
				Pattern p = Pattern.compile(exp);
				Matcher m = p.matcher(word);
				while (m.find()) {
					count++;
					String c1 = m.group();
					this.tokenArray.put(c1);
				}
				currentLine = m.replaceAll(" ");
			}
		}

		return count;
	}

	public String removeRefAndDeref(String currentLine) {
		return Pattern.compile("&[a-zA-Z]+\\w+|[*][a-zA-Z]+\\w+").matcher(currentLine).replaceAll(" ");
	}

	public int countDuplicateKeywords(String currentLine, String[] keys) {

		int count = 0;

		for (String key : keys) {
			// Pattern p = Pattern.compile("([^a-zA-Z0-9])" + key + "([^a-zA-Z0-9])");
			Pattern p = Pattern.compile(key);
			Matcher m = p.matcher(currentLine);
			while (m.find()) {
				count++;
				String c1 = m.group();
				this.tokenArray.put(key);
			}
			currentLine = m.replaceAll(" ");
		}

		return count;
	}

	public int countSizeOfOperators(String currentLine, String[] keys) {

		int count = 0;

		for (String key : keys) {
			Pattern p = Pattern.compile(covertOpKeysToRegEx(key));
			Matcher m = p.matcher(currentLine);
			while (m.find()) {
				count++;
				String c1 = m.group();
				this.tokenArray.put(c1);
			}
			currentLine = m.replaceAll(" ");
		}

		return count;

	}

	public String covertOpKeysToRegEx(String key) {
		String keyRegEx = "";
		for (int i = 0; i < key.length(); i++) {
			keyRegEx += "\\" + key.charAt(i);
		}
		return keyRegEx;
	}

	public String[] reArrangeKeywords(String[] keys) {

		String temp = "";

		for (int i = 0; i < keys.length; i++) {
			for (int j = i + 1; j < keys.length; j++) {
				if (keys[i].length() < keys[j].length()) {
					temp = keys[i];
					keys[i] = keys[j];
					keys[j] = temp;
				}
			}
		}

		return keys;
	}

	public String[] allOperatorsConcatenated() {

		String allOperators[] = Stream.of(MISCELLANEOUS_OPERATORS, RELATION_OPERATORS, ASSIGNMENT_OPERATORS,
				LOGICAL_OPERATORS, BITWISE_OPERATORS, ARITHMETIC_OPERATORS).flatMap(Stream::of).toArray(String[]::new);

		// Sort to descending element size

		String temp = "";

		for (int i = 0; i < allOperators.length; i++) {
			for (int j = i + 1; j < allOperators.length; j++) {
				if (allOperators[i].length() < allOperators[j].length()) {
					temp = allOperators[i];
					allOperators[i] = allOperators[j];
					allOperators[j] = temp;
				}
			}
		}

		return allOperators;
	}

	public String[] allKeywordsConcatenated() {

		String allKeywords[] = Stream.of(WEIGHT_TWO_KEYWORDS, WEIGHT_ONE_KEYWORDS).flatMap(Stream::of)
				.toArray(String[]::new);

		return allKeywords;
	}

	public int countTextInQuotes(String currentLine) {

		int count = 0;

		Pattern p = Pattern.compile("\"([^\"]*)\"");
		Matcher m = p.matcher(currentLine);
		while (m.find()) {
			count++;
			this.tokenArray.put(m.group());
		}
		currentLine = m.replaceAll(" ");

		return count;
	}

	public String removeTextInQuotes(String currentLine) {
		return Pattern.compile("\"([^\"]*)\"").matcher(currentLine).replaceAll(" ");
	}

	public int countMethods(String currentLine) {

		int count = 0;

		Pattern p = Pattern.compile("[a-zA-Z]+\\w+\\(|[a-zA-Z]+\\w+\\s\\(");
		Matcher m = p.matcher(currentLine);
		while (m.find()) {
			count++;
			String c1 = m.group();
			c1 = removeLastChar(c1);
			this.tokenArray.put(c1);
		}
		currentLine = m.replaceAll(" ");

		return count;
	}

	public String removeMethods(String currentLine) {
		return Pattern.compile("[a-zA-Z]+\\w+\\(|[a-zA-Z]+\\w+\\s\\(").matcher(currentLine).replaceAll(" ");
	}

	public boolean isValidKey(String match, String skipKeys[]) {
		for (String key : skipKeys) {
			if (match.contains(key + "(") || match.contains(key + " (")) {
				return false;
			}
		}

		return true;
	}

	public String removeLastChar(String s) {
		return (s == null || s.length() == 0) ? null : (s.substring(0, s.length() - 1));
	}

	public int countNumbers(String currentLine) {

		currentLine = Pattern.compile("([a-zA-Z]+[0-9]+)|([0-9]+[a-zA-Z]+)").matcher(currentLine).replaceAll(" ");
		String[] words = currentLine.split(" ");

		int count = 0;

		for (String word : words) {
			Pattern p = Pattern.compile("^[0-9]+");
			Matcher m = p.matcher(word);
			while (m.find()) {
				count++;
				String c1 = m.group();
				this.tokenArray.put(c1);
			}
			word = m.replaceAll(" ");
		}

		return count;
	}

	public void findDeclaredVariables(String currentLine) {

		for (String dataType : DATATYPES) {
			Pattern p = Pattern.compile("(" + dataType + "\\s[\\w]+)");
			Matcher m = p.matcher(currentLine);
			while (m.find()) {
				String c1 = m.group();
				setVariables(c1.toString());
			}
			currentLine = m.replaceAll(" ");
		}
	}

	public void setVariables(String detection) {
		String words[] = detection.split(" ");
		DECLARED_VARIABLES.add(words[1]);
	}

	public String refineVariable(String variable, String word) {
		Pattern p = Pattern.compile(variable);
		Matcher m = p.matcher(word);
		if (m.find()) {
			return m.group().toString();
		}

		return null;
	}

	public String refineArray(String word) {
		Pattern p = Pattern.compile("[a-zA-Z0-9]+");
		Matcher m = p.matcher(word);
		if (m.find()) {
			return m.group().toString().trim();
		}

		return null;
	}

	public int countVariables(String currentLine) {

		int count = 0;

		for (String variable : DECLARED_VARIABLES) {
			Pattern p = Pattern.compile("([^a-zA-Z0-9])(" + variable + ")([^a-zA-Z0-9])");
			Matcher m = p.matcher(currentLine);
			while (m.find()) {
				count++;
				String c1 = m.group();
				c1 = refineVariable(variable, c1);
				this.tokenArray.put(c1);
			}
			currentLine = m.replaceAll(" ");
		}

		return count;
	}

	public void findDeclaredArrays(String currentLine) {

		Pattern p = Pattern.compile("([\\[\\]]+\\s+[a-zA-Z0-9]+)|([a-zA-Z0-9]+\\s+[\\[\\]]+)");
		Matcher m = p.matcher(currentLine);
		while (m.find()) {
			String c1 = m.group();
			if (isValidArray(c1)) {
				setArrays(refineArray(c1.toString()));
			}
		}
	}

	public boolean isValidArray(String word) {
		Pattern p = Pattern.compile("([a-zA-Z0-9]+)");
		Matcher m = p.matcher(word);
		while (m.find()) {
			if (word.contains(m.group())) {
				return true;
			}
		}

		return false;
	}

	public void setArrays(String detection) {
		DECLARED_ARRAYS.add(detection);
	}

	public int countArrays(String currentLine) {

		int count = 0;

		for (String array : DECLARED_ARRAYS) {
			Pattern p = Pattern.compile("([^\\w+])(" + array + ")([^\\w+])");
			Matcher m = p.matcher(currentLine);
			while (m.find()) {
				count++;
				String c1 = m.group();
				this.tokenArray.put(array);
			}
			currentLine = m.replaceAll(" ");
		}

		return count;
	}

	public int countClasses(String currentLine) {

		String[] words = currentLine.split(" ");

		int count = 0;

		for (String pclass : DECLARED_CLASSES) {
			// Pattern p = Pattern.compile("([^\\w+])(" + pclass + ")([^\\w+])");
			Pattern p = Pattern.compile(pclass);
			Matcher m = p.matcher(currentLine);
			while (m.find()) {
				count++;
				String c1 = m.group();
				this.tokenArray.put(pclass);
			}
			currentLine = m.replaceAll(" ");
		}

		return count;
	}

	public int countObjects(String currentLine) {

		int count = 0;

		for (String object : DECLARED_OBJECTS) {
			// Pattern p = Pattern.compile("([^\\w+])(" + object + ")([^\\w+])");
			Pattern p = Pattern.compile("([^\\w+])(" + object + ")([^\\w+])");
			Matcher m = p.matcher(currentLine);
			while (m.find()) {
				count++;
				String c1 = m.group();
				this.tokenArray.put(object);
			}
			currentLine = m.replaceAll(" ");
		}

		return count;
	}

	public void findDeclaredClassesAndObjects(String currentLine) {

		// Pattern p = Pattern.compile("([A-Z][a-zA-Z]+\\s[a-zA-Z0-9]+)"); // previous
		// regex
		Pattern p = Pattern.compile("([A-Z]+[a-zA-Z]*\\s[a-zA-Z0-9]+)");
		Matcher m = p.matcher(currentLine);
		while (m.find()) {
			String c1 = refineClassAndObject(m.group());
			System.out.println(c1);
			if (isValidClassAndObject((c1))) {
				setClassAndObject(c1.toString());
			}
		}
		currentLine = m.replaceAll(" ");

	}

	public void findCalledStaticClasses(String currentLine) {

		Pattern p2 = Pattern.compile("([A-Z]+[a-zA-Z]*\\.)");
		Matcher m2 = p2.matcher(currentLine);
		while (m2.find()) {
			String c2 = m2.group();
			System.out.println(c2);
			DECLARED_CLASSES.add(refineStaticClassesAndObjects(c2.toString()));

		}
		currentLine = m2.replaceAll(" ");
	}

	public void findCalledObjects(String currentLine) {

		Pattern p2 = Pattern.compile("([a-zA-Z][a-zA-Z0-9]*\\.)");
		Matcher m2 = p2.matcher(currentLine);
		while (m2.find()) {
			String c2 = m2.group();
			System.out.println(c2);
			DECLARED_OBJECTS.add(refineStaticClassesAndObjects(c2.toString()));

		}
		currentLine = m2.replaceAll(" ");
	}

	public boolean isValidClassAndObject(String detection) {

		String words[] = detection.split(" ");

		for (String key : allKeywordsConcatenated()) {
			if (words[0].equals(key) || words[1].equals(key)) {
				return false;
			}
		}

		return true;
	}

	public String refineClassAndObject(String detection) {

		String keyword = "";

		Pattern p = Pattern.compile("([A-Z]+[a-zA-Z]*\\s[a-zA-Z0-9]+)");
		Matcher m = p.matcher(detection);
		while (m.find()) {
			keyword = m.group();
		}
		return keyword;
	}

	public String refineStaticClassesAndObjects(String detection) {

		String keyword = "";

		Pattern p = Pattern.compile("([a-zA-Z0-9]+)");
		Matcher m = p.matcher(detection);
		while (m.find()) {
			keyword = m.group();
		}
		return keyword;
	}

	public void setClassAndObject(String detection) {

		String words[] = detection.split(" ");

		DECLARED_CLASSES.add(words[0]);
		DECLARED_OBJECTS.add(words[1]);
	}

	public boolean canSkip(String currentLine, String[] skipKey) {

		// String words[] = currentLine.split(" ");
		//
		// for(String word: words) {
		// if(word.contains("//")) {
		// return true;
		// }
		// }

		for (int i = 0; i < skipKey.length; i++) {
			if (currentLine.startsWith(skipKey[i])) {
				this.lineNo++;
				return true;
			}
		}

		return false;
	}

}
