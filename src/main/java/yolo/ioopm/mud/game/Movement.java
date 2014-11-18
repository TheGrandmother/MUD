package yolo.ioopm.mud.game;

import yolo.ioopm.mud.Server;
import yolo.ioopm.mud.communication.messages.server.ErrorMessage;
import yolo.ioopm.mud.communication.messages.server.ReplyMessage;
import yolo.ioopm.mud.generalobjects.Character.Inventory;
import yolo.ioopm.mud.generalobjects.Item.UseFailedException;
import yolo.ioopm.mud.generalobjects.ItemContainer;
import yolo.ioopm.mud.generalobjects.Pc;
import yolo.ioopm.mud.generalobjects.Room;
import yolo.ioopm.mud.generalobjects.World;
import yolo.ioopm.mud.generalobjects.World.EntityNotPresent;
import yolo.ioopm.mud.generalobjects.items.Key;

public abstract class Movement {
	
	//TODO
	//TEST THIS FUNCTION LIKE A BOSS
	public static void move(String actor, String[] arguments, World world, Server server){
		if(arguments.length != 1){
			server.sendMessage(new ErrorMessage(actor, "Wrong nuber of arguments for move operation."));
		}
		
		String destination_name = arguments[0];
		Room destination_room = null;
		Room current_room = null;
		Pc player = null;
		try {
			current_room = world.findPc(actor).getLocation();
			destination_room = world.findRoom(destination_name);
			player = world.findPc(actor);
		} catch (EntityNotPresent e1) {
			server.sendMessage(new ErrorMessage(actor, e1.getName()+" does not exist"));
			return;
		}
		
		Room.Door door = current_room.getExit(destination_name);
		if(door == null){
			server.sendMessage(new ErrorMessage(actor, current_room.getName() + " has no exit to " + destination_name+"."));
			return;
		}
		
		if(door.isLocked()){
			Inventory inventory = player.getInventory();
			for (ItemContainer i : inventory.getitems()) {
				if(i.getItem() instanceof Key && destination_name.equals(((Key)i.getItem()).getTargetRomm() ) ){
					try {
						if(((Key)i.getItem()).use(player, destination_room)){
							current_room.removePlayer(player);
							destination_room.addPlayer(player);
							player.setLocation(destination_room);
							
							server.sendMessage(new ReplyMessage(actor, Keywords.MOVE_REPLY, new String[] {"You are now in " + destination_room.getName() + "."}));
							return;
						}
					} catch (UseFailedException e) {
						server.sendMessage(new ErrorMessage(actor,e.getReason()));
						return;
					}
				}
			}
			server.sendMessage(new ErrorMessage(actor, "You dont have the keey to " + destination_name+ "."));
			return;
		}
		
		current_room.removePlayer(player);
		destination_room.addPlayer(player);
		player.setLocation(destination_room);
		
		server.sendMessage(new ReplyMessage(actor, Keywords.MOVE_REPLY, new String[] {"You are now in " + destination_room.getName() + "."}));
		return;
		
	}
	
	
}
