package bgu.spl.net.impl.BGS.Messeges;

import java.util.ArrayList;
import java.util.LinkedList;

public class PMRequest extends MessageRequest {
    private static short opcode = 6;
    private String content;
    private String userName;

   public PMRequest(String userName,String content) {
        super(opcode);
        this.userName=userName;
        this.content=content;

    }

    public String getContent() {
        return content;
    }

    public String getUserName() {
        return userName;
    }
}



