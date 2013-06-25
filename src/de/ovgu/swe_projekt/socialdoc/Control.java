package de.ovgu.swe_projekt.socialdoc;

import android.content.SharedPreferences;
import android.os.Environment;
import java.io.*;
import java.util.Calendar;

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

    public void changeUserOptions(String option, String value) {
        if( option.equals("lastTime") ) _userData.setLastAnsweredAt( Integer.valueOf(value) );
        else
        if( option.equals("useSignalLight") ) _userData.setUseSignalLight( Boolean.valueOf(value) );
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

    public void saveUserInputToCSV(String numContacts, String numHours, String numMinutes) {
        String day, month, year, date;
        Calendar c = Calendar.getInstance();
        day = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
        month = String.valueOf(c.get(Calendar.MONTH));
        year = String.valueOf(c.get(Calendar.YEAR));
        date = day+"."+month+"."+year;

        String probandencode = _userData.getProbandenCode();

        //ToDo: remember if the questions were answered for this time
        String cancelled = "1";
        //ToDo: remember the current alarm time
        String currentAlarm = "25:00";
        //ToDo: remember time the answer was given at
        String answerTime = "25:30";

        String inputString = probandencode+";"+date+";"+currentAlarm+";"
                +answerTime+";"+cancelled+";"+numContacts+";"+numHours+";"+numMinutes;
        saveToCSV(inputString, false);
    }

    private void saveToCSV(String input, boolean newFile) {
        if( Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ) {
            File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
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
