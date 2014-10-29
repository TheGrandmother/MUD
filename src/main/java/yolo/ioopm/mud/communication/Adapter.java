package yolo.ioopm.mud.communication;

import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class Adapter {

    protected final ConcurrentLinkedQueue<Message> inbox  = new ConcurrentLinkedQueue<>();
    protected final ConcurrentLinkedQueue<Message> outbox = new ConcurrentLinkedQueue<>();

    /**
     *
     * Tries to send a message trough the adapter
     *
     * @param message
     * @throws CommunicationError
     */
    public abstract void sendMessage(Message message) throws CommunicationError;

    
    /**
     * 
     * Polls for messages.
     * 
     * @return Returns null if there is no message waiting.
     * @throws CommunicationError
     */
    public abstract Message pollForMessage() throws CommunicationError;

    
    
    
    @SuppressWarnings("serial")
	public class CommunicationError extends Exception{
		
		public CommunicationError(){
			super();
		}
		
		public CommunicationError(String message){
			super(message);
		}
	}
}
