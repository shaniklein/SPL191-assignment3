package bgu.spl.net.impl.BGS.Messeges;


import java.util.ArrayList;
import java.util.LinkedList;

public class PostRequest extends MessageRequest {
    private static short opcode = 5;
    private String content;

    public PostRequest(String content) {
        super(opcode);
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}


