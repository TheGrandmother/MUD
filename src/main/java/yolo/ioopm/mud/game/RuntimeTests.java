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
	
	
	
	
	
	
	
	public Boolean checkNameCollisions(HashSet<? extends Entity> set) throws UnrecoverableInvairantViolation{
		if(set.isEmpty()){return true;}
		
		HashSet<Entity> buffer = new HashSet<Entity>(set);
		
		for (Entity e1 : buffer) {
			buffer.remove(e1);
			for (Entity e2 : buffer) {
				if(e1.getName().equals(e2.getName())){
					throw new UnrecoverableInvairantViolation(e1.getClass().getSimpleName()+":"+e1.getName() + " has the same name as " + e1.getClass().getSimpleName()+":"+e1.getName());
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









