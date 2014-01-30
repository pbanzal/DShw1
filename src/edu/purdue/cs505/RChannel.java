package edu.purdue.cs505;

import java.net.DatagramSocket;
import java.net.SocketException;

public class RChannel implements ReliableChannel {
	private String destinationIP;
	private int destinationPort;
	private int localPort;
	private DatagramSocket udpChannel;
	private ReceiverThread rThread;
	private SenderThread sThread;

	protected Message[] sendBuffer;
	protected Message[] receiveBuffer;

	public void init(String destinationIP, int dPort, int lPort) {
		try {
			udpChannel = new DatagramSocket(lPort);
			Debugger.print(1, "RChannel started at port: " + lPort);
			rThread = new ReceiverThread(udpChannel);
			sThread = new SenderThread(udpChannel);

			rThread.start();
			sThread.start();
		} catch (SocketException e) {
			System.out.println("Cannot init Reliable channel because: " + e.getMessage());
		} 
	}

	/*
	 * Implement it
	 */
	public void rsend(Message m){
	
	}

	/*
	 * Implement it
	 */
	public void rlisten(ReliableChannelReceiver rc) {
	
	}

	/*
	 * Implement it
	 */
	public void halt() {
		rThread.stop();
		sThread.stop();
	}
}
