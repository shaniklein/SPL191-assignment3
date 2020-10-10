package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.ConnectionsImpl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;

public class BlockingConnectionHandler<T> implements Runnable, ConnectionHandler<T> {
    private final BidiMessagingProtocol<T> protocol;
    private final MessageEncoderDecoder<T> encdec;
    private final Socket sock;
    private BufferedInputStream in;
    private BufferedOutputStream out;
    private final int id;
    private volatile boolean connected = true;
    private Connections connections ;

    public BlockingConnectionHandler(Socket sock, MessageEncoderDecoder<T> reader, BidiMessagingProtocol<T> protocol,int id,Connections connections) {
        this.sock = sock;
        this.encdec = reader;
        this.protocol = protocol;
        this.id=id;
        this.connections=connections;
        connections.connect(id,this);
        protocol.start(id,connections);

    }

    @Override
    public void run() {
        try (Socket sock = this.sock) { //just for automatic closing
            int read;

            in = new BufferedInputStream(sock.getInputStream());
            out = new BufferedOutputStream(sock.getOutputStream());
            while (!protocol.shouldTerminate() && connected && (read = in.read()) >= 0) {
                T nextMessage = encdec.decodeNextByte((byte) read);
                if (nextMessage != null)
                     protocol.process(nextMessage);

            }

        }
        catch (IOException ex) {
        }

    }

    @Override
    public void close() throws IOException {
        connected = false;
        sock.close();
    }

    @Override
    public void send(T msg) {
        try {
            byte[] msgToSend=encdec.encode(msg);
            out.write(msgToSend);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
