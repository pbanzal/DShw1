package edu.purdue.cs505;

import java.io.Serializable;

public class RMessage implements Message, Serializable, Comparable<RMessage> {

  private String msgContents;
  private boolean isAck;
  private short seqNo;
  private boolean ackD;
  private int resndCount;
  private boolean end;

  RMessage() {
    isAck = false;
    ackD = false;
    resndCount = 0;
  }

  RMessage(String s) {
    msgContents = s;
  }

  public int getResndCount() {
    return resndCount;
  }

  public void incResndCount() {
    this.resndCount++;
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
        + " AckD: " + ackD + " isEnd " + end);
  }

  public boolean isEnd() {
    return end;
  }

  public void setEnd(boolean end) {
    this.end = end;
  }

  public int compareTo(RMessage obj) {
    return Double.compare(this.seqNo, obj.getSeqNo());
  }

}
