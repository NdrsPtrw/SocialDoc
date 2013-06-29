package de.ovgu.swe_projekt.socialdoc;

import android.content.SharedPreferences;
import android.os.Environment;
import java.io.*;
import android.text.format.Time;

/**
 * Created by Anne-Lena Simon on 09.06.13.
 */
public class Control {

    UserData _userData;

    public Control(SharedPreferences pref) {
        _userData = new UserData();
        _userData.importSavedUserData(pref);
    }

    public void newUser( String probandenCode ) {
        _userData.setUserDataToDefault();
        _userData.setProbandenCode( probandenCode );
    }

    public void changeLastAnsweredAt() {
        _userData.setLastAnsweredAt( _userData.getCurrentAlarmTime() );
    }

    public void updateUserTimes( int[] newTimes ) {
        _userData.setUserTimes( newTimes );
    }

    // call this from activity with parameter "getSharedPreferences("PsyAppPreferences", 0)"
    public void saveUserData( SharedPreferences pref ) {
        _userData.saveUserData(pref);
    }

    public boolean createCSV() {
        saveToCSV("Code;Datum;Alarmzeit;Antwortzeit;Abbruch;Kontakte;Stunden;Minuten", true);
        return true; //ToDo: return whether file already exists or not
    }

    public void saveUserInputToCSV(boolean cancelled, String numContacts, String numHours, String numMinutes) {
        // create a string containing the current date and one containing the current time
        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        String date = today.format("%d.%m.%Y");
        String answerTime = today.format("%H:%M");

        //ToDo: remember the current alarm time
        String currentAlarm = "25:00";

        // get probandencode
        String probandencode = _userData.getProbandenCode();

        String cancelledText = "0";
        if( cancelled ){
            cancelledText = "1";
            answerTime = "-77";
            numContacts = "-77";
            numHours = "-77";
            numMinutes = "-77";
        }

        String inputString = probandencode+";"+date+";"+currentAlarm+";"
                +answerTime+";"+cancelledText+";"+numContacts+";"+numHours+";"+numMinutes;
        saveToCSV(inputString, false);
    }

    private void saveToCSV(String input, boolean newFile) {
        if( Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ) {
            File file = Environment.getExternalStorageDirectory();
            FileOutputStream fOut = null;
            try {
                try {
                    file = new File(file.getAbsolutePath(), "test3.csv");
                    if ( file.exists() && newFile == false ) {
                        fOut = new FileOutputStream(file, true);
                        fOut.write((input+"\n").getBytes());
                    }
                    else if( !file.exists() && newFile == true ) {
                        fOut = new FileOutputStream(file, false);
                        fOut.write((input+"\n").getBytes());
                    }
                    else {
                        //ToDo: error handling - this state should not occur
                    }
                } catch (IOException e) {
                    //ToDo: check for exceptions
                    // display error: ask user to restart app -> should create the file
                    // that functionality HAS to be implemented
                }
            } finally {
                if (fOut != null) {
                    try {
                        fOut.flush();
                        fOut.close();
                    } catch (IOException e) {
                        //ToDo: check for exceptions
                        // display error: ask user to restart app -> should create the file
                        // that functionality HAS to be implemented
                    }
                }
            }
        }
        else {
            // TODO: Error handling: No Storage Device Mounted
            // display error: data not saved
            // have user reenter/give user opportunity to reenter
        }
    }

}
