package com.example.fastgift;

import android.content.Context;
import android.widget.Toast;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
class Question {
    int number;
    String text;
    boolean isWriteDownQuestion;
    ArrayList<String>variants;
    int correctVariantsCounter;

    // ArrayList<String>correctVariants;
    // ArrayList<String>wrongVariants;

    Question() {
        this.number = 0;
        this.isWriteDownQuestion=false;
        this.correctVariantsCounter=0;
        this.variants=new ArrayList<>();
    }
}


public class TextToGift_JE {

    static boolean isQuestion(String sIn) {
        return sIn != null && !sIn.isEmpty() && (sIn.endsWith("?") || sIn.endsWith(":"));
    }

    static boolean isWriteDownQuestion(String sIn) {
        return isQuestion(sIn) && sIn.startsWith("+");
    }

    static boolean isChoiceQuestion(String sIn) {
        return isQuestion(sIn) && !sIn.startsWith("+");
    }

    static ArrayList<String> handleWriteDownQuestion(ArrayList<String> variants) {
        ArrayList<String> sOut=new ArrayList<>();
        while (!variants.isEmpty()) {
            String bufString = variants.remove(variants.size() - 1);
            if (bufString.charAt(0) == '+') {
                bufString = bufString.substring(1);
            }
            sOut.add("="+bufString.trim());
        }
        return sOut;
    }

    static ArrayList<String> handleSingleChoiseQuestion(ArrayList<String> variants) {
        ArrayList<String> sOut=new ArrayList<>();
        while (!variants.isEmpty()) {
            String bufString = variants.remove(variants.size() - 1);

            if (bufString.charAt(0) == '+') {
                bufString = bufString.substring(1);
                sOut.add("="+bufString.trim());
            } else {
                sOut.add("~"+bufString.trim());
            }
        }
        return sOut;
    }

    static ArrayList<String> handleMultipleChoiseQuestion(ArrayList<String> variants, int correctVariantsCounter) {
        ArrayList<String> sOut=new ArrayList<>();

        int variantsOverall = variants.size();
        while (!variants.isEmpty()) {
            String bufString = variants.remove(variants.size() - 1);

            double points;
            DecimalFormat pointsFormat = new DecimalFormat("0.###");

            if (bufString.charAt(0) == '+') {
                bufString = bufString.substring(1);
                points = (double) 100 / correctVariantsCounter;
                sOut.add("~%"+pointsFormat.format(points)+"%"+bufString.trim());
            } else {
                points = (100 / ((double) variantsOverall - (double) correctVariantsCounter));
                sOut.add("~%-"+pointsFormat.format(points)+"%"+bufString.trim());
            }
        }
        return sOut;
    }


    static ArrayList<Integer> findLocation(String sample, char findIt) {
        ArrayList<Integer> characterLocations = new ArrayList<>();
        for (int i = 0; i < sample.length(); i++) {
            if (sample.charAt(i) == findIt) {
                characterLocations.add(i);
            }
        }
        return characterLocations;
    }

    static ArrayList<String> cleanUp(String[] linesIn) {
        ArrayList<String>sOut=new ArrayList<>();
        char[] specialChars = {'~', '=', '#', '{', '}', ':'};

        for (String line : linesIn) {
            line = line.trim();
            if (!(line.length() >= 2 && line.substring(0, 2).equals("//"))) { // ignore if starts with //
                if (line.length()!=0) { // if line isn't empty
                    line = removeBulletedLists(line);
                    for (char c : specialChars) {
                        ArrayList<Integer> locations = findLocation(line, c);
                        while (!locations.isEmpty()) {
                            int index = locations.get(locations.size() - 1);
                            if (line.charAt(index - 1) != '\\') {
                                line = line.substring(0, index) + "\\" + line.substring(index);
                            }
                            locations.remove(locations.size() - 1);
                        }
                    }
                    if (",;.".indexOf(line.charAt(line.length() - 1)) != -1) {
                        try {
                            line = line.substring(0, line.length() - 1);
                        } catch (Exception e) {
                            // Handle exception
                        }
                    }
                    sOut.add(line.trim());
                }
            }
        }
        System.out.println("Special symbols were cleared");
        return sOut;
    }


    static String removeBulletedLists(String mystring) {
        try {
            String substring = mystring.substring(0, 3);
            if (substring.charAt(1) == ')') {
                return mystring.substring(2);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return mystring;

    }



    static ArrayList<String> formGift(String[] lines) {
        ArrayList<String>linesIn=cleanUp(lines);
        ArrayList<String>sOut=new ArrayList<>();

        int singleChoiceCount = 0, multipleChoicesCount = 0, writeDownCount = 0, questionCounter = 0;

        while (linesIn.size()!=0){
            String line=linesIn.get(0);
            linesIn.remove(0);

            if (line.equals("")) {continue;}; // Skip empty lines


            if (isQuestion(line)) {
                Question bufQuestion = new Question();
                questionCounter++;

                if (line.charAt(0)=='+'){bufQuestion.isWriteDownQuestion=true;line=line.substring(0);}

                bufQuestion.text = line;
                bufQuestion.number = questionCounter;


                while (linesIn.size()!=0 && !linesIn.get(0).equals("") && !isQuestion(linesIn.get(0))) {
                    line=linesIn.get(0);
                    linesIn.remove(0);

                    if (line.charAt(0)=='+'){bufQuestion.correctVariantsCounter++;}
                    bufQuestion.variants.add(line);

                }

                sOut.add("::Вопрос "+bufQuestion.number+"::"+bufQuestion.text+"{");

                if (bufQuestion.isWriteDownQuestion){
                    writeDownCount++;
                    sOut.addAll(handleWriteDownQuestion(bufQuestion.variants));
                }
                if (!bufQuestion.isWriteDownQuestion){
                    switch (bufQuestion.correctVariantsCounter) {
                        case 0:
                            System.out.println("Question " + bufQuestion.number + " has no correct answers!");
                            break;

                        case 1:
                            singleChoiceCount++;
                            sOut.addAll(handleSingleChoiseQuestion(bufQuestion.variants));
                            break;
                        default:
                            multipleChoicesCount++;
                            sOut.addAll(handleMultipleChoiseQuestion(bufQuestion.variants,bufQuestion.correctVariantsCounter));
                    }
                }

                sOut.add("}\n");
            }
        }


        return sOut;
    }


    public static String main(String textIn) throws IOException {

        String[] textInStrings = textIn.split("\n");

        ArrayList<String> gift = formGift(textInStrings);

        String finalResultString="";
        for (String string : gift) {
            finalResultString+=string+"\n";
        }


        return(finalResultString);
    }
}
