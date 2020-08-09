package dev.jackwhelan.fiar;

import java.io.Serializable;

public class Packet implements Serializable
{
	private static final long serialVersionUID = 4016460685723998566L;
	private boolean isDead;
	private int playerId;
	private String playerName;
	private String message;
	private Board board;
	
	public Packet() {}
	
	public Packet(int playerId)
	{
		this.setPlayerId(playerId);
	}
	
	public Packet(String message)
	{
		this.setMessage(message);
	}

	public int getPlayerId()
	{
		return this.playerId;
	}
	
	public void setPlayerId(int playerId)
	{
		this.playerId = playerId;
	}
	
	public String getMessage()
	{
		return this.message;
	}
	
	public void setMessage(String message)
	{
		this.message = message;
	}
	
	public Board getBoard()
	{
		return this.board;
	}
	
	public void setBoard(Board board)
	{
		this.board = board;
	}
	
	public String getPlayerName()
	{
		return this.playerName;
	}
	
	public void setPlayerName(String name)
	{
		this.playerName = name;
	}
	
	public boolean getThreadDeath()
	{
		return this.isDead;
	}
	
	public void setThreadDeath(boolean isDead)
	{
		this.isDead = isDead;
	}
}
