import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

public class testTM {

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
	public void logCreate_CreateFile() {
		try {
			Log testLog = new Log();
		} catch (Exception e) {
			fail();
		}

	}

	@Test
	public void logCreate_FileExistsEmpty() {

		File logFile = new File("data.dat");
		try {
			logFile.createNewFile();
		} catch (IOException e1) {
			return;
		}
		try {
			Log testLog = new Log();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

	}

	@Test
	public void logCreate_FileExistsNotEmpty() {
		try {
			populateLogFileMin();
		} catch (Exception e) {
			System.out.println("testTM.logCreatFile_FileExistsNotEmpty() Set-Up has failed");
			return;
		}
		try {
			Log testLog = new Log();
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void logCreate_FileExistsCorrupted() {
		corruptLogFile();
		try {
			Log testLog = new Log();
		} catch (Exception e) {
			fail();
		}
	}
	//@Test (expected = IOException.class)
	
//	public void logCreate_FailToOpenFile() throws IOException{
//		File file = new File("data.dat");
//		try{
//		file.createNewFile();
//		} catch (Exception e){
//			return;
//		}
//		
//		Log log = new Log();
//		Log log2 = new Log();
//	}

	@Test
	public void logAdd_simple() {
		Log log;
		try {
			log = new Log();
		} catch (Exception e) {
			return;
		}
		log.add(Command.start, "testTask1", "1");
		log.add(Command.stop, "testTask1", "2");

		assertEquals(new String[] { "testTask1" }[0], log.getTasks()[0]);
	}

	@Test
	public void logGetTasks_EmptyLog() {
		Log log;
		try {
			log = new Log();
		} catch (Exception e) {
			return;
		}
		String[] expected = {};
		String[] actual = log.getTasks();
		assertArrayEquals(expected, actual);
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
		String[] expected = { "Foo" };
		String[] actual = log.getTasks();
		assertArrayEquals(expected, actual);

	}

	 @Test
	 public void logGetTasks_ManyLogEntry(){
		 Log log;
		 populateLogFileMany();
		 try{
			log = new Log();
		 } catch ( Exception e){
			 return;
		 }
		 String[] Expected = {"Foo","test","boo"};
		 String[] actual = log.getTasks();
		 assertArrayEquals(Expected, actual);
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
			testLog.add(Command.start, "Foo", "Bar");
			testLog.close();
		} catch (Exception e) {

		}

	}
	 private void populateLogFileMany(){
	 try{
	 Log testLog = new Log();
		testLog.add(Command.start, "Foo", "Bar");
		testLog.add(Command.stop, "Foo", "Bar");
		testLog.add(Command.start, "Foo", "Bar");
		testLog.add(Command.stop, "Foo", "Bar");
		testLog.add(Command.start, "boo", "Bar");
		testLog.add(Command.stop, "boo", "b");
		testLog.add(Command.start, "test", "Bar");
		testLog.add(Command.stop, "test", "Bar");
		testLog.add(Command.description, "Foo", "Bar");
		testLog.close();
	 } catch (Exception e){
		 return;
	 }
	 }

}
