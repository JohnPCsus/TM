package main;
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
import java.util.Set;
import java.util.TreeSet;

/**
 * The Log Class Handles all storage operations of the TM program. It handles
 * managing the programs storage file(S). Currently it just creates a single
 * file named "data.dat" in the directory where the program is called.
 * 
 */
@SuppressWarnings("serial")
public class Log implements Serializable, AutoCloseable {

	String filePath = "data.dat";
	LinkedList<Record> logData;

	// @SuppressWarnings("unchecked")
	public Log() throws FileNotFoundException, IOException {

		File file = new File(filePath);

		/*
		 * createNewFile returns true if file did not exist, and false if it
		 * did.
		 */
		try {
			if (file.createNewFile()) {
				logData = new LinkedList<Record>();

			} else {

				loadLogFile();

			}
		} catch (IOException e) {
			throw e;
		}

	}

	private void loadLogFile() throws FileNotFoundException, IOException {
		try (FileInputStream fin = new FileInputStream(filePath); ObjectInputStream in = new ObjectInputStream(fin)) {

			Object readin = in.readObject();

			logData = (LinkedList<Record>) readin;
		} catch (ClassNotFoundException e) {
			// TODO do something better when class not found, current it
			// only silently deletes the file.
			deleteReplaceLogFile();
		} catch (EOFException e) {// means file was empty
			logData = new LinkedList<>();
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
		try (FileOutputStream fout = new FileOutputStream(filePath);
				ObjectOutputStream out = new ObjectOutputStream(fout);) {

			out.writeObject(logData);

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
		String searchKey = task;
		for (Record i : logData) {
			if (i.cmd == cmd && i.task.equals(searchKey)) {
				return i.data;
			}
			if (i.cmd == Command.RENAME && i.task.equals(searchKey)) {
				searchKey = (i.data);
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
		String searchKey = task;
		LinkedList<String> returnValuesList = new LinkedList<>();
		String[] returnValuesArray = new String[0];
		for (Record i : logData) {
			if (i.cmd == cmd && i.task.equals(searchKey)) {
				returnValuesList.add(i.data);
			}
			if (i.cmd == Command.RENAME && i.task.equals(searchKey)) {
				searchKey = (i.data);
			}
		}

		return returnValuesList.toArray(returnValuesArray);
	}

	/**
	 * 
	 * @return returns an array containing each unique task in the log
	 */
	public Set<String> getTasks() {

		TreeSet<String> returnValues = new TreeSet<>();
		for (Record i : logData) {
			returnValues.add(i.task);
		}
		return returnValues;

	}

	public void add(Command newEntryCommand, String newEntryTask, String newEntryData) {
		Record newRecord = new Record(newEntryCommand, newEntryTask, newEntryData);
		logData.addFirst(newRecord);
	}

	private class Record implements Serializable {
		// LocalDateTime timeStamp;// this will provide a unique identifier for
		// records
		Command cmd;
		String task;
		String data;

		public Record(Command type, String task, String value) {
			// timeStamp = LocalDateTime.now();
			cmd = type;
			this.task = task;
			data = value;
		}
	}

}
