/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package snakesandlatters_client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ozen
 */
public class Client {

    public Socket socket;
    public ObjectOutputStream cOutput;
    public ObjectInputStream cInput;

    public String serverIp;
    public int serverPort;

    public void Connect(String serverIp, int serverPort) {

        try {
            this.serverIp = serverIp;
            this.serverPort = serverPort;
            System.out.println("connecting");
            this.socket = new Socket(this.serverIp, this.serverPort);
            System.out.println("connected");
            this.cOutput = new ObjectOutputStream(this.socket.getOutputStream());
            this.cInput = new ObjectInputStream(this.socket.getInputStream());
            this.cOutput.writeObject("merhaba");
            Object msg = cInput.readObject();
            System.out.println(msg.toString());
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
