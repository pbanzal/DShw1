package edu.purdue.cs505;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

public class Main {
  public static void main(String args[]) {
    System.out.println("Hello World2");

    RChannelReceiver rcr = new RChannelReceiver();

    RChannel senderTest = new RChannel(); // Sender
    senderTest.init("localhost", 5000, 4000);

    RChannel receiverTest = new RChannel(); // Receiver
    receiverTest.init("localhost", 4000, 5000);
    receiverTest.rlisten(rcr);

//     try {
//	     PrintWriter outputStream = new PrintWriter(new FileWriter("random.txt"));
//	     for (int i = 0; i < 1024; i++) {
//	    	 String uuid = UUID.randomUUID().toString();
//	    	 while (uuid.length() < 10000) {
//	    		 uuid += uuid;
//	    	 }
//	    	 outputStream.println(uuid);
//	     }
//	     outputStream.close();
//     } catch (IOException e) {
//    	 e.printStackTrace();
//     }
     //return;
    test2(senderTest, receiverTest);
  }

  private static void test1(RChannel sender, RChannel receiver) {
    try {
      BufferedReader input = new BufferedReader(new FileReader("random.txt"));
      String l;
      while ((l = input.readLine()) != null) {
        RMessage m = new RMessage(l);
        sender.rsend(m);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    Debugger.print(2, "All send done!");
  }

  private static void test2(RChannel sender, RChannel receiver) {
    int i = 0;
    while (i < 100001) {
      sender.rsend(new RMessage(new String(Integer.toString(i))));
      i++;
    }
    Debugger.print(2, "All send done!");
  }
}
