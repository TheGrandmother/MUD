package yolo.ioopm.mud.generalobjects.items;

import yolo.ioopm.mud.game.GameEngine;
import yolo.ioopm.mud.generalobjects.Character;
import yolo.ioopm.mud.generalobjects.Item;


public class Weapon extends Item {

	private static final int CRITICAL_NUMBER = 35;
	private static final int TARGET_NUMBER = 21;
	private int damage;
	//private int difficulty;
	
	public Weapon(String name, String description, int size, int level,int damage) {
		super(name, description, -1,  Type.WEAPON,true, size, level);
		this.damage = damage;
	}

	/**
	 * 
	 * This method computes how much damage was dealt to the target.
	 * 
	 * will trow UseFailedException if the attack was unsuccesfull.
	 * 
	 * @param user who is attacking
	 * @param target who is being attacked
	 * @return how much damage was dealt
	 * @throws UseFailedException	
	 */
	public int attack(Character user, Character target) throws UseFailedException {


		int attack_roll =(GameEngine.d20()+GameEngine.d20() - (user.getCs().getLevel()-level));
		
		if(attack_roll >= TARGET_NUMBER){
			if(attack_roll >= CRITICAL_NUMBER){
				return (damage*3)/2;
			}else{
				int defense_roll = (GameEngine.d20()+GameEngine.d20() - (target.getCs().getLevel()-level));
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
	public String inspect() {
		// TODO Auto-generated method stub
		return super.getDescription()+" Requires level: " + super.level + " Damage: " + damage+".";
	}

//	@Override
//	public boolean use(Character user, Entity target) throws UseFailedException {
//		// TODO Maybe i should not do stuff likw this......
//		return false;
//	}

}
