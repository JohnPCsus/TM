package test;
//TODO this suite needs to be completely redone

//we need separate TM and TMModel test suites, may need to use mocking(mockito?) to do this well.

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import main.TM;

public class TmTests {

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

		TM.main(new String[] { "start", "testTask2" });
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TM.main(new String[] { "stop", "testTask2" });
		TM.main(new String[] { "size", "testTask1", "XL" });
		TM.main(new String[] { "size", "testTask2", "XL" });

		TM.main(new String[] { "start", "TM" });
		TM.main(new String[] { "stop", "TM" });

		TM.main(new String[] { "summary" });
	}

	@Before
	public void cleanup() {
		java.io.File file = new File("data.dat");
		file.delete();
	}
}
