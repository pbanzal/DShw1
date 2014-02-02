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
        Thread.sleep(RChannel.timeout);
        synchronized (rChannel.sendBuffer) {
          if (!rChannel.sendBuffer.isEmpty()) {
            Iterator<RMessage> itr = rChannel.sendBuffer.iterator();
            while (itr.hasNext()) {
              RMessage m = itr.next();
              if (m.isAckD()) {
                itr.remove();
              } else {
                break;
              }
            }

            itr = rChannel.sendBuffer.iterator();
            for (int sendCount = 0; sendCount < RChannel.bufferLength
                && itr.hasNext(); sendCount++) {
              ByteArrayOutputStream baos = new ByteArrayOutputStream();
              ObjectOutputStream oos = new ObjectOutputStream(baos);
              RMessage m = itr.next();
              oos.writeObject(m);

              byte[] buf = baos.toByteArray();
              DatagramPacket out = new DatagramPacket(buf, buf.length,
                  InetAddress.getByName(rChannel.getDestinationIP()),
                  rChannel.getDestinationPort());
              Debugger.print(1, "UDP Send " + m.toString());
              // Test:Create Congestion - Simulate missing frames.
              // Random generator = new Random();
              // int i = generator.nextInt(2); //a random number either 0/1.
              // if(i%2 == 0)
              // {
              rChannel.getUdpChannel().send(out);
              // }
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

  public void putMsg(RMessage m) {
    rChannel.sendBuffer.add(m);
  }

}