package bgu.spl.net.impl.BGS.Messeges;

public class Notification extends MessageRequest {
    private static short opcode=9;
    private byte notificationType;
    private String postingUser;
    private String content;



    public Notification(String content, byte notificationType, String postingUser) {
        super(opcode);
        this.notificationType = notificationType;
        this.postingUser = postingUser;
        this.content = content;
    }

    public byte getNotificationType() {
        return notificationType;
    }

    public String getPostingUser() {
        return postingUser;
    }

    public String getContent() {
        return content;
    }
}

