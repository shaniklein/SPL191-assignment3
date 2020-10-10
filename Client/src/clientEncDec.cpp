//
// Created by shanikle@wincs.cs.bgu.ac.il on 1/1/19.
#include <clientEncDec.h>
#include <sstream>
#include <cstring>

clientEncDec::clientEncDec():
numOfbyte(0),numOfZero(0),outputToPrint(""),tmp(""),
flag(false) {
 }
clientEncDec::~clientEncDec() {

};
std::vector<char> clientEncDec::encode(std::string& msg) {
//    char *result;
    std::vector<char> output;


    if (msg.find("REGISTER") != std::string::npos || msg.find("LOGIN") != std::string::npos) {

        std::string username;
        std::string parts;
        std::string password;
        std::istringstream input_String(msg);
        int j = 1;
        while (std::getline(input_String, parts, ' ')) {
            // j==1 - reading the opcode --> continue
            if (j == 1) {
                j = 2;
                continue;
            }
                //j==2 - reading the user name
            else if (j == 2) {
                username = parts;
                j++;
                continue;
            }
                //j==3 - reading the password
            else {
                password = parts;
            }
        }

        if (msg.find("REGISTER") != std::string::npos)
            shortToBytes(1, opcode);
        else
            shortToBytes(2, opcode);

        output.push_back(opcode[0]);
        output.push_back(opcode[1]);
        for (int i = 0; (unsigned)i < username.length(); i++) {
            output.push_back(username[i]);
        }
        output.push_back('\0');
        for (int i = 0;(unsigned) i < password.length(); i++) {
            output.push_back(password[i]);
        }

        output.push_back('\0');
        return output;
    }


    if (msg.find("LOGOUT") != std::string::npos) {
//        result = new char[2];
        shortToBytes(3, result);
        output.push_back(result[0]);
        output.push_back(result[1]);
        return output;

    }
    if (msg.find("FOLLOW") != std::string::npos) {
        std::string parts;
        int numOfUsers = 0;
        std::string users;
        char followUnfollow = -1;
        std::istringstream input_String(msg);

        int j = 1;
        std::string userstoAdd;

        while (std::getline(input_String, parts, ' ')) {
            // j==1 - reading the opcode --> continue
            if (j == 1) {
                j = 2;
                continue;
            }
                //j==2 - reading the follow un follow bytee
            else if (j == 2) {
                if(parts[0]=='0')
                    followUnfollow='\0';
                else
                    followUnfollow='\1';
                j++;
                continue;
            }
                //j==3 - reading the num of Users
            else if (j == 3) {
                numOfUsers = std::stoi(parts, nullptr, 10);
                j++;
                continue;
            } else {
                //reading all users
                if (numOfUsers > 0) {
                    users+=parts+'\0';
                }
            }
        }
            shortToBytes(4, opcode);
            output.push_back(opcode[0]);
            output.push_back(opcode[1]);
            output.push_back(followUnfollow);
//            char *numOfUserArr = new char[2];
            shortToBytes((short) numOfUsers, numOfUserArr);
            output.push_back(numOfUserArr[0]);
            output.push_back(numOfUserArr[1]);
            for (int j = 0; (unsigned)j < users.size(); j++) {
                output.push_back(users.at(j));
           }


            return output;
        }


        if (msg.find("POST") != std::string::npos) {
            std::string parts;
            std::string content;
            std::istringstream input_String(msg);
            //first time it is the opcode
            std::getline(input_String, parts,' ');
            std::getline(input_String, parts);
            int partSize = (int) parts.length();
            shortToBytes(5, opcode);
            output.push_back(opcode[0]);
            output.push_back(opcode[1]);
            for (int i = 0; (unsigned) i < (unsigned)partSize; i++) {
                output.push_back(parts[i]);
            }
            output.push_back('\0');
            return output;
        }

        if (msg.find("PM") != std::string::npos) {
            std::string parts;
            std::string userName;
            std::istringstream input_String(msg);
            //first time it is the opcode
            std::getline(input_String, parts, ' ');
            std::getline(input_String, userName, ' ');
            std::getline(input_String, parts);
            int partSize = (int) parts.length();
//           output=new char[4+partSize+userName.length()];
            shortToBytes(6, opcode);
            output.push_back(opcode[0]);
            output.push_back(opcode[1]);
            for (int i = 0;(unsigned) i < userName.length(); i++)
                output.push_back(userName[i]);
            output.push_back('\0');
            for (int i = 0; (unsigned) i <(unsigned) partSize; i++)
                output.push_back(parts[i]);

            output.push_back('\0');

            return output;

        }
        if (msg.find("USERLIST") != std::string::npos) {
//            opcode = new char[2];
            shortToBytes(7, opcode);
            output.push_back(opcode[0]);
            output.push_back(opcode[1]);
           return output;
        }
        if (msg.find("STAT") != std::string::npos) {
            std::string userName;
            std::istringstream input_String(msg);
            //first time it is the opcode
            std::getline(input_String, userName, ' ');
            std::getline(input_String, userName, ' ');
            shortToBytes(8, opcode);
            output.push_back(opcode[0]);
            output.push_back(opcode[1]);
            for (int i = 0;(unsigned) i < userName.length(); i++)
                output.push_back(userName[i]);
            output.push_back('\0');


            return output;
        }

    return output;
    }


