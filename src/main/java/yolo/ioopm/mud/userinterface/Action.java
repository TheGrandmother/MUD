package yolo.ioopm.mud.userinterface;

public enum Action {

	;

	private final int INDEX;

	private Action(int index) {
		INDEX = index;
	}

	public int getIndex() {
		return INDEX;
	}

	public static Action getFromIndex(int index) {
		for(Action item : values()) {
			if(item.getIndex() == index) {
				return item;
			}
		}

		return null;
	}
}
