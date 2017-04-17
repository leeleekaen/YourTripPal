package lshankarrao.travelatease1;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class HotelResSpecificDisplayInfo extends ReservationDisplayInfo{
    TripDbHelper tripDbHelper = new TripDbHelper(context);
    String pos5, pos6;

    public HotelResSpecificDisplayInfo(Context context) {
        super(context);
        this.context = context;
    }

    public void SpecificResBasedDisplayInfo(Context context, Cursor cursor, View view) {

        this.pos6 = "";
        String temp  = cursor.getString(cursor.getColumnIndex("kind"));
        String[] kind = temp.split("_");

        if (kind[1].equals("start")) {
            this.pos6 = "Check-In";
        } else if (kind[1].equals("end")) {
            this.pos6 = "Check Out";
        }

        this.pos5 = cursor.getString(cursor.getColumnIndex("startPlaceName"));
        if (this.pos5 == null) this.pos5 = "";

        ((TextView)view.findViewById(R.id.pos4)).setText("");
        ((TextView)view.findViewById(R.id.pos5)).setText(this.pos5);
        ((TextView)view.findViewById(R.id.pos6)).setText(this.pos6);

        ((ImageView)view.findViewById(R.id.img1)).setImageResource(R.drawable.hotel_icon);
    }

    public void viewReservationDisplay(int resId){
        ReservationsInfo resInfo = tripDbHelper.getReservation(resId);


        titleOrPlaceName = resInfo.startPlaceName;
        placeNameAddress = resInfo.startPlaceAddress;
        startMsg = "Check-In";
        endMsg = "Check Out";

    }

    public String getReservationItinerary(final ReservationsInfo info){
        String notes = info.getNotes();
        String confNo = info.getConfNo();
        if(notes == null){
            notes = "-";
        }
        if(confNo == null){
            confNo = "-";
        }
        String hotelResevationDetails = info.getStartPlaceName() + "<br>" +
                                        info.getStartPlaceAddress() + "<br> " +
                "Check-In: " + tripDbHelper.getDateAndTimeInStdFormat(info.getStTimeMillis())+"<br> "+
                "Check-Out: " + tripDbHelper.getDateAndTimeInStdFormat(info.getEndTimeMillis())+"<br> "+
                "Confirmation #: " + confNo +"<br> "+
                "Notes: " + notes;
        return hotelResevationDetails;

    }

    public String getPos5() {
        return pos5;
    }

    public String getPos6() {
        return pos6;
    }

}
