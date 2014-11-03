package yolo.ioopm.mud.game;

import yolo.ioopm.mud.communication.messages.ErrorMessage;
import yolo.ioopm.mud.communication.messages.IncomingMessage;
import yolo.ioopm.mud.communication.messages.OutgoingMessage;
import yolo.ioopm.mud.communication.messages.ReplyMessage;
import yolo.ioopm.mud.generalobjects.World;
import yolo.ioopm.mud.generalobjects.World.EntityNotPresent;
import yolo.ioopm.mud.generalobjects.World.EntityNotUnique;

/**
 * This class is responsible for handling the interpreting of the actions passed to the server.
 * 
 * @author TheGrandmother
 */
public class Interpreter {
	
	public static OutgoingMessage executeAction(IncomingMessage message,World world){
		
		String actor = message.getSender();
		String action = message.getAction();
		String[] arguments = message.getArguments();
		
		switch (action) {
		case Keywords.MAGIC_MOVE:
			if(arguments.length != 1){
				return new ErrorMessage(actor, action + " has the wrong number of arguments");
			}
			try {
				
				if(world.findPc(actor).getLocation().getName() == arguments[0]){
					return new ReplyMessage(actor, new String[] {"You cant move to the same room where you allready are."});
				}
				
				world.moveCharacter(world.findPc(actor), world.findRoom(arguments[0]));
				return new ReplyMessage(actor, new String[] {"You moved to " + arguments[0]});
				
			} catch (EntityNotUnique  e) {
				return new ErrorMessage(actor, "Something is not unique.....");
			} catch (EntityNotPresent e) {
				return new ErrorMessage(actor, arguments[0]+" Is not a room");
				
			}
			
			
		case Keywords.SAY:
			
			break;
			
		case Keywords.ECHO:
			return new ReplyMessage(actor, arguments);

		default:
			return new ErrorMessage(actor, action + " is not a valid keyword!");
		}
		
		
		return null;
	}
	
	//package yolo.ioopm.mud.game;


}
























