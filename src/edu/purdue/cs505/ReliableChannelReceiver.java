package edu.purdue.cs505;

public class ReliableChannelReceiver implements IReliableChannelReceiver {
  private int num;

  ReliableChannelReceiver() {
    num = 0;
  }

  public void rreceive(Message m) {
    int rec = Integer.parseInt(m.getMessageContents());
    if (rec != num) {
      Debugger.print(2, "NOT MATCH Received: " + rec + " Expected: " + num);
    } else {
      if (num % 1000 == 0)
        Debugger.print(2, "Received: " + num);
      num++;
    }

  }
}
