package yolo.ioopm.mud;

public class Main {

	public static void main(String[] args) {
		switch(args[0].toLowerCase()) {
			case "client":
				System.out.println("Initiating client...");
				new Client(args[1], Integer.valueOf(args[2]));
				break;
			case "server":
				System.out.println("Initiating server...");
				new Server(Integer.valueOf(args[1]));
				break;
		}
	}
}
