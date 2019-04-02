/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatwme;

/**
 * A messaging application.
 * @author teel6
 */
import java.net.*;
import java.io.*;
import java.util.*;
public class CHATwME {
    private static final String TERMINATE = "Exit"; //Close command
    static String name; //Saves entered name
    static volatile boolean finished = false; //Set to false initially to keep it open
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length != 2){
            System.out.println("You must specify a host and port number."); //checks to see if a valid IP address and socket num are inputted
        }
        else {
            try {
                InetAddress group = InetAddress.getByName(args[0]); 
                int port = Integer.parseInt(args[1]); 
                Scanner sc = new Scanner(System.in); 
                System.out.print("Enter your name: "); //sets username in chat
                name = sc.nextLine(); //sets name variable to save
                MulticastSocket socket = new MulticastSocket(port); 
              
                socket.setTimeToLive(1); //set as 1 when not on local, 0 on local
                  
                socket.joinGroup(group);
                
                Thread t = new Thread(new ReadThread(socket,group,port)); //to create a group
              
                t.start(); //initiate a thread to commence the chat
                  
                System.out.println("Chat has initiated...\n"); //tells users that they can begin talking
                while(true) 
                { 
                    String message; 
                    message = sc.nextLine(); //Sets the message value to what the user has inputted
                    if(message.equalsIgnoreCase(CHATwME.TERMINATE)) //exits the chat if Exit is entered
                    { 
                        finished = true;
                        message = name + " has left the chat.";//notifies that this user has left the chat
                        byte[] buffer = message.getBytes(); 
                        DatagramPacket datagram = new
                        DatagramPacket(buffer,buffer.length,group,port); 
                        socket.send(datagram); //Sends the message
                        System.out.println("You have disconnected.");
                        socket.leaveGroup(group); 
                        socket.close();
                        break; 
                    } 
                    message = name + ": " + message; //Displays name and what the user said
                    byte[] buffer = message.getBytes(); 
                    DatagramPacket datagram = new
                    DatagramPacket(buffer,buffer.length,group,port); 
                    socket.send(datagram); //Sends the message
                } 
            }
            catch(SocketException se) {
                System.out.println("Error creating socket"); //Lets user know the socket couldn't be created
                se.printStackTrace();
            }
            catch(IOException ie){
                System.out.println("Error reading/writing from/to socket"); //Lets the user know that it can't read/send message
                ie.printStackTrace();
            }
        }
    }
    static class ReadThread implements Runnable { 
    private MulticastSocket socket; 
    private InetAddress group; 
    private int port; 
    private static final int MAX_LEN = 1000; //Max length
    ReadThread(MulticastSocket socket,InetAddress group,int port) 
    { 
        this.socket = socket; 
        this.group = group; 
        this.port = port; 
    } 
      
    @Override
    public void run() 
    { 
        while(!CHATwME.finished) //When not finished
        { 
                byte[] buffer = new byte[ReadThread.MAX_LEN]; 
                DatagramPacket datagram = new
                DatagramPacket(buffer,buffer.length,group,port); 
                String message;
            try //Do what is intended
            { 
                socket.receive(datagram); 
                message = new
                String(buffer,0,datagram.getLength(),"UTF-8"); 
                if(!message.startsWith(CHATwME.name)){ 
                    System.out.println(message);
                }
            } 
            catch(IOException e) //Lets the user know that the socket is closed and chat has ended
            { 
                System.out.println("The socket has been closed, chat has ended.");
            } 
        } 
    }
    }
}
