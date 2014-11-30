package ioopm.mud.ui.ansi;

public enum AnsiAttributeCodes {

	RESET_ATTRIBUTES("\u001b[0m"),
	RED_BACKGROUND("\u001b[41m"),
	YELLOW_BACKGROUND("\u001b[43m"),
	MAGENTA_BACKGROUND("\u001b[45m"),
	WHITE_BACKGROUND_BLACK_TEXT("\u001b[30;47m");

	private final String CODE;

	private AnsiAttributeCodes(String code) {
		this.CODE = code;
	}

	public String toString() {
		return CODE;
	}
}
