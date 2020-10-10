package bgu.spl.net.api.bidi;

import bgu.spl.net.srv.ConnectionHandler;

import java.io.IOException;
import java.util.LinkedList;

public interface Connections<T> {
    boolean send(int connectionId, T msg);
    void broadcast(T msg);
    void disconnect(int connectionId);
    void connect(int connetcionId, ConnectionHandler connectionHandler);
}
