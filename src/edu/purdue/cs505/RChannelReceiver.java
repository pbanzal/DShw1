package edu.purdue.cs505;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class RChannelReceiver implements ReliableChannelReceiver {
  private BufferedReader input;
  private int i;
  private PrintWriter outputStream;

  RChannelReceiver() {
    i = 0;
    try {
      input = new BufferedReader(new FileReader("random.txt"));
      outputStream = new PrintWriter(new FileWriter("output.txt"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void rreceive1(RMessage m) {
    String l;
    try {
      l = input.readLine();
      if (!l.equals(m.getMessageContents())) {
        Debugger.print(2, "XXXXXXXXXXXXX----NotMatched-----XXXXXXXXXXXX ");
        Debugger.print(2, "lenght: " + l.length() + " "
            + m.getMessageContents().length());
        outputStream.println(l + "\n != \n" + m.getMessageContents());
        outputStream.close();
        Thread.currentThread().stop();
      } else {
        Debugger.print(2, "Matched " + i);
        i++;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void rreceive(RMessage m) {
    if (!m.getMessageContents().equals(Integer.toString(i))) {
      Debugger.print(2, "Expected: " + i + " Got: " + m.getMessageContents());
    }
    i++;
    if (i % 10000 == 0) {
      Debugger.print(2, "Received till: " + i);
    }
    if (i == 100000) {
      Debugger.print(2, "All received");
    }
  }
}
