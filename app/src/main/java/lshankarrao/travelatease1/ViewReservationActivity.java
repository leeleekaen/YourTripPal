package lshankarrao.travelatease1;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


public class ViewReservationActivity extends ActionBarActivity2 {
    TripDbHelper tripDbHelper;
    TextView titleOrPlaceName, placeNameAddress, startMsg, startTime, startDate, endMsg, endTime,
    endDate, singleDate, confNo, notes, startPlaceNameAndKind, startPlaceAddress,
            endPlaceNameAndKind, endPlaceAddress ;

    ReservationsInfo resInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("Manage Reservation");

        Log.d("poora", "");
        Intent intent = getIntent();
        int resId = intent.getIntExtra("resId", -1);
        if(resId == -1){
            Log.d("Invalid res ID", "");
            return;
        }
        Log.d("res ID View Res ",resId+"" );
        tripDbHelper = new TripDbHelper(this);
        resInfo = tripDbHelper.getReservation(resId);

        if(resInfo == null){
            Log.d("no such res in db","");
            return;
        } else {
            String resType = resInfo.kind.split("_")[0];
            if (resInfo.endPlaceName == null || resInfo.endPlaceName.isEmpty()) {
                setContentView(R.layout.activity_view_reservation_1addr);
                 titleOrPlaceName = (TextView) findViewById(R.id.textViewRes1addrTitleOrName);
                 placeNameAddress= (TextView) findViewById(R.id.textView1addrPlaceNameAddress);
                 startMsg= (TextView) findViewById(R.id.textView1addrStarts);
                 startTime= (TextView) findViewById(R.id.textView1addrStartTime);
                 startDate= (TextView) findViewById(R.id.textView1addrStartDate);
                 endMsg= (TextView) findViewById(R.id.textView1addrEnds);
                 endTime= (TextView) findViewById(R.id.textView1addrEndTime);
                 endDate= (TextView) findViewById(R.id.textView1addrEndDate);
                 confNo= (TextView) findViewById(R.id.textView1addrConfNumber);
                 notes= (TextView) findViewById(R.id.textView1addrNotes);
                 singleDate= (TextView) findViewById(R.id.textView1addrSingleDate);
                ReservationDisplayInfo info = null;
                if(tripDbHelper.hotelSubKinds.contains(resInfo.kind)){
                    Log.d("inside hotel", resInfo.kind);
                     info = new HotelResSpecificDisplayInfo(this);

                }else if(tripDbHelper.eventResSubKinds.contains(resType)){
                    Log.d("inside event", resInfo.kind);
                    info = new EventResSpecificDisplayInfo(this);
                }
                if (info != null) {
                    info.commonViewResDisplay(resId);
                    updateView(info);
                }
            }
            else {
                setContentView(R.layout.activity_view_reservation_2addr);
                titleOrPlaceName = (TextView) findViewById(R.id.textViewRes2addrTitleOrName);
                startTime= (TextView) findViewById(R.id.textView2addrStartTime);
                startDate= (TextView) findViewById(R.id.textView2addrStartDate);
                endTime= (TextView) findViewById(R.id.textView2addrEndTime);
                endDate= (TextView) findViewById(R.id.textView2addrEndDate);
                confNo= (TextView) findViewById(R.id.textView2addrConfNumber);
                notes= (TextView) findViewById(R.id.textView2addrNotes);
                startPlaceAddress= (TextView) findViewById(R.id.textView2addrStartPlaceAddress);
                endPlaceAddress= (TextView) findViewById(R.id.textView2addrEndPlaceAddress);
                startPlaceNameAndKind= (TextView) findViewById(R.id.textView2addrStartPlaceNameAndKind);
                endPlaceNameAndKind= (TextView) findViewById(R.id.textView2addrEndPlaceNameAndKind);
                Log.d("inside 2addr", resInfo.endPlaceName);
                if(tripDbHelper.transportSubKinds.contains(resInfo.kind)){
                    ReservationDisplayInfo info = new TransportResSpecificDisplayInfo(this);
                    info.commonViewResDisplay(resId);
                    updateView(info);

                }else if(tripDbHelper.eventResSubKinds.contains(resType)){
                    ReservationDisplayInfo info = new EventResSpecificDisplayInfo(this);
                    info.commonViewResDisplay(resId);
                    updateView(info);

                }

            }
        }
    }

    private void updateView(ReservationDisplayInfo info) {
        titleOrPlaceName.setText(info.titleOrPlaceName);
        startTime.setText(info.startTime);
        startDate.setText(info.startDate);
        endTime.setText(info.endTime);
        endDate.setText(info.endDate);
        if(info.confNo == null) {
            Log.d("confNoViewRes", "is null"+"");
        }
        if(info.notes == null) {
            Log.d("notesViewRes", "is null"+"");
        }
        confNo.setText(info.confNo);
        notes.setText(info.notes);

        if(resInfo.endPlaceName != null && !resInfo.endPlaceName.isEmpty()) {
            startPlaceNameAndKind.setText(info.startPlaceNameAndKind);
            startPlaceAddress.setText(info.startPlaceAddress);
            endPlaceNameAndKind.setText(info.endPlaceNameAndKind);
            endPlaceAddress.setText(info.endPlaceAddress);
        }else {
            placeNameAddress.setText(info.placeNameAddress);
            startMsg.setText(info.startMsg);
            endMsg.setText(info.endMsg);
            singleDate.setText(info.singleDate);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_reservation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_deleteTrip) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Reservation & Attachment(if any) will be deleted. Proceed?");

            builder.setIcon(R.drawable.warning_msg_icon);

            builder.setPositiveButton("Sure, Go Ahead", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int tripId = resInfo.getTripId();

                    tripDbHelper.deleteEntry("reservationInfo", resInfo.getId());
                    toast("Reservation Deleted");

                    Intent intent = new Intent(ViewReservationActivity.this, ViewTripItineraryActivity.class);
                    intent.putExtra("id", tripId);
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
        else if (id == R.id.action_editTrip) {
            Log.d("edit res", "opt"+"");
            String kind = resInfo.getKind();
            if(tripDbHelper.hotelSubKinds.contains(kind)){
                Intent intent = new Intent(ViewReservationActivity.this, HotelReservationActivity.class);
                intent.putExtra("resId", resInfo.getId());
                intent.putExtra("purpose", "edit");
                intent.putExtra("tripId",resInfo.getTripId());
                startActivity(intent);
                finish();
            }else if(tripDbHelper.transportSubKinds.contains(kind)){
                Intent intent = new Intent(ViewReservationActivity.this, TransportReservationActivity.class);
                intent.putExtra("resId", resInfo.getId());
                intent.putExtra("purpose", "edit");
                intent.putExtra("tripId",resInfo.getTripId());
                startActivity(intent);
                finish();
            }else if(tripDbHelper.eventResSubKinds.contains(kind.split("_")[0])){
                Intent intent = new Intent(ViewReservationActivity.this, EventReservationActivity.class);
                intent.putExtra("resId", resInfo.getId());
                intent.putExtra("purpose", "edit");
                intent.putExtra("tripId",resInfo.getTripId());
                startActivity(intent);
                finish();
            }

        }

        return true;
    }
    public void toast(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ViewReservationActivity.this, ViewTripItineraryActivity.class);
        intent.putExtra("id", resInfo.getTripId());
        startActivity(intent);
    }
}
