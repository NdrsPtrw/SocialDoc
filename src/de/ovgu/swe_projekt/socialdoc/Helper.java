package de.ovgu.swe_projekt.socialdoc;

import android.text.format.Time;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * This class contains several static functions used by the app.
 */
public class Helper {

    //============== TEXT ==============================================================

    /**
     * Set the appropriate question text according to the String passed to this function.
     * @param time String containing the text to insert into the question (time since last answer)
     * @param view text view to display the text in
     */
    public static void setQuestion(String time, TextView view){
        String quest = "Mit wie vielen Interaktionspartnern hatten Sie ";
        quest+= time;
        quest+=" Kontakt? Schätzen Sie bitte zusätzlich die Gesamtdauer der Kontakte ein.";

        view.setText(quest);
    }

    //============== INPUT AND UI ======================================================

    /**
     * Get the text from an edit text.
     * @param edit edit text to extract text from
     * @return text extracted from the edit text
     */
    public static String getEditText(EditText edit){
        String returnText;
        try{
            returnText = edit.getText().toString();
        } catch(NullPointerException ex) {
            returnText = "";
        }
        return returnText;
    }

    /**
     * Get the time (hour in int) from a spinner; requires spinner text of format hh:mm
     * @param spinner spinner to get the selected time from
     * @return hour taken from the spinner
     */
    public static int getTimeFromSpinner(Spinner spinner){
        String selected;
        try{
            selected = spinner.getSelectedItem().toString();
        } catch( NullPointerException ex ){
            selected = "-77";
        }
        // parse the hour selected (we don't care about the minutes, those are 00 anyway)
        return Integer.parseInt(selected.split(":")[0]);
    }

    /**
     * Set an image button disabled. (Or enabled if disabled is false.)
     * @param button Button to set en-/disabled
     * @param disabled set to disabled?
     */
    public static void setButtonDisabled(ImageButton button, boolean disabled){
        try{
            button.setEnabled(!disabled);
        } catch(NullPointerException ex){}
    }

    /**
     * Set the spinner's current selection to the given time.
     * @param spinner Spinner to change selection for
     * @param time Time (hour only) to change to
     */
    public static void setSpinnerToTime(Spinner spinner, int time){
        try{
            int spinnerPosition = 0;

            if( time != -77 ){
                try{
                    // get the position of the element we want to set to
                    // unchecked cast here can't be avoided...
                    @SuppressWarnings("unchecked")
                    ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
                    spinnerPosition = adapter.getPosition(time+":00");
                } catch(NullPointerException ex){}
            }
            spinner.setSelection(spinnerPosition);
        } catch(NullPointerException ex){}
    }

    //============== ALARM =============================================================
    /**
     * Find out the next alarm time by checking which user time is closest to the current moment.
     * @param control the control class
     * @return time of next alarm
     */
    public static int calcTimeAlarm(Control control) {
        Time now = new Time();
        now.setToNow();
        int[] times = control.getUserTimes().clone();
        int hourNextAlarm = times[0];
        if (hourNextAlarm == -77) return 3;
        int minHourDifference = 24;

        for (int i = 0; i < 3; i++){
            int temp = times[i] - Integer.parseInt(now.format("%H"));
            if (temp < minHourDifference && temp > 0){
                minHourDifference = temp;
                hourNextAlarm = times[i];
                //check if alarm Hour is actual Hour
                if (hourNextAlarm == now.hour){
                    hourNextAlarm = times[i+1 % 4];
                }
            }
        }
        return hourNextAlarm;
    }

    /**
     * Calculates the amount of milliseconds until the next alarm time.
     * @param control the control class
     * @return amount of milliseconds until next alarm time
     */
    public static long getTimeDifferenceInMillisecs(Control control){
        // Wenn man davon ausgeht, dass das Array die Uhrzeiten als Int enthält
        int nextAlarmHour = calcTimeAlarm(control);

        // Erstellt ein Date von jetzt und dem Alarm
        Time now = new Time();
        now.setToNow();

        Time alarmTime = new Time();
        alarmTime.set(0, 0, nextAlarmHour, now.monthDay, now.month, now.year);

        if(isNextAlarmTomorrow(nextAlarmHour, Integer.parseInt(now.format("%H")))) alarmTime.monthDay+=1;

        // Berechnet den Zeitunterschied in Millisekunden
        return alarmTime.toMillis(true) - now.toMillis(true);
    }

    /**
     * Checks if the next alarm is tomorrow or today.
     * @param nextAlarmHour hour the next alarm will happen at
     * @param hourNow current hour
     * @return true, if the alarm is tomorrow
     */
    public static boolean isNextAlarmTomorrow(int nextAlarmHour, int hourNow){
        return (hourNow > nextAlarmHour);
    }
}
