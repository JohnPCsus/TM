import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

/**
 * The Log Class Handles all storage operations of the TM program. It handles
 * managing the programs storage file(S). Currently it just creates a single
 * file named "data.dat" in the directory where the program is called.
 * 
 */
@SuppressWarnings("serial")
class Log implements Serializable {

	String filePath = "data.dat";
	LinkedList<Record> logData;

	// @SuppressWarnings("unchecked")
	Log() throws FileNotFoundException, IOException {

		File file = new File(filePath);

		/*
		 * createNewFile returns true if file did not exist, and false if it
		 * did.
		 */
		try{
		if (file.createNewFile()) {
			logData = new LinkedList<Record>();

		} else {

			loadLogFile();

		}
		}catch(IOException e){
			throw e;
		}

	}

	private void loadLogFile() throws FileNotFoundException, IOException {
		try (FileInputStream fin = new FileInputStream(filePath); ObjectInputStream in = new ObjectInputStream(fin)) {

			Object readin = in.readObject();

			logData = (LinkedList<Record>) readin;
		} catch (ClassNotFoundException e) {
			// TODO do something better when class not found, current it
			// only silently deletes
			deleteReplaceLogFile();
		} catch (EOFException e) {
			/*
			 * this catch block is currently identical the the previous catch
			 * block. These catch blocks execute for different failure states
			 * that and handling may change later.
			 */
			deleteReplaceLogFile();
		} catch (StreamCorruptedException e) {
			deleteReplaceLogFile();
		}
	}

	private void deleteReplaceLogFile() throws IOException {
		File file = new File(filePath);

		file.delete();
		file.createNewFile();
		logData = new LinkedList<Record>();

	}

	public void close() {
		try {
			FileOutputStream fout = new FileOutputStream(filePath);
			ObjectOutputStream out = new ObjectOutputStream(fout);
			out.writeObject(logData);
			out.close();
		} catch (Exception e) {
			System.out.println("exception in write");
			// TODO handle exceptions here
		}

	}

	/**
	 * Finds and returns the most recent occurrence of a given Command and task.
	 * It then returns the data given for that command. If no Occurrence of that
	 * command and task is found returns null.
	 * 
	 * @param cmd
	 *            the command to search for
	 * @param task
	 *            the task to search for
	 * @returns a String representation of the data if found, and null if not
	 *          found
	 */
	public String getLastInstanceOf(Command cmd, String task) {
		for (Record i : logData) {
			if (i.cmd == cmd && task.equals(i.task)) {
				return i.data;
			}
		}
		return "";
	}

	/**
	 * 
	 * @param cmd
	 * @param task
	 * @return
	 */
	public String[] getAllInstanceOf(Command cmd, String task) {
		LinkedList<String> returnValuesList = new LinkedList<>();
		String[] returnValuesArray = new String[0];
		for (Record i : logData) {
			if (i.cmd == cmd && i.task.equals(task)) {
				returnValuesList.add(i.data);
			}
		}

		return returnValuesList.toArray(returnValuesArray);
	}

	/**
	 * 
	 * @return returns an array containing each unique task in the log
	 */
	public String[] getTasks() {
		LinkedList<String> returnValuesList = new LinkedList<>();
		String[] returnValuesArray = new String[0];
		for (Record i : logData) {
			if (!returnValuesList.contains(i.task)) {
				returnValuesList.add(i.task);
			}
		}
		return returnValuesList.toArray(returnValuesArray);
	}

	public void add(Command newEntryCommand, String newEntryTask, String newEntryData) {
		Record newRecord = new Record(newEntryCommand, newEntryTask, newEntryData);
		logData.addFirst(newRecord);
	}

	private class Record implements Serializable {

		Command cmd;
		String task;
		String data;

		public Record(Command type, String task, String value) {
			cmd = type;
			this.task = task;
			data = value;
		}
	}

}

enum Command {
	start("start"), stop("stop"), summary("summary"), description("description"), size("size");
	@SuppressWarnings("unused")
	private final String sValue;

	Command(String Value) {
		this.sValue = Value;
	}

}

