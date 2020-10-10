#include <stdlib.h>
#include <connectionHandler.h>
#include <clientEncDec.h>
#include <KeyboardHandler.h>
#include <thread>

/**
* This code assumes that the server replies the exact text the client sent it (as opposed to the practical session example)
*/
int main (int argc, char *argv[]) {
    bool connected = true;
    clientEncDec clientEncDec1;
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);

    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }
    KeyboardHandler keyboardHandler;
    //seperate to 2 threads- one for keyboard and one for the server this thread is to the server
    std::thread keyboardThread(&KeyboardHandler::run,&keyboardHandler, std::ref(connectionHandler),std::ref(clientEncDec1));


    //loop for messages from the server
    while (connected) {
        std::string answer = "";
        char nextByte[1];
        if (!connectionHandler.getBytes(nextByte, 1)) {
            std::cout << "Disconnected. Exiting..." << std::endl;
            break;

        }
        answer = clientEncDec1.decodeNextByte(nextByte[0]);
        if (answer.size() > 0) {
            std::cout << answer << std::endl;

            if (answer.find("ACK 3") != std::string::npos)
                connected = false;

        }
    }
    keyboardThread.join();
    return 0;
}