package de.ovgu.swe_projekt.socialdoc;

import android.content.SharedPreferences;
import android.os.Environment;
import java.io.*;
import android.text.format.Time;

/**
 * Created by Anne-Lena Simon on 09.06.13.
 */
public class Control {

    private UserData _userData;
    private SharedPreferences _pref;

    public Control(SharedPreferences pref) {
        _userData = new UserData();
        _pref = pref;
        _userData.importSavedUserData(_pref);
    }

    public void newUser( String probandenCode ) {
        _userData.setUserDataToDefault();
        _userData.setProbandenCode( probandenCode );
        saveUserData();
    }

    public void changeLastAlarm(Time time) {
        _userData.setLastAlarm(time);
        _userData.setLastQuestionWasAnswered(false, "");
    }

    public void updateUserTimes( int[] newTimes ) {
        _userData.setUserTimes( newTimes );
    }
    public int[] getUserTimes()
    {
        return _userData.getUserTimes();
    }
    public boolean wasLastQuestionAnswered() {
        return _userData.wasLastQuestionAnswered();
    }
    public boolean userAnsweredAQuestion() {
        return !_userData.userIsNew();
    }
    public boolean probandenCodeIsEmpty() {
        return _userData.getProbandenCode().contentEquals("");
    }

    public void setNextAlarmAt(long time) {
        _userData.setNextAlarm(time);
    }
    public boolean isAlarmTime(long currentTime) {
        long difference = _userData.getNextAlarm()-currentTime;
        if( difference < 0 ) difference *= -1;
        return difference < 4000;
    }

    public boolean isQuestionInputOK(String input) {
        // todo: check the input (format: h:m) -  make sure this much time can have passed since last answer
        String timeAtLastAnswer = _userData.getTimeAtLastAnsweredAlarm(); // format: dd.mm.yyyy;hh:mm
        Time now = new Time(Time.getCurrentTimezone());
        now.setToNow();
        // work with day, month, hours, minutes
        return true;
    }
    public String generateQuestionText() {
        String returnText = "";
        if( _userData.userIsNew() )
            returnText = "heute schon";
        else {
            String[] answerComponents = _userData.getTimeAtLastAnsweredAlarm().split(".");
            String[] alarmComponents = _userData.getLastAlarm().split(".");
            Time alarmTimeAtAnswer = new Time(Time.getCurrentTimezone());
            alarmTimeAtAnswer.set(Integer.parseInt(answerComponents[0]),
                    Integer.parseInt(answerComponents[1]),
                    Integer.parseInt(answerComponents[2]));
            Time timeAtAlarm = new Time(Time.getCurrentTimezone());
            timeAtAlarm.set(Integer.parseInt(alarmComponents[0]),
                            Integer.parseInt(alarmComponents[1]),
                            Integer.parseInt(alarmComponents[2]));
            int dayDifference = alarmTimeAtAnswer.yearDay-timeAtAlarm.yearDay;
            if( dayDifference == 0 )
                returnText = "seit dem letzten Signal um " + answerComponents[3] + " Uhr";
            else if( dayDifference == 1 )
                returnText = "seit dem letzten Signal gestern um " + answerComponents[3] + " Uhr";
            else
                returnText = "seit dem letzten Signal am "
                             + answerComponents[0] + "." + answerComponents[1] + "." + answerComponents[3]
                             + " um " + answerComponents[4] + " Uhr";
        }
        return returnText;
    }

    // call this from activity with parameter "getSharedPreferences("PsyAppPreferences", 0)"
    public void saveUserData() {
        _userData.saveUserData(_pref);
    }

    public boolean createCSV() {
        boolean fileAlreadyExists;
        fileAlreadyExists = saveToCSV("Code;Datum;Alarmzeit;Antwortzeit;Abbruch;Kontakte;Stunden;Minuten", true);
        return fileAlreadyExists;
    }

    public void saveUserInputToCSV(boolean cancelled, String numContacts, String numHours, String numMinutes) {
        // create a string containing the current date and one containing the current time
        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        String date = today.format("%d.%m.%Y");
        String answerTime = today.format("%H:%M");

        // get the time of the current alarm
        String currentAlarm = _userData.getTimeAtLastAlarm();

        // get probandencode
        String probandencode = _userData.getProbandenCode();

        // if no answer was given, set values to invalid and save that
        String cancelledText = "0";
        if( cancelled ){
            cancelledText = "1";
            answerTime = "-77";
            numContacts = "-77";
            numHours = "-77";
            numMinutes = "-77";
        }

        // create the line to write to the .csv
        String inputString = probandencode+";"+date+";"+currentAlarm+";"
                +answerTime+";"+cancelledText+";"+numContacts+";"+numHours+";"+numMinutes;
        saveToCSV(inputString, false);

        if(!cancelled) {
            _userData.setLastQuestionWasAnswered(true, _userData.getLastAlarm());
        }
    }

    private boolean saveToCSV(String input, boolean newFile) {
        boolean fileExists = true;
        // check if there is a storage mounted
        if( Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ) {
            // get the directory to write to
            File file = Environment.getExternalStorageDirectory();
            FileOutputStream fOut = null;
            try {
                try {
                    /* create a file and write to the file depending on whether it already existed
                     * (add a new row) or not (create the file and write the headers)
                     * also make sure the return value mirrors this
                     */
                    // todo: rename to probandencode.csv
                    file = new File(file.getAbsolutePath(), "test2.csv");
                    if ( file.exists() && !newFile ) {
                        fOut = new FileOutputStream(file, true);
                        fOut.write((input+"\n").getBytes());
                    }
                    else if( !file.exists() && newFile ) {
                        fOut = new FileOutputStream(file, false);
                        fOut.write((input+"\n").getBytes());
                        fileExists = false;
                    }
                    else {
                        //ToDo: error handling - this state should not occur; display error message
                        // ask user to restart app
                    }
                } catch (IOException e) {
                    //ToDo: check for exceptions
                    // display error: ask user to restart app -> should create the file
                }
            } finally {
                if (fOut != null) {
                    try {
                        fOut.flush();
                        fOut.close();
                    } catch (IOException e) {
                        //ToDo: check for exceptions
                        // display error: ask user to restart app -> should create the file
                    }
                }
            }
        }
        else {
            // TODO: Error handling: No Storage Device Mounted
            // display error: data not saved
            // have user reenter/give user opportunity to reenter
        }
        return fileExists;
    }

}
