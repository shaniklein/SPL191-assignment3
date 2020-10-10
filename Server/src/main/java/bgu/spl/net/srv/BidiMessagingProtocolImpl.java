package bgu.spl.net.srv;
import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGS.Messeges.*;
import sun.plugin2.gluegen.runtime.CPU;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BidiMessagingProtocolImpl implements BidiMessagingProtocol<MessageRequest> {

    private int connectionId;
    private Connections<MessageRequest> connections;
    private static  DataBase dataBase;

    public BidiMessagingProtocolImpl(DataBase dataBase){
    this.dataBase=dataBase;
    }

    @Override
    public void start(int connectionId, Connections<MessageRequest> connections) {
    this.connectionId=connectionId;
    this.connections=connections;
    }


    @Override

    public void process(MessageRequest message) {
        switch (message.getOpcode()) {
            ///--------------Register-----------
            case (1): {
                String userName = ((RegisterRequest) message).getUserName();
                String password = ((RegisterRequest) message).getPassword();
               //an error accured
                if(!dataBase.register(userName,password,connectionId))
                    connections.send(connectionId, new ErrorResponse((short)1));
                //succesfully register
                else connections.send(connectionId, new Ack((short)1));

                break;
            }

            ///--------------Log in----------
            case (2): {
                String userName = ((LoginRequest) message).getUserName();
                String password = ((LoginRequest) message).getPassword();
                if(dataBase.logIn(userName,password,connectionId)) {
                    connections.send(connectionId, new Ack((short) 2));
                    //when user name was log out - we collect all the messages sent to him, now we will handle it
                    if(dataBase.GetQueue(userName).size()!=0) {
                        for (MessageRequest messageRequest : dataBase.GetQueue(userName)) {
                            connections.send(connectionId, messageRequest);
                            dataBase.GetQueue(userName).poll();
                        }

                    }
                }
                else
                    connections.send(connectionId, new ErrorResponse((short)2));
                break;
            }
            ///--------------Log out----------
            case (3): {
                if(!dataBase.logOut(connectionId))
                  connections.send(connectionId, new ErrorResponse((short)3));
                else {
                    connections.send(connectionId, new Ack((short) 3));
                    connections.disconnect(connectionId);
                }
                break;
            }

            ///--------------Follow / UnFollow----------
            case (4): {
                //if the user is not logged an error message will be send back to the client
                if(!dataBase.isLoggedIn(connectionId)){
                    connections.send(connectionId, new ErrorResponse((short)4));
                    return;
                }

                LinkedList<String> successfullUsers = new LinkedList<>();
                short numOfSuccesful = 0;
                byte followUnfollow = ((FollowUnfollowRequest) message).getFollowUnfollow();
                LinkedList<String> userNameList = ((FollowUnfollowRequest) message).getUserNameList();

                for (String userName:userNameList
                     ) {
                    if (dataBase.follow(followUnfollow,userName,connectionId)){
                        successfullUsers.add(userName);
                        numOfSuccesful++;
                        }
                     }
                //follow considered successfull if atleast on follow command succeed
                if(numOfSuccesful!=0){
                connections.send(connectionId,
                        new Ack((short)4, followUnfollow, numOfSuccesful, successfullUsers));}
                else
                            connections.send(connectionId, new ErrorResponse((short)5));
                break;
            }
            //-------------------POST--------------
            case (5): {
                if(!dataBase.isLoggedIn(connectionId)){
                    connections.send(connectionId, new ErrorResponse(message.getOpcode()));
                    return;
                }

                String content = ((PostRequest) message).getContent();
                //get all user that follow us or is tagged in the post
                LinkedList<Integer> usersToSend=dataBase.post(content,connectionId);

                for (Integer idOfUserTOSend:usersToSend
                     ) {
                    //we synchronize here so username cannot get message while logout
                    synchronized (dataBase.getClient(idOfUserTOSend)) {
                        if (dataBase.isLoggedIn(idOfUserTOSend))
                            connections.send(idOfUserTOSend, new Notification(content, (byte) '\1', dataBase.getUserName(connectionId)));
                        else {
                            dataBase.GetQueue(dataBase.getClient(idOfUserTOSend).getUserName()).add(new Notification(content, (byte) '\1', dataBase.getUserName(connectionId)));
                        }
                    }
                }
                connections.send(connectionId, new Ack((short) 5));
                break;
            }


            //------------------POST PM--------------
            case (6): {
                //if the user is not logged an error message will be send back to the client
                if(!dataBase.isLoggedIn(connectionId)){
                    connections.send(connectionId, new ErrorResponse((short)6));
                    return;
                }
                String userName = ((PMRequest) message).getUserName();
                String MsgContent = ((PMRequest) message).getContent();

                    if (dataBase.postPM(userName, MsgContent, connectionId)) {
                        if (!dataBase.isLoggedIn(dataBase.getId(userName)))
                            dataBase.GetQueue(userName).add(new Notification(MsgContent, (byte) '\0', dataBase.getUserName(connectionId)));
                        else
                            connections.send(dataBase.getId(userName), new Notification(MsgContent, (byte) '\0', dataBase.getUserName(connectionId)));
                         connections.send(connectionId, new Ack((short) 6));
                    }
                    else
                        connections.send(connectionId, new ErrorResponse(message.getOpcode()));

                    break;
                }
            //------------userList ---------
            case (7): {
                //if the user is not logged an error message will be send back to the client
                if(!dataBase.isLoggedIn(connectionId)){
                    connections.send(connectionId, new ErrorResponse((short)7));
                    return;
                }
                LinkedList<String> usersToAck = new LinkedList<>();
                LinkedList<String> clientCollection=dataBase.userList(connectionId);
                    if(clientCollection==null){
                        connections.send(connectionId,new ErrorResponse((short)7));
                        return;
                    }
                    for(int i=0;i<clientCollection.size();i++){
                        usersToAck.add(clientCollection.get(i));
                    }

                if(!usersToAck.isEmpty())
                connections.send(connectionId,new Ack((short)7,(byte)'\1', (short) usersToAck.size(), usersToAck));
                return;
            }
            //----------------STAT------------------
            case (8): {
                //if the user is not logged an error message will be send back to the client
                if(!dataBase.isLoggedIn(connectionId)){
                    connections.send(connectionId, new ErrorResponse((short)4));
                    return;
                }
                String userName=((StatRequest)message).getUserName();
                short numOfFollowers=dataBase.statGetNumOfFollowers(userName);
                short numOfFollowing=dataBase.statGetNumOfFollowing(userName);
                short numOfPosts=dataBase.statGetNumOfPost(userName);
                if(numOfFollowers==-1||numOfFollowing==-1||numOfPosts==-1){
                    connections.send(connectionId, new ErrorResponse((short)4));
                    return;}
                connections.send(connectionId,
                        new Ack((short)8,numOfPosts,numOfFollowers,numOfFollowing));

            }
        }
    }

    @Override
    public boolean shouldTerminate() {
        return false;
    }


}
