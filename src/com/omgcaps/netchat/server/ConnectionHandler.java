package com.omgcaps.netchat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionHandler implements Runnable {

	Server main;
	ServerSocket server;
	String test = "";
	
	public ConnectionHandler(ServerSocket server, Server main)
	{
		this.server = server;
		this.main = main;
	}
	
	public void run()
	{
		while (true)
		{
			try {
				Socket sock = server.accept();
				ClientHandler client = new ClientHandler(sock, main);
				
				Thread t = new Thread(client);
				t.start();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
