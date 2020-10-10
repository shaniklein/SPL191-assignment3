package bgu.spl.net.impl.BGS.Messeges;

public class StatRequest extends MessageRequest {
    private String userName;
    private static short opcode=8;


    public StatRequest(String userName) {
        super(opcode);
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }
}
