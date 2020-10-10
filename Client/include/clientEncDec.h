//
// Created by shanikle@wincs.cs.bgu.ac.il on 1/1/19.
//
#ifndef BOOST_ECHO_CLIENT_CLIENTENCDEC_H
#define BOOST_ECHO_CLIENT_CLIENTENCDEC_H
#include <iostream>
#include <vector>
#include <thread>

class clientEncDec {


public:
    clientEncDec();
    virtual ~clientEncDec();
   std::vector<char> encode(std::string& msg);
   std::string decodeNextByte(char byte);

private:
    short bytesToShort(char *bytesArr);
    void shortToBytes(short num, char* bytesArr);
    int numOfbyte;
    int numOfZero;
    std::string outputToPrint;
    std::string tmp;
    char result[2];
    char opcode[2];
    char msgopcode[2];
    char byteNumOfUsers[2];
    char byteNumOfPost[2];
    char byteNumOfFollowing[2];
    char byteNumOfFollowers[2];
    char numOfUserArr[2];
    bool flag;
};
#endif //BOOST_ECHO_CLIENT_CLIENTENCDEC_H)

