package lshankarrao.travelatease1;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class ThingsToPackActivity extends AppCompatActivity implements View.OnClickListener {

    List<ThingsToPackInfo> tripSpecificList =  new ArrayList<>();

    TripDbHelper tripDbHelper;
    int tripId;
    JSONObjHandler jsonObjHandler;
    String[] osArray = {"Accessories", "Child Care", "Clothes", "Documents",
            "Electronics", "Makeup Kit", "Medical Kit", "Personal Care", "Outdoor Activities", "Water Activities",
            "Snow Activities", "Camping", "Party", "Business/Educational", "Indoor Activities"};
    ThingsToPackTripRVAdapter tripPackingItemsRVAdapter;
    List<ThingsToPackInfo> thingsToPackInfosDb;
    RecyclerView rv;
    ArrayList<String> addStdItemsList;
    ImageButton imageButtonAddItem;
    EditText editTextItem;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    HashSet<String> tripPackingItems;
    Button reviewDone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_things_to_pack);

        toolbar = (Toolbar) findViewById(R.id.toolbarTTP);
        toolbar.setBackgroundResource(R.drawable.action_bar_background);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Trip Packing List");
        initNavigationDrawer();

        if(getIntent().hasExtra("tripId")) {
            tripId = getIntent().getIntExtra("tripId", -1);
            if (tripId == -1) {
                Log.d("invalid trip id", "TTP activity");
                return;
            }
        }

        if (getIntent().hasExtra("selectedItemsList")) {
            addStdItemsList = getIntent().getStringArrayListExtra("selectedItemsList");
            for (String s : addStdItemsList) {
                Log.d(s, "List rcvd from StdTTP");
            }
        }

        reviewDone = (Button)findViewById(R.id.buttonTTPReviewDone);
        if(getIntent().hasExtra("purpose") && getIntent().getStringExtra("purpose").equals("review")){
            reviewDone.setVisibility(View.VISIBLE);
            reviewDone.setOnClickListener(this);
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabTTPAdd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });
        fab.setBackgroundColor(Color.parseColor("#FFFF0004"));

        tripDbHelper = new TripDbHelper(this);

        tripPackingItems = new HashSet<>();
        jsonObjHandler = new JSONObjHandler();
        String packingListString = tripDbHelper.getPackingListStringForTrip(tripId);
        thingsToPackInfosDb = new ArrayList<>();
        if (packingListString != null) {

            try {
                thingsToPackInfosDb = jsonObjHandler.ConvertStringToList(packingListString);
                if (thingsToPackInfosDb != null) {
                    for (ThingsToPackInfo tp : thingsToPackInfosDb) {
                        tripPackingItems.add(tp.item.toUpperCase());
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        tripSpecificList = thingsToPackInfosDb;

        if (addStdItemsList != null) {
            for (String s : addStdItemsList) {
                if (!tripPackingItems.contains(s.toUpperCase())) {
                    ThingsToPackInfo t = new ThingsToPackInfo(s, false);
                    tripSpecificList.add(t);
                    tripPackingItems.add(s.toUpperCase());
                }
            }
        }

        tripPackingItemsRVAdapter = new ThingsToPackTripRVAdapter(tripSpecificList, this,
                tripPackingItems);

        rv = (RecyclerView) findViewById(R.id.recyclerViewThingsToPack);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);
        rv.setAdapter(tripPackingItemsRVAdapter);

        final ThingsToPackTripTouchHelperCallBack callbacktripPack = new ThingsToPackTripTouchHelperCallBack(tripPackingItemsRVAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callbacktripPack);
        touchHelper.attachToRecyclerView(rv);

        editTextItem = (EditText) findViewById(R.id.editTextTTPItem);
        editTextItem.setInputType(InputType.TYPE_CLASS_TEXT);

        imageButtonAddItem = (ImageButton) findViewById(R.id.buttonTTPAddItem);
        imageButtonAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String itemName = editTextItem.getText().toString();
                if(itemName == null || itemName.isEmpty()){
                    toast("Please enter a valid item");
                    return;
                }
                if (!tripPackingItems.contains(itemName.toUpperCase())) {
                    ThingsToPackInfo t = new ThingsToPackInfo(itemName, false);
                    tripSpecificList.add(t);
                    tripPackingItems.add(itemName.toUpperCase());
                    toast("Added");
                    updateOrAddListToDb();
                    finish();
                    startActivity(getIntent());
                }else{
                    toast("Item already present in the list");
                }
            }
        });

        ImageButton sort = (ImageButton) findViewById(R.id.imageButtonTTPSortLexicographically);
        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LexicographicSorting lexicographicSorting = new LexicographicSorting(tripSpecificList);
                tripSpecificList = lexicographicSorting.getLexicographicallySortedList();
                updateOrAddListToDb();
                recreate();
            }
        });
    }

    private void toast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        updateOrAddListToDb();
        if(getIntent().hasExtra("purpose") && getIntent().getStringExtra("purpose").equals("review")){
            Intent intent = new Intent(ThingsToPackActivity.this, AddContactsActivity.class);
            intent.putExtra("tripId", tripId);
            intent.putExtra("purpose", "share");
            startActivity(intent);
            finish();
        }else {
            Intent intent = new Intent(ThingsToPackActivity.this, ViewTripItineraryActivity.class);
            intent.putExtra("id", tripId);
            startActivity(intent);
            finish();
        }
    }

    private void updateOrAddListToDb() {

        try {

            String packItemsString = jsonObjHandler.ConvertListToString(tripSpecificList);
            if (tripDbHelper.entryExistsInTripSpecificPackingTable(tripId) == true) {
                int id = (int)tripDbHelper.getPackingListIdForTrip(tripId);
                tripDbHelper.deleteEntry("tripSpecificPackingLists", id );
                tripDbHelper.addThingsToPackForTrip(packItemsString, tripId);
            } else {
                tripDbHelper.addThingsToPackForTrip(packItemsString, tripId);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void navigationBarOptionsHandling(int id) {
        updateOrAddListToDb();
        Intent intent = new Intent(ThingsToPackActivity.this, StdThingsToPackActivity.class);
        intent.putExtra("tripId", tripId);
        intent.putExtra("selectionStatus", "select");
        switch (id) {
            case 0:
                intent.putExtra("category", osArray[0]);
                intent.putExtra("tripId", tripId);
                startActivity(intent);
                break;
            case 1:
                intent.putExtra("category", osArray[1]);
                intent.putExtra("tripId", tripId);
                startActivity(intent);
                break;
            case 2:
                intent.putExtra("category", osArray[2]);
                intent.putExtra("tripId", tripId);
                startActivity(intent);
                break;
            case 3:
                intent.putExtra("category", osArray[3]);
                intent.putExtra("tripId", tripId);
                startActivity(intent);
                break;
            case 4:
                intent.putExtra("category", osArray[4]);
                intent.putExtra("tripId", tripId);
                startActivity(intent);
                break;
            case 5:
                intent.putExtra("category", osArray[5]);
                intent.putExtra("tripId", tripId);
                startActivity(intent);
                break;
            case 6:
                intent.putExtra("category", osArray[6]);
                intent.putExtra("tripId", tripId);
                startActivity(intent);
                break;
            case 7:
                intent.putExtra("category", osArray[7]);
                intent.putExtra("tripId", tripId);
                startActivity(intent);
                break;
            case 8:

                intent.putExtra("category", osArray[8]);
                intent.putExtra("tripId", tripId);
                startActivity(intent);
                break;
            case 9:
                intent.putExtra("category", osArray[9]);
                intent.putExtra("tripId", tripId);
                startActivity(intent);
                break;
            case 10:
                intent.putExtra("category", osArray[10]);
                intent.putExtra("tripId", tripId);
                startActivity(intent);
                break;
            case 11:
                intent.putExtra("category", osArray[11]);
                intent.putExtra("tripId", tripId);
                startActivity(intent);
                break;
            case 12:
                intent.putExtra("category", osArray[12]);
                intent.putExtra("tripId", tripId);
                startActivity(intent);
                break;
            case 13:
                intent.putExtra("category", osArray[13]);
                intent.putExtra("tripId", tripId);
                startActivity(intent);
                break;
            case 14:
                intent.putExtra("category", osArray[14]);
                intent.putExtra("tripId", tripId);
                startActivity(intent);
                break;

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_things_to_pack_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_deleteList) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Complete list will be deleted. Proceed?");

            builder.setIcon(R.drawable.warning_msg_icon);

            builder.setPositiveButton("Sure, Go Ahead", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int id = (int)tripDbHelper.getPackingListIdForTrip(tripId);
                    tripDbHelper.deleteEntry("tripSpecificPackingLists", id );

                    toast("Things to Pack List Deleted");

                    Intent intent = new Intent(ThingsToPackActivity.this, ThingsToPackActivity.class);
                    intent.putExtra("tripId", tripId);
                    startActivity(intent);
                    finish();
                }
            });
            builder.setNegativeButton("No, Thanks", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }
        else if(id == R.id.action_setReminder){
            Intent intent = new Intent(ThingsToPackActivity.this, SetTripPlanningReminderActivity.class);
            intent.putExtra("purpose", "pack");
            intent.putExtra("id", tripId);
            startActivity(intent);
            finish();
        }
        return true;
    }

    public void initNavigationDrawer() {

        NavigationView navigationView = (NavigationView)findViewById(R.id.navigation_view_VTI);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                int id = menuItem.getItemId();


                updateOrAddListToDb();
                Intent intent = new Intent(ThingsToPackActivity.this, StdThingsToPackActivity.class);
                intent.putExtra("tripId", tripId);
                intent.putExtra("selectionStatus", "select");
                switch (id) {
                    case R.id.accessories:

                        intent.putExtra("category", osArray[0]);
                        intent.putExtra("tripId", tripId);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.childCare:
                        intent.putExtra("category", osArray[1]);
                        intent.putExtra("tripId", tripId);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.clothes:
                        intent.putExtra("category", osArray[2]);
                        intent.putExtra("tripId", tripId);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.documents:
                        intent.putExtra("category", osArray[3]);
                        intent.putExtra("tripId", tripId);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.electronics:
                        intent.putExtra("category", osArray[4]);
                        intent.putExtra("tripId", tripId);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.makeupKit:
                        intent.putExtra("category", osArray[5]);
                        intent.putExtra("tripId", tripId);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.medicalKit:
                        intent.putExtra("category", osArray[6]);
                        intent.putExtra("tripId", tripId);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.personalCare:
                        intent.putExtra("category", osArray[7]);
                        intent.putExtra("tripId", tripId);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.outdoorActivities:

                        intent.putExtra("category", osArray[8]);
                        intent.putExtra("tripId", tripId);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.waterActivities:
                        intent.putExtra("category", osArray[9]);
                        intent.putExtra("tripId", tripId);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.snowActivities:
                        intent.putExtra("category", osArray[10]);
                        intent.putExtra("tripId", tripId);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.camping:
                        intent.putExtra("category", osArray[11]);
                        intent.putExtra("tripId", tripId);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.party:
                        intent.putExtra("category", osArray[12]);
                        intent.putExtra("tripId", tripId);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.businessOrEducational:
                        intent.putExtra("category", osArray[13]);
                        intent.putExtra("tripId", tripId);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.indoorActivities:
                        intent.putExtra("category", osArray[14]);
                        intent.putExtra("tripId", tripId);
                        startActivity(intent);
                        finish();
                        break;
                }
                return true;
            }
        });
        View header = navigationView.getHeaderView(0);
        TextView nav_view_heading = (TextView)header.findViewById(R.id.textViewHome);
        nav_view_heading.setText("Things to Pack Templates");
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close){

            @Override
            public void onDrawerClosed(View v){
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonTTPReviewDone:
                updateOrAddListToDb();
                Intent intent = new Intent(ThingsToPackActivity.this, AddContactsActivity.class);
                intent.putExtra("tripId", tripId);
                intent.putExtra("purpose", "share");
                startActivity(intent);
                break;

        }
    }
}
