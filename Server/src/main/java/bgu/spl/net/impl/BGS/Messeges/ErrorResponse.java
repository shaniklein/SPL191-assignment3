package bgu.spl.net.impl.BGS.Messeges;

public class ErrorResponse extends MessageRequest {
    private static short opcode=11;
    private short msgOpc;

    public ErrorResponse(short msgOpc) {
        super(opcode);
        this.msgOpc=msgOpc;
    }

    public short msgOpc() {
        return msgOpc;
    }
}

