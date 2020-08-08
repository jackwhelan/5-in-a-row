package dev.jackwhelan.fiar;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server
{
	public static final int PORT = 5000;
	private ServerSocket socket;
	private ExecutorService threadpool;
	private int playerCount;
	private ClientHandler player1, player2;
	
	public static void main(String[] args)
	{
		Server server = new Server();
		server.listen();
		try
		{
			server.socket.close();
		}
		catch (IOException e)
		{
			System.out.println("Server.java -> IOException in main method");
		}
	}
	
	public Server()
	{
		try
		{
			this.socket = new ServerSocket(PORT);
			this.threadpool = Executors.newFixedThreadPool(2);
			this.playerCount = 0;
		}
		catch(IOException e)
		{
			System.out.println("Server.java -> IOException in Constructor");
		}
	}
	
	public void listen()
	{
		while(playerCount < 2)
		{
			try
			{
				Socket clientSocket = socket.accept();
				playerCount++;
				ClientHandler newConnection = new ClientHandler(clientSocket, playerCount);
				System.out.println("Player " + playerCount + " connected. Requesting their name.");
				if (playerCount == 1)
				{
					player1 = newConnection;
				}
				else
				{
					player2 = newConnection;
				}
				threadpool.execute(newConnection);
			}
			catch (IOException e)
			{
				System.out.println("Server.java -> IOException in listen method");
			}
		}
		System.out.println("Game full, no longer accepting connections");
	}
}

class ClientHandler implements Runnable
{
	private Socket socket;
	private int playerId;
	private String name;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private Boolean active;
	
	public ClientHandler(Socket clientSocket, int playerId)
	{
		this.socket = clientSocket;
		this.playerId = playerId;
		try
		{
			this.in = new ObjectInputStream(socket.getInputStream());
			this.out = new ObjectOutputStream(socket.getOutputStream());
		}
		catch (IOException e)
		{
			System.out.println("Server.java -> IOException in ClientHandler Constructor");
		}
	}

	@Override
	public void run()
	{
		this.active = true;
		this.handshake();
		
		while(active)
		{
			try
			{
				Packet req = (Packet)in.readObject(), res = new Packet();
				System.out.println("[Player " + playerId + "]: " + this.name + " " + req.getMessage());
				switch(req.getMessage())
				{
					case "help":
						res = new Packet("Commands:\n"
								+ "\t help - shows a list of commands\n"
								+ "\t whoami - ask the server whether you\'re player 1 or 2\n"
								+ "\t board - ask the server for the current state of the board\n"
								+ "\t q - quit the game\n");
						break;
					case "q":
						System.out.println("[Player " + playerId + "] " + this.name + " has disconnected.");
						active = false;
					case "whoami":
						res = new Packet("[Server] You are player " + playerId + " and you told us your name was " + name);
					default:
						res.setMessage(req.getMessage() + " is not a valid command");
				}
				
				out.writeObject(res);
			}
			catch (IOException | ClassNotFoundException e)
			{
				System.out.println("[Server] Server.java -> IOException | ClassNotFoundException in ClientHandler run method -> req/res");
			}
		}
	}
	
	public void handshake()
	{
		try
		{
			out.writeObject(new Packet(this.playerId, "You are player " + this.playerId + ". What is your name?"));
			Packet assignName = (Packet)in.readObject();
			this.name = assignName.getMessage();
			System.out.printf("[Server] Player %d has informed the server their name is %s.\n", this.playerId, this.name);
		}
		catch (IOException | ClassNotFoundException e)
		{
			System.out.println("[Server] Server.java -> IOException | ClassNotFoundException in ClientHandler handshake method");
		}
	}
}
