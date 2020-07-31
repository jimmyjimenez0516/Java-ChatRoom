import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.*;

public class ThreadedServerWithPresence{

    public static void main(String[] args ){
        ArrayList<ThreadedHandlerWithPresence> handlers;
        try{
            handlers = new ArrayList<ThreadedHandlerWithPresence>();
            ServerSocket s = new ServerSocket(3000);
            for(;;){
                Socket incoming = s.accept( );
                new ThreadedHandlerWithPresence(incoming,handlers).start();
            }
        }
        catch (Exception e){
            System.out.println(e);
        }
    }
}

class ThreadedHandlerWithPresence extends Thread{

    Socket incoming;
    ArrayList<ThreadedHandlerWithPresence> handlers;
    PrintWriter pw;
    BufferedReader br;
    String userName;
    String tempList[];

    public ThreadedHandlerWithPresence(Socket i,ArrayList<ThreadedHandlerWithPresence> handlers){
        incoming = i;
        this.handlers = handlers;
        handlers.add(this);

    }
    public void setUserName(String userName){
        this.userName = userName;
    }
    public String getUserName(){
        return userName;
    }

    public void run(){
        try{
            br = new BufferedReader(new InputStreamReader(incoming.getInputStream()));

            pw = new PrintWriter(new OutputStreamWriter(incoming.getOutputStream()),true);

            for(;;){
                String incomingMessage = br.readLine();
                System.out.println(incomingMessage); // send message to server for debugging
                int userat = incomingMessage.indexOf(" ");
                System.out.println(userat);// send message to server for debugging

                String currentUserName = incomingMessage.substring(0,(userat));
                System.out.println("CURRENT USER " + currentUserName); // send message to server for debugging

                setUserName(incomingMessage.substring(0,(userat)));
                String listOfUsers="";

                System.out.println("Message read: " + incomingMessage); // send message to server for debugging

                for(int i = 0; i < handlers.size(); i++){
                    listOfUsers += handlers.get(i).getUserName()+",";
                    System.out.println(listOfUsers);// send message to server for debugging
                }

                if(incomingMessage.contains("disconnected")) {
                    System.out.println("List of users before disconnect " + listOfUsers);// send message to server for debugging
                    listOfUsers = listOfUsers.replace(currentUserName,"");
                    System.out.println("List of users after disconnect " + listOfUsers);// send message to server for debugging
                }

                //***********************************************used to send the Private Messages
                if(incomingMessage.contains("PRIVATE_MESSAGE")) {
                    System.out.println("Private message : " + incomingMessage);// send message to server for debugging
                    String[] splitMessage = incomingMessage.trim().split(" ");
                    System.out.println("0 " + splitMessage[0]);// send message to server for debugging
                    System.out.println("1st " + splitMessage[1]);// send message to server for debugging
                    System.out.println("2nd " + splitMessage[2]);// send message to server for debugging
                    System.out.println("3rd " + splitMessage[3]);// send message to server for debugging
                    System.out.println("4th " + splitMessage[4]);// send message to server for debugging
                    System.out.println("5th " + splitMessage[5]);// send message to server for debugging

                    String receivingUser = splitMessage[5]; // saves the split receivingUser into a String to use
                    String sendingUser = splitMessage[3]; // saves the split sendingUser into a String to use

                    // used to send the Private Messages
                    for(int i = 0; i < handlers.size(); i++) {
                        if(handlers.get(i).getUserName().equals(receivingUser)) {
                            handlers.get(i).pw.println(incomingMessage); 
                        }

                        if(handlers.get(i).getUserName().equals(sendingUser)) {
                            handlers.get(i).pw.println(incomingMessage); 
                        }

                    }

                }
                //**************************************************used to send the Private Messages

                listOfUsers = listOfUsers.replace(":","");

                if(!incomingMessage.contains("PRIVATE_MESSAGE")) {
                    for (int i = 0; i < handlers.size(); i++) {
                        handlers.get(i).pw.println(incomingMessage);
                    }
                }

                System.out.println("CURRENT MESSAGE " + incomingMessage);// send message to server for debugging

                if(incomingMessage.contains("chat")) {
                    for(int i = 0; i < handlers.size(); i++){
                        handlers.get(i).pw.println(listOfUsers);
                    }
                }

                System.out.println(listOfUsers);// send message to server for debugging
            }
        }catch (Exception e){
            System.out.println(e);
        }finally{
            handlers.remove(this);
        }
    }
}