package edu.purdue.cs505;

import java.net.DatagramSocket;

class SenderThread extends Thread {
	private DatagramSocket udpChannel;

	SenderThread(DatagramSocket udpChannel) {
		this.udpChannel = udpChannel;
	}
	/*
	 * Implements the sender thread
	 */
	public void run() {
	
	}
}
