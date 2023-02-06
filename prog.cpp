using namespace std;
#include <iostream>
#include <fstream>

struct question{
    question() {
        this->number = 0;
    }
    int number;
    string name;
};
int main() {
    fstream fileIn, fileOut;
    fileIn.open("text.txt");
    fileOut.open("result.txt", std::ofstream::out | std::ofstream::trunc);
    string stringIn;
    question questionIn;

    string titleOut;
    string answer;
    getline(fileIn,stringIn); // got 1-st line

    while ( /*questionIn.number<2*/ stringIn!="end") { // while there are strings left // 2 - number of questions
        if (stringIn[stringIn.length()-1] == '?' or stringIn[stringIn.length()-1] == ':' or stringIn[stringIn.length()-1] == '.') { // if found question text then type it out
            questionIn.number++;
            questionIn.name = stringIn;
            fileOut << "}" << endl;
            fileOut << "::Вопрос " << to_string(questionIn.number) << "::" << questionIn.name <<"{"<<endl;

        } else { // else it's answer
            if (stringIn[0] == '+') { fileOut << "=" << stringIn << endl;} // if right answer then type it out
            else { fileOut << "∼" << stringIn << endl;} // if wrong answer then type it out
        }
        getline(fileIn,stringIn);
    }
    
    return 0;
}
