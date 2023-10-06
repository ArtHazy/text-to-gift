import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;



class Question {
    int number;
    String name;
    boolean isWriteDownQuestion;

    Question() {
        this.number = 0;
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

    static void handleWriteDownQuestion(ArrayList<String> variants, BufferedWriter fOut) throws IOException{
        while (!variants.isEmpty()) {
            String bufString = variants.get(variants.size() - 1);
            variants.remove(variants.size() - 1);
            if (bufString.charAt(0)=='+'){bufString=bufString.substring(1);}
            fOut.write("=" + bufString.trim() + "\n");
        }
    }

    static void handleSingleChoiseQuestion(ArrayList<String> variants, BufferedWriter fOut) throws IOException{
        while (!variants.isEmpty()) {
            String bufString = variants.get(variants.size() - 1);
            variants.remove(variants.size() - 1);

            if (bufString.charAt(0) == '+') {
                bufString = bufString.substring(1);
                fOut.write("=" + bufString.trim() + "\n");
            } else {
                 fOut.write("~" + bufString.trim() + "\n");
            }
        }
    }

    static void handleMultipleChoiseQuestion(ArrayList<String> variants, int correctVariants, BufferedWriter fOut) throws IOException{
        int variantsOverall = variants.size();
        while (!variants.isEmpty()) {
            String bufString = variants.get(variants.size() - 1);
            variants.remove(variants.size() - 1);

            double points = 0;
            DecimalFormat pointsFormat = new DecimalFormat("0.###");

            if (bufString.charAt(0) == '+') {
                bufString = bufString.substring(1);
                points = (double)100/correctVariants;
                fOut.write("~" + "%" + pointsFormat.format(points) + "%" + bufString.trim() + "\n");
            } else {
                points = (100 / ((double) variantsOverall - (double) correctVariants));
                fOut.write("~" + "%" + "-" + pointsFormat.format(points) + "%" + bufString.trim() + "\n");
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

    static void clearSpecialSymbols(BufferedReader inFile, BufferedWriter outFile) throws IOException {
        String line;
        char[] specialChars = {'~', '=', '#', '{', '}', ':'};

        while ((line = inFile.readLine()) != null) {
            line = line.trim();
            if (!(line.length()>=2 && line.substring(0, 2).equals("//"))){
                line = removeBulletedLists(line);
                for (char c : specialChars) {
                    ArrayList<Integer> locations = findLocation(line, c);
                    while (!locations.isEmpty()) {
                        int index = locations.get(locations.size() - 1);
                        line = line.substring(0, index) + "\\" + line.substring(index);
                        locations.remove(locations.size() - 1);
                    }
                }
                if ((line.length()!=0) && (",;.".indexOf(line.charAt(line.length()-1)) != -1)){
                    try {
                        line = line.substring(0, line.length()-1);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
                outFile.write(line.trim() + "\n");
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

    static void formGift(BufferedReader fIn, BufferedWriter fOut) throws IOException {
        int singleChoiceCount=0, multipleChoicesCount=0, writeDownCount=0;

        String sIn;
        sIn = fIn.readLine();

        Question bufQuestion = new Question();
        

        while (sIn != null && sIn!=null && !sIn.equals("")) {

            while (isQuestion(sIn)) {

                if (isWriteDownQuestion(sIn)){
                    bufQuestion.isWriteDownQuestion=true;
                    sIn = sIn.substring(1);
                } else {
                    bufQuestion.isWriteDownQuestion=false;
                }

                bufQuestion.name = sIn;
                bufQuestion.number++;

                fOut.write("::Вопрос " + bufQuestion.number + "::" + bufQuestion.name + "{" + "\n");

                ArrayList<String> variants = new ArrayList<>();
                int correctVariants = 0;

                sIn = fIn.readLine();

                while (sIn != null && !isQuestion(sIn) && sIn!=null && !sIn.equals("")) {
                    if (sIn.charAt(0) == '+') {
                        correctVariants++;
                    }
                    variants.add(sIn);

                    sIn = fIn.readLine();
                }

                if (bufQuestion.isWriteDownQuestion){
                    writeDownCount++;
                    handleWriteDownQuestion(variants,fOut);
                }
                if (!(bufQuestion.isWriteDownQuestion)){
                    switch (correctVariants) {
                    case 0:
                        System.out.println("Question " + bufQuestion.number + " has no correct answers!");
                        break;

                    case 1:
                        singleChoiceCount++;
                        handleSingleChoiseQuestion(variants,fOut);
                        break;
                    default:
                        multipleChoicesCount++;
                        handleMultipleChoiseQuestion(variants,correctVariants,fOut);
                }
                }

                fOut.write("}" + "\n" + "\n");
            }
        }

        System.out.println("Gift was formated");
        System.out.println("Questions found overall: " + bufQuestion.number);
        System.out.println("1-answer questions found: " + singleChoiceCount);
        System.out.println("mult-answer questions found: " + multipleChoicesCount);
        System.out.println("Write down question found: " + writeDownCount);
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
