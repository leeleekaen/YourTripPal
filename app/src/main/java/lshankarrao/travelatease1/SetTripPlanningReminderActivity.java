package lshankarrao.travelatease1;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

public class SetTripPlanningReminderActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    Calendar calendar;
    int tripId;
    String tripTitle, remDurationChoice, onOrB4choice;
    boolean validInput = true;
    EditText reminderMessage;
    Spinner spinner;
    EditText reminderDurationNumber;
    long stTimeMilli;
    RadioGroup radioGrpOnOrB4;
    EditText remDate, remTime;
    ImageButton imgRemDate, imgRemTime;
    TextView textViewTripTimingsInfo;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_planning_reminder);
        getSupportActionBar().setTitle("Set a Reminder");

        Intent intent = getIntent();

        tripId = intent.getIntExtra("id", -1);
        if (tripId == -1) {
            Log.d("trip id invalid: ", tripId + "");
            return;
        }

        spinner = (Spinner) findViewById(R.id.spinnerRemType);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.reminderOptions_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(this);


        reminderDurationNumber = (EditText) findViewById(R.id.editTextSTPRRemNumber);
        reminderDurationNumber.setText("1");

        remDate = (EditText) findViewById(R.id.editTextSTPRRemDate);
        remTime = (EditText) findViewById(R.id.editTextSTPRRemTime);
        imgRemDate = (ImageButton) findViewById(R.id.imageButtonSTPRRemDate);
        imgRemTime = (ImageButton) findViewById(R.id.imageButtonSTPRREmTime);

        final TripDbHelper tripDbHelper = new TripDbHelper(this);
        TripInfo tripInfo = tripDbHelper.getTripInfo(tripId);
        stTimeMilli = tripInfo.getStTimeMillis();
        textViewTripTimingsInfo = (TextView) findViewById(R.id.textViewSTPRTripTimingsInfo);
        String timings = "Trip starts " + tripDbHelper.getDateFromMilli(
                stTimeMilli, "EEE MMM dd ''yy") + " at " +
                tripDbHelper.getTimeFromMilli(stTimeMilli, "hh:mm aaa");
        textViewTripTimingsInfo.setText(timings);

        reminderMessage = (EditText) findViewById(R.id.editTextSTPRMessage);

        if (getIntent().hasExtra("purpose")) {
            Log.d("STPRinsidePurpose", "hi");
            if (getIntent().getStringExtra("purpose").equals("toDoReminder")) {
                reminderMessage.setText("To-do Reminder");
            } else if (getIntent().getStringExtra("purpose").equals("pack")) {
                reminderMessage.setText("Packing Reminder");
            }
        }

        reminderDurationNumber.setEnabled(false);
        spinner.setEnabled(false);
        remDate.setEnabled(false);
        remTime.setEnabled(false);
        imgRemTime.setEnabled(false);
        imgRemDate.setEnabled(false);

        radioGrpOnOrB4 = (RadioGroup) findViewById(R.id.radioGroupRemOnOrBefore);
        radioGrpOnOrB4.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int selected = radioGrpOnOrB4.getCheckedRadioButtonId();
                onOrB4choice = ((RadioButton) findViewById(selected)).getText().toString();

                switch (checkedId) {
                    case R.id.radioButtonSTPRon:

                        reminderDurationNumber.setEnabled(false);
                        spinner.setEnabled(false);
                        remDate.setEnabled(true);
                        remTime.setEnabled(true);
                        imgRemTime.setEnabled(true);
                        imgRemDate.setEnabled(true);
                        break;
                    case R.id.radioButtonSTPRbefore:

                        reminderDurationNumber.setEnabled(true);
                        spinner.setEnabled(true);
                        remDate.setEnabled(false);
                        remTime.setEnabled(false);
                        imgRemTime.setEnabled(false);
                        imgRemDate.setEnabled(false);
                        break;
                }
            }
        });

        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(stTimeMilli);

        imgRemDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = new GregorianCalendar();
                c.setTimeInMillis(stTimeMilli);
                int cYear = c.get(Calendar.YEAR);
                int cMonth = c.get(Calendar.MONTH);
                int cDay = c.get(Calendar.DAY_OF_MONTH);
                System.out.println("the selected " + cDay);
                DatePickerDialog datePickerDialog = new DatePickerDialog(SetTripPlanningReminderActivity.this,
                        new DateSetListener(remDate), cYear, cMonth, cDay);
                datePickerDialog.show();
            }
        });

        imgRemTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = new GregorianCalendar();
                c.setTimeInMillis(stTimeMilli);
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(SetTripPlanningReminderActivity.this,
                        new TimeSetListener(remTime), hour, minute, false);
                timePickerDialog.show();
            }
        });

        Button set = (Button) findViewById(R.id.buttonSTPRset);
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (radioGrpOnOrB4.getCheckedRadioButtonId() == -1) {
                    toast("Please choose an option.");
                    return;
                }
                if (reminderMessage.getText().toString().length() > 20) {
                    toast("Reminder Message too long! Must be less than 20 characters");
                    return;
                }

                if (onOrB4choice.equals("On")) {
                    Log.d("I'm", "on ON" + "");

                    String date = remDate.getText().toString();

                    if (date.equals(null) || date.isEmpty() == true) {
                        toast("Please enter a Date");
                        return;
                    }
                    if (!completeDateAndTimeFieldsCheck(remDate, remTime)) {
                        return;
                    }


                    String[] remSetDate = date.trim().split("/");
                    Log.d("entered st dt ", date);
                    Log.d("date", remSetDate[0] + "/" + remSetDate[1] + "/" + remSetDate[2] + "/");

                    String remTimeSet = remTime.getText().toString();

                    if (remTimeSet.equals(null) || remTimeSet.isEmpty()) {
                        toast("Please enter a Time");
                    }


                    String[] tym = remTimeSet.trim().split(" ");
                    String[] startTime = tym[0].split(":");
                    int hour = Integer.parseInt(startTime[0]);
                    if (tym[1].toUpperCase().equals("PM")) {
                        hour = hour + 12;
                    }
                    Log.d("startTime", startTime[0] + ":" + startTime[1]);

                    calendar.set(Integer.parseInt(remSetDate[2]),
                            (Integer.parseInt(remSetDate[0]) - 1),
                            Integer.parseInt(remSetDate[1]),
                            hour,
                            Integer.parseInt(startTime[1]));

                    prepareToSendRem(calendar);

                } else if (onOrB4choice.equals("Before")) {

                    String tempRemDurNum = reminderDurationNumber.getText().toString();

                    if (tempRemDurNum.equals(null) || tempRemDurNum.isEmpty()) {
                        toast("Enter the number field properly.");
                        return;
                    } else if (tempRemDurNum.length() > 2) {
                        toast("Number limit exceeded. Maximum accepted = 99");
                        return;
                    }

                    int number;
                    try {
                        number = Integer.parseInt(tempRemDurNum);
                    } catch (NumberFormatException e) {
                        toast("Invalid number format.");
                        return;
                    }

                    if (remDurationChoice.equals("hour/s")) {

                        calendar.add(Calendar.HOUR_OF_DAY, -number);
                        Log.d("time: ", calendar.getTime() + "");

                    } else if (remDurationChoice.equals("day/s")) {
                        calendar.add(Calendar.DAY_OF_MONTH, -number);
                    } else if (remDurationChoice.equals("week/s")) {
                        calendar.add(Calendar.WEEK_OF_MONTH, -number);
                    } else if (remDurationChoice.equals("month/s")) {
                        calendar.add(Calendar.MONTH, -number);
                    } else if (remDurationChoice.equals("minute/s")) {
                        calendar.add(Calendar.MINUTE, -number);
                    }

                    prepareToSendRem(calendar);


                } else {
                    toast("Please select a time.");

                }

            }
        });
    }

    private boolean completeDateAndTimeFieldsCheck(EditText remDate, EditText remTime) {

        TripDbHelper tripDbHelper = new TripDbHelper(this);

        remDate.setBackgroundColor(Color.TRANSPARENT);
        remTime.setBackgroundColor(Color.TRANSPARENT);

        Boolean ipFieldErrStatus = true;

        if (!tripDbHelper.dateTimeCheck(remDate, "remDate", getApplicationContext())) {
            ipFieldErrStatus = false;
        }

        if (!tripDbHelper.dateTimeCheck(remTime, "remTime", getApplicationContext())) {
            ipFieldErrStatus = false;
        }

        return ipFieldErrStatus;
    }


    private void prepareToSendRem(Calendar calendar) {
        TripDbHelper tripDbHelper = new TripDbHelper(this);

        long when = calendar.getTimeInMillis();
        Log.d("time after sub: ", when + " ");

        String userMessage = reminderMessage.getText().toString();
        if (userMessage == null) {
            userMessage = "";
        }


        AlarmParams userAlarmParams = new AlarmParams("userSetReminder", tripId, userMessage,
                when, -1, null, getApplicationContext(), 0);
        setAlarm(userAlarmParams);

        String remSetTime = "Reminder set for " + tripDbHelper.getDateFromMilli(
                calendar.getTimeInMillis(), "EEE MMM dd ''yy") + " at " +
                tripDbHelper.getTimeFromMilli(calendar.getTimeInMillis(), "hh:mm a");
        toast(remSetTime);

        Intent goBackToItinerary = new Intent(getApplicationContext(), ViewTripItineraryActivity.class);
        goBackToItinerary.putExtra("id", tripId);
        startActivity(goBackToItinerary);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (menu != null) {

            menu.findItem(R.id.action_addtrip).setVisible(false);
        }
        return true;
    }

    public void setAlarm(AlarmParams alarmParams) {
        Random r = new Random();
        int x = r.nextInt();

        Intent intentAlarm = new Intent(alarmParams.context, AlarmReceiver.class);

        intentAlarm.putExtra("tripId", alarmParams.tripId);
        String purpose = alarmParams.purpose;
        intentAlarm.putExtra("purpose", purpose);
        if (purpose.equals("autoLocationReminder")) {
            intentAlarm.putExtra("stTimeMilli", alarmParams.startTimeMilli);
            intentAlarm.putExtra("placeAddress", alarmParams.startAddress);
        } else if (purpose.equals("autoPlanningReminder")) {
            intentAlarm.putExtra("stTimeMilli", alarmParams.startTimeMilli);
            intentAlarm.putExtra("remTimeSlotNum", alarmParams.slotNum);
        } else if (purpose.equals("userSetReminder")) {
            intentAlarm.putExtra("userMessage", alarmParams.userMessage);
        }


        long reminderTime = alarmParams.reminderTime;
        Log.d("STPRremTime", reminderTime + "");

        AlarmManager alarmManager = (AlarmManager) alarmParams.context.getSystemService(Context.ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP, reminderTime, PendingIntent.getBroadcast(
                alarmParams.context, x, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT |
                        Intent.FILL_IN_DATA));
        Log.d("Alarm ", "set" + "");
        x++;
        String message = "Reminder Set!";
        Log.d("msg", message);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (getIntent().hasExtra("purpose")) {
            if (getIntent().getStringExtra("purpose").equals("toDoReminder")) {
                Intent tripPlanStatus = new Intent(SetTripPlanningReminderActivity.this, ToDoListActivity.class);
                tripPlanStatus.putExtra("tripId", tripId);
                startActivity(tripPlanStatus);
                finish();
            } else if (getIntent().getStringExtra("purpose").equals("pack")) {
                Intent packIntent = new Intent(SetTripPlanningReminderActivity.this, ThingsToPackActivity.class);
                packIntent.putExtra("tripId", tripId);
                startActivity(packIntent);
                finish();
            }
        } else {
            Intent goBackToItinerary = new Intent(SetTripPlanningReminderActivity.this, ViewTripItineraryActivity.class);
            goBackToItinerary.putExtra("id", tripId);
            startActivity(goBackToItinerary);
            finish();
        }
    }

    public void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        remDurationChoice = adapterView.getItemAtPosition(i).toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onClick(View view) {

        int selected = radioGrpOnOrB4.getCheckedRadioButtonId();
        if (selected != -1) {
            onOrB4choice = ((RadioButton) findViewById(selected)).getText().toString();
            if (onOrB4choice.equals("On")) {
                reminderDurationNumber.setEnabled(false);
                spinner.setEnabled(false);
                remDate.setEnabled(true);
                remTime.setEnabled(true);
                imgRemTime.setEnabled(true);
                imgRemDate.setEnabled(true);

            } else if (onOrB4choice.equals("Before")) {
                reminderDurationNumber.setEnabled(true);
                spinner.setEnabled(true);
                remDate.setEnabled(false);
                remTime.setEnabled(false);
                imgRemTime.setEnabled(false);
                imgRemDate.setEnabled(false);
            }

        }


    }
}
