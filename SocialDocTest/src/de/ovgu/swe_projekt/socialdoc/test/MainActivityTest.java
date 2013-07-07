package de.ovgu.swe_projekt.socialdoc.test;

import de.ovgu.swe_projekt.socialdoc.MainActivity;
import android.app.Instrumentation.ActivityMonitor;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {
/*private EditText pc1;
	private EditText pc2;
	private EditText pc3;
	private EditText pc4;
	private EditText pc5;
*/
	private MainActivity activity;
	
	public MainActivityTest() {
		super("de.ovgu.swe_projekt.socialdoc",MainActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp(); 
	    setActivityInitialTouchMode(false);
		activity = getActivity(); 
	}
	
	private boolean checkCodeInput(String a,String b, String c, String d, String e){

		final EditText pc1 = (EditText) activity.findViewById(R.id.pc_1); 
		final EditText pc2 = (EditText) activity.findViewById(R.id.pc_2); 
		final EditText pc3 = (EditText) activity.findViewById(R.id.pc_3); 
		final EditText pc4 = (EditText) activity.findViewById(R.id.pc_4); 
		final EditText pc5 = (EditText) activity.findViewById(R.id.pc_5); 

        pc1.setText(a);
        pc2.setText(b);
        pc3.setText(c);
        pc4.setText(d);
        pc5.setText(e);
        
	    Button ok = (Button) activity.findViewById(R.id.button_proband_code);
	    
	    getActivity().runOnUiThread(new Runnable() {

	        @Override
	        public void run() {
	        	ok.performClick();
	        }
	    });

	    getInstrumentation().waitForIdleSync();
	    
	    
	    
		ActivityMonitor monitor = getInstrumentation().addMonitor(MainActivity.class.getName(), null, false);

	    Button ok = (Button) activity.findViewById(R.id.button_proband_code);
	    TouchUtils.clickView(this, ok);
	    return activity.getCurrentFocus().getId()==activity.findViewById(R.id.set_time);
	}
	public void testProbandCode(){
		assertTrue(checkCodeInput("a", "c", "o", "e", "m"));
		assertTrue(checkCodeInput("g", "b", "w", "z", "u"));
		assertFalse(checkCodeInput("aa", "", "", "e", "m"));
		assertFalse(checkCodeInput("123", "", "", "e", "m"));
		assertFalse(checkCodeInput("  ", "", "7", "5z", "m"));
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	

}
