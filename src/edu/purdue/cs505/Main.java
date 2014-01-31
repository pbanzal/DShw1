package edu.purdue.cs505;

public class Main {
  public static void main(String args[]) {
    System.out.println("Hello World2");

    ReliableChannelReceiver rcr = new ReliableChannelReceiver();

    RChannel senderTest = new RChannel(); // Sender
    senderTest.init("localhost", 5000, 4000);

    RChannel receiverTest = new RChannel(); // Receiver
    receiverTest.init("localhost", 4000, 5000);
    receiverTest.rlisten(rcr);

    test1(senderTest, receiverTest);
  }

  private static void test1(RChannel sender, RChannel receiver) {
    int num = 0;
    while (num < 100001) {
      Message m = new Message(new Integer(num).toString());
      sender.rsend(m);
      num++;
    }
    Debugger.print(2, "done!");
  }
}
