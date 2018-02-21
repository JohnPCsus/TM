import java.util.concurrent.TimeUnit;

public class TmModel {
	Log log = null;

	TmModel() throws Exception {
		try {
			log = new Log();
		} catch (Exception e) {
			
			throw e;
			//TODO do something here about this
		}
	}
	public void close(){
		log.close();
	}

	/*
	 * A helper method to determine whether or not a given task is running.
	 * 
	 * @param task the name of a task.
	 * 
	 * @return True, if the most recent start command is newer than the most
	 * recent stop command or if no stop command has been executed and a start
	 * command has False, if there has never been a start command for that task
	 * or if the most recent stop command is newer than the most recent start
	 * command.
	 * 
	 */
	public boolean isRunning(String task) {
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
	public void commandStart(String task) {
		if (!isRunning(task)) {
			Long time = System.currentTimeMillis();
			log.add(Command.start, task, time.toString());
		} else {
			System.out.println("That task already running");

		}
	}
	
	public void commandStop(String task) {
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
	//TODO this method needs to not print to the screen, it needs to return type
	//String[]
	public void commandSummary(String task) {
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
	
	public void commandSummary() {

		String[] tasks;
		tasks = log.getTasks();
		for (String i : tasks) {
			commandSummary(i);
		}

	}
	
	public void commandSize(String task, String size) {
		log.add(Command.size, task, size);
	}

	public void commandDescribe(String task, String description) {
		log.add(Command.description, task, description);
	}
	//TODO this does not belong in this class
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
	//TODO this does not belong in this class
	private String millisToFormatedTime(Long millis) {
		return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
				TimeUnit.MILLISECONDS.toMinutes(millis)
						- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
				TimeUnit.MILLISECONDS.toSeconds(millis)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
	}
}
