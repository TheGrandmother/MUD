import static org.junit.Assert.*;

import java.util.HashSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import yolo.ioopm.mud.exceptions.EntityNotPresent;
import yolo.ioopm.mud.game.RuntimeTests;
import yolo.ioopm.mud.game.RuntimeTests.InvariantViolation;
import yolo.ioopm.mud.game.RuntimeTests.UnrecoverableInvariantViolation;
import yolo.ioopm.mud.generalobjects.Entity;
import yolo.ioopm.mud.generalobjects.Player;
import yolo.ioopm.mud.generalobjects.Room;
import yolo.ioopm.mud.generalobjects.World;
import yolo.ioopm.mud.generalobjects.items.Key;
import yolo.ioopm.mud.generalobjects.items.Weapon;
import yolo.ioopm.mud.generalobjects.worldbuilder.WorldBuilder;
import yolo.ioopm.mud.generalobjects.worldbuilder.WorldBuilder.BuilderException;


public class TestRtt {

	World world;
	RuntimeTests rt;
	String player_test_name = "p1";
	@Before
	public void setUp() throws Exception {
		world = new World();
		rt = new RuntimeTests();
		
	}
	
	public void makeMeAWorld() throws BuilderException{
		
		/*
		 TEST WORLD:
		 
		room:lol room: lamest room ever!.:false: none :ball room;true,the otter kingdom;false:
		room:ball room: giggle....:true: axe;150 :lol room;false,toilet;false:
		room:the otter kingdom:Here the all mighty otter rules over France!:true:Key to ball room;10:lol room;false
		room:toilet:the shitter:true:tp;20:ball room;false:
		lobby:lol room;0,ball room;5
		
		key:room2:0
		key:ball room:0
		weapon:axe:a stupid axe:5:0:20
		weapon:tp:a magnificent roll of toilet paper.:12:2:35
		*/
		world = new World();
		WorldBuilder wb = new WorldBuilder("world files/items.txt", "world files/rooms.txt");
		wb.buildWorld(world);
	}
	
	@Test
	public void testNameCollisonDetection() throws BuilderException, UnrecoverableInvariantViolation {
		makeMeAWorld();

		assertTrue(rt.checkNameCollisions(world.getRooms()));

	

		world.getRooms().add(new Room("lol", "lol"));
		world.getRooms().add(new Room("lol", "lol"));
		try {
			rt.checkNameCollisions(world.getRooms());
			fail("No exceptionw as thrown!");
		} catch (UnrecoverableInvariantViolation e) {
			assertFalse(e.getMessage(), e.getMessage().equals(""));
		}
		
		
		
	}
	
	@Test
	public void testPlayerInvariantCheck() throws BuilderException, UnrecoverableInvariantViolation, InvariantViolation, EntityNotPresent{
		makeMeAWorld();
		assertTrue("Invariant viaolations in a world with no players",rt.checkPlayersInvariant(world, true).length==0);
		world.getPlayers().add(new Player("p1", "", "", world.getLobby(0)));
		world.findPlayer("p1").setLoggedIn(true);
		world.getLobby(0).addPlayer(world.findPlayer("p1"));
		assertTrue("Invariant vialations after propperly adding a player",rt.checkPlayersInvariant(world, true).length==0);
		
		//Test for precence in multiple rooms
		world.findRoom("ball room").addPlayer(world.findPlayer("p1"));
		testResolvingPlayerIssue();
		
		
		//Test for player not being in any room but logged in (implicitly tests that player is present in his own room)
		world.findPlayer("p1").getLocation().removePlayer(world.findPlayer("p1"));
		testResolvingPlayerIssue();
		
		
		
	}
	
	public void testResolvingPlayerIssue() throws UnrecoverableInvariantViolation, InvariantViolation{
		try {
			rt.checkPlayersInvariant(world, true);
			fail("No exception was thrown");
		} catch (UnrecoverableInvariantViolation e) {
			fail("Unrecoverable violation found");
		} catch (InvariantViolation e){
			System.out.println(e.getMessage());
		}
		
		String[] report = null;
		try {
			report = rt.checkPlayersInvariant(world, false);
		} catch (UnrecoverableInvariantViolation e) {
			fail("Unrecoverable violation found");
		} catch (InvariantViolation e){
			fail("Recoverable violation found");
		}
		assertFalse("Empty report",report.length ==0);
		for (String string : report) {
			System.out.println(string);
		}
		assertTrue("Invariant could not be resolved",rt.checkPlayersInvariant(world, true).length==0);
	}
	
	
	@Test
	public void testGlobalNameSpaceCollisions() throws UnrecoverableInvariantViolation, BuilderException {
		makeMeAWorld();

		assertTrue(rt.checkGlobalNameSpace(world));
		world.getPlayers().add(new Player("fail", "", "", new Room("wierd", "shit")));
		world.getItems().add(new Weapon("fail", "", 0, 0, 0));

		try{
			rt.checkGlobalNameSpace(world);
			fail("No exception thrown!");
		}catch (UnrecoverableInvariantViolation e){
			assertFalse(e.getMessage(), e.getMessage().equals(""));
		}
		
		
	}
}
