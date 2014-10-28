/**
 * 
 */
package yolo.ioopm.mud.communication;


import yolo.ioopm.mud.server.Server;

import java.util.concurrent.ConcurrentLinkedQueue;

public interface Adapter {

    /**
     * 
     * Tries to send a message trough the adapter
     * 
     * @param message
     * @throws CommunicationError
     */
    public void sendMessage(Message message) throws CommunicationError;

    
    /**
     * 
     * Polls for messages.
     * 
     * @return Returns null if there is no message waiting.
     * @throws CommunicationError
     */
    public Message pollForMessage() throws CommunicationError;

    
    
    
    @SuppressWarnings("serial")
	class CommunicationError extends Exception{
		
		public CommunicationError(){
			super();
		}
		
		public CommunicationError(String message){
			super(message);
		}
	}
}
