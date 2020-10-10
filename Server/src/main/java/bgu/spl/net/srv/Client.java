package bgu.spl.net.srv;

import bgu.spl.net.impl.BGS.Messeges.MessageRequest;
import sun.plugin2.message.Message;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Client {
    private String userName;
    private String Password;
    private int connectionID;
    private boolean isLoggedIn;
    private ConcurrentLinkedQueue<String> posts;
    private ConcurrentLinkedQueue<String> following;
    private ConcurrentLinkedQueue<String> followers;
    private ConcurrentLinkedQueue<MessageRequest> messagesToRecieve;


    public Client(String userName, String password, int connectionID) {
        this.messagesToRecieve=new ConcurrentLinkedQueue<>();
        this.userName = userName;
        Password = password;
        this.connectionID = connectionID;
        isLoggedIn = false;
        posts = new ConcurrentLinkedQueue<>();
        following = new ConcurrentLinkedQueue<>();
        followers = new ConcurrentLinkedQueue<>();
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return Password;
    }

    public int getConnectionID() {
        return connectionID;
    }

    public  boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setConnectionID(int connectionID) {
        this.connectionID = connectionID;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.isLoggedIn = loggedIn;
    }

    public boolean isFollowing(String userName) {
        return following.contains(userName);
    }
    public boolean startFollow(String userName){

        if(following.contains(userName))
            return false;
        following.add(userName);
        return true;
    }
    public boolean stopFollow(String userName){
        return following.remove(userName);
    }


    public ConcurrentLinkedQueue<String> getFollowers() {
        return followers;
    }
    public ConcurrentLinkedQueue<String> getFollowing() {
        return following;
    }
    public void SetFollowers(String userName,boolean add){
    if(add)
        followers.add(userName);
    else
        followers.remove(userName);
    }


    public void addPost(String post) {
        posts.add(post);
    }

    public short getNumOfPost() {
        return (short)posts.size();
    }

    public ConcurrentLinkedQueue<MessageRequest> getMessagesToRecieve() {
        return messagesToRecieve;
    }

}