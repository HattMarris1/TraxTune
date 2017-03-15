package com.company;
import com.mongodb.MongoClient;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBCursor;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.BSON;
import org.bson.Document;

import java.io.*;
import java.net.*;

/**
 * Created by Matthew on 06/03/2017.
 */
public class Server {
    private MongoClient mongoClient;
    private MongoDatabase db;
    public Server(){
            // To connect to mongodb server
            mongoClient = new MongoClient( new MongoClientURI("mongodb://Admin:traxtune@ds123080.mlab.com:23080/traxtune") );
            // Now connect to your databases
            db = mongoClient.getDatabase( "traxtune" );
            MongoCollection Users = db.getCollection("Users");
            org.bson.Document doc = new org.bson.Document("UserName","Bret")
                    .append("Password","fjfjf")
                    .append("Friends", new org.bson.Document("1","Jim").append("2","Bob"));
            Users.insertOne(doc);
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
                    DataInputStream b = new DataInputStream(client.getInputStream());
                    System.out.println("client sends:");
                    System.out.println(b.readUTF());
                }
            }
            catch (IOException e){
                System.err.println("couldn't create server socket"+e);
            }
    }
}
