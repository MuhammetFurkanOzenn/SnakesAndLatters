/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SnakesAndLatters_Server;

/**
 *
 * @author Ozen
 */
public class RunServer {
    public static void main(String[] args){
        Server myServer = new Server(5000);
        myServer.Listen();
        
        
    }
}
