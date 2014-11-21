package yolo.ioopm.mud.generalobjects.items;

import yolo.ioopm.mud.game.GameEngine;
import yolo.ioopm.mud.generalobjects.Character;
import yolo.ioopm.mud.generalobjects.Entity;
import yolo.ioopm.mud.generalobjects.Item;


public class Weapon extends Item {

	private static final int CRITICAL_NUMBER = 35;
	private static final int TARGET_NUMBER = 21;
	private int damage;
	//private int difficulty;
	
	public Weapon(String name, String description, int size, int level) {
		super(name, description, -1,  Type.WEAPON,true, size, level);
	}

	public int attack(Character user, Entity target) throws UseFailedException {
		if(!(target instanceof Character)){
			throw new UseFailedException("Dude.... you can only attack people");
		}
		Character atackee = (Character)target;
		int attack_roll =(GameEngine.d20()+GameEngine.d20() - (user.getCs().getLevel()-level));
		
		if(attack_roll >= TARGET_NUMBER){
			if(attack_roll >= CRITICAL_NUMBER){
				return (damage*3)/2;
			}else{
				int defense_roll = (GameEngine.d20()+GameEngine.d20() - (atackee.getCs().getLevel()-level));
				if(defense_roll >= TARGET_NUMBER){
					throw new UseFailedException("Defended!");
				}else{
					return damage;
				}
			}
		}else{
			throw new UseFailedException("Attack missed!");
		}
	}

	@Override
	public boolean use(Character user, Entity target) throws UseFailedException {
		// TODO Maybe i should not do stuff likw this......
		return false;
	}

}
