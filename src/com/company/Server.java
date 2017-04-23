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

    private class clientSession implements Runnable{
        private String userName;

        private long UID;
        public clientSession(String userName){
            this.userName = userName;
        }

        public void run(){

        }
    }
    public Server(){

            // To connect to mongodb server
            mongoClient = new MongoClient( new MongoClientURI("mongodb://Admin:traxtune@ds123080.mlab.com:23080/traxtune") );
            // Now connect to databases
            db = mongoClient.getDatabase( "traxtune" );
            MongoCollection<Document> Users = db.getCollection("Users");
            /*org.bson.Document doc = new org.bson.Document("UserName","Bret")
                    .append("Password","fjfjf")
                    .append("Friends", new org.bson.Document("1","Jim").append("2","Bob"));*/
            /*Users.insertOne(doc);*/
            MongoCursor<Document> cursor = Users.find().iterator();
            while (cursor.hasNext()){
            System.out.println(cursor.next());
            }
            try{
                ServerSocket server = new ServerSocket(7777);

                while (true){
                    System.out.println("Waiting for Client...");
                    Socket client = server.accept();
                    System.out.println("connected to client");

                    ObjectInputStream b = new ObjectInputStream(client.getInputStream());
                    System.out.println("client sends:");
                    java.lang.Object t = (java.lang.Object) b.readObject();
                    Class ct = t.getClass();

                    //if statement to determine the type of request from the user
                    if(ct.equals(loginInfo.class)){
                        loginInfo LI = (loginInfo)t;

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
                                //put user in online user map
                                clientSession thisUserSession = new clientSession(LI.name);
                            }
                        }
                    }
                    else if(ct.equals(registrationInfo.class)){
                        registrationInfo RI = (registrationInfo)t;
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
                }
            }
            catch (IOException e){
                System.err.println("couldn't create server socket "+e);
            }
            catch ( ClassNotFoundException e){
                System.err.println("unable to read incoming data"+e);
        }
    }

}