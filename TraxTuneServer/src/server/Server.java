package server;

import com.mongodb.Cursor;
import com.mongodb.MongoClient;
import com.mongodb.DB;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.bson.BSON;
import org.bson.Document;

import javax.print.Doc;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import static com.mongodb.client.model.Filters.all;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;

/**
 * Created by Matthew on 06/03/2017.
 */
public class Server {
    private MongoClient mongoClient;
    private MongoDatabase db;
    private Map<String, clientSession> clients = new HashMap<>();
    private ServerSocket server;
    private MongoCollection<Document> Users;
    private MongoCollection<Document> Chats;

    private class clientSession implements Runnable{
        private String userName;

        private Socket userSocket;
        private ObjectInputStream clientInput = null;
        private ObjectOutputStream clientOutput = null;


        public clientSession(Socket userSocket) {
            this.userSocket = userSocket;
            try {
                clientInput = new ObjectInputStream(userSocket.getInputStream());
                clientOutput = new ObjectOutputStream(userSocket.getOutputStream());
            }
            catch (IOException e4){
                e4.printStackTrace();
            }
        }

        public void newMessage(Document theChat){
            try{
                clientOutput.writeObject(theChat);
            }
            catch (IOException e){
                e.printStackTrace();
            }

        }

        public void run(){
            try {
                /*clientInput = new ObjectInputStream(userSocket.getInputStream());*/
                //clientOutput = new ObjectOutputStream(userSocket.getOutputStream());
                while (true){
                    Object raw = clientInput.readObject();
                    Document userData = (Document)raw;
                    System.out.println(userData);
                    System.out.println("client sends:");

                    //if statement to determine the type of request from the user
                    if(Objects.equals(userData.getString("header"), "login")){
                        userName = userData.getString("userName");
                        String password = userData.getString("password");

                        System.out.println(userName);
                        System.out.println(password);
                        //check if user exists on system and if they're already online
                        Document theUser = Users.find(eq("name", userName)).first();
                        System.out.println(theUser);
                        if((theUser!=null) && !(clients.containsKey(userName)) ){
                            //check if password is correct
                            System.out.println(password);
                            System.out.println(theUser.get("password").toString());
                        /*for(int i=0; i< LI.password.length; i++){
                            if (LI.password[i]==theUser.get("password").toString()[i]){
                            }
                        }*/
                            System.out.println(password.compareTo(theUser.get("password").toString()));
                            if (password.compareTo(theUser.get("password").toString())==0){
                                System.out.println("user verified");
                                Users.updateOne(eq("name",userName), new Document("$set", new Document("lastLogin", new Date())));
                                //TODO: put user in online user map
                                clients.put(userName,this);
                                Document toClient = new Document("profile",theUser)
                                        .append("header","loginsuccess");

                                clientOutput.writeObject(toClient);
                                clientOutput.flush();
                            }
                        }
                    }
                    else if(Objects.equals(userData.getString("header"), "register")){

                        userName = userData.getString("userName");
                        String password = userData.getString("password");
                        //check if user already exists
                        Document theUser = Users.find(eq("name", userName)).first();
                        System.out.println(theUser);
                        if (theUser== null){
                            //put user in mongodb
                            Date now = new Date();
                            Document doc = new Document("name",userName)
                                    .append("password",password)
                                    .append("registration",now);
                            Users.insertOne(doc);
                            System.out.println("user added to db");
                        }
                        else {
                            //user already exists
                            System.out.println("user already registered");
                        }
                    }
                    else if (Objects.equals(userData.getString("header"), "getallusers")){
                       ArrayList<String> users = new ArrayList<>();
                       MongoCursor<Document> cursor = Users.find().iterator();

                       while (cursor.hasNext()) {
                         String nametemp = cursor.next().getString("name");
                         users.add(nametemp);
                       }

                        Document allUserNames = new Document("header", "alluserdata")
                                .append("users",users);
                        clientOutput.writeObject(allUserNames);
                        clientOutput.flush();

                    }
                    else if (Objects.equals(userData.getString("header"), "addfriends")){
                        //TODO clear up this functional mess...
                        //adds each friend to the user's list
                        Document user = Users.find(eq("name",userName)).first();
                        Document userNew = new Document();
                        Document usersNewFriends = new Document();
                        ArrayList<String > list = (ArrayList<String>) userData.get("users");

                        Iterator<String> iterator1 = list.iterator();
                        while (iterator1.hasNext()){
                            usersNewFriends.append("friends",iterator1.next());
                        }
                        userNew.put("$addToSet", usersNewFriends);
                        Users.updateOne(eq("name", userName),userNew);

                        //adds this user to each of the friend's lists
                        Iterator<String> iterator2 = list.iterator();
                        Document thisUserNew = new Document();
                        Document thisUserAppend = new Document();
                        thisUserAppend.put("$addToSet", new Document().append("friends",userName));
                        while (iterator2.hasNext()){
                            Users.updateOne(eq("name",iterator2.next()),thisUserAppend);
                        }

                        //remove the friends from the users pending list
                        Iterator<String> iterator3 = list.iterator();
                        Document userOldPending = new Document();
                        while (iterator3.hasNext()){
                            userOldPending.append("requests",iterator3.next());
                        }
                        thisUserNew.put("$pull", userOldPending);
                        Users.updateOne(eq("name",userName),thisUserNew);
                        clientOutput.writeObject(new Document("header","refreshaccount")
                                    .append("userdetails",Users.find(eq("name",userName)).first()));
                        clientOutput.flush();
                    }
                    else if (Objects.equals(userData.getString("header"), "friendRequest")){
                        //put this user in the target friends requests list
                        ArrayList<String > list = (ArrayList<String>) userData.get("users");
                       Iterator<String> iterator =  list.iterator();
                        Document thisUser = new Document();
                        thisUser.put("$addToSet", new Document().append("requests",userName));
                       while(iterator.hasNext()){
                           Users.updateOne(eq("name", iterator.next()),thisUser);
                       }
                    }
                    else if (Objects.equals(userData.getString("header"), "getmyaccount")){
                       Document account = Users.find(eq("name",userName)).first();
                       Document accountPackage = new Document("header","refreshaccount")
                               .append("userdetails",account);
                       clientOutput.writeObject(accountPackage);
                       clientOutput.flush();
                    }
                    else if (Objects.equals(userData.getString("header"), "deletefriends")){
                        //delete entry in user document
                        //delete entry in friend doc
                        ArrayList<String > list = (ArrayList<String>) userData.get("users");
                        Iterator<String> iterator = list.iterator();
                        while (iterator.hasNext()){
                            String temp = iterator.next();
                            Users.updateOne(eq("name",userName), (new Document("$pull", new Document("friends",temp))));
                            Users.updateOne(eq("name",temp), (new Document("$pull", new Document("friends",userName))));
                        }
                    }
                    else if (Objects.equals(userData.getString("header"), "createchat")){
                        ArrayList<String > users = (ArrayList<String >)userData.get("users");
                        users.add(userName);
                        Document chatdoc = new Document("chatname",userData.get("chatname"))
                                .append("users",users);
                        Chats.insertOne(chatdoc);
                    }
                    else if (Objects.equals(userData.getString("header"), "getmychats")){
                        ArrayList<String> chats = new ArrayList<>();
                        MongoCursor<Document> cursor = Chats.find(in("users",userName)).iterator();
                        while(cursor.hasNext()){
                            Document temp = cursor.next();
                            String chatName = temp.getString("chatname");
                            chats.add(chatName);
                        }
                        Document toUser = new Document("header","mychats")
                                .append("chatlist",chats);
                        clientOutput.writeObject(toUser);
                    }
                    else if (Objects.equals(userData.getString("header"), "getmessages")){
                        String chatName = userData.getString("chatname");
                        Document theChat = Chats.find(eq("chatname",chatName)).first();
                        Document toUser = new Document("header","messages")
                                .append("chatdata",theChat);
                        clientOutput.writeObject(toUser);
                    }
                    else if (Objects.equals(userData.getString("header"), "sendmessage")){
                        Document newMessage = new Document("messages", new Document("content",userData.getString("message"))
                                .append("from",userName)
                                .append("date",new Date()));

                        Document messageModify = new Document("$addToSet",newMessage);
                       Chats.updateOne(eq("chatname",userData.getString("chat")),messageModify);

                       Document theNewChat = Chats.find(eq("chatname",userData.getString("chat"))).first();
                       Document toUser = new Document("header","messages")
                               .append("chatdata",theNewChat);
                       //updates the user that sent the message
                       clientOutput.writeObject(toUser);
                       //iterate over all clients that are part of the group and online
                        ArrayList<String> groupMembers = (ArrayList<String>) theNewChat.get("users");
                        Iterator<String > iterator = groupMembers.iterator();
                        while(iterator.hasNext()){
                            String temp = iterator.next();
                            if (clients.containsKey(temp)){
                                clients.get(temp).newMessage(toUser);
                            }
                        }
                    }
                    else{
                        System.out.println("not an object type I recognise");
                    }

                    //client sends login/music/signup/chat request
                    //clientInput1.close();
                }
            }
            catch (IOException e1){
                System.err.println(e1);
            }
            catch(ClassNotFoundException e2){
                System.err.println(e2);
            }
        }
    }
    public Server(){

            // To connect to mongodb server
            mongoClient = new MongoClient( new MongoClientURI("mongodb://Admin:traxtune@ds123080.mlab.com:23080/traxtune") );
            // Now connect to databases
            db = mongoClient.getDatabase( "traxtune" );
            Users = db.getCollection("Users");
            Chats = db.getCollection("chats");
            /*ArrayList<String> useres = new ArrayList<>();
        useres.add("Mal");
        useres.add("Kaylee");
            Document doc = new Document("users",useres)
                    .append("messages","fjfjf");
            Chats.insertOne(doc);*/
            MongoCursor<Document> cursor = Users.find().iterator();
            while (cursor.hasNext()){
            System.out.println(cursor.next());
            }
            try{
                server = new ServerSocket(7777);
            }
            catch (IOException e){
                System.err.println("couldn't create server socket "+e);
            }
            while (true){
                System.out.println("Waiting for Client...");
                Socket client;
                try{
                    client = server.accept();
                    Thread currentClientThread  = new Thread (new clientSession(client));
                    currentClientThread.start();
                }
                catch (IOException e){
                    System.err.println(e);
                }

        }
    }
    public static void main(String args[]){
        new Server();
    }
}