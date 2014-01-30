package edu.purdue.cs505;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;

class ReceiverThread extends Thread
{
	private RChannel rChannel;
	private ArrayList<Message> inQ;

	ReceiverThread(RChannel rChannel)
	{
		this.rChannel = rChannel;
		inQ = rChannel.receiveBuffer;
	}

	/*
	 * Implements the receiver thread
	 */
	public void run()
	{
		byte[] buf = new byte[10000];
		DatagramPacket dgp = new DatagramPacket(buf, buf.length);
		DatagramSocket sk;

		try
		{
			sk = new DatagramSocket(rChannel.getLocalPort());

			while (true)
			{
				Debugger.print(1,
						"Waiting to rec on " + rChannel.getLocalPort());
				// Thread.sleep(1000);

				sk.receive(dgp);
				synchronized (inQ)
				{
					inQ.add(new Message(new String(dgp.getData())));
				}

				String rcvd = new String(dgp.getData(), 0, dgp.getLength())
						+ ", from address: " + dgp.getAddress() + ", port: "
						+ rChannel.getLocalPort();
				Debugger.print(1, rcvd);
			}
		} catch (SocketException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
