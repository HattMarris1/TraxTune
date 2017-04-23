package com.company;
import com.mongodb.MongoClient;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBCursor;
import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.sun.xml.internal.bind.v2.TODO;
import org.bson.BSON;
import org.bson.Document;
import communication.registrationInfo;
import communication.loginInfo;
import org.omg.CORBA.Object;
import com.mongodb.client.MongoCursor;


import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

import static com.mongodb.client.model.Filters.eq;

/**
 * Created by Matthew on 06/03/2017.
 */
public class Server {
    private MongoClient mongoClient;
    private MongoDatabase db;
    private Map<String, String> clients = new HashMap<String,String>();
    private ServerSocket server;
    private MongoCollection<Document> Users;
    private class clientSession implements Runnable{
        private String userName;
        private Socket userSocket;
        private ObjectInputStream clientInput;
        private ObjectOutputStream clientOutput;

        private long UID;

        public clientSession(Socket userSocket) {
            this.userSocket = userSocket;
        }

        public void run(){
            try {
                /*clientInput = new ObjectInputStream(userSocket.getInputStream());*/
                clientOutput = new ObjectOutputStream(userSocket.getOutputStream());
                while (true){

                   ObjectInput clientInput1 = new ObjectInputStream(userSocket.getInputStream());
                    java.lang.Object inObj = clientInput1.readObject();

                    System.out.println("client sends:");
                    Class ct = inObj.getClass();

                    //if statement to determine the type of request from the user
                    if(ct.equals(loginInfo.class)){
                        loginInfo LI = (loginInfo)inObj;

                        System.out.println(LI.name);
                        System.out.println(LI.password);
                        //check if user exists on system and if they're already online
                        Document theUser = Users.find(eq("name", LI.name)).first();
                        System.out.println(theUser);
                        if((theUser!=null) && !(clients.containsKey(LI.name)) ){
                            //check if password is correct
                            System.out.println(LI.password);
                            System.out.println(theUser.get("password").toString());
                        /*for(int i=0; i< LI.password.length; i++){
                            if (LI.password[i]==theUser.get("password").toString()[i]){
                            }
                        }*/
                            System.out.println(LI.password.compareTo(theUser.get("password").toString()));
                            if (LI.password.compareTo(theUser.get("password").toString())==0){
                                System.out.println("user verified");

                                //TODO: put user in online user map
                            }
                        }
                    }
                    else if(ct.equals(registrationInfo.class)){
                        registrationInfo RI = (registrationInfo)inObj;
                        System.out.println(RI);
                        //check if user already exists
                        Document theUser = Users.find(eq("name", RI.name)).first();
                        System.out.println(theUser);
                        if (theUser== null){
                            //put user in mongodb
                            org.bson.Document doc = new org.bson.Document("name",RI.name)
                                    .append("password",RI.password);
                            Users.insertOne(doc);
                            System.out.println("user added to db");
                        }
                        else {
                            //user already exists
                            System.out.println("user already registered");
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
            /*org.bson.Document doc = new org.bson.Document("UserName","Bret")
                    .append("Password","fjfjf")
                    .append("Friends", new org.bson.Document("1","Jim").append("2","Bob"));*/
            /*Users.insertOne(doc);*/
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

}