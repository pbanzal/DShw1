package edu.purdue.cs505;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Iterator;

class ReceiverThread extends Thread {
  private RChannel rChannel;

  ReceiverThread(RChannel rChannel) {
    this.rChannel = rChannel;
  }

  /*
   * Implements the receiver thread
   */
  public void run() {
    byte[] buf = new byte[66000];
    DatagramPacket dgp = new DatagramPacket(buf, buf.length);

    /*
     * Set timeout on receive to periodically flush the receiveBuffer & thereby
     * invoke the listener callback.
     */
    try {
      rChannel.getUdpChannel().setSoTimeout(1000);
    } catch (SocketException e1) {
      e1.printStackTrace();
    }

    while (true) {
      try {
          rChannel.getUdpChannel().receive(dgp);
          ByteArrayInputStream bais = new ByteArrayInputStream(dgp.getData());
          ObjectInputStream ois = new ObjectInputStream(bais);
          Message msgReceived = (Message) ois.readObject();

          if (msgReceived.isAck()) {
            Debugger.print(1,
                "Ack Recvd: " + msgReceived.toString() + ", from address: "
                    + dgp.getAddress() + ", port: " + dgp.getPort());

            synchronized (rChannel.sendBuffer) {
              if (!rChannel.sendBuffer.isEmpty()) {
                Iterator<Message> itr = rChannel.sendBuffer.iterator();
                Debugger.print(1, "Iterating for setting ackD");
                for (int sendCount = 0; sendCount < RChannel.bufferLength
                    && itr.hasNext(); sendCount++) {
                  Message m = itr.next();
                  Debugger.print(1, m.toString());
                  if (m.getSeqNo() == msgReceived.getSeqNo()) {
                    m.setAckD(true);
                    Debugger.print(1, "Found and ackD saved");
                    break;
                  }
                }
              }
            }
          }
          // Not Ack
          else {
            int msgSeqNum = msgReceived.getSeqNo();

            // Frame is as expected, within receiver window size limits.
            if (rChannel.getRecvSeqNo() <= msgSeqNum
                && msgSeqNum <= rChannel.getRecvSeqNo() + RChannel.bufferLength) {
              synchronized (rChannel.receiveBuffer) {
                rChannel.receiveBuffer.add(msgReceived);
              }

              Debugger.print(1,
                  "Msg Recvd: " + msgReceived.toString() + ", from address: "
                      + dgp.getAddress() + ", port: " + dgp.getPort());

              // Sending back the Ack
              ByteArrayOutputStream baos = new ByteArrayOutputStream();
              ObjectOutputStream oos = new ObjectOutputStream(baos);
              msgReceived.setAck(true);
              oos.writeObject(msgReceived);
              buf = baos.toByteArray();
              DatagramPacket out = new DatagramPacket(buf, buf.length,
                  dgp.getAddress(), dgp.getPort());
              Debugger.print(1, "Sending Ack for: " + msgReceived.toString());
              rChannel.getUdpChannel().send(out);
            }

            // Missing ACK - Frame is already received, so just send ack.
            else if (msgSeqNum < rChannel.getRecvSeqNo()) {
              // Sending back the Ack
              ByteArrayOutputStream baos = new ByteArrayOutputStream();
              ObjectOutputStream oos = new ObjectOutputStream(baos);
              msgReceived.setAck(true);
              oos.writeObject(msgReceived);
              buf = baos.toByteArray();
              DatagramPacket out = new DatagramPacket(buf, buf.length,
                  dgp.getAddress(), dgp.getPort());
              Debugger.print(1, "Sending Ack for: " + msgReceived.toString());
              rChannel.getUdpChannel().send(out);
            } else {
              // Ignore frames whose seqNum > upper bound of
              // window.
            }
          }
        invokeCallBack();
      } catch (SocketTimeoutException e) {
        invokeCallBack();
      } catch (SocketException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
    }
  }

  /*
   * Sorts the received packets in FIFO order and invokes callback for each
   * message.
   */
  public void invokeCallBack() {
    if (!rChannel.receiveBuffer.isEmpty()) {
      // Invoke callback till successive messages are sequential.
      Iterator<Message> itr = rChannel.receiveBuffer.iterator();
      short expected = rChannel.getRecvSeqNo();
      while (itr.hasNext()) {
        Message msg = itr.next();
        if (expected == msg.getSeqNo()) {
          expected++;
          rChannel.incRecvSeq();
          itr.remove();
          rChannel.reliableChannelReceiver.rreceive(msg);
        } else {
          rChannel.setRecvSeqNo(expected);
          break;
        }
      }
    }
  }
}