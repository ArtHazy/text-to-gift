import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;



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

    static void handleWriteDownQuestion(ArrayList<String> variants, StringBuilder fOut) {
        while (!variants.isEmpty()) {
            String bufString = variants.remove(variants.size() - 1);
            if (bufString.charAt(0) == '+') {
                bufString = bufString.substring(1);
            }
            fOut.append("=").append(bufString.trim()).append("\n");
        }
    }
    
    static void handleSingleChoiseQuestion(ArrayList<String> variants, StringBuilder fOut) {
        while (!variants.isEmpty()) {
            String bufString = variants.remove(variants.size() - 1);
    
            if (bufString.charAt(0) == '+') {
                bufString = bufString.substring(1);
                fOut.append("=").append(bufString.trim()).append("\n");
            } else {
                fOut.append("~").append(bufString.trim()).append("\n");
            }
        }
    }
    
    static void handleMultipleChoiseQuestion(ArrayList<String> variants, int correctVariantsCounter, StringBuilder fOut) {
        int variantsOverall = variants.size();
        while (!variants.isEmpty()) {
            String bufString = variants.remove(variants.size() - 1);
    
            double points;
            DecimalFormat pointsFormat = new DecimalFormat("0.###");
    
            if (bufString.charAt(0) == '+') {
                bufString = bufString.substring(1);
                points = (double) 100 / correctVariantsCounter;
                fOut.append("~").append("%").append(pointsFormat.format(points)).append("%").append(bufString.trim()).append("\n");
            } else {
                points = (100 / ((double) variantsOverall - (double) correctVariantsCounter));
                fOut.append("~").append("%").append("-").append(pointsFormat.format(points)).append("%").append(bufString.trim()).append("\n");
            }
        }
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

    static void clearSpecialSymbols(String fInStep1, StringBuilder fOutStep1) {
        String[] lines = fInStep1.split("\n");
        char[] specialChars = {'~', '=', '#', '{', '}', ':'};
    
        for (String line : lines) {
            line = line.trim();
            if (!(line.length() >= 2 && line.substring(0, 2).equals("//"))) {
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
                if ((line.length() != 0) && (",;.".indexOf(line.charAt(line.length() - 1)) != -1)) {
                    try {
                        line = line.substring(0, line.length() - 1);
                    } catch (Exception e) {
                        // Handle exception
                    }
                }
                fOutStep1.append(line.trim()).append("\n");
            }
        }
    
        System.out.println("Special symbols were cleared");
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

    static void formGift(String fInStep2, StringBuilder fOutStep2) {
        int singleChoiceCount = 0, multipleChoicesCount = 0, writeDownCount = 0;
    
        String[] lines = fInStep2.split("\n");
        int questionCounter = 0;
    
        for (String sIn : lines) {
            if (sIn.equals("")) continue; // Skip empty lines
    
            if (isQuestion(sIn)) {
                Question bufQuestion = new Question();
                bufQuestion.text = sIn;
                questionCounter++;
                bufQuestion.number = questionCounter;
    
                if (isWriteDownQuestion(sIn)){
                    bufQuestion.isWriteDownQuestion = true;
                    sIn = sIn.substring(1);
                } else {
                    bufQuestion.isWriteDownQuestion = false;
                }
    
                fOutStep2.append("::Вопрос ").append(bufQuestion.number).append("::").append(bufQuestion.text).append("{").append("\n");
    
                int currentIndex = 0;
    
                while (currentIndex < lines.length && !isQuestion(lines[currentIndex]) && !lines[currentIndex].equals("")) {
                    String currentLine = lines[currentIndex];
                    if (currentLine.charAt(0) == '+') {
                        bufQuestion.correctVariantsCounter++;
                    }
                    bufQuestion.variants.add(currentLine);
    
                    currentIndex++;
                }
    
                if (bufQuestion.isWriteDownQuestion){
                    writeDownCount++;
                    handleWriteDownQuestion(bufQuestion.variants, fOutStep2);
                }
                if (!bufQuestion.isWriteDownQuestion){
                    switch (bufQuestion.correctVariantsCounter) {
                        case 0:
                            System.out.println("Question " + bufQuestion.number + " has no correct answers!");
                            break;
    
                        case 1:
                            singleChoiceCount++;
                            handleSingleChoiseQuestion(bufQuestion.variants, fOutStep2);
                            break;
                        default:
                            multipleChoicesCount++;
                            handleMultipleChoiseQuestion(bufQuestion.variants, bufQuestion.correctVariantsCounter, fOutStep2);
                    }
                }
    
                fOutStep2.append("}").append("\n").append("\n");
            }
        }
    
        System.out.println("Gift was formatted");
        System.out.println("Questions found overall: " + (singleChoiceCount + multipleChoicesCount + writeDownCount));
        System.out.println("1-answer questions found: " + singleChoiceCount);
        System.out.println("mult-answer questions found: " + multipleChoicesCount);
        System.out.println("Write down question found: " + writeDownCount);
    }
    

    public static String main(String textIn) throws IOException {
        // Simulate reading from a string
        

        String fInStep1 = textIn;
        StringBuilder fOutStep1 = new StringBuilder();

        // Call clearSpecialSymbols method
        clearSpecialSymbols(fInStep1, fOutStep1);

        String resultString = fOutStep1.toString(); // Save the result to a string

        // Simulate reading from a string
        String fInStep2 = resultString;
        StringBuilder fOutStep2 = new StringBuilder();

        // Call formGift method
        formGift(fInStep2, fOutStep2);

        String finalResultString = fOutStep2.toString(); // Save the final result to a string

        // Now you have the final result in the 'finalResultString' variable
        return(finalResultString);
    }
}
