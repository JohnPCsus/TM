import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class testTM {

	@After
	public void deleteLogFile() {
		File logFile = new File("data.dat");
		logFile.delete();
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

		try {
			Log testLog = new Log();
		} catch (Exception e) {
			fail();
		}
	}
	
	
	
	

	private void corruptLogFile() {
		File logFile = new File("data.dat");
		try (java.io.FileWriter writer = new java.io.FileWriter(logFile)) {
			writer.write("garbagegarbage");
		} catch (IOException e) {

		}
	}

	private void populateLogFileMin() throws FileNotFoundException, IOException {

		Log testLog = new Log();
		testLog.add(Command.start, "Foo", "Bar");
		testLog.close();

	}

}
