package dev.jackwhelan.fiar;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client
{
	private Socket socket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private Scanner sc;
	private String SERVER_IP = "127.0.0.1";
	private int SERVER_PORT = 5000;
	private int playerId;
	private String name;
	
	public static void main(String[] args)
	{
		Client client = new Client();
		client.handshake();
		client.localCommLoop();
	}
	
	public Client()
	{
		try
		{
			this.name = "Anonymous";
			this.socket = new Socket(this.SERVER_IP, this.SERVER_PORT);
			this.sc = new Scanner(System.in);
			this.out = new ObjectOutputStream(this.socket.getOutputStream());
			this.in = new ObjectInputStream(this.socket.getInputStream());
		}
		catch(IOException e)
		{
			System.out.println("Client.java -> IOException in Client Constructor");
		}
	}
	
	public void handshake()
	{
		Packet assignId = new Packet();
		
		try
		{
			assignId = (Packet)in.readObject();
		}
		catch (ClassNotFoundException e)
		{
			System.out.println("Client.java -> ClassNotFoundException in handshake method -> assignId");
		}
		catch (IOException e)
		{
			System.out.println("Client.java -> IOException in handshake method -> assignId");
		}
		
		this.playerId = assignId.getPlayerId();
		System.out.println(assignId.getMessage());
		this.name = sc.nextLine();
		Packet assignName = new Packet(this.name);
		
		try
		{
			out.writeObject(assignName);
		}
		catch(IOException e)
		{
			System.out.println("Client.java -> IOException in handshake method -> assignName");
		}
	}
	
	public void localCommLoop()
	{
		while(true)
		{
			System.out.printf("[%s] %s > ", "Player " + this.playerId, this.name);
			String command = sc.nextLine();
			if (command.equals("q")) break;
			Packet req = new Packet(command);
			try
			{
				this.out.writeObject(req);
			}
			catch (IOException e)
			{
				System.out.println("Client.java -> IOException in localCommLoop method -> req");
			}
			
			Packet res = null;
			
			try
			{
				res = (Packet)this.in.readObject();
			}
			catch (ClassNotFoundException e)
			{
				System.out.println("Client.java -> ClassNotFoundException in localCommLoop method -> res");
			}
			catch (IOException e)
			{
				System.out.println("Client.java -> IOException in localCommLoop method -> res");
			}
			
			System.out.println("[Server] " + res.getMessage());
			if(res.getBoard() != null)
			{
				res.getBoard().show();
			}
		}
	}
}
