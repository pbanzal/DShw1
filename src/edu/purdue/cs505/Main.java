package edu.purdue.cs505;

public class Main {
  public static void main(String args[]) {
    System.out.println("Hello World2");

    RChannel rc1 = new RChannel(); // At A
    rc1.init("localhost", 5000, 4000);

    RChannel rc2 = new RChannel(); // At B
    rc2.init("localhost", 4000, 5000);

    rc1.rsend(new Message("AToBMsg1"));
    rc1.rsend(new Message("AToBMsg2"));

    rc2.rsend(new Message("BToAMsg3"));
    rc2.rsend(new Message("BToAMsg4"));

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    ReliableChannelReceiver rcr = new ReliableChannelReceiver();

    rc1.rlisten(rcr);
    rc1.rlisten(rcr);
    rc2.rlisten(rcr);
    rc2.rlisten(rcr);

  }
}
