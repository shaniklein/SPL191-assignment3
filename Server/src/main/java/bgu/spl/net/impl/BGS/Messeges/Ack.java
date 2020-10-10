package bgu.spl.net.impl.BGS.Messeges;

import java.util.LinkedList;

public class Ack extends MessageRequest {
    private static short opcode=10;
    private short msgOpcode;

    //for FollowUnFollow
    private byte follow;
    private short numOfUsers;
    private LinkedList<String> userlist;
    //for STAT
    private short numOfPosts;
    private short numOfFollowers;
    private short numOfFollowing;


    public Ack(short msgOpcode) {
        super(opcode);
        this.msgOpcode=msgOpcode;

    }
    public Ack(short msgOpcode,byte follow, short numOfUsers, LinkedList<String> userlist) {
        super(opcode);
        this.follow=follow;
        this.numOfUsers=numOfUsers;
        this.userlist=userlist;
        this.msgOpcode=msgOpcode;
    }
    public Ack(short msgOpcode, short numOfUsers, LinkedList<String> userlist) {
        super(opcode);
        this.numOfUsers=numOfUsers;
        this.userlist=userlist;
        this.msgOpcode=msgOpcode;
    }

    public Ack(short msgOpcode,short numOfPosts, short numOfFollowers,short numOfFollowing) {
        super(opcode);
        this.msgOpcode=msgOpcode;
        this.numOfPosts=numOfPosts;
        this.numOfFollowers=numOfFollowers;;
        this.numOfFollowing=numOfFollowing;
    }

    public byte getFollow() {
        return follow;
    }

    public short getNumOfUsers() {
        return numOfUsers;
    }

    public LinkedList<String> getUserlist() {
        return userlist;
    }

    public short getMsgOpcode() {
        return msgOpcode;
    }

    public short getNumOfPosts() {
        return numOfPosts;
    }

    public short getNumOfFollowers() {
        return numOfFollowers;
    }

    public short getNumOfFollowing() {
        return numOfFollowing;
    }
}
