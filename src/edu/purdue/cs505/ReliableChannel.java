package edu.purdue.cs505;

public interface ReliableChannel {
	void init(String destinationIP, int dPort, int lPort);
	void rsend(Message m);
	void rlisten(IReliableChannelReceiver rc);
	void halt();
}
