package yolo.ioopm.mud.game;

import yolo.ioopm.mud.communication.Adapter;
import yolo.ioopm.mud.communication.messages.server.ErrorMessage;
import yolo.ioopm.mud.communication.messages.server.ReplyMessage;
import yolo.ioopm.mud.communication.messages.server.SeriousErrorMessage;
import yolo.ioopm.mud.generalobjects.*;
import yolo.ioopm.mud.generalobjects.Character.Inventory;
import yolo.ioopm.mud.generalobjects.Item.Type;
import yolo.ioopm.mud.generalobjects.World.EntityNotPresent;

/**
 * 
 * This class contains all of the methods used for looking at things.
 * 
 * @author TheGrandmother
 *
 */
public final class See {

	
	public static void look(String actor,World world, Adapter server){
		try {
			Room current_room  = world.findPc(actor).getLocation();
			String[] observation = new String[6];
			observation[0] = "You are in room " + current_room.getName() + "."; //NAME
			observation[1] = current_room.getDescription();						//DESCRIPTION
			observation[2] = "";												//EXITS
			observation[3] = "";												//PLAYERS
			observation[4] = "";												//NPCS
			observation[5] = "";												//ITEMS
			
			if(current_room.getExits().isEmpty()){
				observation[2] = "";
			}else{
				for (Room.Door door : current_room.getExits()) {
					
					if(door.isLocked()){
						observation[2] += door.getName() + " locked, ";
					}else{
						observation[2] += door.getName() + ", ";
					}
				}
				observation[2] = observation[2].substring(0, observation[2].length()-2);
			}
			
			if(current_room.getPlayers().isEmpty()){
				observation[3] = "";
			}else{
				for (Pc pc : current_room.getPlayers()) {
					observation[3] += pc.getName()+", ";
				}
				observation[3] = observation[3].substring(0, observation[3].length()-2);
			}
			
			if(current_room.getNpcs().isEmpty()){
				observation[4] = "";
			}else{
				for (Npc npc : current_room.getNpcs()) {
					observation[4] += npc.getName()+", ";
				}
				observation[4] = observation[4].substring(0, observation[4].length()-2);
			}
			
			if(current_room.getItems().isEmpty()){
				observation[5] = "";
			}else{
				for (ItemContainer item : current_room.getItems()) {
					observation[5] += "( "+ item.getAmount() + " "+item.getName()+
							"), ";
				}
				observation[5] = observation[5].substring(0, observation[5].length()-2);
			}
			
			server.sendMessage(new ReplyMessage(actor, Keywords.LOOK_REPLY, observation));
				
		} catch (EntityNotPresent e) {
			server.sendMessage(new SeriousErrorMessage(actor, "Wtf..... you do not exist....."));
			return;
		}
	}
	
	public static void inventory(String actor, World world, Adapter server){
		Inventory inventory = null;
		
		try {
			inventory = world.findPc(actor).getInventory();
		} catch (EntityNotPresent e) {
			server.sendMessage(new SeriousErrorMessage(actor, "Wtf..... you do not exist....."));
			return;
		}
		
		String[] args = new String[3];

		args[0] = ""+(inventory.getMax_volume() - inventory.getVolume());
		args[1] = ""+inventory.getMax_volume();
		args[2] ="";
		
		for (ItemContainer i : inventory.getitems()) {
			if(i.getAmount() == 1){
				args[2] += i.getName()+",";
			}else{
				args[2] += "("+i.getAmount()+" "+i.getName()+"),";
			}
		}
		
		if(args[2].length() > 1){
			args[2] = args[2].substring(0, args[2].length()-1);
		}
		
		server.sendMessage(new ReplyMessage(actor, Keywords.INVENTORY_REPLY, args));
		
	}
		
	
}
