package dev.jackwhelan.fiar;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client
{
	private Scanner sc = new Scanner(System.in);
	private int playerId;
	private int opponentId;
	private String playerName;
	private String opponentName;
	private Socket socket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private boolean active;
	private boolean myTurn;
	
	public Client()
	{
		try
		{
			this.socket = new Socket("localhost", 5000);
			this.out = new ObjectOutputStream(socket.getOutputStream());
			this.in = new ObjectInputStream(socket.getInputStream());
			this.active = true;
		}
		catch(IOException e)
		{
			System.out.println("IOException in GameClient Constructor");
		}
	}
	
	public boolean isActive()
	{
		return this.active;
	}
	
	public void writeCommand()
	{
		System.out.printf("[Player %d](%s)> ", this.playerId, this.playerName);
		String command = sc.nextLine();
		Packet commandPacket = new Packet(command);
		try
		{
			this.out.writeObject(commandPacket);
		}
		catch (IOException e)
		{
			System.out.println("IOException in writeCommand method");
		}
	}
	
	public void readResponse()
	{
		Packet responsePacket = null;
		
		try
		{
			responsePacket = (Packet)this.in.readObject();
		}
		catch (ClassNotFoundException e)
		{
			System.out.println("ClassNotFoundException in readResponse method");
		}
		catch (IOException e)
		{
			System.out.println("IOException in readResponse method");
		}
		
		// If the packet contains news of thread death (dead=true), kill the client.
		if (responsePacket.getThreadDeath())
		{
			this.active = false;
		}
		
		// If the packet contains a message, print it.
		if (responsePacket.getMessage() != null)
		{
			System.out.println("[Server] " + responsePacket.getMessage());
		}
		
		// If the packet contains a player ID and not a Board object, assign the player ID.
		if ((responsePacket.getPlayerId() != 0) && responsePacket.getBoard() == null)
		{
			this.playerId = responsePacket.getPlayerId();
		}
		
		// If the packet's "myTurn" variable is true, it's your turn!
		if (responsePacket.isMyTurn())
		{
			this.setMyTurn(true);
		}
		else if(!responsePacket.isMyTurn())
		{
			this.setMyTurn(false);
		}
		
		// If the packet contains a board state store it locally and display it.
		if (responsePacket.getBoard() != null)
		{
			responsePacket.getBoard().show();
		}
	}
	
	public void identify()
	{
		try
		{
			Packet namePacket = new Packet();
			this.playerId = ((Packet)this.in.readObject()).getPlayerId();
			if (this.playerId == 1)
			{
				this.setMyTurn(true);
			}
			else
			{
				this.setMyTurn(false);
			}
			System.out.println("Connected to server (" + this.socket.getInetAddress().getHostAddress() + ":" + this.socket.getPort() + ") as player " + this.playerId + ".");
			System.out.println("What is your name?:");
			this.playerName = sc.nextLine();
			namePacket.setPlayerName(this.playerName);
			this.out.writeObject(namePacket);
		}
		catch (ClassNotFoundException e)
		{
			System.out.println("ClassNotFoundException in identify method");
		}
		catch (IOException e)
		{
			System.out.println("IOException in identify method");
		}
	}
	
	public void close()
	{
		try
		{
			this.in.close();
			this.out.close();
			this.socket.close();
			this.sc.close();
			System.out.println("Disconnected.");
		}
		catch (IOException e)
		{
			System.out.println("IOException in close method");
		}
	}
	
	public boolean isMyTurn()
	{
		return this.myTurn;
	}
	
	public void setMyTurn(boolean isMyTurn)
	{
		this.myTurn = isMyTurn;
	}
	
	public static void main(String[] args)
	{
		Client gc = new Client();
		gc.identify();
		
		while(gc.isActive())
		{
			if(gc.isMyTurn())
			{
				System.out.println("It\'s your turn " + gc.playerName + "! Type 'help' if you need it.");
				gc.writeCommand();
				gc.readResponse();
			}
			else
			{
				System.out.println("It\'s your opponents turn!");
				gc.readResponse();
			}
		}
		
		gc.close();
	}
}
