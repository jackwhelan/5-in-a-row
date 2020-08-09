package dev.jackwhelan.fiar;

import java.io.Serializable;

public class Packet implements Serializable
{
	private static final long serialVersionUID = 1L;
	private int playerId;
	private String message;
	private Board board;
	private int reqcol;
	
	public Packet() {}
	
	public Packet(String message)
	{
		this.message = message;
	}
	
	public Packet(int playerId, String message)
	{
		this.playerId = playerId;
		this.message = message;
	}
	
	public Packet(String message, Board board)
	{
		this.message = message;
		this.board = board;
	}
	
	public Packet(int reqcol)
	{
		this.reqcol = reqcol;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}
	
	public String getMessage()
	{
		return this.message;
	}
	
	public void setPlayerId(int playerId)
	{
		this.playerId = playerId;
	}
	
	public int getPlayerId()
	{
		return this.playerId;
	}
	
	public void setBoard(Board board)
	{
		this.board = board;
	}
	
	public Board getBoard()
	{
		return this.board;
	}
}
