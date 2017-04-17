package lshankarrao.travelatease1;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class StdThingsToPackActivity extends ActionBarActivity2 implements View.OnClickListener {
    TripDbHelper tripDbHelper;
    List<String> packingItems = new ArrayList<>();
    StdPackRVAdapter stdPackingItemsRVAdapter;
    RecyclerView rv;
    TextView textViewCategory;
    ImageView imageViewAddItems;
    List<StdPackingItemsInfo> stdPackingItemsInfos;
    int tripId;
    CheckBox checkBoxStdTTPselectAll;
    Boolean[] itemsSelectionStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_std_packing_list);
        getSupportActionBar().setTitle("Packing Templates");

        Intent intent = getIntent();
        String category = intent.getStringExtra("category");
        if(category == null){
            Log.d("category null", "std TTP");
            return;
        }
        if(intent.hasExtra("tripId")) {
            tripId = intent.getIntExtra("tripId", -1);
            if (tripId == -1) {
                Log.d("invalid trip id", "StdTTP activity");
                return;
            }
            Log.d("StdTTP trip id", tripId + "");
        }

        tripDbHelper = new TripDbHelper(this);
        Log.d("categoryStdTTP", category + "");

        Cursor c = tripDbHelper.getCursorForPackingItemsCategory(category);
        if(c == null || c.getCount() == 0){
            Log.d("cursor ", "null" + "");

        }
        stdPackingItemsInfos = tripDbHelper.getPackingItemsForCursor(c);

        if(stdPackingItemsInfos == null || stdPackingItemsInfos.size() == 0){
            Log.d("stdPackingItemsInfos ", "null" + "");

        }
        for(StdPackingItemsInfo p: stdPackingItemsInfos){
            packingItems.add(p.getItem());
            Log.d("stdTTP chucking status", p.getChecked()+"");
        }

        checkBoxStdTTPselectAll = (CheckBox)findViewById(R.id.checkBoxStdTTPselectAll);

        if(intent.hasExtra("selectionStatus")) {
            String selectionStatus = intent.getStringExtra("selectionStatus");
            Log.d("selectionStatus", selectionStatus+" nodappa");
            if (selectionStatus.equals("deselect")) {
                Log.d("insideSel","deselect" );
                makeAllEntries(false);
                checkBoxStdTTPselectAll.setText("Select all");
            }else if(selectionStatus.equals("select")){
                Log.d("insideSel","select" );
                makeAllEntries(true);
                checkBoxStdTTPselectAll.setText("Deselect all");

            }
        }

        stdPackingItemsRVAdapter = new StdPackRVAdapter(stdPackingItemsInfos,this,
                itemsSelectionStatus, checkBoxStdTTPselectAll);

        rv =(RecyclerView)findViewById(R.id.recyclerViewStdTTP);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);
        rv.setAdapter(stdPackingItemsRVAdapter);

        textViewCategory = (TextView) findViewById(R.id.textViewStdTTPCategory);
        textViewCategory.setText(category.toUpperCase());


        imageViewAddItems = (ImageView)findViewById(R.id.imageViewStdTTPAddItems);
        if(intent.hasExtra("purpose") && intent.getStringExtra("purpose").equals("viewTemplates")){
            imageViewAddItems.setVisibility(View.INVISIBLE);
            checkBoxStdTTPselectAll.setVisibility(View.INVISIBLE);
        }
        imageViewAddItems.setOnClickListener(this);

        checkBoxStdTTPselectAll = (CheckBox)findViewById(R.id.checkBoxStdTTPselectAll);
        checkBoxStdTTPselectAll.setOnClickListener(this);
    }

    private void makeAllEntries(boolean boolie) {
        int val;
        if(boolie){
            val = 1;
        }else {
            val = 0;
        }
        for(int i = 0; i< stdPackingItemsInfos.size(); i++){
            stdPackingItemsInfos.get(i).checked = val;
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.imageViewStdTTPAddItems :
                List<String> selectedItems = new ArrayList<>();
                for(StdPackingItemsInfo pi: stdPackingItemsInfos){
                    if(pi.checked == 1){
                        selectedItems.add(pi.getItem());
                    }
                }
                for(String s: selectedItems){
                    Log.d(s,"");
                }
                Intent intent = new Intent(StdThingsToPackActivity.this, ThingsToPackActivity.class);
                intent.putStringArrayListExtra("selectedItemsList", (ArrayList<String>) selectedItems);
                intent.putExtra("tripId", tripId);
                startActivity(intent);
                finish();
                break;
            case R.id.checkBoxStdTTPselectAll :
                    if(checkBoxStdTTPselectAll.getText().toString().toLowerCase().equals("deselect all")){
                        getIntent().putExtra("selectionStatus", "deselect");
                        finish();
                        startActivity(getIntent());
                    }else if (checkBoxStdTTPselectAll.getText().toString().toLowerCase().equals("select all")){
                        getIntent().putExtra("selectionStatus", "select");
                        finish();
                        startActivity(getIntent());
                    }
                break;
        }
    }
}
