package bgu.spl.net.impl.BGS.Messeges;

public class LogoutRequest extends MessageRequest {
    private static short opcode=3;
    public LogoutRequest(){super(opcode);}
}
