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
	 * @param adapter	Whit what does he say it.
	 */
	public static void say(Pc actor, String[] arguments, World world, Adapter adapter){
		
			if(arguments == null ||arguments.length != 1){
				adapter.sendMessage( new ErrorMessage(actor.getName(), "Say takes one arguement :("));
				return;
			}
			
			String message = arguments[0];
			Room actor_location= actor.getLocation();
			
			
			ArrayList<ReplyMessage> return_messages = new ArrayList<ReplyMessage>();
			for (Pc recipient : actor_location.getPlayers()) {
				return_messages.add(new ReplyMessage(recipient.getName(),Keywords.SAY_REPLY, new String[]{actor.getName(),message}));
			}

			for (ReplyMessage msg : return_messages) {
				adapter.sendMessage(msg);
			}
		
		
		
	}
	
	/**
	 * 
	 * 
	 * @param actor who whispers
	 * @param arguments list of arguments. needs to be of length 2
	 * @param world where does the whispering occur
	 * @param adapter whit what is it wispered
	 */
	public static void whisper(Pc actor, String[] arguments, World world, Adapter adapter){
		
		if(arguments == null || arguments.length != 2){
			adapter.sendMessage( new ErrorMessage(actor.getName(), "Whisper takes to arguments :("));
			return;
		}
		
		String recipient = arguments[0];
		String message = arguments[1];
		
		Room actor_location = actor.getLocation();
		try {	
			if(actor_location.getPlayers().contains(world.findPc(recipient))){
				adapter.sendMessage(new ReplyMessage(recipient, Keywords.WHISPER_REPLY, new String[]{actor.getName(),message}));
				adapter.sendMessage(new ReplyMessage(actor.getName(), Keywords.WHISPER_REPLY, new String[]{actor.getName(),message}));
			}else{
				adapter.sendMessage( new ErrorMessage(actor.getName(), recipient + " is not in the room."));
			}
		} catch (EntityNotPresent e) {
			adapter.sendMessage( new ErrorMessage(actor.getName(), recipient + " doesn't even exist :P"));
			return;
		}
	}
	
}
