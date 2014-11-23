package yolo.ioopm.mud.game;

import java.io.ObjectInputStream.GetField;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

import yolo.ioopm.mud.communication.Adapter;
import yolo.ioopm.mud.communication.messages.server.*;
import yolo.ioopm.mud.generalobjects.Item.UseFailedException;
import yolo.ioopm.mud.generalobjects.Pc;
import yolo.ioopm.mud.generalobjects.World;
import yolo.ioopm.mud.generalobjects.World.*;

public abstract class Combat {
	
	public static void attack(String actor, String[] arguments, World world, Adapter adapter){
		if(arguments.length != 1){
			adapter.sendMessage(new ErrorMessage(actor, "Attack takes only one argument."));
			return;
		}
		
		String target_name = arguments[0];
		Pc attacker = null;
		Pc target = null;
		
		try {
			attacker = world.findPc(actor);
		} catch (EntityNotPresent e) {
			adapter.sendMessage(new SeriousErrorMessage(actor, "Actor does not exist!"));
			return;
		}
		
		if(target_name.equals(actor)){
			adapter.sendMessage(new ErrorMessage(actor, "Dude... why are you attacking your self?"));
			return;
		}
		
		if(!attacker.getLocation().isPVP()){
			adapter.sendMessage(new ErrorMessage(actor, "PVP is not allowed in this room."));
			return;
		}
		
		try {
			target = world.findPc(target_name);
		} catch (EntityNotPresent e) {
			adapter.sendMessage(new ErrorMessage(actor, "Target " + target_name + "does not exist."));
			return;
		}
		
		if(attacker.getWeapon() == null){
			adapter.sendMessage(new ErrorMessage(actor, "You don't have a weapon equiped."));
			return;
		}
		
		int damage = -1;
		
		try {
			damage = attacker.getWeapon().attack(attacker, target);
		} catch (UseFailedException e) {
			adapter.sendMessage(new ReplyMessage(actor, Keywords.ATTACK_REPLY, new String[]{"Your attack failed. " + e.getReason()}));
			adapter.sendMessage(new ReplyMessage(target_name, Keywords.ATTACK_REPLY, new String[]{actor+" tried to attack you but failed. " + e.getReason()}));
			return;
		}
		
		target.getCs().addHealth(-1*damage);
		
		if(target.getCs().getHealth()==0){
			target.getCs().setHealth((int)(target.getCs().getMaxHealth()*0.75));
			try {
				target.setLocation(GameEngine.get_lobby(target, world));
				GameEngine.broadcastToRoom(adapter, target.getLocation(), target_name+" returned frommthe dead.", target_name);
				
			} catch (EntityNotPresent e) {
				adapter.sendMessage(new SeriousErrorMessage(actor, "Some how the target dos not exist any more."));
				return;
			}
			
			int hp_taken = target.getCs().getHp()*(GameEngine.d6()/10);
			target.getCs().addHp(-1*hp_taken);
			attacker.getCs().addHp(hp_taken);
			adapter.sendMessage(new ReplyMessage(actor, Keywords.ATTACK_REPLY, new String[]{"You killed " + target_name + " and stole " + hp_taken + "university credists!"}));
			adapter.sendMessage(new ReplyMessage(target_name, Keywords.ATTACK_REPLY, new String[]{"You was killed by" + actor + " and he stole " + hp_taken + "university credists!. "
					+ "You respawned in " + target.getLocation().getName()}));
			
			GameEngine.broadcastToRoom(adapter, attacker.getLocation(), target_name+" was killed by "+ actor +"!",new String[]{actor,target_name});
			return;
		}
		
		adapter.sendMessage(new ReplyMessage(actor, Keywords.ATTACK_REPLY, new String[]{"You attacked " + target_name + " and dealt " + damage + " damage."}));
		adapter.sendMessage(new ReplyMessage(target_name, Keywords.ATTACK_REPLY, new String[]{"You was attacked by " + actor + " and suffered " + damage + " damage."}));
		
		GameEngine.broadcastToRoom(adapter, attacker.getLocation(), target_name+" was attacked by "+ actor +"!",new String[]{actor,target_name});
		return;
		

	}
	
	
}