public class TM {
	Log log;

	public static void main(String[] args) {
		TM manager = new TM();
		manager.commandSwitcher(args);
	}

	private void commandSwitcher(String[] args) {
		try {
			log = new Log();
		} catch (Exception e) {
			System.out.println("Could not access log file, Exiting.");
			return;
		}

		if (args.length == 0) {
			printUsage();
			return;
		}
		switch (args[0]) {
		case "start":
			if (args.length == 2) {
				commandStart(args[1]);
			}
			break;
		case "stop":
			if (args.length == 2) {
				commandStop(args[1]);
			}
			break;

		case "size":
			if (args.length == 3) {
				commandSize(args[1], args[2]);
			}
		case "summary":
			if (args.length == 1) {
				System.out.println("Task Name" + "	" + "Time" + "			" + "size" + "	" + "Description" + "	");
				commandSummary();
			} else if (args.length == 2) {
				System.out.println("Task Name" + "	" + "Time" + "			" + "size" + "	" + "Description" + "	");
				commandSummary(args[1]);
			}
			break;
		case "describe":
			if (args.length == 3) {
				commandDescribe(args[1], args[2]);
			} else if (args.length == 4) {
				commandSize(args[1], args[3]);
			}
			break;
		default:
			printUsage();// if we get here then no properly formatted command
							// was entered.
		}
		log.close();

		return;
	}

	private void commandStart(String task) {
		if (!isRunning(task)) {
			Long time = System.currentTimeMillis();
			log.add(Command.start, task, time.toString());
		} else {
			System.out.println("That task already running");

		}
	}

	private void commandStop(String task) {
		if (isRunning(task)) {
			Long time = System.currentTimeMillis();
			log.add(Command.stop, task, time.toString());
		}
	}

	/*
	 * This function calculates the total time spent on a task. as our time
	 * values are all stored in epoch time we sum all of the start times, and
	 * all of the stop times respectively. We then take the difference.
	 * (a-b)+(c-d) = (a+c)-(b+d).
	 */
	private void commandSummary(String task) {
		Long startSum = (long) 0;
		Long stopSum = (long) 0;

		String[] values;
		String description = summaryFormatter(log.getAllInstanceOf(Command.description, task));

		values = log.getAllInstanceOf(Command.start, task);
		for (String i : values) {
			startSum += Long.parseLong(i);
		}
		values = log.getAllInstanceOf(Command.stop, task);
		for (String i : values) {
			stopSum += Long.parseLong(i);
		}

		System.out.println(task + ":	" + millisToFormatedTime(stopSum - startSum) + "		"
				+ log.getLastInstanceOf(Command.size, task) + "	" + description);
		// System.out.println();
	}

	private void commandSummary() {

		String[] tasks;
		tasks = log.getTasks();
		for (String i : tasks) {
			commandSummary(i);
		}

	}

	private String summaryFormatter(String[] line) {
		String returnString = "";
		for (String i : line) {
			returnString = returnString + i + " ";
		}

		return returnString;
	}

	private void commandSize(String task, String size) {
		log.add(Command.size, task, size);
	}

	private void commandDescribe(String task, String description) {
		log.add(Command.description, task, description);
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

	/**
	 * A helper method to determine whether or not a given task is running.
	 * 
	 * @param task
	 *            the name of a task.
	 * @return True, if the most recent start command is newer than the most
	 *         recent stop command or if no stop command has been executed and a
	 *         start command has False, if there has never been a start command
	 *         for that task or if the most recent stop command is newer than
	 *         the most recent start command.
	 * 
	 */
	private boolean isRunning(String task) {
		Long lastStart;
		Long lastStop;

		// if the task has never been started before return true.
		try {
			lastStart = Long.parseLong(log.getLastInstanceOf(Command.start, task));
		} catch (NumberFormatException e) {
			return false;
		}
		// if the task has never been stopped before return true.
		try {
			lastStop = Long.parseLong(log.getLastInstanceOf(Command.stop, task));
		} catch (NumberFormatException e) {
			return true;
		}
		return lastStart > lastStop;
	}
}