std::string clientEncDec::decodeNextByte(char byte) {
    if (numOfbyte < 2) {
        switch (numOfbyte) {
            case (0): {
                opcode[0] = byte;
                numOfbyte++;
                break;
            }
            case (1): {
                opcode[1] = byte;
                numOfbyte++;
                break;
            }

        }
        return "";
    }

    switch (bytesToShort(opcode)) {
        //---notification---
        case (9): {

            if (!flag) {
                outputToPrint = "";
                outputToPrint += "NOTIFICATION ";
                flag = true;
            }
            char notificationType;
            if (numOfbyte == 2) {
                notificationType = byte;
                if (notificationType == '\0')
                    outputToPrint += "PM ";
                else
                    outputToPrint += "Public ";

                numOfbyte++;
                return "";
            }

            if (byte == '\0') {
                numOfZero++;
                if (numOfZero == 1) {
                        outputToPrint += tmp + " ";
                         tmp = "";
                    return "";
                }
                if (numOfZero == 2) {
                    outputToPrint += tmp;
                    tmp = "";
                    numOfbyte = 0;
                    numOfZero = 0;
                    flag = false;
                    return outputToPrint;
                }
            } else {
                tmp.push_back(byte);
                return "";
            }
        }

            //--------ACK-------

        case (10): {
            if (!flag) {
                outputToPrint = "";
                outputToPrint += "ACK ";
                flag = true;
            }

            if (numOfbyte < 4) {
                switch (numOfbyte) {
                    case (2): {
                        msgopcode[0] = byte;
                        numOfbyte++;
                        return "";
                    }
                    case (3): {
                        msgopcode[1] = byte;
                        outputToPrint += std::to_string(bytesToShort(msgopcode));
                    }
                }

            }
            switch (bytesToShort(msgopcode)) {
                //follow and USERLIST else
                case (4):
                case (7): {
                    switch (numOfbyte) {
                        case (3): {
                            numOfbyte++;
                            return "";
                        }

                        case (4): {
                            byteNumOfUsers[0] = byte;
                            numOfbyte++;
                            return "";
                        }
                        case (5): {
                            byteNumOfUsers[1] = byte;
                            outputToPrint += " ";
                            numOfZero = bytesToShort(byteNumOfUsers);
                            outputToPrint += std::to_string(numOfZero);
                            outputToPrint += " ";
                            numOfbyte++;

                            return "";

                            default: {
                                if (byte != '\0') {
                                    outputToPrint += byte;
                                    return "";
                                    }
                                else {
                                    numOfZero--;
                                    if (numOfZero == 0) {
                                        numOfbyte = 0;
                                        flag = false;
                                        return outputToPrint;
                                    }
                                    else {
                                        outputToPrint += " ";
                                        return "";
                                    }
                                }

                            }
                        }
                    }
                }

                    //STAT
                case (8): {
                    switch (numOfbyte) {
                        case (3): {
                            numOfbyte++;
                            return "";
                        }
                        case (4): {
                            byteNumOfPost[0] = byte;
                            numOfbyte++;
                            return "";
                        }
                        case (5): {
                            byteNumOfPost[1] = byte;
                            numOfbyte++;
                            outputToPrint += " ";
                            outputToPrint += std::to_string(bytesToShort(byteNumOfPost));
                            outputToPrint += " ";
                            return "";
                        }
                        case (6): {
                            byteNumOfFollowers[0] = byte;
                            numOfbyte++;
                            return "";
                        }
                        case (7): {
                            byteNumOfFollowers[1] = byte;
                            numOfbyte++;
                            outputToPrint += std::to_string(bytesToShort(byteNumOfFollowers));
                            outputToPrint += " ";
                            return "";
                        }
                        case (8): {
                            byteNumOfFollowing[0] = byte;
                            numOfbyte++;
                            return "";

                        }
                            //case 9
                        default: {
                            byteNumOfFollowing[1] = byte;

                            numOfbyte = 0;
                            flag = false;
                            outputToPrint += std::to_string(bytesToShort(byteNumOfFollowing));

                            return outputToPrint;
                        }
                    }

                }

                    //normal ack
                default: {
                    numOfbyte = 0;
                    flag = false;
                    return outputToPrint;
                }

            }


        }

            //----ERROR---
        case (11): {
            if (numOfbyte < 4) {
                switch (numOfbyte) {
                    case (2): {
                        msgopcode[0] = byte;
                        numOfbyte++;
                        return "";
                    }
                    case (3): {
                        msgopcode[1] = byte;
                        outputToPrint = "ERROR " + std::to_string(bytesToShort(msgopcode));
                        numOfbyte = 0;
                        return outputToPrint;
                    }
                }

            }
        }
        default: {
            return "";
        }
    }
}




    short clientEncDec::bytesToShort(char *bytesArr) {
        short result = (short) ((bytesArr[0] & 0xff) << 8);
        result += (short) (bytesArr[1] & 0xff);
        return result;
    }

    void clientEncDec::shortToBytes(short num, char *bytesArr) {
        bytesArr[0] = ((num >> 8) & 0xFF);
        bytesArr[1] = (num & 0xFF);
    }

