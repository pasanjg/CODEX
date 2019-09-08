package com.sliit.spm.complexity;

import org.json.JSONObject;

public class Inheritance {

    private boolean java;
    private boolean cpp;
    private int ci = 0;
    private int totalCi;
    private String line;
    private String comment;

    public void setJava(boolean java) {
        this.java = java;
    }

    public void setCpp(boolean cpp) {
        this.cpp = cpp;
    }

    public int measure(String currentLine, String fileName ){
        int returnCi = 0;

        if (fileName.contains(".java")) {
            line = currentLine;
            returnCi = ci;
            boolean classLine = false;

            if(line.contains("class ")) {
                String tempString = line.toUpperCase();
                if(tempString.startsWith("CLASS ")) {
                    returnCi = 0;
                    classLine = true;
                }

                if(tempString.contains(" CLASS ")) {
                    returnCi = 0;
                    classLine = true;
                }
            }

            comment = "comment";
            if (line.contains("//")) {
                System.out.println(line.charAt(0));
                comment = line.substring(0, line.indexOf("//"));
            }

            //            inheritanceObj.put("line", line);


            if (classLine) {
                ci = 0;
            }

            if (line != null && line.matches(".*[a-zA-Z].*") && comment.matches(".*[a-zA-Z].*")) {
                if (classLine) {
                    ci = 0;
                }
                totalCi += ci;
                //                inheritanceObj.put("Ci", ci);

            }
            //            inheritanceObj.put("number", count);
            //            inheritanceObjArr.put(inheritanceObj);
            //            inheritanceObj = new JSONObject();

            if (line != null) {
                if (classLine) {
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
        }else if (fileName.contains(".cpp")) {
            line = currentLine;

            returnCi = ci;

            if(containsIgnoreCase(line, " main()")) {
                ci = 0;
                returnCi = 0;
            }

            comment = "comment";

            if (line.contains("//")) {
                System.out.println(line.charAt(0));
                comment = line.substring(0, line.indexOf("//"));
            }

//                inheritanceObj.put("line", line);

            if(!comment.matches(".*[a-zA-Z].*")) {
                ci = 0;
            }


            if (line != null && line.matches(".*[a-zA-Z].*") && comment.matches(".*[a-zA-Z].*")) {
                if (containsIgnoreCase(line, "class ") ) {
                    returnCi = 0;
                    ci = 0;
                }
                totalCi += ci;
//                    inheritanceObj.put("Ci", ci);
                System.out.println(totalCi);
            }
//
//                inheritanceObj.put("number", count);
//                inheritanceObjArr.put(inheritanceObj);
//                inheritanceObj = new JSONObject();

            if (line != null && line.matches(".*[a-zA-Z].*") && comment.matches(".*[a-zA-Z].*")) {

                if (containsIgnoreCase(line, "class ")) {
                    int ciCount = 2;

                    if(line.contains(":")) {
                        for (int i = 0; i < line.length(); i++) {
                            if (line.charAt(i) == ':') {
                                ciCount++;
                            }
                        }
                    }


                    ci = ciCount;
                }

            }

        }

//            count++;



        return returnCi;
    }

    public static boolean containsIgnoreCase(String str, String subString) {
        return str.toLowerCase().contains(subString.toLowerCase());
    }

    public int getTotalCi() {
        return this.totalCi;
    }

}