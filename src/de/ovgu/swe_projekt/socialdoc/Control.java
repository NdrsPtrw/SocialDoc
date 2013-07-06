package de.ovgu.swe_projekt.socialdoc;

import android.content.SharedPreferences;
import android.os.Environment;
import java.io.*;
import android.text.format.Time;

/**
 * This class takes care of the interaction between GUI, user data and external storage.
 */
public class Control {

    /**
     * The user data, it stores all important data and takes care of saving and retrieving it.
     */
    private UserData _userData;
    /**
     * The shared preferences, needed for access to the smart phone's memory.
     */
    private SharedPreferences _pref;

    /**
     * Constructor; Create new instance of UserData and initialize it.
     * Also save shared preferences.
     * @param pref the shared preferences
     */
    public Control(SharedPreferences pref) {
        _userData = new UserData();
        _pref = pref;
        _userData.importSavedUserData(_pref);
    }

    /**
     * Create a new User using the Probandencode entered by the user.
     * @param probandenCode the Probandencode entered by the new user
     */
    public void newUser( String probandenCode ) {
        _userData.setUserDataToDefault();
        _userData.setProbandenCode( probandenCode );
        saveUserData();
    }

    /**
     * Write the last alarm time to _userData.
     * @param time time the last alarm happened at
     */
    public void changeLastAlarm(Time time) {
        _userData.setLastAlarm(time);
        _userData.setLastQuestionWasAnswered(false, "");
    }

    /**
     * Write new user times to _userData.
     * @param newTimes new user times, retrieved from view set_time
     */
    public void updateUserTimes( int[] newTimes ) {
        _userData.setUserTimes( newTimes );
    }

    /**
     * Getter for user times
     * @return user times
     */
    public int[] getUserTimes()
    {
        return _userData.getUserTimes();
    }

    /**
     * Check if the last question opened by an alarm has been answered by the user.
     * @return true, if the user answered the last question
     */
    public boolean wasLastQuestionAnswered() {
        return _userData.wasLastQuestionAnswered();
    }

    /**
     * Check if the user answered any question yet or if he/she is new.
     * @return true, if the user has answered at least one question since he was created
     */
    public boolean userAnsweredAQuestion() {
        return !_userData.userIsNew();
    }

    /**
     * Check if the entry for Probandencode in _userData is empty.
     * @return true, if no Probandencode has been entered by the user yet
     */
    public boolean probandenCodeIsEmpty() {
        return _userData.getProbandenCode().contentEquals("");
    }

    /**
     * Set the time the next alarm will happen at. (see SystemClock.elapsedRealtime())
     * @param time time (in milliseconds since system start) the next alarm will happen at
     */
    public void setNextAlarmAt(long time) {
        _userData.setNextAlarm(time);
    }

    /**
     * Check if the current time is an alarm time by comparing it to the next alarm time.
     * @param currentTime current system time (SystemClock.elapsedRealtime())
     * @return true, if the current time is close enough to the next alarm time
     */
    public boolean isAlarmTime(long currentTime) {
        long difference = _userData.getNextAlarm()-currentTime;
        if( difference < 0 ) difference *= -1;
        return difference < 4000;
    }

    /**
     * Check the user input given for the current question.
     * That means check if this much time can have passed since the last question was answered.
     * @param input the user input as taken from the question layout
     * @return true, if the input is a possible amount of time
     */
    public boolean isQuestionInputOK(String input) {
        // todo: check the input (format: h:m) -  make sure this much time can have passed since last answer
        String timeAtLastAnswer = _userData.getTimeAtLastAnsweredAlarm(); // format: dd.mm.yyyy;hh:mm
        Time now = new Time(Time.getCurrentTimezone());
        now.setToNow();
        // work with day, month, hours, minutes
        return true;
    }

    /**
     * Generate the time-specific text to be used in the question.
     * @return String containing the text to be inserted into the question
     */
    public String generateQuestionText() {
        String returnText;
        if( _userData.userIsNew() )
            returnText = "heute schon";
        else {
            // create new Times from last answer and last alarm
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
            // find out if a day or more has passed since the last answer
            int dayDifference = alarmTimeAtAnswer.yearDay-timeAtAlarm.yearDay;
            // choose the appropriate question text
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

    /**
     * Save the user data to the system's preference storage.
     */
    public void saveUserData() {
        _userData.saveUserData(_pref);
    }

    /**
     * Create the Probandencode.csv if it does not exist yet.
     * @return true, if the file already exists
     */
    public boolean createCSV() {
        boolean fileAlreadyExists;
        fileAlreadyExists = saveToCSV("Code;Datum;Alarmzeit;Antwortzeit;Abbruch;Kontakte;Stunden;Minuten", true);
        return fileAlreadyExists;
    }

    /**
     * Save the user's answer to the current question to the Probandencode.csv.
     * Save dummy-data if the question has been skipped.
     * @param cancelled did the user skip answering the question?
     * @param numContacts amount of contacts the user had since the last question (user input)
     * @param numHours amount of hours during which the user interacted with contacts since the last question (user input)
     * @param numMinutes amount of minutes (59 max) during which the user interacted with contacts since the last question (user input)
     */
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

    /**
     * Writes the input to the Probandencode.csv (as a single line).
     * @param input line to write to the file
     * @param newFile create a new file or add to an existing file?
     * @return true, if the file Probandencode.csv already exists
     */
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
                    file = new File(file.getAbsolutePath(), "Probandencode.csv");
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
