/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package snakesandladders_server;

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
public class SClient {
    
    public int id;
    public String name = "Waiting...";
    public Socket socket;
    public ObjectOutputStream sOutput;
    public ObjectInputStream sInput;
    public ClientListenThread listenThread;
    PairingThread pairThread;
    SClient rival;

    public boolean paired = false;

    public SClient(Socket socket, int IdClient) {

        this.id = IdClient;
        this.socket = socket;

        try {

            this.sOutput = new ObjectOutputStream(this.socket.getOutputStream());
            this.sInput = new ObjectInputStream(this.socket.getInputStream());

        } catch (IOException ex) {
            Logger.getLogger(SClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.listenThread = new ClientListenThread(this);
        this.pairThread = new PairingThread(this);

    }

    public void Listen() {

        this.listenThread.start();
        System.out.println("client listening");
    }

}

class ClientListenThread extends Thread {

    private SClient client;

    public ClientListenThread(SClient client) {
        this.client = client;

    }

    @Override
    public void run() {
        while (!this.client.socket.isClosed()) {
            try {
                System.out.println("waiting message from client");
                Object msg = this.client.sInput.readObject(); 
                System.out.println(msg.toString());
//                FrmServer.clientMessagesModel.addElement(msg);

            } catch (IOException ex) {
                Logger.getLogger(ClientListenThread.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ClientListenThread.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

}


class Listen extends Thread {

    SClient TheClient;

    
    Listen(SClient TheClient) {
        this.TheClient = TheClient;
    }

    public void run() {
        //client ba??l?? oldu??u s??rece d??ns??n
        while (TheClient.socket.isConnected()) {
            try {
                
                Message received = (Message) (TheClient.sInput.readObject());
                //mesaj gelirse bu sat??ra ge??er
                //mesaj tipine g??re i??lemlere ay??r
                switch (received.type) {
                    case Name:
                        TheClient.name = received.content.toString();
                        // isim verisini g??nderdikten sonra e??le??tirme i??lemine ba??la
                        TheClient.pairThread.start();
                        break;
                    case Disconnect:
                        break;
                    case Position:
                        //pozisyonu g??ncelle, rakibe ve bana g??nder.
                        Server.SendMessage(TheClient.rival, received);
                        Server.SendMessage(TheClient, received);

                        break;
                    case Bitis:
                        break;

                }

            } catch (IOException ex) {
                Logger.getLogger(SClient.class.getName()).log(Level.SEVERE, null, ex);
                //client ba??lant??s?? koparsa listeden sil
                Server.Clients.remove(TheClient);

            } catch (ClassNotFoundException ex) {
                Logger.getLogger(SClient.class.getName()).log(Level.SEVERE, null, ex);
                //client ba??lant??s?? koparsa listeden sil
                Server.Clients.remove(TheClient);
            }
        }

    }
}

class PairingThread extends Thread {

    SClient TheClient;

    PairingThread(SClient TheClient) {
        this.TheClient = TheClient;
    }

    public void run() {
        //client ba??l?? ve e??le??memi?? oldu??u durumda d??n
        while (TheClient.socket.isConnected() && TheClient.paired == false) {
            try {

                Server.pairTwo.acquire(1);

                //client e??er e??le??memi??se gir
                if (!TheClient.paired) {
                    SClient crival = null;
                    //e??le??me sa??lanana kadar d??n
                    while (crival == null && TheClient.socket.isConnected()) {
                        for (SClient clnt : Server.Clients) {
                            if (TheClient != clnt && clnt.rival == null) {
                                //e??le??me sa??land?? ve gerekli i??aretlemeler yap??ld??
                                crival = clnt;
                                crival.paired = true;
                                crival.rival = TheClient;
                                TheClient.rival = crival;
                                TheClient.paired = true;
                                break;
                            }
                        }

                        sleep(1000);
                    }

                    Message msg1 = new Message(Message.Message_Type.RivalConnected);
                    msg1.content = TheClient.name;
                    Server.SendMessage(TheClient.rival, msg1);

                    Message msg2 = new Message(Message.Message_Type.RivalConnected);
                    msg2.content = TheClient.rival.name;
                    Server.SendMessage(TheClient, msg2);
                }

                Server.pairTwo.release(1);
            } catch (InterruptedException ex) {
                Logger.getLogger(PairingThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
