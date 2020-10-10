package bgu.spl.net.impl;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.ConnectionHandler;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionsImpl<T> implements Connections<T> {
    private ConcurrentHashMap<Integer,ConnectionHandler> connectionHandlers=new ConcurrentHashMap<>();


    public ConnectionsImpl() {

    }


    @Override
    public boolean send(int connectionId, T msg) {
        if(connectionHandlers.containsKey(connectionId)){
             connectionHandlers.get(connectionId).send(msg);
             return true;}

        return false;
    }

    @Override
    public void broadcast(T msg) {

        for (ConnectionHandler c : connectionHandlers.values()
                ) {
            c.send(msg);

        }

    }

    @Override
    public void disconnect(int connectionId) {
                connectionHandlers.remove(connectionId);

    }



    @Override
    public void connect(int connetcionId, ConnectionHandler connectionHandler) {
        connectionHandlers.put(connetcionId,connectionHandler);

    }


}
