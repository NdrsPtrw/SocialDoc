package de.ovgu.swe_projekt.socialdoc;

import android.content.SharedPreferences;
import android.text.format.Time;

/**
 * Created by Anne-Lena Simon on 09.06.13.
 */
public class UserData {

    // the probandencode
    private String _probandenCode = "";
    // the times the alarm is supposed to happen at
    private int[] _userTimes = {-77,-77,-77,-77};
    // the exact date and time at last alarm, needed for creating the appropriate question
    private String _lastAlarm = "xx.xx.xxxx.77:77";
    // the system time the next alarm will happen at
    private long _nextAlarm = 0;
    // the time the last alarm happened at, needed for writing to csv
    String _timeAtLastAlarm = "77:77";
    // the time the last answer was given at, needed for creating the appropriate question
    String _dateAndTimeOfLastAnsweredAlarm = "xx.xx.xxxx.77:77";
    // was the last question answered? Was a new user created who did not yet answer any questions?
    private boolean _lastWasAnswered = false, _newUser = true;

    public UserData() {
    }

    // import the user data from the settings
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

    // set user data to default values (for new user)
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

    // save the user data
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

    public String getProbandenCode() {
        return _probandenCode;
    }
    public void setProbandenCode(String probandenCode) {
        _probandenCode = probandenCode;
    }

    public int[] getUserTimes() {
        return _userTimes;
    }
    public void setUserTimes(int[] userTimes) {
        _userTimes = userTimes;
        // todo: set alarm to trigger at first possible user time
    }

    public String getLastAlarm() {
        return _lastAlarm;
    }
    public void setLastAlarm(Time lastAlarm) {
        _lastAlarm = lastAlarm.format("%d.%m.%Y.%H")+":00";
        _timeAtLastAlarm = lastAlarm.format("%H")+":00";
    }

    public long getNextAlarm() {
        return _nextAlarm;
    }
    public void setNextAlarm(long nextAlarm) {
        _nextAlarm = nextAlarm;
    }

    public String getTimeAtLastAlarm() {
        return _timeAtLastAlarm;
    }
    public String getTimeAtLastAnsweredAlarm() {
        return _dateAndTimeOfLastAnsweredAlarm;
    }

    public boolean wasLastQuestionAnswered() {
        return _lastWasAnswered;
    }
    public void setLastQuestionWasAnswered(boolean answered, String answeredAlarm){
        _lastWasAnswered = answered;
        if( answered ) {
            userAnsweredAQuestion();
            _dateAndTimeOfLastAnsweredAlarm = answeredAlarm;
        }
    }

    public boolean userIsNew() {
        return _newUser;
    }
    public void userAnsweredAQuestion(){
        _newUser = false;
    }
}
