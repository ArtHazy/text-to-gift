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

// stackoverflow
vector<int> findLocation(string sample, char findIt)
{
    vector<int> characterLocations;
    for(int i =0; i < sample.size(); i++)
        if(sample[i] == findIt)
            characterLocations.push_back(i);

    return characterLocations;
}
// end of stackoverflow


int main() {
    fstream fIn, fOut;
    fIn.open("textIn.txt");
    fOut.open("giftOut.txt", std::ofstream::out | std::ofstream::trunc);
    string sIn="lol"; // string In
    question qIn; // question in
    int plCount = 0; // plus's counter
    vector<int> specialSymbolsLocationsInStringContainer;
    string answer;
    int stringCounter = 0; //!!!!!
    getline(fIn,sIn);
    stringCounter++;//!!!!!!
    while (sIn[0]==' ') {sIn.erase(0,1);}// clear leading spaces
    while (sIn[sIn.length()-1] == ' ') {sIn.pop_back();} // clear trailing spaces

    // MADNESS STARTS!

    while (sIn!="") {
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

        if (sIn[sIn.length()-1] == '?' or sIn[sIn.length()-1] == ':' or sIn[sIn.length()-1] == '.') { // if it's question
            qIn.number++;
            qIn.name = sIn;
            if (qIn.number != 1) {fOut << "}" << endl << endl;} // if not first question
            fOut << "::Вопрос " << to_string(qIn.number) << "::" << qIn.name <<"{"<<endl;

        } else { // else it's answer
            if (sIn[0] == '+') { sIn.erase(0,1); fOut << "=" << sIn << endl; plCount++;} // if right answer
            else { fOut << "~" << sIn << endl;} // if wrong answer
        }
        getline(fIn,sIn);
        stringCounter++; //!!!!!!!!
        while (sIn[0]==' ') {sIn.erase(0,1);}// clear leading spaces
        while (sIn[sIn.length()-1] == ' ') {sIn.pop_back();} // clear trailing spaces
    }
    fOut << "}";
    cout << qIn.number << " questions found" << endl;
    cout << plCount << " correct answers found" << endl;
    return 0;
}
