package yolo.ioopm.mud.generalobjects;

/**
 * Super class for all the entities in the game.
 * 
 * @author TheGrandmother
 *
 */
public abstract class Entity {

	/**
	 * The name of the entity. Must be unique for all entities.
	 */
	protected final String name;
	
	/**
	 * Creates a new entity
	 * @param name The name of the entitiy.
	 */
	public Entity(String name) {
		this.name = name.trim();
	}
	
	/**
	 * 
	 * Returns the name of the entity.
	 * 
	 * @return The name of the entity.
	 */
	public String getName(){
		return name;
	}
	


	
	
	
}
