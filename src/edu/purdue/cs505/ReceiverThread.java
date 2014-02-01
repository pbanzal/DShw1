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
    int ackFailCount = 0;
    byte[] buf = new byte[66000];
    DatagramPacket dgp = new DatagramPacket(buf, buf.length);

    /*
     * Set timeout on receive to periodically flush the receiveBuffer & thereby
     * invoke the listener callback.
     */
    try {
      rChannel.getUdpChannel().setSoTimeout(100);
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
          Debugger.print(
              1,
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
          int start = rChannel.getRecvSeqNo();
          int end = start + RChannel.bufferLength;
          end %= Short.MAX_VALUE;
          if ((start <= msgSeqNum && msgSeqNum < end)
              || (start > end && msgSeqNum >= start && msgSeqNum > end)
              || (start > end && msgSeqNum < start && msgSeqNum < end)) {
            synchronized (rChannel.receiveBuffer) {
              rChannel.receiveBuffer.add(msgReceived);
            }
            Debugger.print(1,
                "Msg Recvd: " + msgReceived.toString() + ", from address: "
                    + dgp.getAddress() + ", port: " + dgp.getPort());
            sendACK(msgReceived, dgp);
            invokeCallBack();
          }

          // Missing ACK - Frame is already received, so just send ack.
          else if (msgSeqNum < start
              || ((msgSeqNum + rChannel.bufferLength) % Short.MAX_VALUE) >= start) {
            sendACK(msgReceived, dgp);
            ackFailCount++;
            // if (ackFailCount % 10000 == 0)
            // Debugger.print(2, "ACK fail Count " + ackFailCount);
            // Debugger.print(2, "msgSeqNum: " + msgSeqNum + " MaxSeqNum: "
            // + ((rChannel.getRecvSeqNo() + RChannel.bufferLength)));
          } else {

            Debugger.print(
                2,
                "Gadbad msgSeqNum: " + msgSeqNum + " startSeq: "
                    + rChannel.getRecvSeqNo() + " MaxSeqNum: "
                    + ((rChannel.getRecvSeqNo() + RChannel.bufferLength)));
            Debugger.print(2, " " + ackFailCount);
            // Thread.currentThread().stop();
            // Ignore frames whose seqNum > upper bound of
            // window.
          }
        }
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
   * Send back ACK
   */
  private void sendACK(Message msgReceived, DatagramPacket dgp) {
    byte[] buf = new byte[66000];
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos;
    try {
      oos = new ObjectOutputStream(baos);
      msgReceived.setAck(true);
      oos.writeObject(msgReceived);
      buf = baos.toByteArray();
      DatagramPacket out = new DatagramPacket(buf, buf.length,
          dgp.getAddress(), dgp.getPort());
      Debugger.print(1, "Sending Ack for: " + msgReceived.toString());
      rChannel.getUdpChannel().send(out);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /*
   * Sorts the received packets in FIFO order and invokes callback for each
   * message.
   */
  private void invokeCallBack() {
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
          rChannel.userBuffer.add(msg);
        } else if (msg.getSeqNo() < expected) {
          itr.remove();
        } else {
          break;
        }
      }
    }

    if (!rChannel.userBuffer.isEmpty()
        && rChannel.reliableChannelReceiver != null) {
      while (!rChannel.userBuffer.isEmpty()) {
        rChannel.reliableChannelReceiver.rreceive(rChannel.userBuffer.remove());
      }
    }
  }
}