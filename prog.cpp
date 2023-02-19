using namespace std;
#include <iostream>
#include <fstream>
#include <vector>

struct question{
    question() {
        this->number = 0;
    }
    int number;
    string name;
};

bool is_it_question(string sIn) {
    if (sIn[sIn.length()-1] == '?' or sIn[sIn.length()-1] == ':' /*or sIn[sIn.length()-1] == '.'*/) { // if is question
        return true;
    } else {
        return false;
    }

}

// stackoverflow // returns location indexes in string "sample" of chars "findIt"
vector<int> findLocation(string sample, char findIt) 
{
    vector<int> characterLocations;
    for(int i =0; i < sample.size(); i++)
        if(sample[i] == findIt)
            characterLocations.push_back(i);

    return characterLocations;
}

void step1_clear_special_symbols(fstream &fIn, fstream &fStep1) {
    string sIn="lol"; // string In
    question qIn; // question in
    int plCount = 0; // plus's counter
    vector<int> specialSymbolsLocationsInStringContainer;
    string answer;
    int stringCounter = 0; //!!!!!
    getline(fIn,sIn);
    stringCounter++;//!!!!!!
    
    while (sIn!="") {
        while (sIn[0]==' ') {sIn.erase(0,1);}// clear leading spaces
        while (sIn[sIn.length()-1] == ' ') {sIn.pop_back();} // clear trailing spaces

        specialSymbolsLocationsInStringContainer = findLocation(sIn,'~');
        while (specialSymbolsLocationsInStringContainer.size()!=0) {
            sIn.insert(specialSymbolsLocationsInStringContainer.back(),"\\");
            specialSymbolsLocationsInStringContainer.pop_back();
        }
        specialSymbolsLocationsInStringContainer = findLocation(sIn,'=');
        while (specialSymbolsLocationsInStringContainer.size()!=0) {
            //cout << stringCounter <<" "<< specialSymbolsLocationsInStringContainer.back() << endl; // for debug
            sIn.insert(specialSymbolsLocationsInStringContainer.back(),"\\");
            specialSymbolsLocationsInStringContainer.pop_back();
        }
        specialSymbolsLocationsInStringContainer = findLocation(sIn,'#');
        while (specialSymbolsLocationsInStringContainer.size()!=0) {
            sIn.insert(specialSymbolsLocationsInStringContainer.back(),"\\");
            specialSymbolsLocationsInStringContainer.pop_back();
        }
        specialSymbolsLocationsInStringContainer = findLocation(sIn,'{');
        while (specialSymbolsLocationsInStringContainer.size()!=0) {
            sIn.insert(specialSymbolsLocationsInStringContainer.back(),"\\");
            specialSymbolsLocationsInStringContainer.pop_back();
        }
        specialSymbolsLocationsInStringContainer = findLocation(sIn,'}');
        while (specialSymbolsLocationsInStringContainer.size()!=0) {
            sIn.insert(specialSymbolsLocationsInStringContainer.back(),"\\");
            specialSymbolsLocationsInStringContainer.pop_back();
        }
        specialSymbolsLocationsInStringContainer = findLocation(sIn,':');
        while (specialSymbolsLocationsInStringContainer.size()!=0) {
            sIn.insert(specialSymbolsLocationsInStringContainer.back(),"\\");
            specialSymbolsLocationsInStringContainer.pop_back();
        }
        // MADNESS ENDS
        
        fStep1 << sIn << endl;
        getline(fIn,sIn);
    }
    cout << "Step 1 is complete" << endl;
}

void step2_format_answers(fstream &fStep1, fstream &fOut) {
    int single_answer_questions_counter=0, multiple_answer_questions_counter=0;

    string sIn; // string in
    question buf_question;
    getline(fStep1,sIn);
    while (sIn!="") {
        // may be skiped if question is not first string
        
        while (is_it_question(sIn)) {
            buf_question.name = sIn;
            buf_question.number++;

            // print question name
            fOut << "::Вопрос " << to_string(buf_question.number) << "::" << buf_question.name <<"{"<<endl;

            vector<string> variants; // not formated
            int correct_variants=0;

            getline(fStep1,sIn);
            while (!is_it_question(sIn) and sIn!="") {
                if (sIn[0]=='+') {correct_variants++;}
                variants.push_back(sIn);
                getline(fStep1,sIn);
            }
            switch (correct_variants)
            {
                case 0:
                cout << "Question " << buf_question.number << " has no correct answers!";
                break;

                case 1:
                single_answer_questions_counter++;
                while (variants.size()!=0) {
                    string buf_string = variants[variants.size()-1];
                    variants.pop_back();
                    
                    if (buf_string[0] == '+') { // if right answer
                        buf_string.erase(0,1);
                        fOut << "=" << buf_string << endl;
                    } else { fOut << "~" << buf_string << endl;} // if wrong answer
                }
                break;
                default:
                multiple_answer_questions_counter++;
                int all_variants = variants.size();
                while (variants.size()!=0) {
                    string buf_string = variants[variants.size()-1];
                    variants.pop_back();

                    if (buf_string[0] == '+') { // if right answer
                        buf_string.erase(0,1);
                        fOut << "~" << "%" << 100/float(correct_variants) << "%" << buf_string << endl;
                    } else { fOut << "~" << "%" << "-" << 100/(float(all_variants)-float(correct_variants)) << "%" << buf_string << endl;} // if wrong answer
                }
                break;
            }
            fOut << "}" << endl << endl;
        }
    }
    cout << "Step 2 is complete:" << endl;
    cout << "Questions found overall: " << buf_question.number << endl;
    cout << "1-answer questions found: " << single_answer_questions_counter << endl;
    cout << "mult-answer questions found: " << multiple_answer_questions_counter << endl;
}


int main() {
    fstream fIn, fStep1, fOut;

    fIn.open("textIn.txt");
    fStep1.open("step1_clear_special_symbols.txt", std::ofstream::out | std::ofstream::trunc);
    fOut.open("giftOut.txt", std::ofstream::out | std::ofstream::trunc);

    step1_clear_special_symbols(fIn,fStep1);
    fStep1.close(); fStep1.open("step1_clear_special_symbols.txt"); // reopen
    step2_format_answers(fStep1,fOut);

    return 0;
}
