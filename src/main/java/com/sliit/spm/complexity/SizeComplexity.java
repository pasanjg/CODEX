package com.sliit.spm.complexity;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class SizeComplexity {

	int cs = 0;
	File file;

	int lineNo = 1;

	public SizeComplexity(File file) {
		this.file = file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public File getFile() {
		return this.file;
	}

	public int getSizeComplexity() {
		return this.cs;
	}

	public int calculateTotalSizeComplexity() throws FileNotFoundException {

		Scanner scanner = new Scanner(this.getFile());

		while (scanner.hasNext()) {

			String currentLine = scanner.nextLine().trim();
			String[] skipKeys = { "/", "*" };
			String fileExt = file.getName().substring(file.getName().lastIndexOf("."));

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

			this.lineNo++;
		}

		return this.getSizeComplexity();
	}

	public void sizeOfRefAndDeref(String currentLine) {

		currentLine = currentLine.toLowerCase();
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
		this.cs += this.countDuplicateOperators(currentLine, keys);
	}

	public void sizeOfRelationOperators(String currentLine) {
		String[] keys = { "==", "!=", ">", "<", ">=", "+<=" };
		this.cs += this.countDuplicateOperators(currentLine, keys);
	}

	public void sizeOfLogicalOperators(String currentLine) {
		String[] keys = { "&&", "||", "!" };
		this.cs += this.countDuplicateOperators(currentLine, keys);
	}

	public void sizeOfBitwiseOperators(String currentLine) {
		String[] keys = { "|", "^", "~", "<<", ">>", "<<<", ">>>" };
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
				"while", "do", "switch", "case" };
		this.cs += this.countDuplicateKeywords(currentLine, keys);
	}

	public void sizeOfManipulators(String currentLine) {
		String[] keys = { "endl", "\n" };
		this.cs += this.countDuplicateOperators(currentLine, keys);
	}

	public int countDuplicateKeywords(String currentLine, String[] keys) {

		currentLine = currentLine.toLowerCase();
		String[] words = currentLine.split(" ");

		int count = 0;

		for (int i = 0; i < words.length; i++) {
			for (int j = 0; j < keys.length; j++) {
				if (words[i].equals(keys[j])) {
					count++;
					System.out.println(this.lineNo + " | " + words[i] + " : cs - " + count + " >> keyword");
				}
			}
		}

		return count;
	}

	public int countDuplicateOperators(String currentLine, String[] keys) {

		currentLine = currentLine.toLowerCase();
		String[] words = currentLine.split(" ");

		int count = 0;

		for (int i = 0; i < words.length; i++) {
			for (int j = 0; j < keys.length; j++) {
				if (words[i].equals(keys[j])) {
					count++;
					System.out.println(this.lineNo + " | " + words[i] + " : cs - " + count + " >> operator");
				} else {
					if (words[i].contains(keys[j])) {
						String[] tempWord = words[i].split(Pattern.quote(keys[j]));
						count += (tempWord.length - 1);
						System.out.println(this.lineNo + " | " + words[i] + " : cs - " + count + " >> operator");
					}
				}
			}
		}

		return count;
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
