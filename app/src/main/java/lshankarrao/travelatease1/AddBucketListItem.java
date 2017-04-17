package lshankarrao.travelatease1;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import java.util.Calendar;


public class AddBucketListItem extends ActionBarActivity implements View.OnClickListener {

    String placeName, placeAddress;
    TextView textViewPlaceAddress;
    CheckBox checkBoxBL;
    Spinner spinnerStartMonth, spinnerEndMonth, spinnerBLTheme;
    Button buttonBLDone;
    Boolean allYearLong = false;
    String startMonth, endMonth, theme;
    TripDbHelper tripDbHelper;

    Button buttonAddOrEditPlace;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    TextView textViewPlaceDisplay, textViewPlaceAddrDisplay;
    TextView textViewBLInfo;
    ImageView imageViewLocImg;

    TextView textViewBLInfoAddress, textViewBLto, textViewBLChooseTheme;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bucket_list_item);
        getSupportActionBar().setTitle("Add Place to Bucket List");

        textViewPlaceAddress = (TextView) findViewById(R.id.textViewBLPlaceAddress);
        checkBoxBL = (CheckBox) findViewById(R.id.checkBoxBLAllYearLong);
        spinnerStartMonth = (Spinner) findViewById(R.id.spinnerBLStartMonth);
        spinnerEndMonth = (Spinner) findViewById(R.id.spinnerBLEndMonth);
        spinnerBLTheme = (Spinner) findViewById(R.id.spinnerBLChooseTheme);
        buttonBLDone = (Button) findViewById(R.id.buttonBLDone);

        textViewPlaceDisplay = (TextView) findViewById(R.id.textViewBLPlace);
        textViewPlaceAddrDisplay = (TextView) findViewById(R.id.textViewBLPlaceAddress);
        buttonAddOrEditPlace = (Button) findViewById(R.id.buttonBLAddPlace);
        imageViewLocImg = (ImageView) findViewById(R.id.imageViewBLLocImg);
        imageViewLocImg.setVisibility(View.INVISIBLE);
        textViewBLInfo = (TextView) findViewById(R.id.textViewBLInfo);

        textViewBLInfoAddress = (TextView) findViewById(R.id.textViewBLInfoAddress);
        textViewBLto = (TextView) findViewById(R.id.textViewBLto);
        textViewBLChooseTheme = (TextView) findViewById(R.id.textViewBLChooseTheme);

        tripDbHelper = new TripDbHelper(this);

        checkBoxBL.setChecked(false);
        checkBoxBL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBoxBL.isChecked() == true) {
                    spinnerStartMonth.setEnabled(false);
                    spinnerEndMonth.setEnabled(false);
                    allYearLong = true;
                } else {
                    spinnerStartMonth.setEnabled(true);
                    spinnerEndMonth.setEnabled(true);
                    allYearLong = false;
                }
            }
        });

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.months_array, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStartMonth.setAdapter(adapter1);

        spinnerStartMonth.setOnItemSelectedListener(new SpinnerStartMonth());

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.months_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerEndMonth.setAdapter(adapter2);

        spinnerEndMonth.setOnItemSelectedListener(new SpinnerEndMonth());

        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this,
                R.array.bucketListThemes_array, android.R.layout.simple_spinner_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerBLTheme.setAdapter(adapter3);

        spinnerBLTheme.setOnItemSelectedListener(new SpinnerBLTheme());

        buttonAddOrEditPlace.setOnClickListener(this);

        showAllInputFields(false);

        buttonBLDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (allYearLong == true) {
                    startMonth = "allYearLong";
                    endMonth = "allYearLong";
                }

                if(startMonth.equals("Select Month") || endMonth.equals("Select Month")){
                    toast("Please choose the \"Best time to visit\"");
                    return;
                }
                if(theme.equals("Select Theme") ){
                    toast("Please choose a \"Theme\"");
                    return;
                }

                BucketListInfo bucketListInfo = new BucketListInfo(theme, placeName, placeAddress,
                        startMonth, endMonth, -1, 0);
                int id = (int) tripDbHelper.addBucketListInfo(bucketListInfo);


                Intent intent = new Intent(AddBucketListItem.this, BucketListDisplayActivity.class);
                if (id != -1) {
                    Log.d("addBuckListItem", "success");
                    intent.putExtra("itemAdded", true);

                }
                startActivity(intent);
                finish();
            }
        });
    }

    private long getStartMonthTime(String startMonth) {
        int one = 1;
        Calendar current = Calendar.getInstance();
        Calendar rem = Calendar.getInstance();
        rem.setTimeInMillis(current.getTimeInMillis());
        int currYear = current.get(Calendar.YEAR);
        int currMon = current.get(Calendar.MONTH);
        int currDay = current.get(Calendar.DAY_OF_MONTH);
        int stMon = getMonthIndex(startMonth);
        if (stMon != -1) {
            rem.set(Calendar.MONTH, stMon);
            rem.set(Calendar.DAY_OF_MONTH, one);
            if (stMon > currMon) {
                rem.set(Calendar.YEAR, currYear);
            } else {
                rem.set(Calendar.YEAR, currYear + 1);

            }
            return rem.getTimeInMillis();
        }
        return -1;
    }

    public int getMonthIndex(String month) {
        String[] months = getResources().getStringArray(R.array.months_array);
        int i = 0;
        for (String s : months) {
            if (month == s) {
                break;
            }
            i++;
        }
        if (i > 11) {
            Log.d("monthInterpretation", "Err");
            return -1;
        }
        return i;
    }

    @Override
    public void onClick(View view) {

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


    class SpinnerStartMonth implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
            String[] startMonthArray = getResources().getStringArray(R.array.months_array);
            Log.d("Your choice :", startMonthArray[position]);
            startMonth = parent.getItemAtPosition(position).toString();
            Log.d("start mon",startMonth);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    class SpinnerEndMonth implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
            String[] endMonthArray = getResources().getStringArray(R.array.months_array);
            Log.d("Your choice :" , endMonthArray[position]);
            endMonth = parent.getItemAtPosition(position).toString();
            Log.d("end Month",endMonth);

        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    public void toast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    class SpinnerBLTheme implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            String[] bucketListThemes = getResources().getStringArray(R.array.bucketListThemes_array);
            Log.d("Your choice :" , bucketListThemes[i]);
            theme = adapterView.getItemAtPosition(i).toString();
            Log.d("theme",theme);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place user_place = PlaceAutocomplete.getPlace(this, data);
                Log.d("luxBL", "Place: " + user_place.getName());
                placeName = user_place.getName().toString();
                Log.d("luxBL", "PlaceAddr: " + user_place.getAddress());
                placeAddress = user_place.getAddress().toString();

                textViewPlaceDisplay.setText(placeName);
                textViewPlaceAddrDisplay.setText(placeAddress);
                imageViewLocImg.setVisibility(View.VISIBLE);

                showAllInputFields(true);

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.d("luxBL", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
            }
        }
    }

    public void showAllInputFields(boolean boo) {

        int b;
        if (boo == true) {
            b = View.VISIBLE;
        } else {
            b = View.INVISIBLE;
        }

        checkBoxBL.setVisibility(b);
        spinnerStartMonth.setVisibility(b);
        spinnerEndMonth.setVisibility(b);
        spinnerBLTheme.setVisibility(b);
        textViewBLInfoAddress.setVisibility(b);
        textViewBLto.setVisibility(b);
        textViewBLChooseTheme.setVisibility(b);
        buttonBLDone.setVisibility(b);
    }
}
