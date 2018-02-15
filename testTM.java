import static org.junit.Assert.*;

import java.io.File;
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
	public void logCreateFile_CreateFile() {
		try {
			Log testLog = new Log();
		} catch (Exception e) {
			fail();
		}

	}

	@Test
	public void logCreateFile_FileExistsEmpty() {

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
	
//	@Test
//	public void logCreatFile_FileExistsNotEmpty(){
//		LinkedList<Record> List = new LinkedList<>();
//	}

}
