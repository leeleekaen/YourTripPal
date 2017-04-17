package lshankarrao.travelatease1;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;


public class TripListActivity extends ActionBarActivity2 implements AdapterView.OnItemClickListener {
    private ListView tripListView;
    private TripListAdapter tla;
    TripDbHelper tripDB;
    Cursor cursor;
    String tripKind;
    TextView textViewTripListTripKind;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_list);
        getSupportActionBar().setTitle("Trips List");

        tripListView = (ListView) findViewById(R.id.listViewTLTripList);
        textViewTripListTripKind = (TextView)findViewById(R.id.textViewTripListTripKind);

        tripDB = new TripDbHelper(this);
        Intent intent = getIntent();
        tripKind = intent.getStringExtra("tripKind");


        if(tripKind.equals("upcoming")){
            cursor = tripDB.fetchUpcomingTrips();
            textViewTripListTripKind.setText("Upcoming Trips");
        }
        else if(tripKind.equals("past")){
            cursor = tripDB.fetchPastTrips();
            textViewTripListTripKind.setText("Past Trips");

        } else if(tripKind.equals("all")){
            cursor = tripDB.fetchAllTrips();
            textViewTripListTripKind.setText("My Trips");

            Log.d("Log_nodu","all trips");
        }



        tla = new TripListAdapter(this, cursor, 0);
        tripListView.setAdapter(tla);

        tripListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent existingTrips = new Intent(TripListActivity.this, ViewTripItineraryActivity.class);
        existingTrips.putExtra("position", position+1);
        int tripId = cursor.getInt(cursor.getColumnIndex("_id"));
        existingTrips.putExtra("id",tripId);
        Log.d("position", position+"+1");
        Log.d("TLA Trip ID", tripId+"");
        startActivity(existingTrips);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        tripDB = new TripDbHelper(this);
        cursor.requery();
        tla = new TripListAdapter(this, cursor, 0);
        tripListView.setAdapter(tla);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_trip_list_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_home) {

            Intent intent = new Intent(TripListActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        else if(id == R.id.action_addtrip){
            Intent intent = new Intent(TripListActivity.this, AddEditTripActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return true;
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(TripListActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
