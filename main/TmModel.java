package main;

import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

public class TmModel implements ITMModel {
	Log log = null;

	TmModel() {
		try {
			log = new Log();
		} catch (Exception e) {
			System.out.println("Could not access log file, Exiting.");
		}
	}

	public void close() {
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
	private boolean isRunning(String task) {
		Long lastStart;
		Long lastStop;

		// if the task has never been started before return true.
		try {
			lastStart = Long.parseLong(log.getLastInstanceOf(Command.START, task));
		} catch (NumberFormatException e) {
			return false;
		}
		// if the task has never been stopped before return true.
		try {
			lastStop = Long.parseLong(log.getLastInstanceOf(Command.STOP, task));
		} catch (NumberFormatException e) {
			return true;
		}
		return lastStart > lastStop;
	}

	public boolean startTask(String task) {
		if (!isRunning(task)) {
			Long time = System.currentTimeMillis();
			log.add(Command.START, task, time.toString());
			return true;
		} else {
			return false;

		}
	}

	public boolean stopTask(String task) {
		if (isRunning(task)) {
			Long time = System.currentTimeMillis();
			log.add(Command.STOP, task, time.toString());
			return true;
		} else {
			return false;
		}
	}

	public boolean sizeTask(String task, String size) {
		log.add(Command.SIZE, task, size);
		return true;
	}

	public boolean describeTask(String task, String description) {
		log.add(Command.DESCRIPTION, task, description);
		return true;
	}

	private String descriptionBuilder(String[] line) {
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

	@Override
	public boolean deleteTask(String task) {
		// we delete our task by setting it's old name to an illegal value
		// ensuring that it never returns a search hit.
		log.add(Command.DELETE, task, null);
		return true;

	}

	@Override
	/*
	 * @see ITMModel#renameTask(java.lang.String, java.lang.String) rename
	 * inserts a rename record in the log with the task as the new task name and
	 * the data as the old task name.
	 */
	public boolean renameTask(String oldTaskName, String newTaskName) {
		log.add(Command.RENAME, newTaskName, oldTaskName);
		return true;
	}

	@Override
	public String taskElapsedTime(String task) {

		return (millisToFormatedTime(taskElapsedTimeMillis(task)));
	}

	private Long taskElapsedTimeMillis(String task) {
		String[] startValues, stopValues;
		Long startSum = (long) 0;
		Long stopSum = (long) 0;

		startValues = log.getAllInstanceOf(Command.START, task);
		stopValues = log.getAllInstanceOf(Command.STOP, task);

		for (int i = 0; i < stopValues.length; i++) {
			stopSum += Long.parseLong(stopValues[i]);
			startSum += Long.parseLong(startValues[i]);
		}
		return stopSum - startSum;
	}

	@Override
	public String taskSize(String task) {
		return log.getLastInstanceOf(Command.SIZE, task);
	}

	@Override
	public String taskDescription(String task) {
		return descriptionBuilder(log.getAllInstanceOf(Command.DESCRIPTION, task));
	}

	@Override
	public String minTimeForSize(String size) {
		Set<String> tasks = taskNamesForSize(size);
		TreeMap<Long, String> tasksWithTimes = new TreeMap<>();
		for (String i : tasks) {
			tasksWithTimes.put(taskElapsedTimeMillis(i), i);
		}
		return millisToFormatedTime(tasksWithTimes.pollFirstEntry().getKey());
	}

	@Override
	public String maxTimeForSize(String size) {
		Set<String> tasks = taskNamesForSize(size);
		TreeMap<Long, String> tasksWithTimes = new TreeMap<>();
		for (String i : tasks) {
			tasksWithTimes.put(taskElapsedTimeMillis(i), i);
		}
		return millisToFormatedTime(tasksWithTimes.pollLastEntry().getKey());

	}

	@Override
	public String avgTimeForSize(String size) {
		Set<String> tasks = taskNamesForSize(size);
		Long sum = (long) 0;
		for (String i : tasks) {
			sum += taskElapsedTimeMillis(i);
		}
		return millisToFormatedTime(sum / tasks.size());
	}

	@Override
	public Set<String> taskNamesForSize(String size) {
		Set<String> returnValues = new TreeSet<>();
		for (String i : log.getTasks()) {
			if (taskSize(i) == size) {
				returnValues.add(i);
			}
		}
		return returnValues;
	}

	@Override
	public String elapsedTimeForAllTasks() {
		Long elapsedTime = (long) 0;
		for (String i : log.getTasks()) {
			elapsedTime += taskElapsedTimeMillis(i);
		}
		return millisToFormatedTime(elapsedTime);
	}

	@Override
	public Set<String> taskNames() {
		return log.getTasks();
	}

	@Override
	public Set<String> taskSizes() {
		Set<String> tasks = log.getTasks();
		Set<String> sizes = new TreeSet<>();
		for (String i : tasks) {
			sizes.add(taskSize(i));
		}
		return sizes;
	}

	/*
	 * This function calculates the total time spent on a task. as our time
	 * values are all stored in epoch time we sum all of the start times, and
	 * all of the stop times respectively. We then take the difference.
	 * (a-b)+(c-d) = (a+c)-(b+d).
	 */
	@Deprecated
	public void commandSummary(String task) {
		Long startSum = (long) 0;
		Long stopSum = (long) 0;

		String[] values;
		String description = descriptionBuilder(log.getAllInstanceOf(Command.DESCRIPTION, task));

		values = log.getAllInstanceOf(Command.START, task);
		for (String i : values) {
			startSum += Long.parseLong(i);
		}
		values = log.getAllInstanceOf(Command.STOP, task);
		for (String i : values) {
			stopSum += Long.parseLong(i);
		}

		System.out.println(task + ":	" + millisToFormatedTime(stopSum - startSum) + "		"
				+ log.getLastInstanceOf(Command.SIZE, task) + "	" + description);
		// System.out.println();
	}

	@Deprecated
	public void commandSummary() {

		for (String i : log.getTasks()) {
			commandSummary(i);
		}

	}
}
