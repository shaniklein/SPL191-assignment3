package bgu.spl.net.impl.BGS.Messeges;

import java.util.ArrayList;
import java.util.Map;

public  class MessageRequest {
    private short opcode;

    public MessageRequest(short opcode){
        this.opcode=opcode;
    }

    public short getOpcode() {
        return opcode;
    }
}
