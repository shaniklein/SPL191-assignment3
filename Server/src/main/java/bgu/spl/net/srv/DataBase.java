package bgu.spl.net.srv;


import bgu.spl.net.impl.BGS.Messeges.MessageRequest;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public  class DataBase {
     private ConcurrentHashMap<String,Client> clients;
    private ConcurrentHashMap<Integer,String> clientsIdName;
    private LinkedList<String> clientsByOrder;
    private final Object objectLock;
    public DataBase() {
        clients = new ConcurrentHashMap<>();
        clientsIdName = new ConcurrentHashMap<>();
        clientsByOrder=new LinkedList<>();
        objectLock=new Object();
    }
        // We want only one user to register at moment
        public synchronized boolean register(String userName, String password,int connectionId) {// usedConcurrentHashMap for synchronization
        //if the username is already exists we wont register the new one
        if (clients.containsKey(userName))
            return false;

            clients.putIfAbsent(userName, new Client(userName, password, connectionId));
            clientsIdName.putIfAbsent(connectionId, userName);
            clientsByOrder.addLast(userName);
            return true;
        }



    public boolean logIn(String userName, String password,int connectionid) {
        if (clientsIdName.containsKey(connectionid)&&clients.get(clientsIdName.get(connectionid)).isLoggedIn())//this connection id is not already logged in
            return false;

        if (clients.containsKey(userName)){
            ////user name is register
            //we synchronized the userName (inside client) which is unuiq to the client so only one login for this user name.
            //(because it is the field of the client and not the function pararameter it is unuiqe)
            synchronized (clients.get(userName)) {
                if (clients.get(userName).getPassword().equals(password)//correct password
                        && !clients.get(userName).isLoggedIn()) {
                    clients.get(userName).setConnectionID(connectionid);
                    clients.get(userName).setLoggedIn(true);
                    clientsIdName.putIfAbsent(connectionid,userName);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isLoggedIn(int connectionId) {
        if(clientsIdName.containsKey(connectionId))
            return clients.get(clientsIdName.get(connectionId)).isLoggedIn();
        return false;
    }


    public boolean logOut(int connectionId) {
        //synchronized with folllow while user do logout other client can't follow him
        synchronized (objectLock) {
            if(!clientsIdName.containsKey(connectionId))//&&//!clients.containsKey(clientsIdName.get(connectionId)))
                return false;
            if (clients.get(clientsIdName.get(connectionId)).isLoggedIn()) {
                clients.get(clientsIdName.get(connectionId)).setLoggedIn(false);

                return true;
            }
        }
        return false;
    }

    public boolean follow(byte followUnfollow, String userName,int connectionId) {
        //synchronized with folllow while user do logout other client can't follow him
        synchronized (objectLock) {
            //check registration of both connection id and userName
            if (!clientsIdName.containsKey(connectionId) || !clients.containsKey(clientsIdName.get(connectionId))||!clients.containsKey(userName)||!clients.get(userName).isLoggedIn()) {
                return false;
            }
            //follow
            if (followUnfollow == '\0') {
                if (clients.containsKey(userName) && clients.get(clientsIdName.get(connectionId)).startFollow(userName)) {
                    clients.get(userName).SetFollowers(getUserName(connectionId), true);
                    return true;
                }
                return false;
            }
            if (clients.containsKey(userName) && clients.get(clientsIdName.get(connectionId)).stopFollow(userName)) {
                clients.get(userName).SetFollowers(getUserName(connectionId), false);
                return true;
            }
            return false;
        }
    }

    public LinkedList<Integer> post(String content,int connectionId) {
        synchronized (clients.get(clientsIdName.get(connectionId))) {
            LinkedList<Integer> usersToSend = new LinkedList<>();
            String[] arrSplit = content.split(" ");
            for (String world : arrSplit)
            //find all users tag in the post
            {
                if (world.charAt(0) == '@' && clients.containsKey(world.substring(1))&&!usersToSend.contains(getUserId(world.substring(1))))
                    usersToSend.add(getUserId(world.substring(1)));
            }
            for (String follower : clients.get(clientsIdName.get(connectionId)).getFollowers()
            ) {
                if(!usersToSend.contains(getUserId(follower)))
                  usersToSend.add(getUserId(follower));

            }

            clients.get(clientsIdName.get(connectionId)).addPost(content);
            return usersToSend;
        }
    }

    public boolean postPM(String userName,String MsgContent,int connectionId) {
            //If the reciepient username isn’t registered an ERROR message will be returned to the client.
            if (!clients.containsKey(userName)) {
                return false;
            }
            clients.get(clientsIdName.get(connectionId)).addPost(MsgContent);
            return true;
        }

    public LinkedList<String> userList(int connectionId) {
        // If userName is not registered, an error message will be returned.
            if(!clientsIdName.containsKey(connectionId))
                return null;
             return clientsByOrder;
        }

    public short statGetNumOfFollowers(String userName) {
        if(!clients.containsKey(userName))
            return -1;
        return(short) clients.get(userName).getFollowers().size();

    }

    public short statGetNumOfPost(String userName) {
        if(!clients.containsKey(userName))
            return -1;
            return clients.get(userName).getNumOfPost();
    }

    public short statGetNumOfFollowing(String userName) {
        if(!clients.containsKey(userName))
            return -1;
            return(short) clients.get(userName).getFollowing().size();
    }


    public String getUserName(int connectionId) {
    return clientsIdName.get(connectionId);
    }
    private Integer getUserId(String userName) {
    return clients.get(userName).getConnectionID();
    }

    public int getId(String userName) {
        for(String client:clients.keySet())
            if(clients.get(client).getUserName().equals(userName))
                return clients.get(client).getConnectionID();
    return -1;
    }

    public ConcurrentLinkedQueue<MessageRequest> GetQueue(String userName){
        return clients.get(userName).getMessagesToRecieve();
    }
    public Client getClient(int id){
        return clients.get(getUserName(id));
    }

}





