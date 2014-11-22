package yolo.ioopm.mud.game;

import yolo.ioopm.mud.communication.Adapter;
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
	public static void move(String actor, String[] arguments, World world, Adapter adapter){
		if(arguments.length != 1){
			adapter.sendMessage(new ErrorMessage(actor, "Wrong nuber of arguments for move operation."));
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
			adapter.sendMessage(new ErrorMessage(actor, e1.getName()+" does not exist"));
			return;
		}
		
		Room.Door door = current_room.getExit(destination_name);
		if(door == null){
			adapter.sendMessage(new ErrorMessage(actor, current_room.getName() + " has no exit to " + destination_name+"."));
			return;
		}
		
		//This shit is pretty fucking ugly :(
		Boolean has_key = true;
		if(door.isLocked()){
			has_key = false;
			Inventory inventory = player.getInventory();
			for (ItemContainer i : inventory.getitems()) {
				if(i.getItem() instanceof Key && destination_name.equals(((Key)i.getItem()).getTargetRomm() ) ){
					if(i.getItem().getLevel() > player.getCs().getLevel()){
						adapter.sendMessage(new ErrorMessage(actor, "Key requires level " + i.getItem().getLevel() + " but you are only level" + player.getCs().getLevel()+"."));
						return;
					}else{
						has_key = true;
						break;
					}
				}
			}
		}
		
		if(has_key){
			current_room.removePlayer(player);
			destination_room.addPlayer(player);
			player.setLocation(destination_room);
			
			GameEngine.broadcastToRoom(adapter, current_room, Keywords.MOVE_BROADCAST, 
					new String[] {player.getName() + " has gone to " + destination_room.getName()+"."},player.getName());
			
			adapter.sendMessage(new ReplyMessage(actor, Keywords.MOVE_REPLY, new String[] {"You are now in " + destination_room.getName() + "."}));
			GameEngine.broadcastToRoom(adapter, destination_room, Keywords.MOVE_BROADCAST, 
					new String[] {player.getName() + " entered from " + current_room.getName()+"."},player.getName());
			return;
		}else{
			adapter.sendMessage(new ErrorMessage(actor, "You dont have the keey to " + destination_name+ "."));
			return;
		}
		
	}
	
	
}
