package edu.purdue.cs505;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

class SenderThread extends Thread
{
	private RChannel rChannel;
	private ArrayList<Message> outQ;

	SenderThread(RChannel rChannel)
	{
		this.rChannel = rChannel;
		this.outQ = rChannel.sendBuffer;
	}

	/*
	 * Implements the sender thread
	 */
	public void run()
	{
		try
		{
			Thread.sleep(1000); // Let recvrs start listening.
			while (true)
			{
				synchronized (outQ)
				{
					if (!outQ.isEmpty())
					{
						String outMessage = outQ.remove(0).getMessageContents();
						byte[] buf = outMessage.getBytes();
						DatagramPacket out = new DatagramPacket(buf,
								buf.length, InetAddress.getByName("localhost"), // TODO
																				// replace
																				// with
																				// hostadd
								rChannel.getDestinationPort());
						DatagramSocket s = new DatagramSocket();
						Debugger.print(1, "Sending" + outMessage);
						s.send(out);
					}
				}
			}

		} catch (UnknownHostException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SocketException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void putMsg(Message m)
	{
		outQ.add(m);
	}

}
