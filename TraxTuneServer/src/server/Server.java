package server;

import com.mongodb.MongoClient;
import com.mongodb.DB;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import org.bson.BSON;
import org.bson.Document;

import javax.print.Doc;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
                    Document userData = (Document)clientInput1.readObject();
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

                                //TODO: put user in online user map
                                Document toClient = new Document("header", "response")
                                        .append("success",true);

                                clientOutput.writeObject(toClient);
                            }
                        }
                    }
                    else if(userData.getString("header")=="request"){

                        userName = userData.getString("username");
                        String password = userData.getString("password");
                        //check if user already exists
                        Document theUser = Users.find(eq("name", userName)).first();
                        System.out.println(theUser);
                        if (theUser== null){
                            //put user in mongodb
                            Document doc = new Document("name",userName)
                                    .append("password",password);
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
    public static void main(String args[]){
        new Server();
    }
}