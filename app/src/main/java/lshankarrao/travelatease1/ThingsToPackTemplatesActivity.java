package lshankarrao.travelatease1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class ThingsToPackTemplatesActivity extends ActionBarActivity2 implements AdapterView.OnItemSelectedListener {
    String[] templates = {"Accessories", "Child Care", "Clothes", "Documents",
            "Electronics", "Makeup Kit", "Medical Kit", "Personal Care", "Outdoor Activities", "Water Activities",
            "Snow Activities", "Camping", "Party", "Business/Educational", "Indoor Activities"};
    List<String> template_things_to_pack;
    Spinner spinner;
    TripDbHelper tripDbHelper;
    Button buttonGoOrAdd;
    List<TripInfo> tripInfos;
    Boolean atleast1tripExists = false;
    int selectedTripId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_templates_things_to_pack);
        getSupportActionBar().setTitle("Things to Pack Templates");
        ListView listviewTTPtemplates = (ListView) findViewById(R.id.listView_packing_templates);
        buttonGoOrAdd = (Button) findViewById(R.id.buttonTTPTemplateGoORAdd);

        template_things_to_pack = new ArrayList<String>();
        for(int i = 0; i < templates.length; i++){
            template_things_to_pack.add(templates[i]);
        }

        listviewTTPtemplates.setAdapter(new template_ttp_array_adapter(this, R.layout.templates_ttp_custom_row, template_things_to_pack));

        listviewTTPtemplates.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ThingsToPackTemplatesActivity.this, StdThingsToPackActivity.class);
                String template = template_things_to_pack.get(i);

                intent.putExtra("category", template);
                intent.putExtra("purpose","viewTemplates");
                startActivity(intent);
            }
        });

        spinner = (Spinner) findViewById(R.id.spinner1TTPTemplateTripList);
        tripDbHelper = new TripDbHelper(this);
        tripInfos = tripDbHelper.fetchAllTripsPlaceName();
        if(tripInfos == null || tripInfos.size() == 0){
            String[] trips = new String[1];
            trips[0] = "No Trips Added";
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, trips);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(dataAdapter);
            buttonGoOrAdd.setText("Add a Trip");
        }else {
            atleast1tripExists = true;
            String[] trips = new String[tripInfos.size()];
            int i = 0;
            for (TripInfo tripPlaceName : tripInfos) {
                trips[i] = tripPlaceName.placeName;
                i++;
            }
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, trips);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(dataAdapter);
            buttonGoOrAdd.setText("Go");
        }
        buttonGoOrAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(atleast1tripExists == true){
                    if(selectedTripId > -1){
                        long tripStTimeMilli = tripInfos.get(selectedTripId).getStTimeMillis();
                        int tripId =tripDbHelper.getTripIdForStTimeMilli(tripStTimeMilli);
                        if(tripId == -1){
                            Log.d("TTPTemplate","tripID -1");
                            return;
                        }
                        Intent intent = new Intent(ThingsToPackTemplatesActivity.this, ThingsToPackActivity.class);
                        intent.putExtra("tripId", tripId);
                        startActivity(intent);
                        finish();
                    }
                }else{
                    Intent intent = new Intent(ThingsToPackTemplatesActivity.this, AddEditTripActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });



        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        selectedTripId = i;


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ThingsToPackTemplatesActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
