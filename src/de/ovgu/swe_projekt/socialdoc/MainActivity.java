package de.ovgu.swe_projekt.socialdoc;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.*;

public class MainActivity extends Activity {

    protected Control _control;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                                                            /*Hier wird die App gestartet
                                                            abfrageob ein User bereits existiert
                                                            und dann den pasenden View auszusuchen
                                                             */

        // set up control
        _control = new Control(getSharedPreferences("PsyAppPreferences", 0));
        // TODO: also check if user exists on show, not only on create!!
        if( _control.createCSV() )
            setContentView(R.layout.mainmenu);
        else
            setContentView(R.layout.proband_code);
    }
    @Override
    public void onBackPressed() {
        // do nothing.
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void button_menu_to_quest(View view){
        setContentView(R.layout.question);
    }
    public void button_menu_to_time(View view){
        setContentView(R.layout.set_time);
    }
    public void button_question_ok(View view){
        // todo: check for wrong input! And show a popup and request new input if required!
        String numContacts, numHours, numMinutes;
        numContacts = getEditText(R.id.editText_contacts);
        numHours = getEditText(R.id.editText_hours);
        numMinutes = getEditText(R.id.editText_minutes);

        // todo: on alarm alarmsSinceLastAnswered has to be increased by 1
        // nextAlarmTime has to be set to next
        _control.changeLastAnsweredAt();
        _control.saveUserInputToCSV(false, numContacts, numHours, numMinutes);
        _control.saveUserData(getSharedPreferences("PsyAppPreferences", 0));

        //setButtonEnabled(R.id.goto_question_button, false);

        setContentView(R.layout.mainmenu);
    }
    public void button_time_ok(View view){
        // todo: get times
        _control.saveUserData(getSharedPreferences("PsyAppPreferences", 0));
        setContentView(R.layout.mainmenu);
    }
    public void button_time_back(View view){
        setContentView(R.layout.mainmenu);
    }
    public void button_proband_ok(View view){
        String code = "";
        // create probandencode from different parts
        // ToDo: adjust the view to actually support this and check if all are actually characters
        // (and not numbers/other symbols)
        _control.newUser( code );
        setContentView(R.layout.mainmenu);
    }
    private String getEditText(int id){
        EditText editText = (EditText) findViewById(id);
        return editText.getText().toString();
    }
    private void setButtonEnabled(int id, boolean enabled){
        Button button = (Button)findViewById(id);
        button.setEnabled(enabled);
    }
}
