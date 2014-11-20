package yolo.ioopm.mud.userinterface;

public enum MenuItem {
	LOGIN(1),
	REGISTER(2);

	private final int INDEX;

	private MenuItem(int index) {
		INDEX = index;
	}

	public int getIndex() {
		return INDEX;
	}

	public static MenuItem getFromIndex(int index) {
		for(MenuItem item : values()) {
			if(item.getIndex() == index) {
				return item;
			}
		}

		return null;
	}
}
