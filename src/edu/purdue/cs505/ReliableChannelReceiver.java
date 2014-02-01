package edu.purdue.cs505;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ReliableChannelReceiver implements IReliableChannelReceiver {
  private BufferedReader input;
  private int i;

  ReliableChannelReceiver() {
    i = 0;
    try {
      input = new BufferedReader(new FileReader("random.txt"));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  public void rreceive1(Message m) {
    String l;
    Debugger.print(2,"Receive");
    try {
      l = input.readLine();
      if (!l.equals(m.getMessageContents())) {
        Debugger.print(2, "XXXXXXXXXXXXX----NotMatched-----XXXXXXXXXXXX ");
        // Debugger.print(2, l + " != " + m.getMessageContents());
      } else {
        Debugger.print(2, "Matched " + i);
        i++;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void rreceive(Message m) {
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
