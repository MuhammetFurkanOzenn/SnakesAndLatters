/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package snakesandladders_server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ozen
 */
public class Server {

    public static int port = 0;

    
    public static ServerSocket sSocket;
    public static int IdClient = 0;
    public ServerThread listenThread;
    
    public static ServerThread runThread;
    
    public static Semaphore pairTwo = new Semaphore(1, true);

    public static ArrayList<SClient> Clients = new ArrayList<>();

    public static void Start(int port) {
        try {

            Server.port = port;
            Server.sSocket = new ServerSocket(Server.port);
            
            Server.runThread = new ServerThread();
            Server.runThread.start();

        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static void SendMessage(SClient cl, Message msg) {

        try {
            cl.sOutput.writeObject(msg);
        } catch (IOException ex) {
            Logger.getLogger(SClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}

class ServerThread extends Thread {

    @Override
    public void run() {
        while (!Server.sSocket.isClosed()) {
            try {

                System.out.println("listening");
                Socket nSocket = Server.sSocket.accept();                  
                System.out.println("client accepted");

                SClient nClient = new SClient(nSocket, Server.IdClient);
                Server.IdClient++;
                Server.Clients.add(nClient);
                
                nClient.listenThread.start();                                   

            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

    
    


}
