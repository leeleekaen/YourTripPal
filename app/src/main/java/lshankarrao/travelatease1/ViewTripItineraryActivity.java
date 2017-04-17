package lshankarrao.travelatease1;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class ViewTripItineraryActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    TripDbHelper tripDb;
    Cursor cursor;
    ReservationsListAdapter ela;
    int tripId;
    String tripTitle;
    String tripStartDate;
    String tripStartTime;
    String tripTimings;
    String tripPlace;
    final int REMINDER_DURATION = 12345;
    String mfullTripDetails;
    TripInfo tripInfo;
    String endTime, stTime;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_trip_itinerary);
        toolbar = (Toolbar) findViewById(R.id.toolbarVTI);
        toolbar.setBackgroundResource(R.drawable.action_bar_background);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Manage Trip");

        initNavigationDrawer();

        tripDb = new TripDbHelper(this);

        Intent tripIntent = getIntent();
        tripId = tripIntent.getIntExtra("id", -1);
        if (tripId == -1) {
            Log.d("invalid trip id", "View Trip activity");
            return;
        }
        Log.d("tripId: VTA ", tripId + "");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabAddReservation);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowAlertDialogWithListview();
            }
        });
        fab.setBackgroundColor(Color.parseColor("#FFFF0004"));

        FloatingActionButton fabMoreOptions = (FloatingActionButton) findViewById(R.id.fabManageTripMoreOptions);
        fabMoreOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });
        fab.setBackgroundColor(Color.parseColor("#FFFF0004"));



        Log.d("viewTrip", "ella res");
        tripDb.logAllReservations(tripId);

        tripInfo = tripDb.getTripInfo(tripId);
        TextView titleDisplay = (TextView) findViewById(R.id.textViewVTATitle);
        tripTitle = tripInfo.getTitle();
        titleDisplay.setText(tripTitle);
        if (tripTitle == null) {
            Log.d("empty s ", "");
        }

        ListView reservationListView = (ListView) findViewById(R.id.listViewTLAReservationsList);
        cursor = tripDb.fetchAllReservationsForTrip(tripId);
        Log.d("Creating ", cursor.getCount() + "");
        ela = new ReservationsListAdapter(this, cursor, 0);
        reservationListView.setAdapter(ela);

        reservationListView.setOnItemClickListener(this);

        TextView placeDisplay = (TextView) findViewById(R.id.textViewVTAPlace);
        tripPlace = tripInfo.getPlaceName();
        placeDisplay.setText(tripPlace);
        placeDisplay.setEllipsize(TextUtils.TruncateAt.MARQUEE);


        tripTimings = tripDb.getDateFromMilli(tripInfo.stTimeMillis, "EEE MMM dd ''yy")+
                " to " +tripDb.getDateFromMilli(tripInfo.endTimeMillis, "EEE MMM dd ''yy");
        TextView durationDisplay = (TextView) findViewById(R.id.textViewVTADuration);
        durationDisplay.setText(tripTimings);

        stTime = tripDb.getTimeFromMilli(tripInfo.stTimeMillis, "hh:mm aaa");
        endTime = tripDb.getTimeFromMilli(tripInfo.endTimeMillis, "hh:mm aaa");


        mfullTripDetails = tripInfo.getTitle() + "\n" +
                "PLACE: " + tripPlace + "\n" +
                "Timings: " + tripTimings + "\n" + "\n";
        titleDisplay.setText(tripTitle);
        if (tripTitle == null || tripTitle.isEmpty()) {
            Log.d("empty s ", "");
        }

       Log.d("string:  ", tripTitle);

        this.tripTitle = tripInfo.getTitle();
        long stMilli = tripInfo.getStTimeMillis();
        String[] stDt = tripDb.getDatePartsFromMilli(stMilli,"MM/dd/yyyy");
        tripStartTime = tripDb.getTimeFromMilli(stMilli, "hh:mm");
        tripStartDate = stDt[0] +"/" + stDt[1] + "/"+ stDt[2];

        LinearLayout linearLayoutSetReminder = (LinearLayout) findViewById(R.id.linearLayoutVTIsetReminder);
        linearLayoutSetReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPlanningReminderOption();
            }
        });

        LinearLayout linearLayoutPack = (LinearLayout) findViewById(R.id.linearLayoutVTIThingsToPack);
        linearLayoutPack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewTripItineraryActivity.this, ThingsToPackActivity.class);

                intent.putExtra("tripId", tripId);
                startActivity(intent);
                finish();
            }
        });

        LinearLayout linearLayoutToDoList = (LinearLayout) findViewById(R.id.linearLayoutVTItodoList);
        linearLayoutToDoList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewTripItineraryActivity.this, ToDoListActivity.class);

                intent.putExtra("tripId", tripId);
                startActivity(intent);
                finish();
            }
        });



    }
    @Override
    public void onClick(View v) {

        Intent addContacts = new Intent(ViewTripItineraryActivity.this, AddContactsActivity.class);
        addContacts.putExtra("tripId",tripId);
        addContacts.putExtra("purpose","addContact");
        startActivity(addContacts);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_trip_itinerary, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
         if (id == R.id.action_home) {
            goHome();
            return true;
        }
        else if(id == R.id.action_editTrip){
             editTripOption();
         }
        else if(id == R.id.action_deleteTrip){
             AlertDialog.Builder builder = new AlertDialog.Builder(this);
             builder.setTitle("All reservations in the trip too will be deleted. Proceed?");

             builder.setIcon(R.drawable.warning_msg_icon);

             builder.setPositiveButton("Yes, Go Ahead", new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialog, int which) {
                     deleteTripOption();
                 }
             });
             builder.setNegativeButton("No, Thanks", new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialog, int which) {
                     dialog.cancel();
                 }
             });

             builder.show();
         }else if(id == R.id.action_share){
             Intent addContacts = new Intent(ViewTripItineraryActivity.this, AddContactsActivity.class);
             addContacts.putExtra("tripId",tripId);
             addContacts.putExtra("purpose","share");
             startActivity(addContacts);
             finish();
         }

        return super.onOptionsItemSelected(item);
    }



    private void toast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public String getTripKind(){
        Calendar current = Calendar.getInstance();
        long currentTimeInMilli = current.getTimeInMillis();
        String tripKind = null;
        if(tripInfo.stTimeMillis > currentTimeInMilli){
            tripKind = "upcoming";
        }else if(tripInfo.endTimeMillis < currentTimeInMilli){
            tripKind = "past";
        }
        return tripKind;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        String tripKind = getTripKind();
        if (tripKind != null) {

            Intent intent = new Intent(ViewTripItineraryActivity.this, TripListActivity.class);
            intent.putExtra("tripKind", tripKind);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(ViewTripItineraryActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

    }

    private void editTripOption() {
        Intent intent = new Intent(ViewTripItineraryActivity.this, AddEditTripActivity.class);
        intent.putExtra("purpose", "edit");
        intent.putExtra("id", tripId);
        startActivity(intent);
        finish();
    }

    private void goHome() {
        Intent intent = new Intent(ViewTripItineraryActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void setPlanningReminderOption() {
        Intent newIntent = new Intent(ViewTripItineraryActivity.this, SetTripPlanningReminderActivity.class);
        newIntent.putExtra("id", tripId);
        startActivity(newIntent);
        finish();
    }



    private void locationBasedNotificationOption() {

        LocationBasedNotifier location = new LocationBasedNotifier(getApplicationContext(), tripId, tripInfo.getPlaceAddress());

    }

    private void deleteTripOption() {
        String tripKind = getTripKind();
        cursor = tripDb.fetchAllReservationsForTrip(tripId);
        List<ReservationsInfo> reservationsInfos = tripDb.getAllReservationsInfo(tripId);
        if (reservationsInfos != null) {
            for (ReservationsInfo resInfo : reservationsInfos) {

                tripDb.deleteEntry("reservationInfo", resInfo.getId());
            }
        }
        tripDb.deleteEntry("tripInfo", tripId);

        if (tripKind != null) {

            Intent intent = new Intent(ViewTripItineraryActivity.this, TripListActivity.class);
            intent.putExtra("tripKind", tripKind);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(ViewTripItineraryActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("namo", "namo");
        Intent reservationView = new Intent(ViewTripItineraryActivity.this, ViewReservationActivity.class);
        reservationView.putExtra("position", position+1);
        int resId = cursor.getInt(cursor.getColumnIndex("_id"));
        reservationView.putExtra("resId",resId);
        Log.d("res position", position+"+1");
        Log.d("res ID", resId+"");
        startActivity(reservationView);
        finish();
    }


    public void initNavigationDrawer() {

        NavigationView navigationView = (NavigationView)findViewById(R.id.navigation_view_VTI);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                int id = menuItem.getItemId();

                switch (id){
                    case R.id.addStay:
                        Intent intent = new Intent(ViewTripItineraryActivity.this, HotelReservationActivity.class);
                        intent.putExtra("tripId", tripId);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.addTransport:
                        Intent transportIntent = new Intent(ViewTripItineraryActivity.this, TransportReservationActivity.class);
                        transportIntent.putExtra("tripId", tripId);
                        startActivity(transportIntent);
                        finish();
                        break;
                    case R.id.addEvent:
                        Intent eventIntent = new Intent(ViewTripItineraryActivity.this, EventReservationActivity.class);
                        eventIntent.putExtra("tripId", tripId);
                        startActivity(eventIntent);
                        finish();
                        break;
                    case R.id.shareItinerary:
                        Intent shareItineraryIntent = new Intent(ViewTripItineraryActivity.this, AddContactsActivity.class);
                        shareItineraryIntent.putExtra("tripId",tripId);
                        shareItineraryIntent.putExtra("purpose","share");
                        startActivity(shareItineraryIntent);
                        finish();
                        break;
                    case R.id.thingsToPack:
                        Intent packIntent = new Intent(ViewTripItineraryActivity.this, ThingsToPackActivity.class);
                        packIntent.putExtra("tripId", tripId);
                        startActivity(packIntent);
                        finish();
                        break;
                    case R.id.setPlanningReminder:
                        setPlanningReminderOption();
                        break;
                    case R.id.addFamilyFriends:
                        Intent addContacts = new Intent(ViewTripItineraryActivity.this, AddContactsActivity.class);
                        addContacts.putExtra("tripId",tripId);
                        addContacts.putExtra("purpose","addContact");
                        startActivity(addContacts);
                        finish();
                        break;
                    case R.id.tripPlanningStatus:
                        Intent tripPlanStatus = new Intent(ViewTripItineraryActivity.this, ToDoListActivity.class);
                        tripPlanStatus.putExtra("tripId",tripId);
                        startActivity(tripPlanStatus);
                        finish();
                        break;


                }
                return true;
            }
        });
        View header = navigationView.getHeaderView(0);
        TextView nav_view_heading = (TextView)header.findViewById(R.id.textViewHome);
        nav_view_heading.setText("YourTripPal");
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

    public void ShowAlertDialogWithListview()
    {
        List<String> mResOptions = new ArrayList<String>();
        mResOptions.add("Stay");
        mResOptions.add("Transport");
        mResOptions.add("Event");

        final CharSequence[] ResOpts = mResOptions.toArray(new String[mResOptions.size()]);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Add a Reservation (Select Type)");
        dialogBuilder.setItems(ResOpts, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                String selectedText = ResOpts[item].toString();
                Log.d("selectedText", selectedText);
                switch (selectedText){
                    case "Stay":
                        Intent intent = new Intent(ViewTripItineraryActivity.this, HotelReservationActivity.class);
                        intent.putExtra("tripId", tripId);
                        startActivity(intent);
                        finish();
                        break;
                    case "Transport" :
                        Intent transportIntent = new Intent(ViewTripItineraryActivity.this, TransportReservationActivity.class);
                        transportIntent.putExtra("tripId", tripId);
                        startActivity(transportIntent);
                        finish();
                        break;
                    case "Event" :
                        Intent eventIntent = new Intent(ViewTripItineraryActivity.this, EventReservationActivity.class);
                        eventIntent.putExtra("tripId", tripId);
                        startActivity(eventIntent);
                        finish();
                        break;
                }
            }
        });

        AlertDialog alertDialogObject = dialogBuilder.create();
        alertDialogObject.show();

    }

}