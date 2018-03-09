package main;


import java.util.Formatter;
import java.util.Set;

public class TM {

	private TmModel model = null;

	public static void main(String[] args) {
		TM manager = new TM();
		manager.commandSwitcher(args);
	}

	TM() {

		model = new TmModel();

	}

	/*
	 * Rudimentary CLI input interpreter, uses only the first arguments value,
	 * and the number of additional argument to determine which methods in the
	 * model should be called and which arguments should be passed.
	 */
	private void commandSwitcher(String[] args) {

		if (args.length == 0) {
			printUsage();
			return;
		}
		switch (args[0]) {
		case "start":
			if (args.length == 2) {
				model.startTask(args[1]);
			}
			break;
		case "stop":
			if (args.length == 2) {
				model.stopTask(args[1]);
			}
			break;

		case "size":
			if (args.length == 3) {
				model.sizeTask(args[1], args[2]);
			}
			break;
		case "summary":
			if (args.length == 1) {
				summaryHandler();
			} else if (args.length == 2)
				summaryHandler(args[1]);
			break;
		case "describe":
			if (args.length == 3) {
				model.describeTask(args[1], args[2]);
			}
			if (args.length == 4) {
				model.describeTask(args[1], args[2]);
				model.sizeTask(args[1], args[3]);
			}
			break;

		case "delete":
			if (args.length == 2)
				model.deleteTask(args[1]);
			break;
		case "rename":
			if (args.length == 3) {
				model.renameTask(args[1], args[2]);
			}
			break;
		default:
			printUsage();// if we get here then no properly formatted command
							// was entered.
		}

		model.close();
		return;
	}

	private void summaryHandler() {
		// prints header for output
		System.out.print(summaryFormatter(new String[] { "Task Name", "Time", "size", "Description" }));
		printVerticalSeparator();

		for (String i : model.taskNames()) {
			summaryHandler(i);
		}
		printVerticalSeparator();
		statisticsPrinter();
	}

	private void printVerticalSeparator() {
		System.out.println("===================================================================");
	}

	private void summaryHandler(String task) {
		String[] tokens = new String[4];
		tokens[0] = task;
		tokens[1] = model.taskElapsedTime(task);
		tokens[2] = model.taskSize(task);
		tokens[3] = model.taskDescription(task);
		System.out.print(summaryFormatter(tokens));

	}

	private void statisticsPrinter() {
		System.out.print(summaryFormatter(new String[] { "Size", "Min", "Max", "Avg" }));
		printVerticalSeparator();
		Set<String> sizes = model.taskSizes();
		String[] tokens = new String[4];
		for (String i : sizes) {
			if (model.taskNamesForSize(i).size() < 2)// No reason to do
														// avg/min/max of less
														// than two arguments
				break;
			tokens[0] = i;
			tokens[1] = model.minTimeForSize(i);
			tokens[2] = model.maxTimeForSize(i);
			tokens[3] = model.avgTimeForSize(i);

			System.out.print(summaryFormatter(tokens));
		}

	}

	private String summaryFormatter(String[] tokens) {

		Formatter fmt = new Formatter();
		fmt.format("%16s\t%10s\t%8s\t%s%n", tokens[0], tokens[1], tokens[2], tokens[3]);
		// System.out.print(fmt.toString());
		String returnString = fmt.toString();
		fmt.close();

		return returnString;
	}

	private void printUsage() {
		System.out.println("usage");
		// TODO add code to print usage information
	}

}