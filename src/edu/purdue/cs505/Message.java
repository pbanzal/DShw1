package edu.purdue.cs505;

import java.io.Serializable;

public class Message implements IMessage, Serializable, Comparable<Message> {

  private String msgContents;
  private boolean isAck;
  private short seqNo;
  private boolean ackD;

  Message() {
    isAck = false;
    ackD = false;
  }

  Message(String s) {
    msgContents = s;
  }

  public boolean isAckD() {
    return ackD;
  }

  public void setAckD(boolean ackD) {
    this.ackD = ackD;
  }

  public boolean isAck() {
    return isAck;
  }

  public void setAck(boolean isAck) {
    this.isAck = isAck;
  }

  public short getSeqNo() {
    return seqNo;
  }

  public void setSeqNo(short seqNo) {
    this.seqNo = seqNo;
  }

  public String getMessageContents() {
    return msgContents;
  }

  public void setMessageContents(String contents) {
    msgContents = contents;
  }

  public String toString() {
    return ("SeqNo: " + seqNo + " " + msgContents + " isAck: " + isAck
        + " AckD: " + ackD);
  }

  public int compareTo(Message obj) {
    return Double.compare(this.seqNo, obj.getSeqNo());
  }
}
