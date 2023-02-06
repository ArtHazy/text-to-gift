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
    fstream fIn, fOut;
    fIn.open("textIn.txt");
    fOut.open("giftOut.txt", std::ofstream::out | std::ofstream::trunc);
    string sIn="lol"; // string In
    question qIn; // question in
    int plCount = 0; // plus's counter
    string answer;
    getline(fIn,sIn); // got 1-st line
    while (sIn[0]==' ') {sIn.erase(0,1);}// clear leading spaces
    while (sIn[sIn.length()-1] == ' ') {sIn.pop_back();} // clear trailing spaces

    while (sIn!="") { // while there are no empty strings
        if (sIn[sIn.length()-1] == '?' or sIn[sIn.length()-1] == ':' or sIn[sIn.length()-1] == '.') { // if found question text then type it out
            qIn.number++;
            qIn.name = sIn;
            if (qIn.number != 1) {fOut << "}" << endl << endl;} // if not first question
            fOut << "::Вопрос " << to_string(qIn.number) << "::" << qIn.name <<"{"<<endl;

        } else { // else it's answer
            if (sIn[0] == '+') { sIn.erase(0,1); fOut << "=" << sIn << endl; plCount++;} // if right answer then type it out
            else { fOut << "~" << sIn << endl;} // if wrong answer then type it out
        }
        getline(fIn,sIn);
        while (sIn[0]==' ') {sIn.erase(0,1);}// clear leading spaces
        while (sIn[sIn.length()-1] == ' ') {sIn.pop_back();} // clear trailing spaces
    }
    fOut << "}";
    cout << qIn.number << " questions found" << endl;
    cout << plCount << " correct answers found" << endl;
    return 0;
}
