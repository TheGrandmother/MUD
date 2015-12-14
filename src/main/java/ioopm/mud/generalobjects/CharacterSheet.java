package ioopm.mud.generalobjects;

/**
 * The CharacterSheet contains information about the characters current status.
 * <p>
 * <b>NOTE:</b> hp denotes university credits which is so dumb its insane. This is due to the original specification being in Swedish. One day.... it shall be refactored.
 *
 * @author TheGrandmother
 */
public class CharacterSheet {
	private final int BASE_HP = 30;
	private final double HP_FACTOR = 1.5;
	private final double HEALTH_FACTOR = 1.3;
	private int hp;
	private int health;
	private int max_health;
	private int level;

	/**
	 * Creates a new default character sheet with<p>
	 * Level = 1<p>
	 * Health = 50<p>
	 * Max_health = 50<p>
	 * Hp = 30<p>
	 */
	public CharacterSheet() {
		this.hp = BASE_HP;
		this.level = 1;
		this.health = 50;
		this.max_health = 50;
		this.hp = 30;

	}

	/**
	 * @return The hp (university credits) of the character
	 */
	public int getHp() {
		return hp;
	}

	/**
	 * sets the hp (university credits)
	 *
	 * @param hp
	 */
	public void setHp(int hp) {
		this.hp = hp;
	}

	/**
	 * Computes how many university credits left to the next level.<p>
	 * <b>NOTE:</b> This needs to correspond with how {@link CharacterSheet#levelUp()} works.
	 *
	 * @return how many hp left until the new level is reached.
	 */
	public int hpToNextLevel() {
		return (int) (BASE_HP * (HP_FACTOR * level)) - hp;
	}

	/**
	 * Checks if the player can be leveled up.<p>
	 * The mechanics of leveling will be described in the specs/Mechanics.txt file.<p>
	 * If the level-up is successful the level will be incremented and a new max health will be set
	 * and full health will be restored.
	 *
	 * @return true if the player leveled up. False otherwise.
	 */
	//TODO This seriously needs to be reworked!
	public boolean levelUp() {
		if(hp >= BASE_HP * (HP_FACTOR * level)) {
			level++;
			max_health = (int) (max_health * (HEALTH_FACTOR));
			health = max_health;
			return true;
		}
		return false;
	}

	/**
	 * Adds or subtracts hp. Can't set hp to less than 0
	 *
	 * @param hp
	 */
	public void addHp(int hp) {
		if(this.hp + hp < 0) {
			this.hp = 0;
		} else {
			this.hp += hp;
		}
	}

	/**
	 * @return The characters health
	 */
	public int getHealth() {
		return health;
	}

	/**
	 * Tries to set the Characters health to the number given as argument.
	 * If the argument given is greater than the max health of the character
	 * the character will get its maximum health.
	 *
	 * @param health
	 */
	public void setHealth(int health) {
		if(health + this.health > this.max_health) {
			this.health = this.max_health;
		} else {
			this.health = health;
		}
	}

	/**
	 * Adds the amount of health. Which can also be negative.
	 * <p>
	 * If the new health is greater than the maximum health of the
	 * character the characters health will be its maximum health.
	 * If it where to be less than zero the characters health will be set to zero.
	 *
	 * @param amount
	 */
	public void addHealth(int amount) {
		if(this.health + amount > this.max_health) {
			this.health = this.max_health;
		} else if(this.health + amount < 0) {
			this.health = 0;
		} else {
			this.health += amount;
		}
	}

	/**
	 * Sets the maximum health
	 *
	 * @param max_health
	 */
	public void setMax_health(int max_health) {
		this.max_health = max_health;
	}

	/**
	 * gets the maximum health.
	 *
	 * @return
	 */
	public int getMaxHealth() {
		return max_health;
	}

	/**
	 * @return The current level of the character
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * Sets the level of the character
	 *
	 * @param level
	 */
	public void setLevel(int level) {
		this.level = level;
	}


}
