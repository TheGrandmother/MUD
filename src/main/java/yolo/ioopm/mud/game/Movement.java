package yolo.ioopm.mud.game;

import java.util.Iterator;

import yolo.ioopm.mud.Server;
import yolo.ioopm.mud.communication.messages.server.ErrorMessage;
import yolo.ioopm.mud.communication.messages.server.ReplyMessage;
import yolo.ioopm.mud.generalobjects.Pc;
import yolo.ioopm.mud.generalobjects.Room;
import yolo.ioopm.mud.generalobjects.World;
import yolo.ioopm.mud.generalobjects.World.EntityNotPresent;

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
			server.sendMessage(new ErrorMessage(actor, "Door is locked and keys are not yet implemented."));
			return;
		}
		
		current_room.removePlayer(player);
		destination_room.addPlayer(player);
		player.setLocation(destination_room);
		
		server.sendMessage(new ReplyMessage(actor, Keywords.MOVE_REPLY, new String[] {"You are now in " + destination_room.getName() + "."}));
		return;
		
	}
	
	
}
