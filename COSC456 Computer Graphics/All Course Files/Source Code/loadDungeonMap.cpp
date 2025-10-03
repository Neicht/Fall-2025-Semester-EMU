/***

The purpose of this code is to demonstrate how to read a 2-d array of characters from a data
file.  The code is general: it will work for any input file; you don't have to know how long each string is,
or how many there will be.

Created by Matt Evett on 6/10/10
Copyright Matt Evett 2010.  All rights reserved.

***/
#include <iostream>
using namespace std;

#include <fstream>
#include <string>
#include <iterator>  // Needed for the ostream_iterator class (used with copy())
#include <vector>
#include <cstdlib>  // Needed for exit() to work properly

int main (int argc, char * const argv[]) {
    ifstream mapFile;
	mapFile.open("dungeon.dat");
	if (mapFile.fail()) {
		cout<<"Couldn't find file dungeon.dat";
		exit(1);
	}
	else
		cout<<"Success in opening map definition file\n";
		   
	string str;	
	vector<string> v;
	
	while (! mapFile.eof()) {
		getline(mapFile,str);
		if (str[str.size()-1] == '\n') // Note use of size() method to see how many chars are in each string
			str.erase(str.size()-1);  // Erase the trailing '\n', except in last line, where there isn't one
		v.push_back(str);
	}
		
    //The next two lines print the contents of the vector of strings, using an iterator.
	ostream_iterator<string> bob(cout,"\n");
    copy (v.begin(),v.end(), bob);
    		
	//The remaining code demonstrates what you can do with our array of strings
	//You can access the contents of the file as a 2-d array of chars
    cout<<"v[0][2]:"<<v[0][2]<<"v[1][2]:"<<v[1][2]<<endl;  //3rd char of 1st row, then 3rd of 2nd row
	char testChar = v[2][0];   // 1st char of 3rd row

	
	const char *oneLine = str.c_str();  //If you really want to use old-style C strings (0-terminated)
			//This code will generate it for you.
	
	cout << "Number of lines in the input file: " << v.size() << endl;
	
	return 0;
}
