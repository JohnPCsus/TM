package test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.TreeSet;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import main.Command;
import main.Log;

public class LogTests {

	@Before
	public void deleteLogFile() {
		File logFile = new File("data.dat");
		logFile.delete();
		// System.out.println("after");
	}

	@AfterClass
	public static void deleteLogFileAfter() {
		File logFile = new File("data.dat");
		logFile.delete();
		// System.out.println("after");
	}

	@Test
	public void logConstruct_CreateFile() {
		try {

			Log testLog = new Log();
			testLog.close();
		} catch (Exception e) {
			fail();
		}

	}

	@Test
	public void logConstruct_FileExists_Empty() {

		File logFile = new File("data.dat");
		try {
			logFile.createNewFile();
		} catch (IOException e1) {
			return;
		}
		try {
			Log testLog = new Log();
			testLog.close();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

	}

	@Test
	public void logConstruct_FileExists_NotEmpty() {
		try {
			populateLogFileMin();
		} catch (Exception e) {
			System.out.println("testTM.logCreatFile_FileExistsNotEmpty() Set-Up has failed");
			return;
		}
		try {
			Log testLog = new Log();
			testLog.close();
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void logConstruct_FileExistsCorrupted() {
		corruptLogFile();
		try {
			Log testLog = new Log();
			testLog.close();
		} catch (Exception e) {
			fail();
			return;
		}

	}
	// @Test (expected = IOException.class)

	// public void logCreate_FailToOpenFile() throws IOException{
	// File file = new File("data.dat");
	// try{
	// file.createNewFile();
	// } catch (Exception e){
	// return;
	// }
	//
	// Log log = new Log();
	// Log log2 = new Log();
	// }

	@Test
	public void logAdd_simple() {
		Log log;
		try {
			log = new Log();
		} catch (Exception e) {
			return;
		}
		log.add(Command.START, "testTask1", "1");
		log.add(Command.STOP, "testTask1", "2");
		TreeSet<String> expectedValues = new TreeSet<>();
		expectedValues.add("testTask1");
		assertTrue(expectedValues.equals(log.getTasks()));
		log.close();
	}

	@Test
	public void logGetTasks_EmptyLog() {
		Log log;
		try {
			log = new Log();
		} catch (Exception e) {
			return;
		}
		TreeSet<String> expected = new TreeSet<>();
		assertTrue(expected.equals(log.getTasks()));
		log.close();
	}

	@Test
	public void logGetTasks_OneLogEntry() {
		populateLogFileMin();
		Log log;
		try {
			log = new Log();

		} catch (Exception e) {
			return;
		}
		TreeSet<String> expected = new TreeSet<>();
		expected.add("Foo");
		assertTrue(expected.equals(log.getTasks()));
		log.close();
	}

	@Test
	public void logGetTasks_ManyLogEntry() {
		Log log;
		populateLogFileMany();
		try {
			log = new Log();
		} catch (Exception e) {
			return;
		}
		TreeSet<String> expected = new TreeSet<>();
		expected.add("Foo");
		expected.add("boo");
		expected.add("test");
		assertTrue(expected.equals(log.getTasks()));
		log.close();
	}

	@Test
	public void logGetTask_renamedTask() {
		Log log;
		try {
			log = new Log();
		} catch (Exception e) {
			return;
		}
		log.add(Command.START, "Foo", "123");
		log.add(Command.RENAME, "Bar", "Foo");
		TreeSet<String> expected = new TreeSet<>();
		expected.add("Bar");
		assertTrue(expected.equals(log.getTasks()));
		log.close();
	}
	
	@Test
	public void logGetTasks_taskRenamedOldTaskNameReused() {
		Log log;
		try {
			log = new Log();
		} catch (Exception e) {
			return;
		}
		log.add(Command.START, "Foo", "123");
		log.add(Command.RENAME, "Bar", "Foo");
		log.add(Command.START, "Foo", "1234");
		TreeSet<String> expected = new TreeSet<>();
		expected.add("Bar");
		expected.add("Foo");
		assertTrue(expected.equals(log.getTasks()));
		log.close();
	}

	@Test
	public void logGetTask_deleteddTask() {
		Log log;
		try {
			log = new Log();
		} catch (Exception e) {
			return;
		}
		log.add(Command.START, "Foo", "123");
		log.add(Command.DELETE, "Foo", null);
		TreeSet<String> expected = new TreeSet<>();

		assertTrue(expected.equals(log.getTasks()));
		log.close();
	}

	@Test
	public void logGetTask_deletedTaskNameReused() {
		Log log;
		try {
			log = new Log();
		} catch (Exception e) {
			return;
		}
		log.add(Command.START, "Foo", "123");
		log.add(Command.DELETE, "Foo", null);
		log.add(Command.START, "Foo", "1234");
		TreeSet<String> expected = new TreeSet<>();
		expected.add("Foo");
		assertTrue(expected.equals(log.getTasks()));
		log.close();
	}

	

	private void corruptLogFile() {
		File logFile = new File("data.dat");
		try (java.io.FileWriter writer = new java.io.FileWriter(logFile)) {
			writer.write("garbagegarbage");
		} catch (IOException e) {

		}
	}

	private void populateLogFileMin() {
		try {
			Log testLog = new Log();
			testLog.add(Command.START, "Foo", "Bar");
			testLog.close();
		} catch (Exception e) {

		}

	}

	private void populateLogFileMany() {
		try {
			Log testLog = new Log();
			testLog.add(Command.START, "Foo", "Bar");
			testLog.add(Command.STOP, "Foo", "Bar");
			testLog.add(Command.START, "Foo", "Bar");
			testLog.add(Command.STOP, "Foo", "Bar");
			testLog.add(Command.START, "boo", "Bar");
			testLog.add(Command.STOP, "boo", "b");
			testLog.add(Command.START, "test", "Bar");
			testLog.add(Command.STOP, "test", "Bar");
			testLog.add(Command.DESCRIPTION, "Foo", "Bar");
			testLog.close();
		} catch (Exception e) {
			return;
		}
	}

}
