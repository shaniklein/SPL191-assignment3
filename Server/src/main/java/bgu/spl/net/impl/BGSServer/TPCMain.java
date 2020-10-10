package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.impl.BGS.Messeges.MessageRequest;
import bgu.spl.net.impl.ConnectionsImpl;
import bgu.spl.net.srv.BidiEncoderDecoder;
import bgu.spl.net.srv.BidiMessagingProtocolImpl;
import bgu.spl.net.srv.DataBase;
import bgu.spl.net.srv.Server;

import java.util.function.Supplier;

public class TPCMain {

    public static void main(String[] args) {
        DataBase dataBase=new DataBase();
        Supplier<MessageEncoderDecoder<MessageRequest>> encdec= BidiEncoderDecoder::new;
        Supplier<BidiMessagingProtocol<MessageRequest>> protocolSupplier= ()->new BidiMessagingProtocolImpl(dataBase);

        Server.threadPerClient(
                Integer.parseInt(args[0]), //port
                protocolSupplier, //protocol factory
                encdec, //message encoder decoder factory
                new ConnectionsImpl<MessageRequest>()

        ).serve();
    }

}