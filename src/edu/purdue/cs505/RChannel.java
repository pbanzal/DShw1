package edu.purdue.cs505;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.PriorityQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class RChannel implements ReliableChannel {
  protected static int bufferLength = 4;
  protected static int stringLength = 50000;

  protected LinkedBlockingQueue<Message> sendBuffer;
  protected PriorityQueue<Message> receiveBuffer;

  private String destinationIP;
  private int destinationPort;
  private int localPort;
  private short sendSeqNo;
  private short recvSeqNo;
  private ReceiverThread rThread;
  private SenderThread sThread;
  private DatagramSocket udpChannel;
  public ReliableChannelReceiver reliableChannelReceiver;

  /*
   * Initilaize the channel. Creates a socket and starts the sender and receiver
   * thread.
   */
  public void init(String destinationIP, int dPort, int lPort) {
    try {
      sendBuffer = new LinkedBlockingQueue();

      receiveBuffer = new PriorityQueue<Message>();
      destinationPort = dPort;
      localPort = lPort;

      udpChannel = new DatagramSocket(lPort);
      Debugger.print(1, "RChannel started at port: " + lPort);

      rThread = new ReceiverThread(this);
      sThread = new SenderThread(this);

      rThread.start();
      sThread.start();
    } catch (SocketException e) {
      System.out.println("Cannot init Reliable channel because: "
          + e.getMessage());
    }
  }

  /*
   * The method breaks the incoming message such that message Content is not
   * more than 65,507 bytes. To be on the safe side, underlying string cannot
   * contain more than 50,000 bytes
   */
  public void rsend(Message m) {
    synchronized (sendBuffer) {
      String stringToSend = m.getMessageContents();
      while (stringToSend.length() > stringLength) {
        Message msgToSend = new Message(stringToSend.substring(0, stringLength));
        if (!send(msgToSend)) {
          continue;
        }
        stringToSend = stringToSend.substring(stringLength);
      }
      send(new Message(stringToSend));
    }
  }

  /*
   * Implement it
   */
  public void rlisten(IReliableChannelReceiver rc) {
    reliableChannelReceiver = (ReliableChannelReceiver) rc;
    // if (!receiveBuffer.isEmpty())
    // rc.rreceive(receiveBuffer.remove(0));
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

  public int getDestinationPort() {
    return destinationPort;
  }

  public int getLocalPort() {
    return localPort;
  }

  public DatagramSocket getUdpChannel() {
    return udpChannel;
  }

  public short getRecvSeqNo() {
    return recvSeqNo;
  }

  public void setRecvSeqNo(short recvSeqNo) {
    this.recvSeqNo = recvSeqNo;
  }

  private void incSendSeq() {
    this.sendSeqNo = (short) ((this.sendSeqNo + 1) % Short.MAX_VALUE);
  }

  public void incRecvSeq() {
    this.recvSeqNo = (short) ((this.recvSeqNo + 1) % Short.MAX_VALUE);
  }

  /*
   * Puts the message in the buffer so that sender thread can send the message
   * If message is successfully put, returns true else false
   */
  private boolean send(Message msgToSend) {
    msgToSend.setAck(false);
    msgToSend.setSeqNo(this.sendSeqNo);
    incSendSeq();
    try {
      synchronized (sendBuffer) {
        sendBuffer.put(msgToSend);
      }
    } catch (InterruptedException e) {
      Debugger.print(1, e.getMessage());
      return false;
    }
    Debugger.print(1, "Adding to Send Buffer " + msgToSend.toString());
    return true;
  }

}
