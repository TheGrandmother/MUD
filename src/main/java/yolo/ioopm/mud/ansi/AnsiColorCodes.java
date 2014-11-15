package yolo.ioopm.mud.ansi;

public enum AnsiColorCodes {

	;

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
