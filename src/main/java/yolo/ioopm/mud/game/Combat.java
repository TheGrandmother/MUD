package yolo.ioopm.mud.game;

import yolo.ioopm.mud.communication.Adapter;
import yolo.ioopm.mud.communication.messages.server.*;
import yolo.ioopm.mud.generalobjects.Item.UseFailedException;
import yolo.ioopm.mud.generalobjects.Pc;
import yolo.ioopm.mud.generalobjects.World;
import yolo.ioopm.mud.generalobjects.World.*;

public abstract class Combat {
	
	
	/**
	 * 
	 * This method is called when an attack action gets issued.
	 * It tries to attack the target given by the first element in the arguments
	 * array.
	 * 
	 * If an attack was successful a broadcast message goes out to the room.
	 * 
	 * @param actor Who is attacking
	 * @param arguments First entry is the name of the target
	 * @param world
	 * @param adapter
	 */
	public static void attack(Pc actor, String[] arguments, World world, Adapter adapter){
		if(arguments == null || arguments.length != 1){
			adapter.sendMessage(new ErrorMessage(actor.getName(), "Attack takes only one argument."));
			return;
		}
		
		String target_name = arguments[0];
		Pc target = null;
		
		
		if(target_name.equals(actor.getName())){
			adapter.sendMessage(new ErrorMessage(actor.getName(), "Dude... why are you attacking your self?"));
			return;
		}
		
		if(!actor.getLocation().isPVP()){
			adapter.sendMessage(new ErrorMessage(actor.getName(), "PVP is not allowed in this room."));
			return;
		}
		
		try {
			target = world.findPc(target_name);
		} catch (EntityNotPresent e) {
			adapter.sendMessage(new ErrorMessage(actor.getName(), "Target " + target_name + "does not exist."));
			return;
		}
		
		if(actor.getWeapon() == null){
			adapter.sendMessage(new ErrorMessage(actor.getName(), "You don't have a weapon equiped."));
			return;
		}
		
		int damage = -1;
		
		try {
			damage = actor.getWeapon().attack(actor, target);
		} catch (UseFailedException e) {
			adapter.sendMessage(new ReplyMessage(actor.getName(), Keywords.ATTACK_REPLY, new String[]{"Your attack failed. " + e.getReason()}));
			adapter.sendMessage(new ReplyMessage(target_name, Keywords.ATTACK_REPLY, new String[]{actor.getName()+" tried to attack you but failed. " + e.getReason()}));
			return;
		}
		
		target.getCs().addHealth(-1*damage);
		
		if(target.getCs().getHealth()==0){
			target.getCs().setHealth((int)(target.getCs().getMaxHealth()*0.75));
		
			target.setLocation(world.getLobby(target.getCs().getLevel()));
			GameEngine.broadcastToRoom(adapter, target.getLocation(), target_name+" returned from the dead.", target_name);
			

			
			int hp_taken = target.getCs().getHp()*(GameEngine.d6()/10);
			target.getCs().addHp(-1*hp_taken);
			actor.getCs().addHp(hp_taken);
			adapter.sendMessage(new ReplyMessage(actor.getName(), Keywords.ATTACK_REPLY, new String[]{"You killed " + target_name + " and stole " + hp_taken + "university credists :D"}));
			adapter.sendMessage(new ReplyMessage(target_name, Keywords.ATTACK_REPLY, new String[]{"You was killed by" + actor.getName() + " and he stole " + hp_taken + "university credists!. "
					+ "You respawned in " + target.getLocation().getName()}));
			
			GameEngine.broadcastToRoom(adapter, actor.getLocation(), target_name+" was killed by "+ actor.getName() +"!",new String[]{actor.getName(),target_name});
			if(actor.getCs().levelUp()){
				adapter.sendMessage(new ReplyMessage(actor.getName(), Keywords.ATTACK_REPLY, new String[]{"Yo leled up and are now lvl: " + actor.getCs().getLevel()+
					". Your max health is now " + actor.getCs().getMaxHealth() + "!"}));
			}
			return;
		}
		
		adapter.sendMessage(new ReplyMessage(actor.getName(), Keywords.ATTACK_REPLY, new String[]{"You attacked " + target_name + " and dealt " + damage + " damage."}));
		adapter.sendMessage(new ReplyMessage(target_name, Keywords.ATTACK_REPLY, new String[]{"You was attacked by " + actor.getName() + " and suffered " + damage + " damage."}));
		
		GameEngine.broadcastToRoom(adapter, actor.getLocation(), target_name+" was attacked by "+ actor.getName() +"!",new String[]{actor.getName(),target_name});
		return;
		

	}
	
	
}
