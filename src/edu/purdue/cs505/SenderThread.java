package edu.purdue.cs505;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Iterator;

class SenderThread extends Thread {
  private RChannel rChannel;

  SenderThread(RChannel rChannel) {
    this.rChannel = rChannel;
  }

  /*
   * Implements the sender thread
   */
  public void run() {
    try {
      while (true) {
        Thread.sleep(8);
        synchronized (rChannel.sendBuffer) {
          if (!rChannel.sendBuffer.isEmpty()) {
            Iterator<Message> itr = rChannel.sendBuffer.iterator();
            while (itr.hasNext()) {
              Message m = itr.next();
              if (m.isAckD()) {
                itr.remove();
              } else {
                break;
              }
            }

            itr = rChannel.sendBuffer.iterator();
            // Debugger.print(2, "Sending from: " + rChannel.sendBuffer.peek().getSeqNo() + " till " + (rChannel.sendBuffer.peek().getSeqNo() + 31));
            for (int sendCount = 0; sendCount < RChannel.bufferLength
                && itr.hasNext(); sendCount++) {
              ByteArrayOutputStream baos = new ByteArrayOutputStream();
              ObjectOutputStream oos = new ObjectOutputStream(baos);
              Message m = itr.next();
              oos.writeObject(m);

              byte[] buf = baos.toByteArray();
              DatagramPacket out = new DatagramPacket(buf, buf.length,
                  InetAddress.getByName("localhost"),
                  rChannel.getDestinationPort());
              Debugger.print(1, "UDP Send " + m.toString());
              rChannel.getUdpChannel().send(out);
              m.incResndCount();
            }
          } else {
            Debugger.print(1, "Nothing to send");
          }
        }
      }
    } catch (UnknownHostException e) {
      e.printStackTrace();
    } catch (SocketException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void putMsg(Message m) {
    rChannel.sendBuffer.add(m);
  }

}