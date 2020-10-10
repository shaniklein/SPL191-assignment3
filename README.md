# SPL191-assignment3
My solution for assigmnet 3 in Systems Programming Course BGU University

Assignment Description
In this assignment I implemented a simple social network server and client. The
communication between the server and the client(s) performed using a binary
communication protocol. A registered user is abled to follow other users and post
messages.

The implementation of the server is based on the **Thread-Per-Client (TPC)** and **Reactor** servers taught in class. I implementded a server with pull and push notification. .

The first part of the assignment was to replace some of the current interfaces (pull notification) with new interfaces that will allow push notification.
Once the server implementation has been extended you I implemented  an example protocol, The BGS protocols that is explanied in [Assignment Specification](https://github.com/shaniklein/SPL191-assignment3/blob/main/Assignment%20Specifications.pdf) .

The Server is implemented in Java while the Client is implemented with C++
