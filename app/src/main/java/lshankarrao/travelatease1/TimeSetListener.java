package lshankarrao.travelatease1;

import android.app.TimePickerDialog;
import android.widget.EditText;
import android.widget.TimePicker;


public class TimeSetListener implements TimePickerDialog.OnTimeSetListener {


    EditText editText;
    public TimeSetListener(EditText editText){
        this.editText = editText;
    }

    int hour, minute ;
    String amOrPm;

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        this.minute = minute;
        this.hour = hourOfDay;
        this.amOrPm = "AM";
        if(hourOfDay >= 12){
            this.amOrPm = "PM";
            if(hourOfDay != 12) {
                this.hour = hourOfDay - 12;
            }
        }
        String min = minute+"";
        if(minute < 10){
            min = "0"+min;
        }

        this.editText.setText(this.hour+":"+min + " "+this.amOrPm);
    }
}
