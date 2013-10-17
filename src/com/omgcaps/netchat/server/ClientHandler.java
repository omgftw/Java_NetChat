package com.omgcaps.netchat.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.util.Date;
import java.util.Scanner;

public class ClientHandler implements Runnable {
	
	Socket sock;
	Server main;
	Scanner in;
	public String ip;
	public String name = "";
	public PrintWriter out = null;
	
	
	
	public ClientHandler(Socket sock, Server main)
	{
		this.sock = sock;
		this.main = main;
		this.ip = sock.getRemoteSocketAddress().toString().replace("/", "");
	}
	
	public void sendMessage(String message)
	{
		if (out != null)
		{
			out.println(message);
			out.flush();
		}
	}
	
	public void getName()
	{
		
		try 
		{
			in = new Scanner(sock.getInputStream());
			out = new PrintWriter(sock.getOutputStream());
			
			while (true)
			{
				out.println("Please enter your nickname.");
				out.flush();
			
				if (in.hasNext())
				{
					this.name = in.nextLine().trim();
				}
				
				if (main.clientNames.contains(this.name) || this.name == "")
				{
					out.println("That name is already chosen, please pick another.");
					out.flush();
				}
				else
				{
					main.addClient(this);
					out.println("Welcome " + this.name + ".");
					out.flush();
					break;
				}
			}
			
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run()
	{
		try
		{
			//in = new Scanner(sock.getInputStream());
			//out = new PrintWriter(sock.getOutputStream());
			getName();
			
			while (true)
			{				
				
				if (in.hasNext())
				{
					String dateAndName = main.df.format(new Date()) + " " + this.name;
					String input = in.nextLine();
					
					if (input.startsWith("/"))
					{
						main.userCommand(input, this);
						System.out.println(dateAndName + " issued the command: " + input);
					}
					else
					{
						String msg =  dateAndName + ": " + input;
						main.sendAll(msg, this);
						System.out.println(msg);
					}
				}
				else
				{
					main.removeClient(this);
					in.close();
					if (!sock.isClosed())
					{sock.close();}
					break;
				}
				
			}
			
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

}
