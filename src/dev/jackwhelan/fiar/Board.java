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
        rows = 6;
        cols = 9;
        slots = new char[rows][cols];
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                slots[i][j] = ' ';
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

    public Boolean insert(char disc, int col)
    {
        for (int i = rows-1; i >= 0; i--)
        {
            if(slots[i][col-1] == ' ')
            {
                slots[i][col-1] = disc;
                return true;
            }
        }
        return false;
    }
}