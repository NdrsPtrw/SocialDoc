package de.ovgu.swe_projekt.socialdoc;

import android.text.format.Time;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Created by Anne-Lena Simon on 01.07.13.
 */
public class Helper {

    //============== TEXT ==============================================================
    public static void setQuestion(String time, View view){
        String quest = "Mit wie vielen Interaktionspartnern hatten Sie ";
        quest+= time;
        quest+=" Kontakt? Schätzen Sie bitte zusätzlich die Gesamtdauer der Kontakte ein.";

        TextView text = (TextView)view;
        text.setText(quest);
    }

    //============== INPUT AND UI ======================================================
    public static String getEditText(View view){
        String returnText;
        try{
            EditText editText = (EditText)view;
            returnText = editText.getText().toString();
        } catch(NullPointerException ex) {
            returnText = "";
        }
        return returnText;
    }
    public static int getTimeFromSpinner(View view){
        String selected;
        try{
            Spinner spinner = (Spinner)view;
            selected = spinner.getSelectedItem().toString();
        } catch( NullPointerException ex ){
            selected = "-77";
        }
        // parse the hour selected (we don't care about the minutes, those are 00 anyway)
        return Integer.parseInt(selected.split(":")[0]);
    }
    public static void setButtonDisabled(View view, boolean disabled){
        try{
            ImageButton button = (ImageButton)view;
            button.setEnabled(!disabled);
        } catch(NullPointerException ex){}
    }
    public static void setSpinnerToTime(View view, int time){
        try{
            Spinner spinner = (Spinner)view;
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
    public static boolean isNextAlarmTomorrow(int nextAlarmHour, int hourNow){
        return (hourNow > nextAlarmHour);
    }
}
