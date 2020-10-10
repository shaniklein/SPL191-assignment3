package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.impl.BGS.Messeges.*;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;


public class BidiEncoderDecoder implements MessageEncoderDecoder<MessageRequest> {

    private boolean newWorld=true;

    private LinkedList<Byte> message = new LinkedList<>();
    private byte[] opcodeInBytes = new byte[2];
    private int numOfZero = 0;
    private int indexOfFirstZero;
    //for login and register
    private String userName;
    private String passwordOrcontent;

    //for FollowUnfollowMessage
    private Byte followUnFollow = null;
    private byte[] numOfUsers = new byte[2];
    private LinkedList<String> usersList = new LinkedList<>();

    public BidiEncoderDecoder(){

    }

    @Override
    public MessageRequest decodeNextByte(byte nextByte) {
        //we read the opcode
        if (message.size() < 2) {
            message.add(nextByte);
            if (message.size() == 2) {
                opcodeInBytes[0] = message.get(0);
                opcodeInBytes[1] = message.get(1);

                //-------USERLIST-----------

                if (bytesToShort(opcodeInBytes) == 7 ) {
                    message.clear();
                    return new UserListRequest();
                }
                //--------------------- Log out ------------

                if (bytesToShort(opcodeInBytes) == 3) {
                    message.clear();
                    return new LogoutRequest();
                }
            }
            return null;

        } else { //we read 2 bytes and therefore can take the opcode
            switch (bytesToShort(opcodeInBytes)) {
                //-----------------register and log in --------------
                case (1):
                case (2):
                case (6): {
                    //we finished to read user name
                    if (nextByte == '\0' && numOfZero == 0) {
                        indexOfFirstZero = message.size();
                        byte[] bytesToString = new byte[message.size() - 2];
                        for (int i = 0; i < bytesToString.length; i++) {
                            bytesToString[i] = message.get(i + 2);
                        }
                        userName = new String(bytesToString, StandardCharsets.UTF_8);
                        numOfZero++;
                    }
                    //we finished to read the password
                    else if (nextByte == '\0' && numOfZero == 1) {
                        byte[] bytesToString = new byte[message.size() - indexOfFirstZero];
                        for (int i = 0; i < bytesToString.length; i++) {
                            bytesToString[i] = message.get(i + indexOfFirstZero);
                        }
                        passwordOrcontent = new String(bytesToString, StandardCharsets.UTF_8);

                        //we got all information for making the request
                        MessageRequest request;
                        //Register request
                        if (bytesToShort(opcodeInBytes) == 1) {
                            request = new RegisterRequest(userName, passwordOrcontent);
                        }
                        //Login request
                        else if (bytesToShort(opcodeInBytes) == 2)
                            request = new LoginRequest(userName, passwordOrcontent);
                        else //PM
                            request = new PMRequest(userName, passwordOrcontent);

                        //initialize to next messages
                        message.clear();
                        numOfZero = 0;
                        indexOfFirstZero = 0;
                        return request;
                    }


                    //didn't got to '/0' - it is not a complete message yet- keep reading.
                    else
                        message.add(nextByte);

                    //in all those cases which we didn't finish we will return null
                    return null;
                }


                //--------------------- Follow UnFollow ------------

                case (4): {
                    //if we still haven't read the followunfollow byte
                    if (followUnFollow == null) {
                        followUnFollow = nextByte;
                        message.add(nextByte);
                        return null;
                    }

                    //if we are reading the numOfuser 2 bytes (short)
                    if (message.size() < 5) {
                        message.add(nextByte);
                        if (message.size() == 5) {
                            numOfUsers[0] = message.get(3);
                            numOfUsers[1] = message.get(4);
                            numOfZero = bytesToShort(numOfUsers);
                            indexOfFirstZero = 5;
                        }
                        return null;

                    }
                    //we are reading the userList.
                    else if (message.size() > 5 && nextByte == '\0') {
                        numOfZero--;
//
                        byte[] bytesToString = new byte[message.size() - indexOfFirstZero];
                        for (int i = 0; i < bytesToString.length; i++) {
                            bytesToString[i] = message.get(i + indexOfFirstZero);
                        }

                        usersList.add(new String(bytesToString, StandardCharsets.UTF_8)); // add the user to usersList
                        indexOfFirstZero = message.size();
                        //if finished to read all users
                        if (numOfZero == 0) {
                            FollowUnfollowRequest followUnfollowRequest =
                                    new FollowUnfollowRequest(followUnFollow, bytesToShort(numOfUsers), new LinkedList<>(usersList));
                            usersList.clear();
                            message.clear();
                            followUnFollow = null;
                            return followUnfollowRequest;

                        } else
                            return null;
                    } else {
                        message.add(nextByte);
                        return null;
                    }

                }
                //--------------------- Post AND stat---------------

                case (5):
                case (8): {
                    MessageRequest request;
                    if (nextByte == '\0') {
                        byte[] bytesToString = new byte[message.size() - 2];
                        for (int i = 0; i < bytesToString.length; i++) {
                            bytesToString[i] = message.get(i + 2);
                        }
                        if (bytesToShort(opcodeInBytes) == 5)
                            request = new PostRequest(new String(bytesToString, StandardCharsets.UTF_8));
                        else
                            request = new StatRequest(new String(bytesToString, StandardCharsets.UTF_8));

                        message.clear();
                        return request;
                    }

                    message.add(nextByte);
                    return null;
                }

                default:
                    return null;
            }
        }
    }


