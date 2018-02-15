import static org.junit.Assert.*;

import java.io.*;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Test;

public class LogTests {

//	@Test
//	public void Logtests() {
//		java.io.File file = new File("data.txt");
//		file.delete();
//		Log log = new Log();
//		log.close();
//		log = new Log();

//		log.add(Command.start, "testTask1", "1");
//		log.add(Command.stop, "testTask1", "2");
//		String[] expected = { "testTask1" };
//		String[] actual = log.getTasks();
//		assertEquals(expected[0], actual[0]);

//		log.add(Command.start, "testTask2", "1");
//		log.add(Command.stop, "testTask2", "2");
//		expected = new String[] { "testTask1", "testTask2" };
//		actual = log.getTasks();
//		assertEquals(expected[1], actual[0]);
//		assertEquals(expected[0], actual[1]);

//		log.close();
//		log = new Log();
//		expected = new String[] { "testTask1", "testTask2" };
//		actual = log.getTasks();
//		assertEquals(expected[1], actual[0]);
//		assertEquals(expected[0], actual[1]);
//
//		file.delete();

//	}

	@Test
	public void TMtests() {
		TM.main(new String[] { "start", "testTask1" });
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TM.main(new String[] { "stop", "testTask1" });

		TM.main(new String[] { "start", "testTask1" });
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TM.main(new String[] { "stop", "testTask1" });

		TM.main(new String[] { "describe", "testTask1", "task" });

		TM.main(new String[] { "describe", "testTask1", "a task" });

		TM.main(new String[] { "start", "testTask2" });

		TM.main(new String[] { "stop", "testTask2" });

		TM.main(new String[] { "start", "testTask1" });
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TM.main(new String[] { "stop", "testTask1" });
		TM.main(new String[] { "size", "testTask1", "XL" });

		TM.main(new String[] { "start", "TM" });
		TM.main(new String[] { "stop", "TM" });

		TM.main(new String[] { "summary" });
	}
	@After
	public void cleanup(){
		java.io.File file = new File("data.txt");
		file.delete();
	}
}
