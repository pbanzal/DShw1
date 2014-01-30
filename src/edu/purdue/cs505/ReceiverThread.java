package edu.purdue.cs505;

import java.net.DatagramSocket;

class ReceiverThread extends Thread {
	private DatagramSocket udpChannel;

	ReceiverThread(DatagramSocket udpChannel) {
		this.udpChannel = udpChannel;
	}
	/*
	 * Implements the receiver thread
	 */
	public void run() {
	
	}
}
