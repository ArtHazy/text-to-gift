import java.io.*;
import java.util.ArrayList;



class Question {
    int number;
    String name;

    Question() {
        this.number = 0;
    }
}

public class TextToGift_JE {
    
    static boolean IsQuestion(String sIn) {
        if (sIn!=null){
            if (!sIn.equals("")) {
                return sIn.charAt(sIn.length() - 1) == '?' || sIn.charAt(sIn.length() - 1) == ':';
            }
        }
        return false;
        
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

    static void clearSpecialSymbols(BufferedReader inFile, BufferedWriter outFile) throws IOException {
        String line;
        char[] specialChars = {'~', '=', '#', '{', '}', ':'};

        while ((line = inFile.readLine()) != null) {
            line = removeBulletedLists(line);
            for (char c : specialChars) {
                ArrayList<Integer> locations = findLocation(line, c);
                while (!locations.isEmpty()) {
                    int index = locations.get(locations.size() - 1);
                    line = line.substring(0, index) + "\\" + line.substring(index);
                    locations.remove(locations.size() - 1);
                }
            }
            outFile.write(line.trim() + "\n");
        }
        
        System.out.println("Special symbols were cleared");
    }

    static String removeBulletedLists(String mystring) {
        String substring = mystring.substring(0, 3);
        if (substring.charAt(1) == ')' && substring.charAt(2) == ' ') {
            return mystring.substring(3);
        } else {
            return mystring;
        }
    }

    static void formGift(BufferedReader fIn, BufferedWriter fOut) throws IOException {
        int singleAnswerQuestionsCounter = 0;
        int multipleAnswerQuestionsCounter = 0;

        String sIn;
        Question bufQuestion = new Question();
        sIn = fIn.readLine();
        while (sIn != null && sIn!=null && !sIn.equals("")) {

            while (IsQuestion(sIn)) {
                bufQuestion.name = sIn;
                bufQuestion.number++;

                fOut.write("::Вопрос " + bufQuestion.number + "::" + bufQuestion.name + "{" + "\n");

                ArrayList<String> variants = new ArrayList<>();
                int correctVariants = 0;

                sIn = fIn.readLine();
                while (sIn != null && !IsQuestion(sIn) && sIn!=null && !sIn.equals("")) {
                    if (sIn.charAt(0) == '+') {
                        correctVariants++;
                    }
                    variants.add(sIn);
                    sIn = fIn.readLine();
                }

                switch (correctVariants) {
                    case 0:
                        System.out.println("Question " + bufQuestion.number + " has no correct answers!");
                        break;

                    case 1:
                        singleAnswerQuestionsCounter++;
                        while (!variants.isEmpty()) {
                            String bufString = variants.get(variants.size() - 1);
                            variants.remove(variants.size() - 1);

                            if (bufString.charAt(0) == '+') {
                                bufString = bufString.substring(1);
                                fOut.write("=" + bufString + "\n");
                            } else {
                                fOut.write("~" + bufString + "\n");
                            }
                        }
                        break;

                    default:
                        multipleAnswerQuestionsCounter++;
                        int allVariants = variants.size();
                        while (!variants.isEmpty()) {
                            String bufString = variants.get(variants.size() - 1);
                            variants.remove(variants.size() - 1);

                            if (bufString.charAt(0) == '+') {
                                bufString = bufString.substring(1);
                                fOut.write("~" + "%" + 100 / (float) correctVariants + "%" + bufString + "\n");
                            } else {
                                fOut.write("~" + "%" + "-" + 100 / ((float) allVariants - (float) correctVariants) + "%" + bufString + "\n");
                            }
                        }
                        break;
                }

                fOut.write("}" + "\n" + "\n");
            }
        }

        System.out.println("Gift was formated");
        System.out.println("Questions found overall: " + bufQuestion.number);
        System.out.println("1-answer questions found: " + singleAnswerQuestionsCounter);
        System.out.println("mult-answer questions found: " + multipleAnswerQuestionsCounter);
    }

    public static void main(String[] args) throws IOException {
        BufferedReader fInStep1 = new BufferedReader(new FileReader("~textIn.txt"));
        BufferedWriter fOutStep1 = new BufferedWriter(new FileWriter("buf.txt"));

        clearSpecialSymbols(fInStep1, fOutStep1);
        fInStep1.close();
        fOutStep1.close();

        BufferedReader fInStep2 = new BufferedReader(new FileReader("buf.txt"));
        BufferedWriter fOutStep2 = new BufferedWriter(new FileWriter("~giftOut.txt"));

        formGift(fInStep2, fOutStep2);

        fInStep2.close();
        fOutStep2.close();
    }
}
