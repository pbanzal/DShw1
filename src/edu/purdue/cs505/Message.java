package edu.purdue.cs505;

public class Message implements IMessage{

	private String msgContents;
	
	public Message(String s)
	{
		msgContents = s;
	}
	
	@Override
	public String getMessageContents() {
		return msgContents;
	}

	@Override
	public void setMessageContents(String contents) {
		msgContents = contents;
		
	}

}
