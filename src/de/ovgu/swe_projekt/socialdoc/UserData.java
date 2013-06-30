package de.ovgu.swe_projekt.socialdoc;

import android.content.SharedPreferences;

/**
 * Created by Anne-Lena Simon on 09.06.13.
 */
public class UserData {

    // the probandencode
    private String _probandenCode = "";
    // the times the alarm is supposed to happen at
    private int[] _userTimes = {-77,-77,-77,-77};
    // the time the last answer was given and the amount of alarms since that answer
    private int _currentAlarmTime = 0, _alarmsSinceLastAnswer = 0;
    // the time the last answer was given
    String _lastAnsweredAt = "77:77";
    // was the last question answered?
    private boolean _lastWasAnswered = false;

    public UserData() {
    }

    // import the user data from the settings
    public void importSavedUserData( SharedPreferences pref) {
        _probandenCode = pref.getString("Probandencode", "");
        _userTimes[0] = pref.getInt("time1", -77);
        _userTimes[1] = pref.getInt("time2", -77);
        _userTimes[2] = pref.getInt("time3", -77);
        _userTimes[3] = pref.getInt("time4", -77);
        _lastAnsweredAt = pref.getString("lastAnswered", "77:77");
        _alarmsSinceLastAnswer =  pref.getInt("alarmsSinceLastAnswered", 0);
    }

    // set user data to default values (for new user)
    public void setUserDataToDefault() {
        _probandenCode = "";
        _userTimes[0] = -77;
        _userTimes[1] = -77;
        _userTimes[2] = -77;
        _userTimes[3] = -77;
        _lastAnsweredAt = "77:77";
        _alarmsSinceLastAnswer = 0;
        // todo: set this to true as soon as the alarms work!
        _lastWasAnswered = false;
    }

    // save the user data
    public void saveUserData( SharedPreferences pref) {
        SharedPreferences.Editor preferencesEditor = pref.edit();
        preferencesEditor.putString("Probandencode", _probandenCode);
        preferencesEditor.putString("lastAnswered", _lastAnsweredAt);
        preferencesEditor.putInt("alarmsSinceLastAnswered", _alarmsSinceLastAnswer);
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
        this._probandenCode = probandenCode;
    }

    public int[] getUserTimes() {
        return _userTimes;
    }
    public void setUserTimes(int[] userTimes) {
        this._userTimes = userTimes;
        // todo: set alarm to trigger at first possible user time
    }

    public String getLastAnsweredAt() {
        return _lastAnsweredAt;
    }
    public void setLastAnsweredAt(String lastAnsweredAt) {
        this._lastAnsweredAt = lastAnsweredAt;
    }

    public int getCurrentAlarmTime() {
        return this._currentAlarmTime;
    }
    public String getStringOfCurrentAlarmTime() {
        return _userTimes[_currentAlarmTime] + ":00";
    }

    public int getAlarmsSinceLastAnswer() {
        return _alarmsSinceLastAnswer;
    }
    public void resetAlarmsSinceLastAnswer() {
        this._alarmsSinceLastAnswer = 0;
    }
    public void increaseAlarmsSinceLastAnswer() {
        this._alarmsSinceLastAnswer++;
    }

    public boolean wasLastQuestionAnswered() {
        return _lastWasAnswered;
    }
    public void setLastQuestionWasAnswered(boolean answered){
        // todo: set to false on alarm
        _lastWasAnswered = answered;
    }
}
