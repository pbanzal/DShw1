package edu.purdue.cs505;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.TreeSet;

public class RChannel implements ReliableChannel {
  protected static int bufferLength = 32;
  protected static int stringLength = 300;

  protected LinkedList<Message> sendBuffer;
  protected TreeSet<Message> receiveBuffer;
  protected LinkedList<Message> userBuffer;

  private String destinationIP;
  private int destinationPort;
  private int localPort;
  private short sendSeqNo;
  private short recvSeqNo;
  private ReceiverThread rThread;
  private SenderThread sThread;
  private DatagramSocket udpChannel;
  public ReliableChannelReceiver reliableChannelReceiver;

  public void init(String destinationIP, int dPort) {
    init(destinationIP, dPort, dPort);
    return;
  }

  /*
   * Initilaize the channel. Creates a socket and starts the sender and receiver
   * thread.
   */
  public void init(String destinationIP, int dPort, int lPort) {
    try {
      sendBuffer = new LinkedList();
      userBuffer = new LinkedList();

      receiveBuffer = new TreeSet<Message>();
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
        msgToSend.setEnd(false);
        if (!send(msgToSend)) {
          continue;
        }
        stringToSend = stringToSend.substring(stringLength);
      }
      Message msgToSend = new Message(stringToSend);
      msgToSend.setEnd(true);
      send(msgToSend);
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
    synchronized (sendBuffer) {
      sendBuffer.add(msgToSend);
    }
    Debugger.print(1, "Adding to Send Buffer " + msgToSend.toString());
    return true;
  }

}
