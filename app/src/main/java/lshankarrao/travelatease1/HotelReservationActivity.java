package lshankarrao.travelatease1;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import java.util.Calendar;
import java.util.GregorianCalendar;


public class HotelReservationActivity extends ActionBarActivity implements View.OnClickListener {

    TripDbHelper db;
    int tripId, resId;
    boolean purposeEdit = false;
    long oldStTimeMilli;

    EditText editTextStartDate, editTextStartTime, editTextEndDate,
            editTextEndTime;
    String placeName, placeAddress;
    EditText editTextConfNum, editTextNotes;
    TextView startPlaceAddress, textViewStartPlace;
    ImageButton imageButtonStartDate, imageButtonStartTime, imageButtonEndDate, imageButtonEndTime;
    TextView textViewPlaceDisplay, textViewPlaceAddrDisplay;
    Button buttonAddOrEditPlace;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    ImageView imageViewHotelLocImg;
    Button donebutton;

    TextView textViewHotelReservationTimeDetails, textViewHotelReservationStartTimings,
            textViewHotelReservationEndTimings, TextViewHotelconfNumber;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_reservations);


        Intent intent = getIntent();
        tripId = intent.getIntExtra("tripId", -1);
        if (tripId == -1) {
            Log.d("invalid Trip ID passed", "hotelActivity");
            return;
        }

        editTextStartDate = (EditText) (findViewById(R.id.editTextHotelStartDate));
        editTextStartTime = (EditText) (findViewById(R.id.editTextHotelReservationStartTime));
        editTextEndDate = (EditText) (findViewById(R.id.editTextHotelEndDate));
        editTextEndTime = (EditText) (findViewById(R.id.editTextHotelEndTime));


        imageButtonStartDate = (ImageButton) findViewById(R.id.imageButtonHotelStartDate);
        imageButtonStartTime = (ImageButton) findViewById(R.id.imageButtonHotelStartTime);
        imageButtonEndDate = (ImageButton) findViewById(R.id.imageButtonAddEditTripEndDate);
        imageButtonEndTime = (ImageButton) findViewById(R.id.imageButtonHotelEndTime);

        editTextConfNum = (EditText) findViewById(R.id.editTextHotelConfNumber);
        editTextNotes = (EditText) findViewById(R.id.editTextHotelNotes);

        textViewStartPlace = (TextView) findViewById(R.id.textViewHotelStartPlace);
        textViewHotelReservationTimeDetails = (TextView) findViewById(R.id.textViewHotelReservationTimeDetails);
        textViewHotelReservationStartTimings = (TextView) findViewById(R.id.textViewHotelReservationStartTimings);
        textViewHotelReservationEndTimings = (TextView) findViewById(R.id.textViewHotelReservationEndTimings);
        TextViewHotelconfNumber = (TextView) findViewById(R.id.TextViewHotelconfNumber);

        textViewPlaceDisplay = (TextView) findViewById(R.id.textViewHotelPlace);
        textViewPlaceAddrDisplay = (TextView) findViewById(R.id.textViewHotelStartPlaceAddress);
        buttonAddOrEditPlace = (Button) findViewById(R.id.buttonHotelAddOrEdit);
        imageViewHotelLocImg = (ImageView) findViewById(R.id.imageViewHotelLocImg);
        imageViewHotelLocImg.setVisibility(View.INVISIBLE);

        donebutton = (Button) findViewById(R.id.buttonHotelDone);


        db = new TripDbHelper(this);


        if (getIntent().hasExtra("purpose") && getIntent().getStringExtra("purpose").equals("edit")) {

            resId = getIntent().getIntExtra("resId", -1);
            if (resId == -1) {
                Log.d("res id invalid! ", "");
                return;
            }
            purposeEdit = true;
            getSupportActionBar().setTitle("Edit Stay");

            buttonAddOrEditPlace.setText("Change Stay");
            imageViewHotelLocImg.setVisibility(View.VISIBLE);

            ReservationsInfo resInfo = db.getReservation(resId);

            final long resStartTimeMilli = resInfo.stTimeMillis;
            oldStTimeMilli = resStartTimeMilli;
            String resStartDate = db.getDateFromMilli(resStartTimeMilli, "MM/dd/yyyy");
            editTextStartDate.setText(resStartDate);
            String resStartTime = db.getTimeFromMilli(resStartTimeMilli, "hh:mm a");
            editTextStartTime.setText(resStartTime);

            final long resEndTimeMilli = resInfo.endTimeMillis;
            String resEndDate = db.getDateFromMilli(resEndTimeMilli, "MM/dd/yyyy");
            editTextEndDate.setText(resEndDate);
            String resEndTime = db.getTimeFromMilli(resEndTimeMilli, "hh:mm a");
            editTextEndTime.setText(resEndTime);

            imageButtonStartDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar c = new GregorianCalendar();
                    c.setTimeInMillis(resStartTimeMilli);
                    int cYear = c.get(Calendar.YEAR);
                    int cMonth = c.get(Calendar.MONTH);
                    int cDay = c.get(Calendar.DAY_OF_MONTH);
                    System.out.println("the selected " + cDay);
                    DatePickerDialog datePickerDialog = new DatePickerDialog(HotelReservationActivity.this,
                            new DateSetListener(editTextStartDate), cYear, cMonth, cDay);
                    datePickerDialog.show();
                }
            });

            imageButtonStartTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar c = new GregorianCalendar();
                    c.setTimeInMillis(resStartTimeMilli);
                    int hour = c.get(Calendar.HOUR_OF_DAY);
                    int minute = c.get(Calendar.MINUTE);
                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            HotelReservationActivity.this, new TimeSetListener(editTextStartTime),
                            hour, minute, false);
                    timePickerDialog.show();
                }
            });

            imageButtonEndDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar c = new GregorianCalendar();
                    c.setTimeInMillis(resEndTimeMilli);
                    int cYear = c.get(Calendar.YEAR);
                    int cMonth = c.get(Calendar.MONTH);
                    int cDay = c.get(Calendar.DAY_OF_MONTH);
                    System.out.println("the selected " + cDay);
                    DatePickerDialog datePickerDialog = new DatePickerDialog(HotelReservationActivity.this, new DateSetListener(editTextEndDate), cYear, cMonth, cDay);
                    datePickerDialog.show();
                }
            });

            imageButtonEndTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar c = new GregorianCalendar();
                    c.setTimeInMillis(resEndTimeMilli);
                    int hour = c.get(Calendar.HOUR_OF_DAY);
                    int minute = c.get(Calendar.MINUTE);
                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            HotelReservationActivity.this, new TimeSetListener(editTextEndTime), hour, minute, false);
                    timePickerDialog.show();
                }
            });


            placeName = resInfo.startPlaceName;
            placeAddress = resInfo.startPlaceAddress;
            textViewPlaceDisplay.setText("");
            textViewPlaceAddrDisplay.setText(placeAddress);
            textViewStartPlace.setText("Place of Stay: " + placeName);

            editTextConfNum.setText(resInfo.confNo);
            editTextNotes.setText(resInfo.notes);


        } else {

            getSupportActionBar().setTitle("Add Stay");
            buttonAddOrEditPlace.setText("Add Place of Stay");
            showAllInputFields(false);

            TripInfo tripInfo = db.getTripInfo(tripId);

            final long tripStartTimeMilli = tripInfo.stTimeMillis;
            String tripStartDate = db.getDateFromMilli(tripStartTimeMilli, "MM/dd/yyyy");
            editTextStartDate.setText(tripStartDate);
            String tripStartTime = db.getTimeFromMilli(tripStartTimeMilli, "hh:mm a");
            editTextStartTime.setText(tripStartTime);

            final long tripEndTimeMilli = tripInfo.endTimeMillis;
            String tripEndDate = db.getDateFromMilli(tripEndTimeMilli, "MM/dd/yyyy");
            editTextEndDate.setText(tripEndDate);
            String tripEndTime = db.getTimeFromMilli(tripEndTimeMilli, "hh:mm a");
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
                    DatePickerDialog datePickerDialog = new DatePickerDialog(HotelReservationActivity.this,
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
                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            HotelReservationActivity.this, new TimeSetListener(editTextStartTime),
                            hour, minute, false);
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
                    DatePickerDialog datePickerDialog = new DatePickerDialog(HotelReservationActivity.this,
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
                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            HotelReservationActivity.this, new TimeSetListener(editTextEndTime), hour, minute, false);
                    timePickerDialog.show();
                }
            });
        }

        buttonAddOrEditPlace.setOnClickListener(this);

        donebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!completeDateAndTimeFieldsCheck(editTextStartDate, editTextEndDate, editTextStartTime,
                        editTextEndTime)){
                    return;
                }

                Calendar checkIn = db.getCalendarForDateAndTime(editTextStartDate.getText().toString(),
                        editTextStartTime.getText().toString());
                Calendar checkOut = db.getCalendarForDateAndTime(editTextEndDate.getText().toString(),
                        editTextEndTime.getText().toString());
                long checkInTimeMilli = checkIn.getTimeInMillis();
                long checkOutTimeMilli = checkOut.getTimeInMillis();

                if(!endAndStartTimeMilliCheckOK(checkInTimeMilli, checkOutTimeMilli)){
                    return;
                }


                String confNo = editTextConfNum.getText().toString();
                String notes = editTextNotes.getText().toString();


                ReservationsInfo info = new ReservationsInfo();

                info.kind = "hotel_start";
                info.stTimeMillis = checkInTimeMilli;
                info.startPlaceName = placeName;
                info.startPlaceAddress = placeAddress;
                info.endTimeMillis = checkOutTimeMilli;
                info.confNo = confNo;
                info.notes = notes;
                info.tripId = tripId;


                ReservationsInfo info2 = new ReservationsInfo();

                info2.kind = "hotel_end";
                info2.stTimeMillis = checkOutTimeMilli;
                info2.startPlaceName = placeName;
                info2.startPlaceAddress = placeAddress;
                info2.endTimeMillis = checkInTimeMilli;
                info2.confNo = confNo;
                info2.notes = notes;
                info2.tripId = tripId;

                db.logAllReservations(tripId);

                if (purposeEdit == true) {
                    db.updateReservationSanitized(info, resId);
                    int matchingResId = db.getMatchingReservation(oldStTimeMilli);
                    Log.d("matchingResId", matchingResId + "");
                    Log.d("resId", resId + "");
                    db.updateReservationSanitized(info2, matchingResId);
                } else {
                    db.addReservationsInfo(info);
                    db.addReservationsInfo(info2);
                }

                Intent intent = new Intent(HotelReservationActivity.this, ViewTripItineraryActivity.class);
                intent.putExtra("id", tripId);
                startActivity(intent);
                finish();
            }
        });

    }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place user_place = PlaceAutocomplete.getPlace(this, data);
                Log.d("luxAET", "Place: " + user_place.getName());
                placeName = user_place.getName().toString();
                Log.d("luxAET", "PlaceAddr: " + user_place.getAddress());
                placeAddress = user_place.getAddress().toString();

                textViewPlaceDisplay.setText(placeName);
                textViewPlaceAddrDisplay.setText(placeAddress);
                textViewStartPlace.setText("Hotel Details");
                imageViewHotelLocImg.setVisibility(View.VISIBLE);

                showAllInputFields(true);



            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.d("luxAET", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (menu != null) {

            menu.findItem(R.id.action_addtrip).setVisible(false);
        }
        return true;
    }

    public void showAllInputFields(boolean boo) {

        int b;
        if (boo == true) {
            b = View.VISIBLE;
        } else {
            b = View.INVISIBLE;
        }
        editTextStartDate.setVisibility(b);
        editTextStartTime.setVisibility(b);
        editTextEndDate.setVisibility(b);
        editTextEndTime.setVisibility(b);

        imageButtonStartDate.setVisibility(b);
        imageButtonStartTime.setVisibility(b);
        imageButtonEndDate.setVisibility(b);
        imageButtonEndTime.setVisibility(b);

        editTextConfNum.setVisibility(b);
        editTextNotes.setVisibility(b);

        textViewHotelReservationTimeDetails.setVisibility(b);
        textViewHotelReservationStartTimings.setVisibility(b);
        textViewHotelReservationEndTimings.setVisibility(b);
        TextViewHotelconfNumber.setVisibility(b);

        donebutton.setVisibility(b);

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

    private boolean endAndStartTimeMilliCheckOK(long stTimeMillis, long endTimeMillis) {
        TripDbHelper tripDbHelper = new TripDbHelper(this);
        return tripDbHelper.endAndStartTimeInMilliCheck(stTimeMillis, endTimeMillis, getApplicationContext());

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(HotelReservationActivity.this, ViewTripItineraryActivity.class);
        intent.putExtra("id", tripId);
        startActivity(intent);
        finish();
    }
}
