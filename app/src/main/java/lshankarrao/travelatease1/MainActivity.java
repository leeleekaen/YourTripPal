package lshankarrao.travelatease1;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ArrayAdapter<String> mAdapter;
    TripDbHelper tripDbHelper = new TripDbHelper(this);
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkAndRequestPermissions();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");

        toolbar.setBackgroundResource(R.drawable.action_bar_background);

        initNavigationDrawer();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabTripAdd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddEditTripActivity.class);
                startActivity(intent);
                finish();
            }
        });
        fab.setBackgroundColor(Color.parseColor("#FFFF0004"));

        FloatingActionButton fabMoreOptions = (FloatingActionButton) findViewById(R.id.fabTripMoreOptions);
        fabMoreOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });
        fab.setBackgroundColor(Color.parseColor("#FFFF0004"));


        TextView upcomingTripList = (TextView) findViewById(R.id.textViewMainUpcomingTripDetails);
        Cursor upcomingCursor = tripDbHelper.fetchUpcomingTrips();
        if (upcomingCursor.getCount() > 0) {
            String upcomingTrips = "";
            Log.d("hi", "namskara");
            upcomingCursor.moveToFirst();
            while (!upcomingCursor.isAfterLast()) {
                Log.d("hi hello", "namskara");
                upcomingTrips += upcomingCursor.getString(upcomingCursor.getColumnIndex("title"))
                        + "\n";
                upcomingCursor.moveToNext();
            }
            upcomingTripList.setText(upcomingTrips);
        }
        TextView pastTripList = (TextView) findViewById(R.id.textViewMainPastTripDetails);
        Cursor pastTripCursor = tripDbHelper.fetchPastTrips();
        if (pastTripCursor.getCount() > 0) {
            String pastTrips = "";
            Log.d("hi", "namskara");
            pastTripCursor.moveToFirst();
            while (!pastTripCursor.isAfterLast()) {
                Log.d("hi hello", "namskara");
                pastTrips += pastTripCursor.getString(upcomingCursor.getColumnIndex("title"))
                        + "\n";
                pastTripCursor.moveToNext();
            }
            pastTripList.setText(pastTrips);
        }


        Button upcomingTrips = (Button) findViewById(R.id.button_upcomingTrips);
        upcomingTrips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TripListActivity.class);
                intent.putExtra("tripKind", "upcoming");
                startActivity(intent);
                finish();
            }
        });

        Button pastTrips = (Button) findViewById(R.id.button_pastTrips);
        pastTrips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TripListActivity.class);
                intent.putExtra("tripKind", "past");
                startActivity(intent);
                finish();
            }
        });

        Button currentTrip = (Button) findViewById(R.id.button_currentTrip);
        TextView currentTripTitle = (TextView) findViewById(R.id.textViewCurrentTripTitle);

        final Cursor cursor = tripDbHelper.getCurrentTrip();
        if (cursor.getCount() > 0) {
            currentTrip.setVisibility(View.VISIBLE);
            currentTripTitle.setVisibility(View.VISIBLE);
            cursor.moveToFirst();
            String tripTitle = cursor.getString(upcomingCursor.getColumnIndex("title"));
            currentTripTitle.setText(tripTitle);
        } else {
            currentTrip.setVisibility(View.INVISIBLE);
            currentTripTitle.setVisibility(View.INVISIBLE);
        }

        currentTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cursor.moveToFirst();
                int tripId = cursor.getInt(cursor.getColumnIndex("_id"));
                Intent currentTripIntent = new Intent(
                        MainActivity.this, ViewTripItineraryActivity.class);
                currentTripIntent.putExtra("id", tripId);
                startActivity(currentTripIntent);
                finish();

            }
        });

    }



    private  boolean checkAndRequestPermissions() {
        int permissionWriteExtStorage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionReadExtStorage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        int locationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (permissionWriteExtStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permissionReadExtStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(
                    new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }


    public void initNavigationDrawer() {

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                int id = menuItem.getItemId();

                switch (id) {

                    case R.id.yourTrips:
                        Intent intent = new Intent(MainActivity.this, TripListActivity.class);
                        intent.putExtra("tripKind", "all");
                        startActivity(intent);
                        finish();
                        break;

                    case R.id.yourBucketList:
                        Intent bucketListIntent = new Intent(MainActivity.this, BucketListDisplayActivity.class);
                        startActivity(bucketListIntent);
                        finish();
                        break;
                    case R.id.addFriends:
                        Intent addContacts = new Intent(MainActivity.this, AddContactsActivity.class);
                        addContacts.putExtra("purpose", "addContactMain");
                        startActivity(addContacts);
                        finish();
                        break;
                    case R.id.thingsToPackTemplates:
                        Intent intentPacking = new Intent(MainActivity.this, ThingsToPackTemplatesActivity.class);
                        startActivity(intentPacking);
                        finish();
                        break;
                    case R.id.aboutYourTripPal:

                        Intent intentAbout = new Intent(MainActivity.this, AboutAppActivity.class);
                        startActivity(intentAbout);
                        finish();
                        break;
                    case R.id.sendFeedback:
                        feedback();
                        break;
                    case R.id.rateApp:
                        rateApp();
                        break;
                    case R.id.settings:
                        Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(settingsIntent);
                        finish();
                        break;


                }
                return true;
            }
        });
        View header = navigationView.getHeaderView(0);
        TextView tv_email = (TextView) header.findViewById(R.id.textViewHome);
        tv_email.setText("YourTripPal");
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View v) {
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

    public void feedback() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Your feedback is important to us. Please write your feedback below.");
        final EditText editTextFeedback = new EditText(this);
        editTextFeedback.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(editTextFeedback);

        builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String feedback = editTextFeedback.getText().toString();
                String[] emailAddr = new String[1];
                emailAddr[0] = "YourTripPal@gmail.com";
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, emailAddr);
                i.putExtra(Intent.EXTRA_SUBJECT, "Feedback for YourTripPal");
                i.putExtra(Intent.EXTRA_TEXT, feedback);
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getApplicationContext(),
                            "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_addtrip) {

            Intent intent = new Intent(MainActivity.this, AddEditTripActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void rateApp() {
        Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName())));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

