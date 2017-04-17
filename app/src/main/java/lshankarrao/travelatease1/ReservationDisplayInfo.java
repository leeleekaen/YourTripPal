package lshankarrao.travelatease1;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


public class ReservationDisplayInfo {


    String pos1, pos2, pos3;
    Context context;
    int tripId;

    public ReservationDisplayInfo(Context context){
        this.context = context;

    }

    public void SpecificResBasedDisplayInfo(Context context, Cursor cursor, View view) {

    }

    public void commonDisplayInfo(Cursor cursor, View view){

        TripDbHelper tripDbHelper = new TripDbHelper(context);
        long startTimeMilli = cursor.getLong(cursor.getColumnIndex("stTimeMillis"));

        this.pos1 = tripDbHelper.getTimeFromMilli(startTimeMilli,"hh:mm a");

        this.pos2 = tripDbHelper.getDateFromMilli(startTimeMilli,"EEE, MMM d, ''yy");

        this.pos3 = cursor.getString(cursor.getColumnIndex("startPlaceAddress"));

        ((TextView)view.findViewById(R.id.pos1)).setText(this.pos1);
        ((TextView)view.findViewById(R.id.pos2)).setText(this.pos2);
        ((TextView)view.findViewById(R.id.pos3)).setText(this.pos3);

        SpecificResBasedDisplayInfo(context,cursor, view);


    }
    String titleOrPlaceName, placeNameAddress, startMsg, startTime, startDate, endMsg, endTime,
    endDate, singleDate, confNo, notes;
    String startPlaceNameAndKind, startPlaceAddress, endPlaceNameAndKind, endPlaceAddress ;

    public void viewReservationDisplay(int resId){

    }

    public void commonViewResDisplay(int resId){
        TripDbHelper tripDbHelper = new TripDbHelper(context);
        ReservationsInfo resInfo = tripDbHelper.getReservation(resId);
        String notes_res = resInfo.getNotes();
        String confNo_res = resInfo.getConfNo();
        if(notes_res == null){
            notes_res = "-";
        }
        if(confNo_res == null){
            confNo_res = "-";
        }
        long sDateMilli = resInfo.stTimeMillis;
        startDate = tripDbHelper.getDateFromMilli(sDateMilli, "dd MMM yy");
        startTime = tripDbHelper.getTimeFromMilli(sDateMilli,"hh:mm a");

        long eDateMilli = resInfo.endTimeMillis;
        endDate = tripDbHelper.getDateFromMilli(eDateMilli,"dd MMM yy");
        endTime = tripDbHelper.getTimeFromMilli(eDateMilli, "hh:mm a");
        if(startDate.equals(endDate) && resInfo.endPlaceName == null){
            singleDate = startDate;
            startDate = "";
            endDate = "";
        }
        confNo = "Conf#: " + confNo_res;
        notes = "Notes: "+notes_res;

        String resTimelineInfo = resInfo.kind.split("_")[1];
        if(resTimelineInfo.equals("end")){
            Log.d("swapping", startDate+"");
            String temp = startDate;
            startDate = endDate;
            endDate = temp;
            temp = startTime;
            startTime = endTime;
            endTime = temp;
        }

        viewReservationDisplay(resId);
    }

    public String getPos1() {
        return pos1;
    }

    public String getPos2() {
        return pos2;
    }

    public String getPos3() {
        return pos3;
    }

    public String getReservationItinerary(final ReservationsInfo info){
        return  null;
    }


}
