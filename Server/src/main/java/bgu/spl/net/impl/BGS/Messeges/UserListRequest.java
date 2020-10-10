package bgu.spl.net.impl.BGS.Messeges;

public class UserListRequest extends MessageRequest {
    private static short opcode=7;
   public UserListRequest(){
        super(opcode);
    }
}

