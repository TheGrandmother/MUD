package yolo.ioopm.mud.game;

import java.util.Iterator;

import yolo.ioopm.mud.Server;
import yolo.ioopm.mud.communication.messages.server.ErrorMessage;
import yolo.ioopm.mud.communication.messages.server.ReplyMessage;
import yolo.ioopm.mud.generalobjects.*;
import yolo.ioopm.mud.generalobjects.World.EntityNotPresent;

/**
 * 
 * This class contains all of the methods used for looking at things.
 * 
 * @author TheGrandmother
 *
 */
public final class See {

	
	public static void look(String actor,World world, Server server){
		try {
			Room current_room  = world.findPc(actor).getLocation();
			String[] observation = new String[4];
			observation[0] = "You are in room " + current_room.getName() + ".";
			observation[1] = current_room.getDescription();
			observation[2] = "";
			observation[3] = "";
			
			if(current_room.getPlayers().isEmpty()){
				observation[2] = "";
			}else{
				for (Pc pc : current_room.getPlayers()) {
					observation[2] += pc.getName()+", ";
				}
			}
			
			if(current_room.getNpcs().isEmpty()){
				observation[3] = "";
			}else{
				for (Npc npc : current_room.getNpcs()) {
					observation[3] += npc.getName()+", ";
				}
			}
			
			server.sendMessage(new ReplyMessage(actor, "look_reply", observation));
				
		} catch (EntityNotPresent e) {
			server.sendMessage(new ErrorMessage(actor, "Wtf..... you do not exist....."));
			return;
		}
		

	}
	
	
}
