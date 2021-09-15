package taskManager.utility;

public class RecievedMessage {
	private final String		sender;
	private final String		message;
	
	public RecievedMessage(String sender, String message) {
		this.sender = sender;
		this.message = message;
	}
	public String getSender() {
		return sender;
	}
	public String getMessage() {
		return message;
	}
	@Override
	public String toString() {
		
		return sender + " " +message;
	}
}
