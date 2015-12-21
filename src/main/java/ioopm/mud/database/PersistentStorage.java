package ioopm.mud.database;

import ioopm.mud.generalobjects.Player;
import ioopm.mud.generalobjects.World;

public interface PersistentStorage {

	/**
	 * Stores the given player in the storage.
	 *
	 * @param player - The player to store.
	 * @throws IllegalArgumentException If something went wrong while trying to store the player.
	 */
	void storePlayer(Player player) throws IllegalArgumentException;

	/**
	 * Loads a player stored in the storage by the given name.
	 *
	 * @param username - The player to load from storage.
	 * @param world The world into which the player is to be added
	 * @return Loaded player.
	 * @throws IllegalArgumentException If the player does not exist in the storage.
	 */
	Player loadPlayer(String username,World world) throws IllegalArgumentException;
}
