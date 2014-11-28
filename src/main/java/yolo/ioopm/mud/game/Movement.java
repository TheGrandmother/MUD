package yolo.ioopm.mud.game;

import yolo.ioopm.mud.communication.Adapter;
import yolo.ioopm.mud.communication.messages.server.ErrorMessage;
import yolo.ioopm.mud.communication.messages.server.ReplyMessage;
import yolo.ioopm.mud.generalobjects.Character.Inventory;
import yolo.ioopm.mud.generalobjects.ItemContainer;
import yolo.ioopm.mud.generalobjects.Player;
import yolo.ioopm.mud.generalobjects.Room;
import yolo.ioopm.mud.generalobjects.World;
import yolo.ioopm.mud.generalobjects.World.EntityNotPresent;
import yolo.ioopm.mud.generalobjects.items.Key;

public abstract class Movement {
	
	//TODO
	//TEST THIS FUNCTION LIKE A BOSS
	public static void move(Player actor, String[] arguments, World world, Adapter adapter){
		if(arguments == null || arguments.length != 1){
			adapter.sendMessage(new ErrorMessage(actor.getName(), "Wrong number of arguments for move operation."));
			return;
		}
		
		String destination_name = arguments[0];
		Room destination_room = null;
		Room current_room = actor.getLocation();

		try {
			
			destination_room = world.findRoom(destination_name);

		} catch (EntityNotPresent e1) {
			adapter.sendMessage(new ErrorMessage(actor.getName(), destination_name + " isn't even a real room!"));
			return;
		}
		
		Room.Exit door = current_room.getExit(destination_name);
		if(door == null){
			adapter.sendMessage(new ErrorMessage(actor.getName(), current_room.getName() + " has no exit to " + destination_name+"."));
			return;
		}
		
		//This shit is pretty fucking ugly :(
		Boolean has_key = true;
		if(door.isLocked()){
			has_key = false;
			Inventory inventory = actor.getInventory();
			for (ItemContainer i : inventory.getitems()) {
				if(i.getItem() instanceof Key && destination_name.equals(((Key)i.getItem()).getTargetRoom() ) ){
					if(i.getItem().getLevel() > actor.getCs().getLevel()){
						adapter.sendMessage(new ErrorMessage(actor.getName(), "Key requires level " + i.getItem().getLevel() + " but you are only level" + actor.getCs().getLevel()+"."));
						return;
					}else{
						has_key = true;
						break;
					}
				}
			}
		}
		
		if(has_key){
			
			current_room.removePlayer(actor);
			destination_room.addPlayer(actor);
			actor.setLocation(destination_room);
			
			GameEngine.broadcastToRoom(adapter, current_room, actor.getName() + " has gone to " + destination_room.getName()+".",actor.getName());
			
			adapter.sendMessage(new ReplyMessage(actor.getName(), Keywords.MOVE_REPLY, "You are now in " + destination_room.getName() + "."));
			GameEngine.broadcastToRoom(adapter, destination_room, actor.getName() + " entered from " + current_room.getName()+".",actor.getName());
			return;
			
		}else{
			adapter.sendMessage(new ErrorMessage(actor.getName(), "You don't have the key to " + destination_name+ "."));
			return;
		}
		
	}
	
	
}
