//
// Created by shanikle@wincs.cs.bgu.ac.il on 1/1/19.
//

#ifndef BOOST_ECHO_CLIENT_KEYBOARDTHREAD_H
#define BOOST_ECHO_CLIENT_KEYBOARDTHREAD_H


#include "connectionHandler.h"

class KeyboardHandler {
private:
    bool terminate;
    bool login;
public:

    KeyboardHandler();
    virtual ~KeyboardHandler();
    void run(ConnectionHandler& handler, clientEncDec& dec);

};


#endif //BOOST_ECHO_CLIENT_KEYBOARDTHREAD_H
