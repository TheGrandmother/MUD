package tests;
import static org.junit.Assert.*;

import java.util.HashSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import yolo.ioopm.mud.exceptions.EntityNotPresent;
import yolo.ioopm.mud.exceptions.EntityNotUnique;
import yolo.ioopm.mud.game.RuntimeTests;
import yolo.ioopm.mud.game.RuntimeTests.InvariantViolation;
import yolo.ioopm.mud.game.RuntimeTests.UnrecoverableInvariantViolation;
import yolo.ioopm.mud.generalobjects.Entity;
import yolo.ioopm.mud.generalobjects.ItemContainer;
import yolo.ioopm.mud.generalobjects.Player;
import yolo.ioopm.mud.generalobjects.Room;
import yolo.ioopm.mud.generalobjects.World;
import yolo.ioopm.mud.generalobjects.items.Key;
import yolo.ioopm.mud.generalobjects.items.Weapon;
import yolo.ioopm.mud.generalobjects.worldbuilder.WorldBuilder;
import yolo.ioopm.mud.generalobjects.worldbuilder.WorldBuilder.BuilderException;


public class TestRtt {

	private World world;
	private RuntimeTests rt;
	private final static String test_player_name = "p1";
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
	public void testLobbySystem() throws BuilderException, EntityNotPresent, UnrecoverableInvariantViolation, InvariantViolation{
		//Will make use of testResolvingPlayerIssue
		makeMeAWorld();
		world.getPlayers().add(new Player(test_player_name, "", "", world.getLobby(0)));
		world.findPlayer(test_player_name).setLoggedIn(true);
		world.getLobby(0).addPlayer(world.findPlayer(test_player_name));
		assertEquals("lol room", world.findPlayer(test_player_name).getLocation().getName());
		world.findPlayer(test_player_name).getCs().setLevel(7);
		world.findPlayer(test_player_name).getLocation().removePlayer(world.findPlayer(test_player_name));
		testResolvingPlayerIssue();
		assertEquals("ball room", world.findPlayer(test_player_name).getLocation().getName());
		
		
	}
	
	@Test 
	public void testRoomInvariantCheck() throws BuilderException, UnrecoverableInvariantViolation, InvariantViolation, EntityNotUnique, EntityNotPresent{
		makeMeAWorld();
		assertTrue("Generated world does not pass invariant check",rt.checkRoomInvariant(world, true).length == 0);
		//Test for a room with no exits
		makeMeAWorld();
		world.getRooms().add(new Room("lol", ""));
		try{
			assertFalse("World with violations passed invariant chek.",rt.checkRoomInvariant(world, true).length == 0);
			fail("no exception thrown");
		}catch(InvariantViolation e){
			fail("wrong exception type");
		}catch(UnrecoverableInvariantViolation e){
			System.out.println(e.getMessage());
		}

		//Test for duplicate names
		makeMeAWorld();
		world.getRooms().add(new Room("lol room",""));
		try{
			assertFalse("World with violations passed invariant chek.",rt.checkRoomInvariant(world, true).length == 0);
			fail("no exception thrown");
		}catch(InvariantViolation e){
			fail("wrong exception type");
		}catch(UnrecoverableInvariantViolation e){
			System.out.println(e.getMessage());
		}
		
		//Test for double containers
		makeMeAWorld();
		world.findRoom("lol room").getItems().add(new ItemContainer(world.findItem("tp")));
		world.findRoom("lol room").getItems().add(new ItemContainer(world.findItem("tp")));
		testResolvingRoomIssue();
		
		//Test for less than zero amount
		makeMeAWorld();
		ItemContainer silly =new ItemContainer(world.findItem("tp"));
		silly.setAmount(-10);
		world.findRoom("lol room").getItems().add(silly);
		testResolvingRoomIssue();
		
		//Test for non exsisting item
		makeMeAWorld();
		world.findRoom("lol room").getItems().add(new ItemContainer(new Weapon("lol", "", 0, 0, 0)));
		testResolvingRoomIssue();
		
		//Test logged out player.
		world.findRoom("lol room").getPlayers().add(new Player(test_player_name, "", "", world.findRoom("lol room")));
		testResolvingRoomIssue();
		
		
		
		
		
	}
	
	@Test
	public void testPlayerInvariantCheck() throws BuilderException, UnrecoverableInvariantViolation, InvariantViolation, EntityNotPresent{
		makeMeAWorld();
		assertTrue("Invariant viaolations in a world with no players",rt.checkPlayersInvariant(world, true).length==0);
		world.getPlayers().add(new Player(test_player_name, "", "", world.getLobby(0)));
		world.findPlayer(test_player_name).setLoggedIn(true);
		world.getLobby(0).addPlayer(world.findPlayer(test_player_name));
		assertTrue("Invariant vialations after propperly adding a player",rt.checkPlayersInvariant(world, true).length==0);
		
		//Test for precence in multiple rooms
		world.findRoom("ball room").addPlayer(world.findPlayer(test_player_name));
		testResolvingPlayerIssue();
		
		
		//Test for player not being in any room but logged in (implicitly tests that player is present in his own room)
		world.findPlayer(test_player_name).getLocation().removePlayer(world.findPlayer(test_player_name));
		testResolvingPlayerIssue();
		
		//Test for player being present in room but logged out.
		world.findPlayer(test_player_name).setLoggedIn(false);
		testResolvingPlayerIssue();
		
		//Test for player being logged out but not in any room.
		world.findPlayer(test_player_name).setLoggedIn(true);
		testResolvingPlayerIssue();
		
		//Test for player not logged in and present in multiple rooms
		world.findRoom("ball room").addPlayer(world.findPlayer(test_player_name));
		world.findPlayer(test_player_name).setLoggedIn(false);
		testResolvingPlayerIssue();
		
		//Test health less than -1
		world.findPlayer(test_player_name).getCs().setHealth(-100);
		testResolvingPlayerIssue();
		
		
	}
	
	
	public void testResolvingRoomIssue() throws UnrecoverableInvariantViolation, InvariantViolation{
		try {
			rt.checkRoomInvariant(world, true);
			fail("No exception was thrown");
		} catch (UnrecoverableInvariantViolation e) {
			fail("Unrecoverable violation thrown");
		} catch (InvariantViolation e){
			//System.out.println(e.getMessage());
		}
		
		String[] report = null;
		try {
			report = rt.checkRoomInvariant(world, false);
		} catch (UnrecoverableInvariantViolation e) {
			fail("Unrecoverable violation found");
		} catch (InvariantViolation e){
			fail("Recoverable violation found");
		}
		assertFalse("Empty report",report.length ==0);
		for (String string : report) {
			System.out.println(string);
		}
		assertTrue("Invariant could not be resolved",rt.checkRoomInvariant(world, true).length==0);
	}
	
	
	public void testResolvingPlayerIssue() throws UnrecoverableInvariantViolation, InvariantViolation{
		try {
			rt.checkPlayersInvariant(world, true);
			fail("No exception was thrown");
		} catch (UnrecoverableInvariantViolation e) {
			fail("Unrecoverable violation found");
		} catch (InvariantViolation e){
			//System.out.println(e.getMessage());
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
