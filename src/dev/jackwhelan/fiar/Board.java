package dev.jackwhelan.fiar;

import java.io.Serializable;

public class Board implements Serializable
{
	private static final long serialVersionUID = -4923201661841085209L;
	private char[][] slots;
    private int rows;
    private int cols;

    public Board()
    {
        this.rows = 6;
        this.cols = 9;
        this.slots = new char[rows][cols];
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
            	this.slots[i][j] = ' ';
            }
        }
    }

    public void show()
    {
        System.out.printf("\n(1)(2)(3)(4)(5)(6)(7)(8)(9)\n");
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                System.out.print("[" + slots[i][j] + "]");
            }
            System.out.print("\n");
        }
        System.out.printf("\n");
    }

    public boolean insert(char disc, int col)
    {
        for (int i = rows-1; i >= 0; i--)
        {
            if(this.slots[i][col-1] == ' ')
            {
                this.slots[i][col-1] = disc;
                return true;
            }
        }
        return false;
    }
    
    public boolean check(char player)
    {
    	// Horizontal
        for (int i = 0; i < this.rows; i++)
        {
            for (int j = 0; j < this.cols-4; j++)
            {
                if (this.slots[i][j] == player && this.slots[i][j+1] == player && this.slots[i][j+2] == player && this.slots[i][j+3] == player && this.slots[i][j+4] == player)
                {
                    return true;
                }           
            }
        }
        
        // Vertical
        for (int i = 0; i < this.rows-4; i++)
        {
            for (int j = 0; j < this.cols; j++)
            {
                if (this.slots[i][j] == player && this.slots[i+1][j] == player && this.slots[i+2][j] == player && this.slots[i+3][j] == player && this.slots[i+4][j] == player)
                {
                    return true;
                }
            }
        }
        
        // Diagonal Up
		for (int i = 4; i < this.rows; i++)
		{
			for (int j = 0; j < this.cols-4; j++)
			{
				if (this.slots[i][j] == player && this.slots[i-1][j+1] == player && this.slots[i-2][j+2] == player && this.slots[i-3][j+3] == player && this.slots[i-4][j+4] == player)
				{
					return true;
				}
			}
		}
        
        // Diagonal Down
        for (int i = 0; i < this.rows-4; i++)
        {
            for (int j = 0; j < this.cols-4; j++)
            {
                if (this.slots[i][j] == player && this.slots[i+1][j+1] == player && this.slots[i+2][j+2] == player && this.slots[i+3][j+3] == player && this.slots[i+4][j+4] == player)
                {
                	return true;
                }
            }
        }
        return false;
    }
}