package de.ovgu.swe_projekt.socialdoc;

import android.content.SharedPreferences;

/**
 * Created by Anne-Lena Simon on 09.06.13.
 */
public class UserData {

    private String _probandenCode = "";
    private int[] _userTimes = {77,77,77,77};
    private int _lastAnsweredAt = 77;
    private Boolean _useSignalLight = false;
    // missing: sound, volume, light color

    public UserData() {
    }

    public void importSavedUserData( SharedPreferences pref) {
        _probandenCode = pref.getString("Probandencode", "");
        _userTimes[0] = pref.getInt("time1", 77);
        _userTimes[1] = pref.getInt("time2", 77);
        _userTimes[2] = pref.getInt("time3", 77);
        _userTimes[3] = pref.getInt("time4", 77);
        _lastAnsweredAt = pref.getInt("lastAnswered", 77);
        _useSignalLight = pref.getBoolean("useSignalLight", false);
    }

    public void setUserDataToDefault() {
        _probandenCode = "";
        _userTimes[0] = 77;
        _userTimes[1] = 77;
        _userTimes[2] = 77;
        _userTimes[3] = 77;
        _lastAnsweredAt = 77;
        _useSignalLight = false;
    }

    public void saveUserData( SharedPreferences pref) {
        SharedPreferences.Editor preferencesEditor = pref.edit();
        preferencesEditor.putBoolean("useSignalLight", _useSignalLight);
        preferencesEditor.putString("Probandencode", _probandenCode);
        preferencesEditor.putInt("lastAnswered", _lastAnsweredAt);
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
    }

    public int getLastAnsweredAt() {
        return _lastAnsweredAt;
    }

    public void setLastAnsweredAt(int lastAnsweredAt) {
        this._lastAnsweredAt = lastAnsweredAt;
    }

    public Boolean getUseSignalLight() {
        return _useSignalLight;
    }

    public void setUseSignalLight(Boolean useSignalLight) {
        this._useSignalLight = useSignalLight;
    }
}
