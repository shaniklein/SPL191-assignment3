package bgu.spl.net.impl.BGS.Messeges;

public class LoginRequest extends MessageRequest {
    private String  userName;
    private String password;
    private static short opcode=2;

    public LoginRequest(String userName, String password) {
        super(opcode);
        this.userName = userName;
        this.password = password;
    }


    public String getPassword() {
        return password;
    }

    public String getUserName() {
        return userName;
    }

}
