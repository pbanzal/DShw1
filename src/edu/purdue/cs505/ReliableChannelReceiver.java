package edu.purdue.cs505;

public class ReliableChannelReceiver implements IReliableChannelReceiver
{

	@Override
	public void rreceive(Message m)
	{
		Debugger.print(1, "*******************************");
		System.out.println(m.getMessageContents());
	}
}
