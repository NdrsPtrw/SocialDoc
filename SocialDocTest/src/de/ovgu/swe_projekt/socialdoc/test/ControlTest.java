package de.ovgu.swe_projekt.socialdoc.test;

import android.content.SharedPreferences;
import de.ovgu.swe_projekt.socialdoc.Control;
import junit.framework.TestCase;

public class ControlTest extends TestCase {
	
	private Control control;

	public ControlTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		control = new Control(getSharedPreferences("PsyAppPreferences", 0));
		control.newUser("abcde");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testWasLastQuestionAnswered() {
		assertFalse(control.wasLastQuestionAnswered());
		control.saveUserInputToCSV(false,""+3,""+1,""+25);
		try {
			//test after 89 minutes
			Thread.currentThread().sleep(1000*60*89);
			assertTrue(control.wasLastQuestionAnswered());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			//test after 1 day
			Thread.currentThread().sleep(1000*60*60*24);
			assertTrue(control.wasLastQuestionAnswered());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			//test after 1 day
			Thread.currentThread().sleep(1000*60*60*24*3);
			assertTrue(control.wasLastQuestionAnswered());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	public void testGetTimeSinceLastAnswer() {
		control.saveUserInputToCSV(false,""+3,""+1,""+25);
		try {
			//test after 89 minutes
			Thread.currentThread().sleep(1000*60*89);
			assertTrue(control.getTimeSinceLastAnswer()==89);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			//test after 489 minutes
			Thread.currentThread().sleep(1000*60*489);
			assertTrue(control.getTimeSinceLastAnswer()==489);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			//test after 489 minutes
			Thread.currentThread().sleep(1000*60*489);
			assertTrue(control.getTimeSinceLastAnswer()==489);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void testIsQuestionInputOK() {
		control.saveUserInputToCSV(false,""+3,""+1,""+25);
		try {
			//test after 89 minutes
			Thread.currentThread().sleep(1000*60*89);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(control.isQuestionInputOK("1:12"));//after 72 minutes
		assertTrue(control.isQuestionInputOK("0:42"));//after 42 minutes
		assertFalse(control.isQuestionInputOK("2:05"));//after 125 minutes
		assertFalse(control.isQuestionInputOK("1:30"));//after 90 minutes
		assertFalse(control.isQuestionInputOK("10:35"));//after 635 minutes
	}

	public void testGenerateQuestionText() {
		control.saveUserInputToCSV(false,""+3,""+1,""+25);
		try {
			//test after 89 minutes
			Thread.currentThread().sleep(1000*60*89);
			assertTrue(control.generateQuestionText().contains("heute schon"));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			//test after 5hours
			Thread.currentThread().sleep(1000*60*60*5);
			assertTrue(control.generateQuestionText().contains("Signal um"));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			//test after 1 day
			Thread.currentThread().sleep(1000*60*60*24);
			assertTrue(control.generateQuestionText().contains("gestern"));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			//test after 3 day
			Thread.currentThread().sleep(1000*60*60*24*3);
			assertTrue(control.generateQuestionText().contains("Signal am"));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
