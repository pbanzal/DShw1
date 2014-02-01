package edu.purdue.cs505;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {
  public static void main(String args[]) {
    System.out.println("Hello World2");

    ReliableChannelReceiver rcr = new ReliableChannelReceiver();

    RChannel senderTest = new RChannel(); // Sender
    senderTest.init("localhost", 5000, 4000);

    RChannel receiverTest = new RChannel(); // Receiver
    receiverTest.init("localhost", 4000, 5000);
    receiverTest.rlisten(rcr);
    /*
     * try { PrintWriter outputStream = new PrintWriter(new
     * FileWriter("random.txt"));
     * 
     * for (int i = 0; i < 1024; i++) {
     * 
     * String uuid = UUID.randomUUID().toString(); // while (uuid.length() <
     * 1048576) { // uuid += uuid; // } outputStream.println(uuid);
     * Debugger.print(2, "one string done"); } outputStream.close();
     * Debugger.print(2, "file closed"); } catch (IOException e) {
     * e.printStackTrace(); }
     */
    test2(senderTest, receiverTest);
  }

  private static void test1(RChannel sender, RChannel receiver) {
    try {
      BufferedReader input = new BufferedReader(new FileReader("random.txt"));
      String l;
      while ((l = input.readLine()) != null) {
        Message m = new Message(l);
        sender.rsend(m);
        break;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    Debugger.print(2, "All send done!");
  }

  private static void test2(RChannel sender, RChannel receiver) {
    int i = 0;
    while (i < 1000000) {
      sender.rsend(new Message(new String(Integer.toString(i))));
      i++;
    }
    Debugger.print(2, "All send done!");
  }
}