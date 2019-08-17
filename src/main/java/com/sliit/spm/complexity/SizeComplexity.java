package com.sliit.spm.complexity;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;


public class SizeComplexity {

	private int cs = 0;
	private File file;
	private String readLine;
	private String detectedWord;
	private int count;

	private JSONObject json;
    private JSONObject tempJSON;
    private JSONArray tempArray;
    private JSONArray tokenArray;

	int lineNo = 1;

	public SizeComplexity(File file) {
		this.file = file;
		this.tempArray = new JSONArray();
	}

	public void setFile(File file) {
		this.file = file;
	}

	public File getFile() {
		return this.file;
	}

	public String getSizeComplexity() {
		JSONObject json = new JSONObject();
		json.put("code",tempArray);
		json.put("cs",this.cs);
		return json.toString();
	}

	public String getReadLine() {
		return readLine;
	}

	public void setReadLine(String readline) {
		this.readLine = readline;
	}

	public String calculateTotalSizeComplexity() throws FileNotFoundException {

		Scanner scanner = new Scanner(this.getFile());
		String fileExt = file.getName().substring(file.getName().lastIndexOf("."));
		
		json = new JSONObject();
        

		while (scanner.hasNext()) {

			this.readLine = scanner.nextLine();
			String currentLine = this.readLine.trim();
			String[] skipKeys = { "/", "*" };
			
			this.tokenArray = new JSONArray();
			this.tempJSON = new JSONObject();

			this.count = 0;

			if (canSkip(currentLine, skipKeys)) {
				continue;
			}

			if (fileExt.equals(".cpp")) {
				this.sizeOfRefAndDeref(currentLine);
			}

			this.weightTwoKeywords(currentLine);
			this.sizeOfArithmeticOperators(currentLine);
			this.sizeOfRelationOperators(currentLine);
			this.sizeOfLogicalOperators(currentLine);
			this.sizeOfBitwiseOperators(currentLine);
			this.sizeOfMiscellaneousOperators(currentLine);
			this.sizeOfAssignmentOperators(currentLine);
			this.sizeOfKeywords(currentLine);
			this.sizeOfManipulators(currentLine);
			this.sizeOfQuotes(currentLine);
			this.sizeOfClasses(currentLine);
			this.sizeOfMethods(currentLine);
			this.sizeOfObjects(currentLine);
			this.sizeOfVariables(currentLine);
			this.sizeOfArrays(currentLine);
			
			
			this.tempJSON.put("tokens", this.tokenArray);
			this.tempJSON.put("no", this.lineNo);
			this.tempJSON.put("line", this.readLine);

			this.tempArray.put(this.tempJSON);

			this.lineNo++;
			
		}

		return this.getSizeComplexity();
	}

	public void sizeOfRefAndDeref(String currentLine) {

		String[] words = currentLine.split(" ");

		for (int i = 0; i < words.length; i++) {
			if (Pattern.matches("^&[a-zA-z]+\\w+", words[i]) || Pattern.matches("^[*][a-zA-z]+\\w+", words[i])) {
				this.cs += 2;
			}
		}
	}

	public void weightTwoKeywords(String currentLine) {
		String[] keys = { "new", "delete", "throw", "throws" };
		this.cs += this.countDuplicateKeywords(currentLine, keys) * 2;
	}

	public void sizeOfArithmeticOperators(String currentLine) {
		String[] keys = { "+", "-", "/", "*", "%", "++", "--" };
		String[][] regex = 
				{ { "^[a-zA-z]+\\w+", "^[0-9]+" }, 
				{ "^[a-zA-z]+\\w+", "^[0-9]+" },
				{ "^[a-zA-z]+\\w+", "^[0-9]+" }, 
				{ "^[a-zA-z]+\\w+", "^[0-9]+" }, 
				{ "^[a-zA-z]+\\w+", "^[0-9]+" },
				{ "^[a-zA-z]+\\w+", "^[0-9]+" }, 
				{ "^[a-zA-z]+\\w+", "^[0-9]+" } };
		this.cs += this.countArithmeticOperators(currentLine, keys, regex);
	}

	public void sizeOfRelationOperators(String currentLine) {
		String[] keys = { "==", "!=", ">", "<", ">=", "<=" };
		this.cs += this.countDuplicateOperators(currentLine, keys);
	}

	public void sizeOfLogicalOperators(String currentLine) {
		String[] keys = { "&&", "||", "!" };
		this.cs += this.countDuplicateOperators(currentLine, keys);
	}

