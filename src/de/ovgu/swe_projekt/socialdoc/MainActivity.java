package de.ovgu.swe_projekt.socialdoc;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.os.SystemClock;
import android.os.Vibrator;
import android.text.format.Time;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

    protected Control _control;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // set up control
        _control = new Control(getSharedPreferences("PsyAppPreferences", 0));
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onStart(){
        super.onStart();

        try
        {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            v.vibrate(500);
        }catch (Exception e) {}

        // check if the probandencode.csv exists and create it (and a new user) if it doesn't
        if( _control.createCSV() ) {
            if(_control.probandenCodeIsEmpty()) {
                setContentView(R.layout.proband_code);
            } else {
                setContentView(R.layout.mainmenu);
                Helper.setButtonDisabled(findViewById(R.id.goto_question_button),
                                         _control.wasLastQuestionAnswered());
            }
        } else {
            setContentView(R.layout.proband_code);
        }

        // only call this if the app was opened by an alarm!
        if( _control.isAlarmTime(SystemClock.elapsedRealtime()) )
            setAlarm(Helper.getTimeDifferenceInMillisecs(_control), true);
    }
    @Override
    public void onBackPressed() {
        // do nothing.
    }
    @Override       //Fehlermeldungen
    protected Dialog onCreateDialog(int id){
        switch(id){
            case 10:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.dialog_message).setTitle(R.string.dialog_title);
                builder.setCancelable(true);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();


        }


        return super.onCreateDialog(id);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }



    public void button_menu_to_quest(View view){
        setContentView(R.layout.question);
        Helper.setQuestion(_control.generateQuestionText(), findViewById(R.id.question));
    }
    public void button_menu_to_time(View view){
        setContentView(R.layout.set_time);
        // set the spinners to the currently chosen user times (if there are any)
        int[] times = _control.getUserTimes();
        Helper.setSpinnerToTime(findViewById(R.id.spinner_termin1), times[0]);
        Helper.setSpinnerToTime(findViewById(R.id.spinner_termin2), times[1]);
        Helper.setSpinnerToTime(findViewById(R.id.spinner_termin3), times[2]);
        Helper.setSpinnerToTime(findViewById(R.id.spinner_termin4), times[3]);
    }
    public void button_question_ok(View view){
        String numContacts, numHours, numMinutes;
        numContacts = Helper.getEditText(findViewById(R.id.editText_contacts));
        numHours = Helper.getEditText(findViewById(R.id.editText_hours));
        numMinutes = Helper.getEditText(findViewById(R.id.editText_minutes));

        // only 60 minutes per hour
        if( Integer.parseInt(numMinutes) > 59 )
            return;
        if( !_control.isQuestionInputOK(numHours+":"+numMinutes) ){
            // todo: display popup with warning
            return;
        }
        try{
            _control.saveUserInputToCSV(false, numContacts, numHours, numMinutes);
            _control.saveUserData();

            setContentView(R.layout.mainmenu);
            Helper.setButtonDisabled(findViewById(R.id.goto_question_button),
                                     _control.wasLastQuestionAnswered());
        } catch(IllegalStateException ex){
            // do nothing, wait for user to click on the button again
        }
    }
    public void button_time_ok(View view){
        // get times from spinners
        int[] times = new int[4];
        times[0] = Helper.getTimeFromSpinner(findViewById(R.id.spinner_termin1));
        times[1] = Helper.getTimeFromSpinner(findViewById(R.id.spinner_termin2));
        times[2] = Helper.getTimeFromSpinner(findViewById(R.id.spinner_termin3));
        times[3] = Helper.getTimeFromSpinner(findViewById(R.id.spinner_termin4));
        // and update the user data
        _control.updateUserTimes(times);
        // set the next alarm to the next possible time
        setAlarm(Helper.getTimeDifferenceInMillisecs(_control), false);
        _control.saveUserData();
        // then change to main menu if user did already answer at least one question, else to question
        if( _control.userAnsweredAQuestion() ) {
            setContentView(R.layout.mainmenu);
            Helper.setButtonDisabled(findViewById(R.id.goto_question_button),
                                     _control.wasLastQuestionAnswered());
        }
        else {
            setContentView(R.layout.question);
            Helper.setQuestion(_control.generateQuestionText(), findViewById(R.id.question));
        }
    }
    public void button_time_back(View view){
        setContentView(R.layout.mainmenu);
        Helper.setButtonDisabled(findViewById(R.id.goto_question_button),
                                 _control.wasLastQuestionAnswered());
    }
    public void button_proband_ok(View view){
        // create probandencode from different parts
        String codePart1 = Helper.getEditText(findViewById(R.id.pc_1));
        String codePart2 = Helper.getEditText(findViewById(R.id.pc_2));
        String codePart3 = Helper.getEditText(findViewById(R.id.pc_3));
        String codePart4 = Helper.getEditText(findViewById(R.id.pc_4));
        String codePart5 = Helper.getEditText(findViewById(R.id.pc_5));
        // ToDo: check if all are actually characters
        // (and not numbers/other symbols)
        _control.newUser( codePart1+codePart2+codePart3+codePart4+codePart5 );
        setContentView(R.layout.set_time);
    }

    private void setAlarm(long timeDifference, boolean triggeredByAlarm){
        if(triggeredByAlarm) {
            if (!_control.wasLastQuestionAnswered())
                _control.saveUserInputToCSV(true,"","","");
            Time now = new Time(Time.getCurrentTimezone());
            now.setToNow();
            _control.changeLastAlarm(now);
        }
        Intent nextActivityIntent = new Intent(this, MainActivity.class);

        PendingIntent next = PendingIntent.getActivity(getApplicationContext(), 0, nextActivityIntent, 0);

        // alarmanager setzen
        AlarmManager alarmManager = (AlarmManager)(this.getSystemService( Context.ALARM_SERVICE ));
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + timeDifference, next);
        _control.setNextAlarmAt(SystemClock.elapsedRealtime() + timeDifference);
    }
}
