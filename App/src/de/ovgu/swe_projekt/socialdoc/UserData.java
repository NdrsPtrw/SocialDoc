package de.ovgu.swe_projekt.socialdoc;

import android.content.SharedPreferences;
import android.text.format.Time;

/**
 * This class contains all user data needed to run the app.
 * All of these variables are saved to the app's preferences and are loaded from there if they exist.
 */
public class UserData {

    /**
     * the Probandencode entered by the user (exactly once per user)
     */
    private String _probandenCode = "";

    /**
     * the times the alarm is supposed to happen at
     */
    private int[] _userTimes = {-77,-77,-77,-77};

    /**
     * the system time the next alarm will happen at
     */
    private long _nextAlarm = 0;

    /**
     * the exact date and time at last alarm, needed for creating the appropriate question
     */
    private String _lastAlarm = "xx.xx.xxxx.77:77";

    /**
     * the time the last alarm happened at, needed for writing to csv
     */
    String _timeAtLastAlarm = "77:77";

    /**
     * the time the last answer was given at, needed for creating the appropriate question
     */
    String _dateAndTimeOfLastAnsweredAlarm = "xx.xx.xxxx.77:77";

    /**
     * was the last question answered? Was a new user created who did not yet answer any questions?
     */
    private boolean _lastWasAnswered = false, _newUser = true;

    /**
     * Empty constructor.
     */
    public UserData() {
    }

    /**
     * Import the user data from the settings.
     * @param pref shared preferences used to access the settings
     */
    public void importSavedUserData( SharedPreferences pref) {
        _probandenCode = pref.getString("Probandencode", "");
        _lastAlarm = pref.getString("lastAlarm", "xx.xx.xxxx.77:77");
        _timeAtLastAlarm =  pref.getString("timeAtLastAlarm", "77:77");
        _dateAndTimeOfLastAnsweredAlarm =  pref.getString("dateAndTimeAtLastAnswer", "xx.xx.xxxx.77:77");
        _nextAlarm = pref.getLong("nextAlarm", 0);
        _lastWasAnswered = pref.getBoolean("lastQuestionWasAnswered", false);
        _newUser = pref.getBoolean("newUser", true);
        _userTimes[0] = pref.getInt("time1", -77);
        _userTimes[1] = pref.getInt("time2", -77);
        _userTimes[2] = pref.getInt("time3", -77);
        _userTimes[3] = pref.getInt("time4", -77);
    }

    /**
     * Set the user data to default values (for creation of new user).
     */
    public void setUserDataToDefault() {
        _probandenCode = "";
        _userTimes[0] = -77;
        _userTimes[1] = -77;
        _userTimes[2] = -77;
        _userTimes[3] = -77;
        _nextAlarm = 0;
        _lastAlarm = "xx.xx.xxxx.77:77";
        _timeAtLastAlarm = "77:77";
        _dateAndTimeOfLastAnsweredAlarm = "xx.xx.xxxx.77:77";
        _lastWasAnswered = false;
        _newUser = true;
    }

    /**
     * Save the user data to the app's settings.
     * @param pref shared preferences used to access the settings
     */
    public void saveUserData( SharedPreferences pref) {
        SharedPreferences.Editor preferencesEditor = pref.edit();
        preferencesEditor.putString("Probandencode", _probandenCode);
        preferencesEditor.putString("lastAlarm", _lastAlarm);
        preferencesEditor.putString("timeAtLastAlarm", _timeAtLastAlarm);
        preferencesEditor.putString("dateAndTimeAtLastAnswer", _dateAndTimeOfLastAnsweredAlarm);
        preferencesEditor.putLong("nextAlarm", _nextAlarm);
        preferencesEditor.putBoolean("lastQuestionWasAnswered", _lastWasAnswered);
        preferencesEditor.putBoolean("newUser", _newUser);
        preferencesEditor.putInt("time1", _userTimes[0]);
        preferencesEditor.putInt("time2", _userTimes[1]);
        preferencesEditor.putInt("time3", _userTimes[2]);
        preferencesEditor.putInt("time4", _userTimes[3]);
        preferencesEditor.commit();
    }

    /**
     * Getter for the Probandencode entered by the user on first use of the app
     * @return the Probandencode
     */
    public String getProbandenCode() {
        return _probandenCode;
    }

    /**
     * Setter for the Probandencode
     * @param probandenCode the Probandencode
     */
    public void setProbandenCode(String probandenCode) {
        _probandenCode = probandenCode;
    }

    /**
     * Getter for the user times (alarm times)
     * @return int array containing the 4 selected user times
     */
    public int[] getUserTimes() {
        return _userTimes;
    }

    /**
     * Setter for the user times (alarm times)
     * @param userTimes the 4 alarm times selected by the user
     */
    public void setUserTimes(int[] userTimes) {
        _userTimes = userTimes;
    }

    /**
     * Getter for time and date of last alarm
     * @return a string containing the formatted date and time of the last alarm
     */
    public String getLastAlarm() {
        return _lastAlarm;
    }

    /**
     * Setter for time and date of last alarm.
     * Sets both _lastAlarm and _timeAtLastAlarm.
     * @param lastAlarm
     */
    public void setLastAlarm(Time lastAlarm) {
        _lastAlarm = lastAlarm.format("%d.%m.%Y.%H")+":00";
        _timeAtLastAlarm = lastAlarm.format("%H")+":00";
    }

    /**
     * Getter for the time (in milliseconds since system start) at next alarm
     * @return the time (in milliseconds since system start) at next alarm
     */
    public long getNextAlarm() {
        return _nextAlarm;
    }

    /**
     * Setter for the time (in milliseconds since system start) at next alarm
     * @param nextAlarm the time (in milliseconds since system start) at next alarm
     */
    public void setNextAlarm(long nextAlarm) {
        _nextAlarm = nextAlarm;
    }

    /**
     * Getter for the time (in hours only) the last alarm happened at
     * @return the time (in hours only) the last alarm happened at
     */
    public String getTimeAtLastAlarm() {
        return _timeAtLastAlarm;
    }

    /**
     * Getter for the time the last alarm for a question that has been answered happened at (hours only)
     * @return the time the last alarm for a question that has been answered happened at (hours only)
     */
    public String getTimeAtLastAnsweredAlarm() {
        return _dateAndTimeOfLastAnsweredAlarm;
    }

    /**
     * Check if the last question has been answered by the user.
     * @return true, if the last question has been answered by the user
     */
    public boolean wasLastQuestionAnswered() {
        return _lastWasAnswered;
    }

    /**
     * Setter for both _lastWasAnswered and _dateAndTimeOfLastAnsweredAlarm
     * @param answered was the last question answered by the user?
     * @param answeredAlarm contains date and time of last answered alarm in format "dd.mm.yyyy.HH:MM"
     */
    public void setLastQuestionWasAnswered(boolean answered, String answeredAlarm){
        _lastWasAnswered = answered;
        if( answered ) {
            userAnsweredAQuestion();
            _dateAndTimeOfLastAnsweredAlarm = answeredAlarm;
        }
    }

    /**
     * Check if the user is new (i.e. has not answered a question yet).
     * @return true, if the user is new
     */
    public boolean userIsNew() {
        return _newUser;
    }

    /**
     * Saves that the user answered at least one question and therefore is no longer new.
     */
    public void userAnsweredAQuestion(){
        _newUser = false;
    }
}