	public void sizeOfBitwiseOperators(String currentLine) {
		String[] keys = { "|", "^", "~", "<<<", ">>>", ">>", "<<" };
		this.cs += this.countDuplicateOperators(currentLine, keys);
	}

	public void sizeOfMiscellaneousOperators(String currentLine) {
		String[] keys = { ",", "->", ".", "::" };
		this.cs += this.countDuplicateOperators(currentLine, keys);
	}

	public void sizeOfAssignmentOperators(String currentLine) {
		String[] keys = { "+=", "-=", "*=", "/=", "=", ">>>=", "|=", "&=", "%=", "<<=", ">>=", "^=" };
		this.cs += this.countDuplicateOperators(currentLine, keys);
	}

	public void sizeOfKeywords(String currentLine) {
		String[] keys = { "void", "double", "int", "float", "String", "printf", "println", "cout", "cin", "if", "for",
				"while", "do", "do", "switch", "case" };
		this.cs += this.countDuplicateKeywords(currentLine, keys);
	}

	public void sizeOfManipulators(String currentLine) {
		String[] keys = { "endl", "\n" };
		this.cs += this.countDuplicateOperators(currentLine, keys);
	}
	
	public void sizeOfQuotes(String currentLine) {
		this.cs += this.textInQuotes(currentLine);
	}
	
	public void sizeOfClasses(String currentLine) {
		this.cs += this.countClasses(currentLine);
	}
		
	public void sizeOfMethods(String currentLine) {
		this.cs += this.countMethods(currentLine);
	}
	
	public void sizeOfObjects(String currentLine) {
		this.cs += this.countObjects(currentLine);
	}
	
	public void sizeOfVariables(String currentLine) {
		this.cs += this.countVariables(currentLine);
	}
	
	public void sizeOfArrays(String currentLine) {
		this.cs += this.countArrays(currentLine);
	}
	
	public void sizeOfNumbers(String currentLine) {
		this.cs += this.countNumbers(currentLine);
	}


	public int countDuplicateKeywords(String currentLine, String[] keys) {

		String[] words = currentLine.split(" ");

		int count = 0;

		for (int i = 0; i < words.length; i++) {
			for (int j = 0; j < keys.length; j++) {
				if (words[i].contains(keys[j])) {
					count++;
					this.tokenArray.put(keys[j]);
					System.out.println(this.lineNo + "| " + this.readLine + "\t\t token: [" + keys[j] + "] count: " + count + "[Cs: " + this.cs + "]");
				}
			}
		}

		return count;
	}

	public int countDuplicateOperators(String currentLine, String[] keys) {

		String[] words = currentLine.split(" ");

		int count = 0;

		for (int i = 0; i < words.length; i++) {
			for (int j = 0; j < keys.length; j++) {
				if (words[i].equals(keys[j])) {
					count++;
					this.tokenArray.put(keys[j]);
					System.out.println(this.lineNo + "| " + this.readLine + "\t\t token: [" + keys[j] + "] count: " + count + "[Cs: " + this.cs + "]");
				} else {
					if (words[i].contains(keys[j])) {
						String[] tempWord = words[i].split(Pattern.quote(keys[j]));
						count += (tempWord.length - 1);
						this.tokenArray.put(keys[j]);
						System.out.println(this.lineNo + "| " + this.readLine + "\t\t token: [" + keys[j] + "] count: " + count + "[Cs: " + this.cs + "]");
					}
				}
			}
		}

		return count;
	}

	public int countArithmeticOperators(String currentLine, String[] keys, String[][] regex) {

		String[] words = currentLine.split(" ");

		int count = 0;

		for (int i = 0; i < words.length; i++) {
			for (int j = 0; j < keys.length; j++) {
				if (words[i].equals(keys[j])) {
					if (isValidKey(regex, words[i - 1])) {
						this.detectedWord = words[i];
						count++;
						this.tokenArray.put(keys[j]);
						System.out.println(this.lineNo + "| " + this.readLine + "\t\t token: [" + keys[j] + "] count: " + count + "[Cs: " + this.cs + "]");
					}
					else if (isValidKey(regex, words[i + 1])) {
						this.detectedWord = words[i];
						count++;
						this.tokenArray.put(keys[j]);
						System.out.println(this.lineNo + "| " + this.readLine + "\t\t token: [" + keys[j] + "] count: " + count + "[Cs: " + this.cs + "]");
					}
				} else {
					if (words[i].contains(keys[j])) {
						String[] tempWord = words[i].split(Pattern.quote(keys[j]));
						this.detectedWord = words[i];
						count += (tempWord.length - 1);
						this.tokenArray.put(keys[j]);
						System.out.println(this.lineNo + "| " + this.readLine + "\t\t token: [" + keys[j] + "] count: " + count + "[Cs: " + this.cs + "]");
					}
				}
			}
		}

		return count;
	}
	
