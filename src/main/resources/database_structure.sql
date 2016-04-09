--- This file contains all SQL code to construct a new database according to the EER-diagram in issue #9, with some modifications.

--- ENTITIES
CREATE TABLE IF NOT EXISTS Item (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	name VARCHAR NOT NULL,
	description VARCHAR,
	size INTEGER
);

CREATE TABLE IF NOT EXISTS Location (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	name VARCHAR NOT NULL,
	description VARCHAR,
	pvp BOOLEAN
);

CREATE TABLE IF NOT EXISTS Character (
	id INTEGER PRIMARY KEY AUTOINCREMENT
	has_equiped INTEGER,

	FOREIGN KEY(has_equiped) REFERENCES Item(id)
);

--- WEAK ENTITIES
CREATE TABLE IF NOT EXISTS Weapon (
	id INTEGER PRIMARY KEY,
	damage INTEGER,

	FOREIGN KEY(id) REFERENCES Item(id)
);

CREATE TABLE IF NOT EXISTS Key (
	id INTEGER PRIMARY KEY,
	target INTEGER,

	FOREIGN KEY(id) REFERENCES Item(id),
	FOREIGN KEY(target) REFERENCES Location(id)
);

CREATE TABLE IF NOT EXISTS Player (
	id INTEGER PRIMARY KEY,
	username VARCHAR NOT NULL,
	password VARCHAR,
	is_admin BOOLEAN,

	FOREIGN KEY(id) REFERENCES Character(id)
);

CREATE TABLE IF NOT EXISTS CharacterSheet (
	id INTEGER PRIMARY KEY,
	hp INTEGER,
	level INTEGER,
	health INTEGER,
	maximum_health INTEGER,

	FOREIGN KEY(id) REFERENCES Character(id)
);

CREATE TABLE IF NOT EXISTS Inventory (
	id INTEGER PRIMARY KEY,
	volume INTEGER NOT NULL,

	FOREIGN KEY(id) REFERENCES Character(id)
);

--- MANY-MANY RELATIONS
CREATE TABLE IF NOT EXISTS ContainsItem (
	inv_id INTEGER,
	item_id INTEGER,
	amount INTEGER NOT NULL,

	PRIMARY KEY(inv_id, item_id),
	FOREIGN KEY(inv_id) REFERENCES Inventory(id),
	FOREIGN KEY(item_id) REFERENCES Item(id)
);

CREATE TABLE IF NOT EXISTS ExitsTo (
	loc_1 INTEGER,
	loc_2 INTEGER,

	PRIMARY KEY(loc_1, loc_2),
	FOREIGN KEY(loc_1) REFERENCES Location(id),
	FOREIGN KEY(loc_2) REFERENCES Location(id)
);
