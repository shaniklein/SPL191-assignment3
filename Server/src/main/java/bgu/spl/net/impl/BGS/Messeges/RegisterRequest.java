package bgu.spl.net.impl.BGS.Messeges;

import java.util.ArrayList;
import java.util.LinkedList;

public class RegisterRequest extends MessageRequest {
    private String userName;
    private String password;
    private static short opcode=1;

    public RegisterRequest(String userName,String _password) {
        super(opcode);
        this.password=_password;
     this.userName=userName;
    }

    public String getPassword() {
        return password;
    }

    public String getUserName() {
        return userName;
    }

}
