package yolo.ioopm.mud.game;

import java.util.HashSet;

import yolo.ioopm.mud.communication.Adapter;
import yolo.ioopm.mud.generalobjects.Entity;
import yolo.ioopm.mud.generalobjects.World;


/**
 *This class is used to perform invariant checks at runtime.
 * 
 * @author TheGrandmother
 */
public class RuntimeTests {
	World world;
	Adapter adapter;
	
	public RuntimeTests(World world, Adapter adapter) {
		this.world = world;
		this.adapter = adapter;
	}

	
	public Boolean checkNameCollisions(HashSet<? extends Entity> set) throws UnrecoverableInvairantViolation{
		if(set.isEmpty()){return true;}
		
		HashSet<Entity> buffer = new HashSet<Entity>(set);
		boolean name_found = false;
		
		for (Entity e1 : buffer) {
			name_found = false;
			for (Entity e2 : buffer) {
				if(e1.getName().equals(e2.getName())){
					if(name_found){
						throw new UnrecoverableInvairantViolation(e1.getClass().getSimpleName()+":"+e1.getName() + " has the same name as " + e1.getClass().getSimpleName()+":"+e1.getName());
					}else{
						name_found = true;
					}
				}
			}
		}
		
		return true;
		
	}
	
	/** 
	 * These exceptions are to be thrown when an invariant violation has occurred and there is
	 * no viable option to restore the invariant.
	 * 
	 * @author TheGrandmother
	 */
	@SuppressWarnings("serial")
	public class UnrecoverableInvairantViolation extends Exception{
		public UnrecoverableInvairantViolation(String message) {
			super(message);
		}
		
	}
	
	/**
	 * These exceptions are to be thrown when an invariant violation has occurred that can be
	 * handled in a sensible fashion.
	 * 
	 * @author TheGrandmother
	 */
	@SuppressWarnings("serial")
	public class InvairantViolation extends Exception{
		public InvairantViolation(String message) {
			super(message);
		}
	}
	
}









