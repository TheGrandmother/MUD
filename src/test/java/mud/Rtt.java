package mud;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import yolo.ioopm.mud.game.RuntimeTests;
import yolo.ioopm.mud.game.RuntimeTests.UnrecoverableInvairantViolation;
import yolo.ioopm.mud.generalobjects.Room;
import yolo.ioopm.mud.generalobjects.World;
import yolo.ioopm.mud.generalobjects.worldbuilder.WorldBuilder;

public class Rtt {

	World world;
	RuntimeTests rt;
	@Before
	public void setUp() throws Exception {
		world = new World();
		WorldBuilder wb = new WorldBuilder("world files/items.txt", "world files/rooms.txt");
		wb.buildWorld(world);
		rt = new RuntimeTests(world, null);
		
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testNameCollisonDetection() {
		try {
			rt.checkNameCollisions(world.getRooms());
		} catch (UnrecoverableInvairantViolation e) {
			fail("Name collison: " + e.getMessage());
		}
		
		String fail_msg = "";
		world.getRooms().add(new Room("lol", "lol"));
		world.getRooms().add(new Room("lol", "lol"));
		try {
			rt.checkNameCollisions(world.getRooms());
		} catch (UnrecoverableInvairantViolation e) {
			fail_msg= e.getMessage();
		}
		System.out.println(fail_msg);
		assertFalse(fail_msg, fail_msg.equals(""));
		
	}

}
