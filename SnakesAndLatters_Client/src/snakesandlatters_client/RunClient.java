/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package snakesandlatters_client;

/**
 *
 * @author Ozen
 */
public class RunClient {
    public static void main(String[] args) {
        Client myClient = new Client();
        myClient.Connect("127.0.0.1", 5000);
        
        
    }
}
