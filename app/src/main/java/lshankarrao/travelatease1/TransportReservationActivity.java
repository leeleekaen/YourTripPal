package lshankarrao.travelatease1;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;

import java.util.Calendar;
import java.util.GregorianCalendar;


public class TransportReservationActivity extends ActionBarActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    TripDbHelper db;
    int tripId, resId;
    boolean purposeEdit = false;
    long oldStTimeMilli;
    String startPlaceName, startPlaceAddress, endPlaceName, endPlaceAddress;
    TextView textViewStartPlaceAddress, textViewEndPlaceAddress, textViewStartPlace, textViewEndPlace;
    String resStartPlaceAddress, resEndPlaceAddress;
    String placeName, placeAddress;
    EditText editTextStartDate, editTextStartTime, editTextEndDate,
            editTextEndTime;
    Button doneButton;

    EditText editTextTitleOrCompanyName, editTextConfNum, editTextNotes;
    String subKind;

    TextView textViewTransportDeparturePlace, textViewTransportStartTimings,
            textViewTransportEndTimings, textViewTransportArrivalPlace, TextViewTransportCompanyName,
            TextViewTransportReservationconfNumber, textViewTransportReservationTimeDetails;

    Spinner spinner;
    PlaceAutocompleteFragment startPlaceAutocompleteFragment, endPlaceAutocompleteFragment;
    ImageButton imageButtonStartDate, imageButtonStartTime, imageButtonEndDate, imageButtonEndTime;
    ReservationsInfo resInfo;

    TextView textViewStartPlaceDisplay, textViewStartPlaceAddrDisplay;
    Button buttonAddOrEditStartPlace;
    int START_PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    ImageView imageViewStartLocImg;

    TextView textViewEndPlaceDisplay, textViewEndPlaceAddrDisplay;
    Button buttonAddOrEditEndPlace;
    int END_PLACE_AUTOCOMPLETE_REQUEST_CODE = 2;
    ImageView imageViewEndLocImg;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport_reservations);

        Intent intent = getIntent();
        tripId = intent.getIntExtra("tripId", -1);
        if (tripId == -1) {
            Log.d("invalid Trip ID passed", "TransportActivity");
            return;
        }

        db = new TripDbHelper(this);

        textViewTransportDeparturePlace = (TextView) findViewById(R.id.textViewTransportStartPlaceInfo);
        textViewTransportStartTimings = (TextView) findViewById(R.id.textViewTransportReservationStartTimings);
        textViewTransportEndTimings = (TextView) findViewById(R.id.textViewTransportReservationEndTimings);
        textViewTransportArrivalPlace = (TextView) findViewById(R.id.textViewTransportArrivalPlaceText);
        TextViewTransportCompanyName = (TextView) findViewById(R.id.TextViewTransportCompanyName);
        TextViewTransportReservationconfNumber = (TextView) findViewById(R.id.TextViewTransportReservationconfNumber);
        editTextTitleOrCompanyName = (EditText) findViewById(R.id.editTextTransportCompanyName);
        editTextConfNum = (EditText) findViewById(R.id.editTextTransportConfNumber);
        editTextNotes = (EditText) findViewById(R.id.editTextTransportNotes);

        textViewStartPlaceAddress = (TextView) findViewById(R.id.textViewTransportReservationStartPlaceAddress);

        textViewEndPlaceAddress = (TextView) findViewById(R.id.textViewTransportReservationEndPlaceAddress);

        editTextStartDate = (EditText) (findViewById(R.id.editTextTransportReservationStartDate));
        editTextStartTime = (EditText) (findViewById(R.id.editTextTransportStartTime));
        editTextEndDate = (EditText) (findViewById(R.id.editTextTransportReservationEndDate));
        editTextEndTime = (EditText) (findViewById(R.id.editTextTransportArrivalTime));

        imageButtonStartDate = (ImageButton) findViewById(R.id.imageButtonTransportStartDate);
        imageButtonStartTime = (ImageButton) findViewById(R.id.imageButtonTransportStartTime);
        imageButtonEndDate = (ImageButton) findViewById(R.id.imageButtonTransportEndDate);
        imageButtonEndTime = (ImageButton) findViewById(R.id.imageButtonTransportEndTime);
        textViewStartPlace = (TextView) findViewById(R.id.textViewTransportStartPlaceInfo);
        textViewEndPlace = (TextView) findViewById(R.id.textViewTransportArrivalPlaceText);
        textViewTransportReservationTimeDetails = (TextView)findViewById(R.id.textViewTransportReservationTimeDetails);


        textViewStartPlaceDisplay = (TextView) findViewById(R.id.textViewTransportStartPlaceDisplay);
        textViewStartPlaceAddrDisplay = (TextView) findViewById(R.id.textViewTransportReservationStartPlaceAddress);
        buttonAddOrEditStartPlace = (Button) findViewById(R.id.buttonTransportAddStartPlace);
        imageViewStartLocImg = (ImageView) findViewById(R.id.imageViewTransportStartLocImg);
        imageViewStartLocImg.setVisibility(View.INVISIBLE);

        textViewEndPlaceDisplay = (TextView) findViewById(R.id.textViewTransportEndPlaceDisplay);
        textViewEndPlaceAddrDisplay = (TextView) findViewById(R.id.textViewTransportReservationEndPlaceAddress);
        buttonAddOrEditEndPlace = (Button) findViewById(R.id.buttonTransportAddEndPlace);
        imageViewEndLocImg = (ImageView) findViewById(R.id.imageViewTransportEndLocImg);
        imageViewEndLocImg.setVisibility(View.INVISIBLE);

        doneButton = (Button) findViewById(R.id.buttonTransportDone);

        db.logAllReservations(tripId);

        TripInfo tripInfo = db.getTripInfo(tripId);

        spinner = (Spinner) findViewById(R.id.spinner1TransportSubKind);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.transportSubKinds_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);


        if (getIntent().hasExtra("purpose") && getIntent().getStringExtra("purpose").equals("edit")) {

            resId = getIntent().getIntExtra("resId", -1);
            if (resId == -1) {
                Log.d("res id invalid! ", "");
                return;
            }
            purposeEdit = true;
            getSupportActionBar().setTitle("Edit Transport");
            buttonAddOrEditEndPlace.setText("Edit Place");
            buttonAddOrEditStartPlace.setText("Edit Place");
            imageViewStartLocImg.setVisibility(View.VISIBLE);


            resInfo = db.getReservation(resId);

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
                    DatePickerDialog datePickerDialog = new DatePickerDialog(TransportReservationActivity.this, new DateSetListener(editTextStartDate), cYear, cMonth, cDay);
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
                    TimePickerDialog timePickerDialog = new TimePickerDialog(TransportReservationActivity.this,
                            new TimeSetListener(editTextStartTime), hour, minute, false);
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
                    DatePickerDialog datePickerDialog = new DatePickerDialog(TransportReservationActivity.this,
                            new DateSetListener(editTextEndDate), cYear, cMonth, cDay);
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
                    TimePickerDialog timePickerDialog = new TimePickerDialog(TransportReservationActivity.this,
                            new TimeSetListener(editTextEndTime), hour, minute, false);
                    timePickerDialog.show();
                }
            });

            startPlaceName = resInfo.startPlaceName;
            startPlaceAddress = resInfo.startPlaceAddress;
            textViewStartPlaceDisplay.setText("");
            textViewStartPlaceAddrDisplay.setText(startPlaceAddress);
            textViewStartPlace.setText("Start Place: " + startPlaceName);

            if (resInfo.endPlaceName != null) {
                imageViewEndLocImg.setVisibility(View.VISIBLE);
                endPlaceName = resInfo.endPlaceName;
                endPlaceAddress = resInfo.endPlaceAddress;
                textViewEndPlaceDisplay.setText("");
                textViewEndPlaceAddrDisplay.setText(endPlaceAddress);
                textViewEndPlace.setText("End Place: " + endPlaceName);
            }


            editTextTitleOrCompanyName.setText(resInfo.titleOrName);
            editTextConfNum.setText(resInfo.confNo);
            editTextNotes.setText(resInfo.notes);

        } else {


            getSupportActionBar().setTitle("Add Transport");
            buttonAddOrEditEndPlace.setText("Add Place");
            buttonAddOrEditStartPlace.setText("Add Place");
            showAllinputFields(false);


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
                    DatePickerDialog datePickerDialog = new DatePickerDialog(TransportReservationActivity.this, new DateSetListener(editTextStartDate), cYear, cMonth, cDay);
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
                    TimePickerDialog timePickerDialog = new TimePickerDialog(TransportReservationActivity.this,
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
                    DatePickerDialog datePickerDialog = new DatePickerDialog(TransportReservationActivity.this, new DateSetListener(editTextEndDate), cYear, cMonth, cDay);
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
                    TimePickerDialog timePickerDialog = new TimePickerDialog(TransportReservationActivity.this,
                            new TimeSetListener(editTextEndTime), hour, minute, false);
                    timePickerDialog.show();
                }
            });

        }

        buttonAddOrEditEndPlace.setOnClickListener(this);
        buttonAddOrEditStartPlace.setOnClickListener(this);

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(startPlaceAddress == null ||startPlaceAddress.isEmpty() ){
                    toast("Please enter the Start Address");
                    return;
                }
                if(endPlaceAddress == null || endPlaceAddress.isEmpty() ){
                    toast("Please enter the End Address");
                    return;
                }

                if(!completeDateAndTimeFieldsCheck(editTextStartDate, editTextEndDate, editTextStartTime,
                        editTextEndTime)){
                    return;
                }
                Calendar startPlaceCalendar = db.getCalendarForDateAndTime(editTextStartDate.getText().toString(),
                        editTextStartTime.getText().toString());
                Calendar endPlaceCalendar = db.getCalendarForDateAndTime(editTextEndDate.getText().toString(),
                        editTextEndTime.getText().toString());
                long startTimeMilli = startPlaceCalendar.getTimeInMillis();
                long endTimeMilli = endPlaceCalendar.getTimeInMillis();
                if(!endAndStartTimeMilliCheckOK(startTimeMilli, endTimeMilli)){
                    return;
                }

                String companyName = editTextTitleOrCompanyName.getText().toString();
                String confNo = editTextConfNum.getText().toString();
                String notes = editTextNotes.getText().toString();


                ReservationsInfo info = new ReservationsInfo();

                info.kind = subKind.toLowerCase() + "_start";
                info.titleOrName = companyName;
                info.stTimeMillis = startTimeMilli;
                info.startPlaceName = startPlaceName;
                info.startPlaceAddress = startPlaceAddress;
                info.endTimeMillis = endTimeMilli;
                info.endPlaceName = endPlaceName;
                info.endPlaceAddress = endPlaceAddress;
                info.confNo = confNo;
                info.notes = notes;
                info.tripId = tripId;

                int matchingResId = db.getMatchingReservation(oldStTimeMilli);
                if (endPlaceName != null) {

                    ReservationsInfo info2 = new ReservationsInfo();

                    info2.kind = subKind.toLowerCase() + "_end";
                    info2.titleOrName = companyName;
                    info2.stTimeMillis = endTimeMilli;
                    info2.startPlaceName = endPlaceName;
                    info2.startPlaceAddress = endPlaceAddress;
                    info2.endTimeMillis = startTimeMilli;
                    info2.endPlaceName = startPlaceName;
                    info2.endPlaceAddress = startPlaceAddress;
                    info2.confNo = confNo;
                    info2.notes = notes;
                    info2.tripId = tripId;

                    if (purposeEdit == true && matchingResId >= 0) {
                        Log.d("matchingResId", matchingResId + "");
                        db.updateReservationSanitized(info2, matchingResId);
                    } else {
                        db.addReservationsInfo(info2);
                    }

                } else {
                    if (matchingResId >= 0) {
                        db.deleteEntry("reservationInfo", matchingResId);
                    }
                }

                if (purposeEdit == true) {
                    db.updateReservationSanitized(info, resId);
                    Log.d("resId", resId + "");
                } else {
                    db.addReservationsInfo(info);
                }

                Intent intent = new Intent(TransportReservationActivity.this, ViewTripItineraryActivity.class);
                intent.putExtra("id", tripId);
                startActivity(intent);
                finish();
            }
        });
    }

    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.buttonTransportAddStartPlace:
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                    .build(this);
                    startActivityForResult(intent, START_PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }

                break;
            case R.id.buttonTransportAddEndPlace:

                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                    .build(this);
                    startActivityForResult(intent, END_PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
                break;
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


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        subKind = parent.getItemAtPosition(position).toString();
        if(!subKind.equals("None Selected")){
            showAllinputFields(true);
        }
        if (purposeEdit == true) {
            String kind = resInfo.kind.split("_")[0];
            Log.d("kind here", kind);
            String[] spinner_choices = getResources().getStringArray(R.array.transportSubKinds_array);
            int selectedPos = -2;

            for (int i = 0; i < spinner_choices.length; i++) {
                Log.d("choice " + i + "=", spinner_choices[i]);
                String choice = spinner_choices[i].toLowerCase();
                if (choice.equals(kind)) {
                    selectedPos = i;
                    spinner.setSelection(i);
                }
            }
            Log.d("pos spinner tran", selectedPos + "");

            textViewEndPlace.setText(resEndPlaceAddress);
            textViewStartPlace.setText(resStartPlaceAddress);
        } else {
            switch (subKind) {
                case "Car":
                    changeLayoutWordings("Car");
                    break;
                case "Flight":
                    changeLayoutWordings("Flight");
                    break;
                case "Bus":
                    changeLayoutWordings("Bus");
                    break;
                case "Train":
                    changeLayoutWordings("Train");
                    break;
                default: return;
            }
        }

        Log.d("Selected: " , subKind+"");

    }

    private void changeLayoutWordings(String transpKind) {
        switch (transpKind) {
            case "Car":
                textViewTransportDeparturePlace.setText("Pick-up Point");
                textViewTransportStartTimings.setText("Pick-Up Time");
                textViewTransportEndTimings.setText("Approximate Drop Time");
                textViewTransportArrivalPlace.setText("Drop Point");
                TextViewTransportCompanyName.setText("Rental Car Company");
                break;
            case "Flight":
                textViewTransportDeparturePlace.setText("Departure Airport");
                textViewTransportStartTimings.setText("Departure Time");
                textViewTransportEndTimings.setText("Arrival Time");
                textViewTransportArrivalPlace.setText("Arrival Airport");
                TextViewTransportCompanyName.setText("Airlines");
                TextViewTransportReservationconfNumber.setText("Flight #");
                break;
            case "Bus":
                textViewTransportDeparturePlace.setText("Departure Station");
                textViewTransportStartTimings.setText("Departure Time");
                textViewTransportEndTimings.setText("Arrival Time");
                textViewTransportArrivalPlace.setText("Arrival Station");
                TextViewTransportCompanyName.setText("Transport Agency");
                break;
            case "Train":
                textViewTransportDeparturePlace.setText("Departure Station");
                textViewTransportStartTimings.setText("Departure Time");
                textViewTransportEndTimings.setText("Arrival Time");
                textViewTransportArrivalPlace.setText("Arrival Station");
                TextViewTransportCompanyName.setText("Transport Agency");
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == START_PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place user_place = PlaceAutocomplete.getPlace(this, data);
                Log.d("luxTrans", "Place: " + user_place.getName());
                startPlaceName = user_place.getName().toString();
                Log.d("luxTrans", "PlaceAddr: " + user_place.getAddress());
                startPlaceAddress = user_place.getAddress().toString();

                textViewStartPlaceDisplay.setText(startPlaceName);
                textViewStartPlaceAddrDisplay.setText(startPlaceAddress);
                textViewStartPlace.setText("Start Place");
                imageViewStartLocImg.setVisibility(View.VISIBLE);

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.d("luxTrans", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        } else if (requestCode == END_PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place user_place = PlaceAutocomplete.getPlace(this, data);
                Log.d("luxTrans", "Place: " + user_place.getName());
                endPlaceName = user_place.getName().toString();
                Log.d("luxTrans", "PlaceAddr: " + user_place.getAddress());
                endPlaceAddress = user_place.getAddress().toString();

                textViewEndPlaceDisplay.setText(endPlaceName);
                textViewEndPlaceAddrDisplay.setText(endPlaceAddress);
                textViewEndPlace.setText("End Place");
                imageViewEndLocImg.setVisibility(View.VISIBLE);

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.d("luxTrans", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    public void showAllinputFields(boolean boo) {

        int b;
        if (boo == true) {
            b = View.VISIBLE;
        } else {
            b = View.INVISIBLE;
        }
        textViewTransportReservationTimeDetails.setVisibility(b);
        textViewTransportDeparturePlace.setVisibility(b);
        textViewTransportStartTimings.setVisibility(b);
        textViewTransportEndTimings.setVisibility(b);
        textViewTransportArrivalPlace.setVisibility(b);
        TextViewTransportCompanyName.setVisibility(b);
        TextViewTransportReservationconfNumber.setVisibility(b);
        editTextTitleOrCompanyName.setVisibility(b);
        editTextConfNum.setVisibility(b);
        editTextNotes.setVisibility(b);

        textViewStartPlaceAddress.setVisibility(b);
        textViewEndPlaceAddress.setVisibility(b);

        editTextStartDate.setVisibility(b);
        editTextStartTime.setVisibility(b);
        editTextEndDate.setVisibility(b);
        editTextEndTime.setVisibility(b);

        imageButtonStartDate.setVisibility(b);
        imageButtonStartTime.setVisibility(b);
        imageButtonEndDate.setVisibility(b);
        imageButtonEndTime.setVisibility(b);

        textViewStartPlace.setVisibility(b);
        textViewEndPlace.setVisibility(b);

        buttonAddOrEditStartPlace.setVisibility(b);
        buttonAddOrEditEndPlace.setVisibility(b);

        doneButton.setVisibility(b);
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

    public void toast(String msg) {
        Toast.makeText(getApplicationContext(), msg
                , Toast.LENGTH_SHORT).show();
    }

    private boolean endAndStartTimeMilliCheckOK(long stTimeMillis, long endTimeMillis) {
        TripDbHelper tripDbHelper = new TripDbHelper(this);
        return tripDbHelper.endAndStartTimeInMilliCheck(stTimeMillis, endTimeMillis, getApplicationContext());

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(TransportReservationActivity.this, ViewTripItineraryActivity.class);
        intent.putExtra("id", tripId);
        startActivity(intent);
        finish();
    }
}
