package bgu.spl.net.impl.BGS.Messeges;

import java.util.LinkedList;

public class FollowUnfollowRequest extends MessageRequest {
    private byte followUnfollow;
    private short NumOfUsers;
    private final LinkedList<String> UserNameList;
    private static short opcode=4;

    public FollowUnfollowRequest(byte followUnfollow, short numOfUsers, LinkedList<String> userNameList) {
        super(opcode);
        this.followUnfollow = followUnfollow;
        this.NumOfUsers = numOfUsers;

        this.UserNameList = userNameList;
    }

    public byte getFollowUnfollow() {
        return followUnfollow;
    }


    public short getNumOfUsers() {
        return NumOfUsers;
    }

    public LinkedList<String> getUserNameList() {
        return UserNameList;
    }
}

