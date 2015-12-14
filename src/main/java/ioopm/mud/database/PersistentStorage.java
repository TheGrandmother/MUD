package ioopm.mud.database;

import ioopm.mud.generalobjects.Player;

public interface PersistentStorage {

	/**
	 * Stores the given player in the storage.
	 *
	 * @param player - The player to store.
	 */
	void storePlayer(Player player);

	/**
	 * Loads a player stored in the storage by the given name.
	 *
	 * @param username - The player to load from storage.
	 * @return Loaded player.
	 * @throws IllegalArgumentException If the player does not exist in the storage.
	 */
	Player loadPlayer(String username) throws IllegalArgumentException;
}
