/**
 * 
 */
package communication;

/**
 * 		This interface acts as a mediator and passes messages from the player client to the server.
 *
 */
public interface Mediator {
	
	public void establishLink() throws CommunicationError;
	
	public passMessage() throws CommunicationError;
	
	
	
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
