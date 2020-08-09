package dev.jackwhelan.fiar;

import java.io.*;
import java.net.*;

public class Server
{
	private final int PORT = 5000;
	private ServerSocket socket;
	private int playerCount;
	private ClientHandler p1, p2;
	
	public Server()
	{
		this.playerCount = 0;
		
		try
		{
			this.socket = new ServerSocket(this.PORT);
		}
		catch(IOException e)
		{
			System.out.println("IOException in GameServer Constructor");
		}
	}
	
	public void listen()
	{
		try
		{
			System.out.println("Listening for connections on " + this.PORT);
			while (this.playerCount < 2)
			{
				Socket clientSocket = this.socket.accept();
				this.playerCount++;
				System.out.println("Player " + this.playerCount + " joined the game.");
				ClientHandler ch = new ClientHandler(clientSocket, this.playerCount);
				
				if (this.playerCount == 1)
				{
					p1 = ch;
				}
				else
				{
					p2 = ch;
				}
				
				Thread t = new Thread(ch);
				t.start();
			}
			System.out.println("Game full, no longer accepting connections.");
		}
		catch(IOException e)
		{
			System.out.println("IOException from listen method.");
		}
	}
	
	private class ClientHandler implements Runnable
	{
		private Socket socket;
		private int playerId;
		private int opponentId;
		private String playerName;
		private ObjectOutputStream out;
		private ObjectInputStream in;
		private volatile Board limboBoard;
		private int currentPlayer;
		
		public ClientHandler(Socket clientSocket, int playerId)
		{
			this.socket = clientSocket;
			this.playerId = playerId;
			this.opponentId = (this.playerId == 1) ? 1 : 2;
			this.currentPlayer = 1;
			this.limboBoard = new Board();
			
			try
			{
				this.in = new ObjectInputStream(this.socket.getInputStream());
				this.out = new ObjectOutputStream(this.socket.getOutputStream());
			}
			catch(IOException e)
			{
				System.out.println("IOException in ClientHandler constructor.");
			}
		}
		
		public void identify() throws IOException, ClassNotFoundException
		{
			Packet assignPlayerId = new Packet(this.playerId);
			this.out.writeObject(assignPlayerId);
			this.out.reset();
			Packet namePacket = (Packet)this.in.readObject();
			this.playerName = namePacket.getPlayerName();
		}
		
		public void stop()
		{
			Packet threadDeathNotice = new Packet();
			threadDeathNotice.setThreadDeath(true);
			try
			{
				this.out.writeObject(threadDeathNotice);
				this.out.reset();
				this.out.close();
				this.in.close();
				this.socket.close();
				Thread.currentThread().join();
			}
			catch (InterruptedException e)
			{
				System.out.println("InterruptedException in stop method");
			}
			catch (IOException e)
			{
				System.out.println("IOException in stop method");
			}
		}
		
		public void swapCurrentPlayer()
		{
			this.currentPlayer = (this.currentPlayer == 1) ? 2 : 1;
		}
		
		public void insertBoth(char disc, int col)
		{
			p1.insert(disc, col);
			p2.insert(disc, col);
		}
		
		public void insert(char disc, int col)
		{
			this.limboBoard.insert(disc, col);
		}
		
		@Override
		public void run()
		{
			try
			{
				this.identify();
				while(true) 
				{
					Packet req = (Packet)this.in.readObject();
					Packet res = new Packet();
					
					System.out.println("[Player " + this.playerId + " (" + this.playerName +")]: " + req.getMessage());
					if (req.getMessage().matches(".*\\d.*"))
					{
						int reqcol = Integer.parseInt(req.getMessage());
						if(p1 != null && p2 != null)
						{
							if (this.currentPlayer == this.playerId)
							{
								if (reqcol > 0 && reqcol < 10)
								{
									p1.swapCurrentPlayer();
									p2.swapCurrentPlayer();
									this.insertBoth((char)(this.playerId+'0'), reqcol);
									this.out.writeObject(new Packet("You inserted to column " + reqcol + "."));
									this.out.reset();
								}
								else
								{
									System.out.println("[Player " + this.playerId + " (" + this.playerName +")]: Attempted an illegal move. (" + reqcol + ").");
									this.out.writeObject(new Packet("There is no column " + reqcol + "!"));
									this.out.reset();
								}
							}
							else
							{
								System.out.println("[Player " + this.playerId + " (" + this.playerName +")]: Attempted to move during opponents turn.");
								this.out.writeObject(new Packet("It\'s not your turn!"));
								this.out.reset();
							}
						}
						else
						{
							System.out.println("[Player " + this.playerId + " (" + this.playerName +")]: Attempted to move before the opponent joined.");
							this.out.writeObject(new Packet("You can\'t move yet, your opponent hasn\'t joined!"));
							this.out.reset();
						}
					}
					else
					{
						switch(req.getMessage())
						{
							case "help":
								res.setMessage("To move, just enter a column number!\n\n"
										+ "Commands:\n"
										+ "\t help - shows a list of commands\n"
										+ "\t whoami - ask the server whether you\'re player 1 or 2\n"
										+ "\t board - ask the server for the current state of the board\n"
										+ "\t q - quit the game\n");
								break;
							case "whoami":
								res.setMessage("You are player " + playerId + " and you told us your name was " + this.playerName);
								break;
							case "board":
								res.setMessage("Here is the board!");
								res.setBoard(this.limboBoard);
								break;
							case "q":
								System.out.println("[Player " + playerId + "] " + this.playerName + " has disconnected.");
								this.stop();
								break;
							default:
								res.setMessage(req.getMessage() + " is not a valid command");
						}
					}
					
					try
					{
						if(res.getMessage() != null) out.writeObject(res);
						this.out.reset();
					}
					catch (IOException e)
					{
						System.out.println("[Server] " + this.playerName + " (player " + this.playerId + ") is no longer reachable.");
					}
				}
			}
			catch(IOException e)
			{
				System.out.println("IOException in run method");
			}
			catch (ClassNotFoundException e)
			{
				System.out.println("ClassNotFoundException in run method");
			}
		}
	}
	
	public static void main(String[] args)
	{
		Server server = new Server();
		server.listen();
	}
}
