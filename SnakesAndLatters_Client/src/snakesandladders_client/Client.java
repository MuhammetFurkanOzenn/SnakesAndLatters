/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package snakesandladders_client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import static snakesandladders_client.Client.sInput;

/**
 *
 * @author Ozen
 */
public class Client {

    public static Socket socket;
    public static ObjectInputStream sInput;
    public static ObjectOutputStream sOutput;

    public static Listen listenMe;

    public static void Start(String serverIp, int serverPort) {
        try {
            Client.socket = new Socket(serverIp, serverPort);
            System.out.println("Connected...");

            Client.sInput = new ObjectInputStream(Client.socket.getInputStream());
            Client.sOutput = new ObjectOutputStream(Client.socket.getOutputStream());

            Client.listenMe = new Listen();
            Client.listenMe.start();

            //ilk mesaj olarak isim gönderiyorum
            Message msg = new Message(Message.Message_Type.Name);
            //msg.content = FrmClient.frmClient.txt_name.getText();
            Client.SendMessage(msg);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void Stop() {
        try {
            if (Client.socket != null) {
                Client.listenMe.stop();
                Client.socket.close();
                Client.sOutput.flush();
                Client.sOutput.close();

                Client.sInput.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void SendMessage(Message msg) {
        try {
            Client.sOutput.writeObject(msg);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}

class Listen extends Thread {

    public void run() {

        while (Client.socket.isConnected()) {
            try {

                Message received = (Message) (sInput.readObject());                 //mesaj gelmesini bloking olarak dinyelen komut
                //mesaj gelirse bu satıra geçer
                //mesaj tipine göre yapılacak işlemi ayır.
                switch (received.type) {
                    case Name:
                        break;
                    case RivalConnected:
                        String name = received.content.toString();
                        System.out.println("rival connected...");

//                        FrmClient.frmClient.txt_rival_name.setText(name);
                        break;
                    case Disconnect:
                        break;
                    case Position:
                        //FrmClient.frmClient.lbl_oyuncu_1.setText();
                        break;
                    case Bitis:
                        break;

                }

            } catch (IOException ex) {

                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                Client.Stop();
                break;
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                Client.Stop();
                break;
            }

        }

    }
}
