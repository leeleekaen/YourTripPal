package lshankarrao.travelatease1;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class AddEditTripActivity extends ActionBarActivity implements View.OnClickListener {


    Cursor cursor;

    TripInfo tripInfo;
    boolean purposeEdit = false;
    boolean shareItineraryReminder;
    int tripId;
    String placeName, placeAddress;
    EditText editTextStartDate, editTextStartTime, editTextEndDate, editTextEndTime;
    EditText editTextTripTitle;
    CheckBox checkBox0;
    CheckBox checkBox1;
    CheckBox checkBox2;
    CheckBox checkBox3;
    CheckBox checkBox4;
    CheckBox checkBox5;
    CheckBox checkBox6;
    CheckBox checkBox7;

    Button doneButton;

    Context context;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    TextView textViewPlaceDisplay, textViewPlaceAddrDisplay;
    Button buttonAddOrEditPlace;
    TextView textViewAddEditTripStartPlace;
    ImageView imageViewLocImg;
    ImageButton imageButtonStartDate, imageButtonStartTime, imageButtonEndDate, imageButtonEndTime;
    TextView textViewAETTitleInfo, textViewAETTimeDetails,textViewAETStartTimings,
            textViewAETEndTimings, textViewAddEditTripThemeInfo, editTextAddEditTripNotes ;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_trip);
        context = getApplicationContext();

        checkBox0 = (CheckBox) findViewById(R.id.checkBox0TripTheme);
        checkBox1 = (CheckBox) findViewById(R.id.checkBox1TripTheme);
        checkBox2 = (CheckBox) findViewById(R.id.checkBox2TripTheme);
        checkBox3 = (CheckBox) findViewById(R.id.checkBox3TripTheme);
        checkBox4 = (CheckBox) findViewById(R.id.checkBox4TripTheme);
        checkBox5 = (CheckBox) findViewById(R.id.checkBox5TripTheme);
        checkBox6 = (CheckBox) findViewById(R.id.checkBox6TripTheme);
        checkBox7 = (CheckBox) findViewById(R.id.checkBox7TripTheme);

        editTextStartDate = (EditText) (findViewById(R.id.editTextAddEditTripStartDate));
        editTextStartTime = (EditText) (findViewById(R.id.editTextAddEditTripStartTime));
        editTextEndDate = (EditText) (findViewById(R.id.editTextAddEditTripEndDate));
        editTextEndTime = (EditText) (findViewById(R.id.editTextAddEditTripEndTime));
        imageButtonStartDate = (ImageButton) findViewById(R.id.imageButtonAddEditTripStartDate);
        imageButtonStartTime = (ImageButton) findViewById(R.id.imageButtonAddEditTripStartTime);
        imageButtonEndDate = (ImageButton) findViewById(R.id.imageButtonAddEditTripEndDate);
        imageButtonEndTime = (ImageButton) findViewById(R.id.imageButtonAddEditTripEndTime);
        editTextTripTitle = (EditText) findViewById(R.id.editTextAddEditTripTitle);
        textViewAddEditTripStartPlace = (TextView) findViewById(R.id.textViewAddEditTripStartPlace);

        textViewPlaceDisplay = (TextView)findViewById(R.id.textViewAETPlace);
        textViewPlaceAddrDisplay = (TextView)findViewById(R.id.textViewAETPlaceAddress);
        buttonAddOrEditPlace = (Button)findViewById(R.id.buttonAETAddPlace);
        imageViewLocImg =(ImageView)findViewById(R.id.imageViewAETLocImg);

        textViewAETTitleInfo = (TextView)findViewById(R.id.textViewAETTitleInfo);
        textViewAETTimeDetails = (TextView)findViewById(R.id.textViewAETTimeDetails);
        textViewAETStartTimings = (TextView)findViewById(R.id.textViewAETStartTimings);
        textViewAETEndTimings = (TextView)findViewById(R.id.textViewAETEndTimings);
        textViewAddEditTripThemeInfo = (TextView)findViewById(R.id.textViewAddEditTripThemeInfo);
        editTextAddEditTripNotes = (TextView)findViewById(R.id.editTextAddEditTripNotes);

        doneButton = (Button) findViewById(R.id.buttonAddEditTripDone);




        if (getIntent().hasExtra("purpose") && getIntent().getStringExtra("purpose").equals("edit")) {

            getSupportActionBar().setTitle("Edit Trip");
            buttonAddOrEditPlace.setText("Edit Place");
            imageViewLocImg.setVisibility(View.VISIBLE);

            tripId = getIntent().getIntExtra("id", -1);
            if (tripId == -1) {
                Log.d("trip id invalid! ", "");
                return;
            }
            purposeEdit = true;

            tripInfo = getTripInfoForTripId(tripId, this);

            Log.d("state qwert", tripInfo.getTitle());
            editTextTripTitle.setText(tripInfo.getTitle());

            EditText tvnotes = (EditText) findViewById(R.id.editTextAddEditTripNotes);
            Log.d("state qwert", tripInfo.getNotes());
            tvnotes.setText(tripInfo.getNotes());


            final long tripStartTimeMilli = tripInfo.stTimeMillis;

            String tripStartDate = getDateFromMilliUtil(tripStartTimeMilli, "MM/dd/yyyy");
            editTextStartDate.setText(tripStartDate);
            String tripStartTime = getTimeFromMilliUtil(tripStartTimeMilli, "hh:mm a");
            editTextStartTime.setText(tripStartTime);

            final long tripEndTimeMilli = tripInfo.endTimeMillis;
            String tripEndDate = getDateFromMilliUtil(tripEndTimeMilli, "MM/dd/yyyy");
            editTextEndDate.setText(tripEndDate);
            String tripEndTime = getTimeFromMilliUtil(tripEndTimeMilli, "hh:mm a");
            editTextEndTime.setText(tripEndTime);

            imageButtonStartDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar c = new GregorianCalendar();
                    c.setTimeInMillis(tripStartTimeMilli);
                    int cYear = c.get(Calendar.YEAR);
                    int cMonth = c.get(Calendar.MONTH);
                    int cDay = c.get(Calendar.DAY_OF_MONTH);
                    System.out.println("the selected " + cDay);
                    DatePickerDialog datePickerDialog = new DatePickerDialog(AddEditTripActivity.this,
                            new DateSetListener(editTextStartDate), cYear, cMonth, cDay);
                    datePickerDialog.show();
                }
            });

            imageButtonStartTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar c = new GregorianCalendar();
                    c.setTimeInMillis(tripStartTimeMilli);
                    int hour = c.get(Calendar.HOUR_OF_DAY);
                    int minute = c.get(Calendar.MINUTE);
                    TimePickerDialog timePickerDialog = new TimePickerDialog(AddEditTripActivity.this,
                            new TimeSetListener(editTextStartTime), hour, minute, false);
                    timePickerDialog.show();
                }
            });

            imageButtonEndDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar c = new GregorianCalendar();
                    c.setTimeInMillis(tripEndTimeMilli);
                    int cYear = c.get(Calendar.YEAR);
                    int cMonth = c.get(Calendar.MONTH);
                    int cDay = c.get(Calendar.DAY_OF_MONTH);
                    System.out.println("the selected " + cDay);
                    DatePickerDialog datePickerDialog = new DatePickerDialog(AddEditTripActivity.this,
                            new DateSetListener(editTextEndDate), cYear, cMonth, cDay);
                    datePickerDialog.show();
                }
            });

            imageButtonEndTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar c = new GregorianCalendar();
                    c.setTimeInMillis(tripEndTimeMilli);
                    int hour = c.get(Calendar.HOUR_OF_DAY);
                    int minute = c.get(Calendar.MINUTE);
                    TimePickerDialog timePickerDialog = new TimePickerDialog(AddEditTripActivity.this,
                            new TimeSetListener(editTextEndTime), hour, minute, false);
                    timePickerDialog.show();
                }
            });


            placeName = tripInfo.placeName;
            placeAddress = tripInfo.placeAddress;
            textViewPlaceDisplay.setText("");
            textViewPlaceAddrDisplay.setText(placeAddress);
            textViewAddEditTripStartPlace.setText("Trip Destination: "+placeName);

            String themeStatus = tripInfo.getThemes();
            Log.d("themes recv ", themeStatus);
            String[] checkedThemes = themeStatus.split("_");
            for (int i = 0; i < checkedThemes.length; i++) {
                String x = checkedThemes[i];
                switch (x) {
                    case "0":
                        checkBox0.setChecked(true);
                        break;
                    case "1":
                        checkBox1.setChecked(true);
                        break;

                    case "2":
                        checkBox2.setChecked(true);
                        break;
                    case "3":
                        checkBox3.setChecked(true);
                        break;
                    case "4":
                        checkBox4.setChecked(true);
                        break;
                    case "5":
                        checkBox5.setChecked(true);
                        break;
                    case "6":
                        checkBox6.setChecked(true);
                        break;
                    case "7":
                        checkBox7.setChecked(true);
                        break;

                }
            }


        } else {

            getSupportActionBar().setTitle("Add a Trip");

            buttonAddOrEditPlace.setText("Add a Place");
            editTextStartTime.setText("09:30 AM");
            editTextEndTime.setText("09:30 PM");

            if (getIntent().hasExtra("purpose") && getIntent().getStringExtra("purpose").equals("bucketList")) {
                buttonAddOrEditPlace.setText("Change Place");
                placeName = getIntent().getStringExtra("placeName");
                placeAddress = getIntent().getStringExtra("placeAddress");
                textViewPlaceDisplay.setText("");
                textViewPlaceAddrDisplay.setText(placeAddress);
                editTextTripTitle.setText("Trip to "+placeName);
                textViewAddEditTripStartPlace.setText("Destination: "+placeName);
                imageViewLocImg.setVisibility(View.VISIBLE);

            }else{
                textViewAddEditTripStartPlace.setText("Where are you going? ");
                showAllinputFields(false);
            }

            imageButtonStartDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar c = Calendar.getInstance();
                    int cYear = c.get(Calendar.YEAR);
                    int cMonth = c.get(Calendar.MONTH);
                    int cDay = c.get(Calendar.DAY_OF_MONTH);
                    System.out.println("the selected " + cDay);
                    DatePickerDialog datePickerDialog = new DatePickerDialog(AddEditTripActivity.this,
                            new DateSetListener(editTextStartDate), cYear, cMonth, cDay);
                    datePickerDialog.show();
                }
            });

            imageButtonStartTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar c = Calendar.getInstance();
                    int hour = c.get(Calendar.HOUR_OF_DAY);
                    int minute = c.get(Calendar.MINUTE);
                    TimePickerDialog timePickerDialog = new TimePickerDialog(AddEditTripActivity.this,
                            new TimeSetListener(editTextStartTime), hour, minute, false);
                    timePickerDialog.show();
                }
            });


            imageButtonEndDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar c = Calendar.getInstance();
                    int cYear = c.get(Calendar.YEAR);
                    int cMonth = c.get(Calendar.MONTH);
                    int cDay = c.get(Calendar.DAY_OF_MONTH);
                    System.out.println("the selected " + cDay);
                    DatePickerDialog datePickerDialog = new DatePickerDialog(AddEditTripActivity.this,
                            new DateSetListener(editTextEndDate), cYear, cMonth, cDay);
                    datePickerDialog.show();
                }
            });

            imageButtonEndTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar c = Calendar.getInstance();
                    int hour = c.get(Calendar.HOUR_OF_DAY);
                    int minute = c.get(Calendar.MINUTE);
                    TimePickerDialog timePickerDialog = new TimePickerDialog(AddEditTripActivity.this,
                            new TimeSetListener(editTextEndTime), hour, minute, false);
                    timePickerDialog.show();
                }
            });
        }


        buttonAddOrEditPlace.setOnClickListener(this);

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title, notes;

                title = editTextTripTitle.getText().toString();

                EditText eNotes = (EditText) findViewById(R.id.editTextAddEditTripNotes);
                notes = eNotes.getText().toString();
                if(notes == null){
                    notes = "-";
                }

                Calendar stCalendar = new GregorianCalendar();
                Calendar endCalendar = new GregorianCalendar();
                Calendar currentCalendar = Calendar.getInstance();

                String sDate = editTextStartDate.getText().toString();
                String eDate = editTextEndDate.getText().toString();

                if(!completeDateAndTimeFieldsCheck(editTextStartDate, editTextEndDate, editTextStartTime,
                        editTextEndTime)){
                    return;
                }

                String[] startDate = sDate.trim().split("/");
                String[] endDate = eDate.trim().split("/");
                Log.d("entered st dt ", sDate);
                Log.d("startDate", startDate[0] + "/" + startDate[1] + "/" + startDate[2] + "/");

                String[] stTym = editTextStartTime.getText().toString().trim().split(" ");
                String[] startTime = stTym[0].split(":");
                int startHour = Integer.parseInt(startTime[0]);
                if (stTym[1].toUpperCase().equals("PM")) {
                    startHour = startHour + 12;
                }
                String[] endTym = editTextEndTime.getText().toString().trim().split(" ");
                String[] endTime = endTym[0].split(":");
                int endHour = Integer.parseInt(endTime[0]);
                if (endTym[1].toUpperCase().equals("PM")) {
                    endHour = endHour + 12;
                }
                Log.d("startTime", startTime[0] + ":" + startTime[1]);

                stCalendar.set(Integer.parseInt(startDate[2]),
                        (Integer.parseInt(startDate[0]) - 1),
                        Integer.parseInt(startDate[1]),
                        startHour,
                        Integer.parseInt(startTime[1]));

                endCalendar.set(Integer.parseInt(endDate[2]),
                        (Integer.parseInt(endDate[0]) - 1),
                        Integer.parseInt(endDate[1]),
                        endHour,
                        Integer.parseInt(endTime[1]));

                long stTimeMillis = stCalendar.getTimeInMillis();
                long endTimeMillis = endCalendar.getTimeInMillis();
                long currentTimeMillis = currentCalendar.getTimeInMillis();
                Log.d("stTimeMillis", stTimeMillis + "");
                Log.d("endTimeMillis", endTimeMillis + "");
                Log.d("currentTimeMillis", currentTimeMillis + "");

                if(!endAndStartTimeMilliCheckOK(stTimeMillis, endTimeMillis)){
                    return;
                }

                String themes = "_";
                if (checkBox0.isChecked()) {
                    themes = themes + "0_";
                }
                if (checkBox1.isChecked()) {
                    themes = themes + "1_";
                }
                if (checkBox2.isChecked()) {
                    themes = themes + "2_";
                }
                if (checkBox3.isChecked()) {
                    themes = themes + "3_";
                }
                if (checkBox4.isChecked()) {
                    themes = themes + "4_";
                }
                if (checkBox5.isChecked()) {
                    themes = themes + "5_";
                }
                if (checkBox6.isChecked()) {
                    themes = themes + "6_";
                }
                if (checkBox7.isChecked()) {
                    themes = themes + "7_";
                }

                TripInfo info = new TripInfo();

                info.title = title;
                info.placeName = placeName;
                info.placeAddress = placeAddress;
                info.stTimeMillis = stTimeMillis;
                info.endTimeMillis = endTimeMillis;
                info.themes = themes;
                info.notes = notes;

                if (purposeEdit == true) {
                    updateTripToDB(info, tripId);

                } else {
                    info.planningDone = 0;
                    tripId = addTripInfoToDB(info);
                    Log.d("addTrip", "addTripInfo id : " + tripId);
                    if (getIntent().hasExtra("purpose") && getIntent().getStringExtra("purpose").equals("bucketList")) {
                        Log.d("placeAddrTrip", placeAddress);
                        String bktListPlcAddr = getIntent().getStringExtra("placeAddress");
                        Log.d("placeAddrBktList", bktListPlcAddr);
                        if(placeAddress.equals(bktListPlcAddr)) {
                            int bucketId = getBucketListId(getIntent().getStringExtra("placeAddress"));
                            Log.d("bucketIdAET", +bucketId + "");
                            addTripIdToBucketListItem(bucketId, tripId);
                        }
                    }

                }
                AutoPlanningReminderHandler remTime = new AutoPlanningReminderHandler(tripId,
                        stTimeMillis, getApplicationContext(), 0);
                initiateAutoLocationBasedReminder(tripId, stTimeMillis, placeAddress);

                Intent intent = new Intent(AddEditTripActivity.this, ViewTripItineraryActivity.class);
                intent.putExtra("id", tripId);
                startActivity(intent);
                finish();
            }
        });


    }

    private boolean endAndStartTimeMilliCheckOK(long stTimeMillis, long endTimeMillis) {
        TripDbHelper tripDbHelper = new TripDbHelper(this);
        return tripDbHelper.endAndStartTimeInMilliCheck(stTimeMillis, endTimeMillis, getApplicationContext());

    }

    public boolean completeDateAndTimeFieldsCheck(EditText editTextStartDate, EditText editTextEndDate,
                                                  EditText editTextStartTime, EditText editTextEndTime){
        TripDbHelper tripDbHelper = new TripDbHelper(this);

        editTextStartDate.setBackgroundColor(Color.TRANSPARENT);
        editTextStartTime.setBackgroundColor(Color.TRANSPARENT);
        editTextEndDate.setBackgroundColor(Color.TRANSPARENT);
        editTextEndTime.setBackgroundColor(Color.TRANSPARENT);

        Boolean ipFieldErrStatus = true;

        if(!tripDbHelper.dateTimeCheck(editTextStartDate, "startDate", getApplicationContext())){
            ipFieldErrStatus =  false;
        }

        if(!tripDbHelper.dateTimeCheck(editTextEndDate, "endDate", getApplicationContext())){
            ipFieldErrStatus =  false;
        }

        if(!tripDbHelper.dateTimeCheck(editTextStartTime, "startTime", getApplicationContext())){
            ipFieldErrStatus =  false;
        }

        if(!tripDbHelper.dateTimeCheck(editTextEndTime, "endTime", getApplicationContext())){
            ipFieldErrStatus =  false;
        }
        return ipFieldErrStatus;
    }


    private void addTripIdToBucketListItem(int bucketId, int tripId) {
        TripDbHelper tripDbHelper = new TripDbHelper(this);
        BucketListInfo bucketInfo= tripDbHelper.getBucketListInfo(bucketId);
        bucketInfo.tripId = tripId;
        tripDbHelper.updateBucketItemInfoSanitized(bucketInfo, bucketId);
        Log.d("bktIdAETAfterUpdte", +bucketId+"");
        Log.d("bktplaceAddrAETAftUpdte", bucketInfo.placeAddress);
        Log.d("bktTripIdAETAftUpdte", bucketInfo.tripId+"");


    }

    private int getBucketListId(String placeAddress) {
        TripDbHelper tripDbHelper = new TripDbHelper(this);
        return tripDbHelper.getBucketListId(placeAddress);

    }

    private String getTimeFromMilliUtil(long tripStartTimeMilli, String s) {
        TripDbHelper tripDbHelper = new TripDbHelper(this);
        return tripDbHelper.getTimeFromMilli(tripStartTimeMilli, "hh:mm a");

    }

    private String getDateFromMilliUtil(long tripStartTimeMilli, String s) {
        TripDbHelper tripDbHelper = new TripDbHelper(this);
        return  tripDbHelper.getDateFromMilli(tripStartTimeMilli, "MM/dd/yyyy");

    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {

        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    private int addTripInfoToDB(TripInfo info) {
        TripDbHelper db = new TripDbHelper(this);
        return (int) db.addTripInfo(info);

    }

    private void updateTripToDB(TripInfo info, int tripId) {
        TripDbHelper db = new TripDbHelper(this);
        db.updateTripInfoSanitized(info, tripId);

    }

    public void initiateAutoLocationBasedReminder(int tripId, long stTimeMillis, String placeAddress) {

        long reminderTime = getLocReminderTimeInMilli(stTimeMillis);
        Log.d("AET locRemTime=", reminderTime + "");


        AlarmParams autoLocationReminderAlarmParams = new AlarmParams("autoLocationReminder", tripId, null,
                reminderTime, stTimeMillis, placeAddress, getApplicationContext(), 0);

        SetTripPlanningReminderActivity location = new SetTripPlanningReminderActivity();
        location.setAlarm(autoLocationReminderAlarmParams);

    }

    private long getLocReminderTimeInMilli(long stTimeMillis) {
        Calendar tripDateTime = Calendar.getInstance();
        tripDateTime.setTimeInMillis(stTimeMillis);
        Calendar locationRemTime = tripDateTime;
        locationRemTime.add(Calendar.HOUR_OF_DAY, -1);
        return locationRemTime.getTimeInMillis();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (menu != null) {
            menu.findItem(R.id.action_addtrip).setVisible(false);
        }
        return true;
    }

    public void toast(String msg) {
        Toast.makeText(getApplicationContext(), msg
                , Toast.LENGTH_SHORT).show();
    }

    private TripInfo getTripInfoForTripId(int tripId, Context context) {
        TripDbHelper tripDbHelper = new TripDbHelper(context);
        TripInfo tripInfo = tripDbHelper.getTripInfo(tripId);
        return tripInfo;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place user_place = PlaceAutocomplete.getPlace(this, data);
                Log.d("luxAET", "Place: " + user_place.getName());
                placeName = user_place.getName().toString();
                Log.d("luxAET", "PlaceAddr: "+user_place.getAddress());
                placeAddress = user_place.getAddress().toString();

                textViewPlaceDisplay.setText(placeName);
                textViewPlaceAddrDisplay.setText(placeAddress);
                editTextTripTitle.setText("Trip to "+placeName);
                textViewAddEditTripStartPlace.setText("Where are you going?");
                imageViewLocImg.setVisibility(View.VISIBLE);

                showAllinputFields(true);

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.d("luxAET", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private void showAllinputFields(boolean boo) {

        int b;
        if(boo == true){
            b = View.VISIBLE;
        }else{
            b = View.INVISIBLE;
        }
        checkBox0.setVisibility(b);
        checkBox1.setVisibility(b);
        checkBox2.setVisibility(b);
        checkBox3.setVisibility(b);
        checkBox4.setVisibility(b);
        checkBox5.setVisibility(b);
        checkBox6.setVisibility(b);
        checkBox7.setVisibility(b);

        editTextStartDate.setVisibility(b);
        editTextStartTime.setVisibility(b);
        editTextEndDate.setVisibility(b);
        editTextEndTime.setVisibility(b);
        imageButtonStartDate.setVisibility(b);
        imageButtonStartTime.setVisibility(b);
        imageButtonEndDate.setVisibility(b);
        imageButtonEndTime.setVisibility(b);
        editTextTripTitle.setVisibility(b);

        textViewAETTitleInfo.setVisibility(b);
        textViewAETTimeDetails.setVisibility(b);
        textViewAETStartTimings.setVisibility(b);
        textViewAETEndTimings.setVisibility(b);
        textViewAddEditTripThemeInfo.setVisibility(b);
        editTextAddEditTripNotes.setVisibility(b);

        doneButton.setVisibility(b);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(getIntent().hasExtra("purpose") && getIntent().getStringExtra("purpose").equals("edit")){
            Intent intent = new Intent(AddEditTripActivity.this, ViewTripItineraryActivity.class);
            intent.putExtra("id", tripId);
            startActivity(intent);
            finish();
        }else {
            Intent intent = new Intent(AddEditTripActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
