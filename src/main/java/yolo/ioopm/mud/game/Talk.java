package yolo.ioopm.mud.game;

import yolo.ioopm.mud.communication.Adapter;
import yolo.ioopm.mud.communication.messages.server.ErrorMessage;
import yolo.ioopm.mud.communication.messages.server.ReplyMessage;
import yolo.ioopm.mud.generalobjects.Pc;
import yolo.ioopm.mud.generalobjects.Room;
import yolo.ioopm.mud.generalobjects.World;
import yolo.ioopm.mud.generalobjects.World.EntityNotPresent;

import java.util.ArrayList;

/**
 * As the name suggest this class contains methods to handle the talking/chating
 * @author TheGrandmother
 *
 */
public abstract class Talk {

	
	/**
	 * 
	 * Brodcasts the message to all of the players in the room
	 * 
	 * @param actor		Who says what
	 * @param message	What does he say
	 * @param world		Where does he say it
	 * @param server	Whit what does he say it.
	 */
	public static void say(String actor, String message, World world, Adapter server){
		
			Room actor_location= null;
			try {
				actor_location = world.findPc(actor).getLocation();
			} catch (EntityNotPresent e) {
				server.sendMessage(new ErrorMessage(actor, "WTF!? "+actor+" does not exist... But that is you!?"));
				return;
			}
			
			
			ArrayList<ReplyMessage> return_messages = new ArrayList<ReplyMessage>();
			for (Pc recipient : actor_location.getPlayers()) {
				return_messages.add(new ReplyMessage(recipient.getName(),Keywords.SAY_REPLY, new String[]{actor,message}));
			}

			for (ReplyMessage msg : return_messages) {
				server.sendMessage(msg);
			}
		
		
		
	}
	
	/**
	 * 
	 * 
	 * @param actor who whispers
	 * @param recipient who gets whispered to
	 * @param message what gets whispered
	 * @param world where does the whispering occur
	 * @param server whit what is it wispered
	 */
	public static void whisper(String actor, String recipient, String message, World world, Adapter server){
		try {
			Room actor_location = world.findPc(actor).getLocation();
			if(actor_location.getPlayers().contains(world.findPc(recipient))){
				server.sendMessage(new ReplyMessage(recipient, Keywords.WHISPER_REPLY, new String[]{actor,message}));
				server.sendMessage(new ReplyMessage(actor, Keywords.WHISPER_REPLY, new String[]{actor,message}));
			}else{
				server.sendMessage( new ErrorMessage(actor, recipient + " not inna da room."));
			}
		} catch (EntityNotPresent e) {
			server.sendMessage( new ErrorMessage(actor, recipient + " not inna da room."));
			return;
		}
	}
	
}
