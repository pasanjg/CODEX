package com.sliit.spm.complexity;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class SizeComplexity {

	int cs = 0;
	File file;
	
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
			String[] skipKeys = {"/", "*"};
			String fileExt = file.getName().substring(file.getName().lastIndexOf("."));
			
			if(canSkip(currentLine, skipKeys)) {
				continue;
			}
			
			System.out.println(currentLine);
			
			if(fileExt.equals(".cpp")) {
				this.refAndDeref(currentLine);
			}
			
			this.weightTwoKeywords(currentLine);
		}
		return this.getSizeComplexity();
	}
	
	public void refAndDeref(String currentLine) {
		
		currentLine = currentLine.toLowerCase();
		String[] words = currentLine.split(" ");

		for (int i = 0; i < words.length; i++) {				
				if(Pattern.matches("^&[a-zA-z]+\\w+", words[i]) || Pattern.matches("[*][a-zA-z]+\\w+", words[i])) {
					this.cs+=2;
				}
		}
	}
	
	public void weightTwoKeywords(String currentLine) {
		String[] keys = {"new", "delete", "throw", "throws"};
		this.cs += this.countDuplicateKeywords(currentLine, keys) * 2;
	}

	public int countDuplicateKeywords(String currentLine, String[] keys) {

		currentLine = currentLine.toLowerCase();
		String[] words = currentLine.split(" ");
		
		int count = 0;

		for (int i = 0; i < words.length; i++) {
			for (int j = 0; j < keys.length; j++){
				if (words[i].equals(keys[j])) {
					count++;
				}
			}		
		}
		
		return count;
	}
	
	public boolean canSkip(String currentLine, String[] skipKey) {
		for(int i = 0; i < skipKey.length; i++) {
			if(currentLine.startsWith(skipKey[i])) {
				return true;
			}
		}
		
		return false;
	}
	
}
