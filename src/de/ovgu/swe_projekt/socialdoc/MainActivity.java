package de.ovgu.swe_projekt.socialdoc;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                                                            /*Hier wird die App gestartet
                                                            abfrageob ein User bereits existiert
                                                            und dann den pasenden View auszusuchen
                                                             */
        setContentView(R.layout.mainmenu);
        //setContentView(R.layout.proband_code);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
                                                /*
                                                Wie man an die textinformationen kommt:
                                                EditText editText = (EditText) findViewById(R.id.<textfield>);
                                                editText.getText
                                                 */
    public void button_menu_to_quest(View view){
        setContentView(R.layout.question);
    }
    public void button_menu_to_time(View view){
        setContentView(R.layout.set_time);
    }
    public void button_menu_to_settings(View view){
        setContentView(R.layout.settings);
    }
    public void button_question_ok(View view){
        setContentView(R.layout.mainmenu);
    }
    public void button_time_ok(View view){
        setContentView(R.layout.mainmenu);
    }
    public void button_settings_ok(View view){
        setContentView(R.layout.mainmenu);
    }
    public void button_proband_ok(View view){
        setContentView(R.layout.mainmenu);
    }
}