	public int textInQuotes(String currentLine) {
		
		int count = 0;
		
		Pattern p = Pattern.compile("\"([^\"]*)\"");
		Matcher m = p.matcher(currentLine);
		while (m.find()) {
			count++;
			this.tokenArray.put(m.group(1));
			System.out.println(this.lineNo + "| " + this.readLine + "\t\t token: [" + m.group(1) +"] count: " + count + "[Cs: " + this.cs + "]");
		}
		
		return count;
	}
	
	public int countClasses(String currentLine) {
		
		String[] words = currentLine.split(" ");
		
		int count = 0;
		
		for (int i = 0; i < words.length; i++) {
			if ((i + 1) < words.length) {
				if (words[i].equals("class")) {
					count++;
					this.tokenArray.put(words[i + 1]);
					System.out.println(this.lineNo + "| " + this.readLine + "\t\t token: [" + words[i + 1] + "] count: " + count + "[Cs: " + this.cs + "]");
				}
			}
		}
		
		return count;
	}
	
	public int countMethods(String currentLine) {
		
		String[] words = currentLine.split(" ");
		
		int count = 0;
		
		for (int i = 0; i < words.length; i++) {
			if ((i + 1) < words.length) {
				if (Pattern.matches("[a-zA-Z0-9_]*\\s[a-zA-Z0-9_]*\\(", words[i] + " " + words[i + 1])) {
					count++;
					this.tokenArray.put(words[i + 1]);
					System.out.println(this.lineNo + "| " + this.readLine + "\t\t token: [" + words[i + 1] + "] count: " + count + "[Cs: " + this.cs + "]");
				}
			}
		}
		
		return count;
	}
	
	public int countObjects(String currentLine) {
		
		String[] words = currentLine.split(" ");
		
		int count = 0;
		
		for (int i = 0; i < words.length; i++) {
			if ((i + 2) < words.length) {
				if (Pattern.matches("\\=\\s(new)\\s[a-zA-Z0-9_]*\\(", words[i] + " " + words[i + 1] + " " + words[i + 2])) {
					count++;
					this.tokenArray.put(words[i + 1]);
					System.out.println(this.lineNo + "| " + this.readLine + "\t\t token: [" + words[i - 1] + "] count: " + count + "[Cs: " + this.cs + "]");
				}
			}
		}
		
		return count;
	}
	
	public int countVariables(String currentLine) {
		
		String[] words = currentLine.split(" ");
		
		int count = 0;
		
		for (int i = 0; i < words.length; i++) {
			if ((i + 1) < words.length) {
				if (Pattern.matches("(int|String|float|double)\\s[a-zA-Z0-9_]*", words[i] + " " + words[i + 1])) {
					count++;
					this.tokenArray.put(words[i + 1]);
					System.out.println(this.lineNo + "| " + this.readLine + "\t\t token: [" + words[i + 1] + "] count: " + count + "[Cs: " + this.cs + "]");
				}
			}
		}
		
		return count;
	}
	
	public int countArrays(String currentLine) {
		
		String[] words = currentLine.split(" ");
		
		int count = 0;
		
		for (int i = 0; i < words.length; i++) {
			if ((i + 1) < words.length) {
				if (Pattern.matches("[a-zA-Z0-9_]*\\[]\\s[a-zA-Z0-9_]*", words[i] + " " + words[i + 1])) {
					count++;
					this.tokenArray.put(words[i + 1]);
					System.out.println(this.lineNo + "| " + this.readLine + "\t\t token: [" + words[i + 1] + "] count: " + count + "[Cs: " + this.cs + "]");
				}
			}
		}
		
		return count;
	}
	
	public int countNumbers(String currentLine) {
		
		String[] words = currentLine.split(" ");
		
		int count = 0;
		
		for (int i = 0; i < words.length; i++) {
			if (Pattern.matches("^[0-9]+", words[i])) {
				count++;
				this.tokenArray.put(words[i]);
				System.out.println(this.lineNo + "| " + this.readLine + "\t\t token: [" + words[i] + "] count: " + count + "[Cs: " + this.cs + "]");
			}
		}
		
		return count;
	}

	public boolean isValidKey(String regex[][], String previousOrAfter) {

		for (int k = 0; k < regex.length; k++) {
			for (int m = 0; m < regex[k].length; m++) {
				if (Pattern.matches(regex[k][m], previousOrAfter)) {
					return true;
				}
			}
		}
		
		return false;
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

}