    @Override
    public byte[] encode(MessageRequest message) {

        byte[] result;
        byte[] output;
        byte[] opcodeInBytes = shortToBytes(message.getOpcode());

        switch (message.getOpcode()) {
            //----------Notification -----------//
            case (9):{
                byte notificationType=((Notification) message).getNotificationType();
                byte[] content=((Notification) message).getContent().getBytes();
                byte[] postingUser=((Notification) message).getPostingUser().getBytes();
                LinkedList<Byte> outputNot=new LinkedList<>();

//                output=new byte[content.length+postingUser.length+4];
                outputNot.add(opcodeInBytes[0]);
                outputNot.add(opcodeInBytes[1]);
                outputNot.add(notificationType);

                    for (int i = 0; i < postingUser.length; i++)
                        outputNot.add(postingUser[i]);

                    outputNot.add((byte) '\0');


                for (int i=0;i<content.length;i++)
                    outputNot.add(content[i]);

                outputNot.add((byte)'\0');

                result=new byte[outputNot.size()];
                for(int i=0;i<result.length;i++)
                    result[i]=outputNot.get(i);
                return result;
            }
            //----------  ACK  -------------//
            case (10):{
                switch(((Ack)message).getMsgOpcode()){
                    ///----Follow Ack---
                    case (4):
                    case (7)://ack for follow and userList
                         {
                        byte[] msgOpcode = shortToBytes(((Ack) message).getMsgOpcode());
                        byte[] numOfUsers = shortToBytes(((Ack) message).getNumOfUsers());
                        LinkedList<String> userNameList = ((Ack) message).getUserlist();
                        ArrayList<Byte> ListOfUserNameInByte = new ArrayList<>();

                        for (String name : userNameList) {
                            byte[] userNameInByte = name.getBytes();
                            for (byte b : userNameInByte) ListOfUserNameInByte.add(b);
                            ListOfUserNameInByte.add((byte) '\0');
                        }

                        output = new byte[6 + ListOfUserNameInByte.size()];
                        output[0] = opcodeInBytes[0];
                        output[1] = opcodeInBytes[1];
                        output[2] = msgOpcode[0];
                        output[3] = msgOpcode[1];
                        output[4] = numOfUsers[0];
                        output[5] = numOfUsers[1];

                        for (int i = 0; i < ListOfUserNameInByte.size(); i++)
                            output[6 + i] = ListOfUserNameInByte.get(i);

                        return output;
                    }
                    case(8):{ //acknowledge to Stat
                        output=new byte[10];
                        byte[] msgOpcode=shortToBytes(((Ack)message).getMsgOpcode());
                        byte[] numOfPost=shortToBytes(((Ack)message).getNumOfPosts());
                        byte[] numOfFollowers=shortToBytes(((Ack)message).getNumOfFollowers());
                        byte[] numOfFollowing=shortToBytes(((Ack)message).getNumOfFollowing());

                        output[0] = opcodeInBytes[0];
                        output[1] = opcodeInBytes[1];
                        output[2] = msgOpcode[0];
                        output[3] = msgOpcode[1];
                        output[4] = numOfPost[0];
                        output[5] = numOfPost[1];
                        output[6] = numOfFollowers[0];
                        output[7] = numOfFollowers[1];
                        output[8] = numOfFollowing[0];
                        output[9] = numOfFollowing[1];
                        return output;
                    }

                    default:{
                        //all ather acknowledge
                        output=new byte[4];
                        byte[] msgOpcode=shortToBytes(((Ack)message).getMsgOpcode());
                        output[0] = opcodeInBytes[0];
                        output[1] = opcodeInBytes[1];
                        output[2] = msgOpcode[0];
                        output[3] = msgOpcode[1];
                        return output;
                    }
                }
            }
            //---------- Error ------------//
            case (11):{
                output=new byte[4];
                byte[] msgopc=shortToBytes(((ErrorResponse)message).msgOpc());
                output[0]=opcodeInBytes[0];
                output[1]=opcodeInBytes[1];
                output[2]=msgopc[0];
                output[3]=msgopc[1];

                return output;
            }
            default:{
                return null;
        }
        }

}



    public short bytesToShort(byte[] byteArr)
    {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }

    public byte[] shortToBytes(short num)
    {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }

}
