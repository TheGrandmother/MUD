import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import yolo.ioopm.mud.generalobjects.World;
import yolo.ioopm.mud.generalobjects.worldbuilder.WorldBuilder;
import yolo.ioopm.mud.generalobjects.worldbuilder.WorldBuilder.BuilderException;


public class TestWorldBuilder {

	World world;
	@Before
	public void testMakeWorld() throws BuilderException{
		WorldBuilder wb = new WorldBuilder("world files/test_items.txt", "world files/test_rooms.txt");
		world = wb.buildWorld();
	}
	@Test
	public void testForNull() {
		assertFalse("Room set is null", world.getRooms() == null);
		assertFalse("Item set is null", world.getItems() == null);
		assertFalse("Player set is null", world.getPlayers() == null);
		assertFalse("NPC set is null", world.getNpcs() == null);
	}
	
	@Test
	public void testExistsRooms(){
		assertFalse(world.getRooms().isEmpty());
	}
	
	@Test
	public void testExistsItems(){
		assertFalse(world.getItems().isEmpty());
	}
	
	@Test
	public void testNoPlayers(){
		assertTrue(world.getPlayers().isEmpty());
	}
	
	@Test
	public void testNoNullElements(){
		assertFalse("Contains null Rooms", world.getRooms().contains(null));
		assertFalse("Contains null Items", world.getItems().contains(null));
	}
	

}
