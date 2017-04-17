package lshankarrao.travelatease1;

import android.app.DatePickerDialog;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.EditText;


class DateSetListener implements DatePickerDialog.OnDateSetListener {

    int mYear ;
    int mMonth ;
    int mDay ;
    EditText editText;

    public DateSetListener(EditText editText){

        this.editText = editText;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear,
                          int dayOfMonth) {
        this.mYear = year;
        this.mMonth = monthOfYear;
        this.mDay = dayOfMonth;

        StringBuilder date = new StringBuilder().append(this.mMonth + 1).append("/").append(this.mDay).append("/")
                .append(this.mYear).append(" ");
        Log.d("Date = ", date.toString());

        this.editText.setText(date.toString());

        System.out.println(editText.getText().toString());

    }
}
