package com.omgcaps.netchat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Server {
	
	ClientHandler client = null;
	DateFormat df = new SimpleDateFormat("HH:mm:ss");
	List<ClientHandler> clients = new ArrayList<ClientHandler>();
	List<String> clientNames = new ArrayList<String>();
	
	public void addClient(ClientHandler client)
	{
		clients.add(client);
		clientNames.add(client.name);
		System.out.println(df.format(new Date()) + " (" + client.ip + ") has connected with the nickname: " + client.name + ".");
		sendAll(client.ip + " has connected with the nickname: " + client.name + ".", client);
	}
	
	public void removeClient(ClientHandler client)
	{
		int ind = clients.indexOf(client);
		clientNames.remove(ind);
		clients.remove(ind);
		System.out.println(df.format(new Date()) + " (" + client.ip + ") has disconnected.");
		sendAll(client.name  + " (" + client.ip + ") has disconnected.", client);
	}
	
	public void sendAll(String message, ClientHandler sender)
	{
		for (int i = 0; i < clients.size(); i++)
		{
			if (sender != clients.get(i))
			{
				clients.get(i).sendMessage(message);
			}
		}
	}
	
	public void userCommand(String command, ClientHandler client)
	{
		if (command.equalsIgnoreCase("/help"))
		{
			client.sendMessage("Available Commands:");
			client.sendMessage("\t/help");
			client.sendMessage("\t/list");
			client.sendMessage("\t/exit");
		}
		else if (command.equalsIgnoreCase("/list"))
		{
			if (clients.size() > 0)
			{
				client.sendMessage("Connected Users:");
				for (int i = 0; i < clients.size(); i++)
				{
					client.sendMessage("\t" + clientNames.get(i) + " - " + clients.get(i).ip);
				}
			}
		}
		else if (command.equalsIgnoreCase("/exit"))
		{
			client.sendMessage("You have disconnected.");
			try {client.sock.close();} catch (IOException e) {e.printStackTrace();}
		}
		else
		{
			client.sendMessage("Command \"" + command + "\" not found.");
		}
	}
	
	public void main()
	{
			Scanner cmds = new Scanner(System.in);
			
				try
				{
					final int port = 1235;
					
					ServerSocket server = new ServerSocket(port);
					ConnectionHandler con = new ConnectionHandler(server, this);
					
					Thread t = new Thread(con);
					t.start();
					
					System.out.println("Waiting for connections");
					
					
					while (true)
					{
						if (cmds.hasNext())
						{
							String inp = cmds.nextLine();
							
							if (inp.equals("/clients"))
							{
								if (clients.size() > 0)
								{
									System.out.println("Clients:");
									for (int i = 0; i < clients.size(); i++)
									{
										System.out.println("\t" + clientNames.get(i) + " - " + clients.get(i).ip);
									}
								}
								else
								{
									System.out.println("No clients connected.");
								}
							}
							
							else if (inp.startsWith("/kick"))
							{
								String user = inp.substring(6);
								//Integer clientID = -1;
								for (int i = 0; i < clients.size(); i++)
								{
									ClientHandler curClient = clients.get(i);
									if (curClient.ip.contains(user) || clientNames.get(i).contains(user))
									{
										curClient.sendMessage("You have been kicked!");
										curClient.sock.close();
										break;
									}
								}
							}
							
						}
					}
					
				}
				catch(Exception ex)
				{
					System.out.println("Error:");
					ex.printStackTrace();
				}
		
				cmds.close();
	}

	
	public void dispatch(String inp)
	{
		
	}

}
