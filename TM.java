import java.util.concurrent.TimeUnit;

enum Command {
	start("start"), stop("stop"), summary("summary"), description("description"), size("size");
	// @SuppressWarnings("unused")
	// private final String sValue;

	Command(String Value) {
		// this.sValue = Value;
	}

}

public class TM {

	private TmModel model = null;

	public static void main(String[] args) {
		TM manager = new TM();
		manager.commandSwitcher(args);
	}

	TM() {

		model = new TmModel();

	}

	private void commandSwitcher(String[] args) {

		if (args.length == 0) {
			printUsage();
			return;
		}
		switch (args[0]) {
		case "start":
			if (args.length == 2) {
				model.commandStart(args[1]);
			}
			break;
		case "stop":
			if (args.length == 2) {
				model.commandStop(args[1]);
			}
			break;

		case "size":
			if (args.length == 3) {
				model.commandSize(args[1], args[2]);
			}
		case "summary":
			if (args.length == 1) {
				System.out.println("Task Name" + "	" + "Time" + "			" + "size" + "	" + "Description" + "	");
				model.commandSummary();
			} else if (args.length == 2) {
				System.out.println("Task Name" + "	" + "Time" + "			" + "size" + "	" + "Description" + "	");
				model.commandSummary(args[1]);
			}
			break;
		case "describe":
			if (args.length == 3) {
				model.commandDescribe(args[1], args[2]);
			} else if (args.length == 4) {
				model.commandSize(args[1], args[3]);
			}
			break;
		default:
			printUsage();// if we get here then no properly formatted command
							// was entered.
		}

		model.close();
		return;
	}

	private String summaryFormatter(String[] line) {
		String returnString = "";
		for (String i : line) {
			returnString = returnString + i + " ";
		}

		return returnString;
	}

	/**
	 * Converts a time interval in milliseconds to proper HH:MM:SS format. code
	 * taken from.
	 * https://stackoverflow.com/questions/9027317/how-to-convert-milliseconds-to-hhmmss-format
	 * 
	 * @param millis
	 *            a time interval
	 * @return a properly formated String in HH:MM:SS format
	 */
	private String millisToFormatedTime(Long millis) {
		return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
				TimeUnit.MILLISECONDS.toMinutes(millis)
						- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
				TimeUnit.MILLISECONDS.toSeconds(millis)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
	}

	private void printUsage() {
		System.out.println("usage");
		// TODO add code to print usage information
	}

}