package de.ovgu.swe_projekt.socialdoc;

import android.content.SharedPreferences;
import android.os.Environment;
import java.io.*;

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

    public void saveUserInputToCSV() {
        if( Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ) {
            File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            FileOutputStream fOut = null;
            try {
                try {
                    file = new File(file.getAbsolutePath(), "test.csv");
                    if (file != null) {
                        fOut = new FileOutputStream(file, true);
                        if (fOut != null) {
                            fOut.write("test\n".getBytes());
                        }
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
