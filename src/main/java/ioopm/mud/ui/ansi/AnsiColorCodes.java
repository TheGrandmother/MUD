package ioopm.mud.ui.ansi;

public enum AnsiColorCodes {

	RESET_ATTRIBUTES("\u001b[0m"),
	RED_BACKGROUND("\u001b[41m"),
	YELLOW_BACKGROUND("\u001b[43m"),
	MAGENTA_BACKGROUND("\u001b[45m"),
	WHITE_BACKGROUND_BLACK_TEXT("\u001b[30;47m");

	private final String CODE;

	private int intOne = 0;
	private int intTwo = 0;

	private AnsiColorCodes(String code) {
		this.CODE = code;
	}

	public AnsiColorCodes setIntOne(int intOne) {
		this.intOne = intOne;
		return this;
	}

	public AnsiColorCodes setIntTwo(int intTwo) {
		this.intTwo = intTwo;
		return this;
	}

	public String toString() {
		return CODE.replace("$", String.valueOf(intOne)).replace("&", String.valueOf(intTwo));
	}
}
