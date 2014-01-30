package edu.purdue.cs505;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Queue;

public class RChannel implements ReliableChannel {
	private String destinationIP;
	private int destinationPort;
	private int localPort;
	private DatagramSocket udpChannel;
	private ReceiverThread rThread;
	private SenderThread sThread;
	private int sendSeq;

	protected ArrayList<Message> sendBuffer;
	protected ArrayList<Message> receiveBuffer;

	protected static int bufferLength = 4;

	RChannel() {
		sendBuffer = new MessageClass[RChannel.bufferLength * 100];
		receiveBuffer = new MessageClass[RChannel.bufferLength];
		ackIndex = sendIndex = endIndex = sendSeq = 0;
	}

	public void init(String destinationIP, int dPort, int lPort) {
		//try {
			sendBuffer = new ArrayList();
			receiveBuffer = new ArrayList();
			destinationPort = dPort;
			localPort = lPort;
			
			//udpChannel = new DatagramSocket(lPort);
			Debugger.print(1, "RChannel started at port: " + lPort);
			rThread = new ReceiverThread(this);
			sThread = new SenderThread(this);

			rThread.start();
			sThread.start();
		//} catch (SocketException e) {
			//System.out.println("Cannot init Reliable channel because: " + e.getMessage());
		//} 
	}

	/*
	 * Implement it
	 */
	public  void  rsend(Message m){
		synchronized(sendBuffer)
		{
		Debugger.print(1, "Adding to Send Buffer"+m.getMessageContents());
		sendBuffer.add(m); //Take care of synchronization
	
		}
	}

	/*
	 * Implement it
	 */
	public void rlisten(IReliableChannelReceiver rc) {
		
		synchronized(receiveBuffer)
		{
			if(!receiveBuffer.isEmpty())
				rc.rreceive(receiveBuffer.remove(0));
		}
	}
	/*
	 * Implement it
	 */
	public void halt() {
		rThread.stop();
		sThread.stop();
	}

	public String getDestinationIP() {
		return destinationIP;
	}

	public void setDestinationIP(String destinationIP) {
		this.destinationIP = destinationIP;
	}

	public int getDestinationPort() {
		return destinationPort;
	}

	public void setDestinationPort(int destinationPort) {
		this.destinationPort = destinationPort;
	}

	public int getLocalPort() {
		return localPort;
	}

	public void setLocalPort(int localPort) {
		this.localPort = localPort;
	}
}
