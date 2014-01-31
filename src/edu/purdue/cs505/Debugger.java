package edu.purdue.cs505;

public class Debugger
{
	public static void print(int level, String msg)
	{
		if (level >= 1)
		{
			System.out.println(Thread.currentThread().getId() + " " + msg);
		}
		return;
	}
}
