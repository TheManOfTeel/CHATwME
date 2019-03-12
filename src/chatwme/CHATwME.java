/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatwme;

/**
 * A message application.
 * @author teel6
 */
import java.net.*;
import java.io.*;
import java.util.*;
public class CHATwME {
    private static final String TERMINATE = "Exit";
    static String name;
    static volatile boolean finished = false;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length != 2){
            System.out.println("Two arguments are required: <multicast host> <port number>"); //checks to see if the base requirements are met to start chat
        }
        else {
            try {
                InetAddress group = InetAddress.getByName(args[0]); 
                int port = Integer.parseInt(args[1]); 
                Scanner sc = new Scanner(System.in); 
                System.out.print("Enter your name: "); //sets username in chat
                name = sc.nextLine(); //sets name
                MulticastSocket socket = new MulticastSocket(port); 
              
                socket.setTimeToLive(1); //set as 1 when not on local
                  
                socket.joinGroup(group);
                
                Thread t = new Thread(new ReadThread(socket,group,port)); //to create a group
              
                t.start(); //initiate a thread to commence the chat
                  
                System.out.println("Chat has initiated...\n"); //tells users that they can begin talking
                while(true) 
                { 
                    String message; 
                    message = sc.nextLine(); 
                    if(message.equalsIgnoreCase(CHATwME.TERMINATE)) //exits the chat if Exit is entered
                    { 
                        finished = true; 
                        socket.leaveGroup(group); 
                        socket.close(); 
                        break; 
                    } 
                    message = name + ": " + message; 
                    byte[] buffer = message.getBytes(); 
                    DatagramPacket datagram = new
                    DatagramPacket(buffer,buffer.length,group,port); 
                    socket.send(datagram); 
                } 
            }
            catch(SocketException se) {
                System.out.println("Error creating socket"); 
                se.printStackTrace();
            }
            catch(IOException ie){
                System.out.println("Error reading/writing from/to socket"); 
                ie.printStackTrace();
            }
        }
    }
    static class ReadThread implements Runnable { 
    private MulticastSocket socket; 
    private InetAddress group; 
    private int port; 
    private static final int MAX_LEN = 1000; 
    ReadThread(MulticastSocket socket,InetAddress group,int port) 
    { 
        this.socket = socket; 
        this.group = group; 
        this.port = port; 
    } 
      
    @Override
    public void run() 
    { 
        while(!CHATwME.finished) 
        { 
                byte[] buffer = new byte[ReadThread.MAX_LEN]; 
                DatagramPacket datagram = new
                DatagramPacket(buffer,buffer.length,group,port); 
                String message; 
            try
            { 
                socket.receive(datagram); 
                message = new
                String(buffer,0,datagram.getLength(),"UTF-8"); 
                if(!message.startsWith(CHATwME.name)) 
                    System.out.println(message); 
            } 
            catch(IOException e) 
            { 
                System.out.println("Socket closed!"); 
            } 
        } 
    }
    }
}
