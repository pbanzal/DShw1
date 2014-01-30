package edu.purdue.cs505;

protected class MessageClass implements Message {
	boolean isAck;
	int seqNo;
	String message;

	MessageClass() {
	}

	MessageClass(int seq) {
		seqNo = seq;
	}

	String getMessageContents() {
		return this.message;
	} 

	void setMessageContents(String contents) {
		this.message = contents;
	}

}
