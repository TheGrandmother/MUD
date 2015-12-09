package ioopm.mud.exceptions;

public class EntityNotPresent extends Exception {
	String name;

	public EntityNotPresent() {
		super();
	}

	public EntityNotPresent(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}
}