package yolo.ioopm.mud.ansi;

public enum GeneralAnsiCodes {

	CLEAR_SCREEN("\u001b[2J"),
	CLEAR_LINE("\u001b[2K"),

	CURSOR_MOVE_DOWN_N_LINES("\u001b[$B"),
	CURSOR_MOVE_UP_N_LINES("\u001b[$A"),
	CURSOR_REPORT_POSITION("\u001b[6n"),
	CURSOR_SET_POSITION("\u001b[$;&H"),
	CURSOR_STORE_POSITION("\u001b7"),
	CURSOR_RESTORE_POSITION("\u001b8"),

	BUFFER_SET_TOP_BOTTOM("\u001b[$;&r"),
	BUFFER_MOVE_UP_ONE("\u001bD");

	private final String CODE;

	private int intOne = 0;
	private int intTwo = 0;

	private GeneralAnsiCodes(String code) {
		this.CODE = code;
	}

	public GeneralAnsiCodes setIntOne(int intOne) {
		this.intOne = intOne;
		return this;
	}

	public GeneralAnsiCodes setIntTwo(int intTwo) {
		this.intTwo = intTwo;
		return this;
	}

	public String toString() {
		return CODE.replace("$", String.valueOf(intOne)).replace("&", String.valueOf(intTwo));
	}
}
