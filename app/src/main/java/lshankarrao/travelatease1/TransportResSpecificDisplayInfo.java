package lshankarrao.travelatease1;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class TransportResSpecificDisplayInfo extends ReservationDisplayInfo{
    TripDbHelper tripDbHelper = new TripDbHelper(context);

    String pos4, pos5, pos6, img1AssetName;

    public TransportResSpecificDisplayInfo(Context context) {
        super(context);
    }

    public void SpecificResBasedDisplayInfo(Context context, Cursor cursor, View view) {

        this.pos4 = cursor.getString(cursor.getColumnIndex("titleOrName"));
        this.pos6 = "";
        String temp  = cursor.getString(cursor.getColumnIndex("kind"));
        String[] kind = temp.split("_");
        if(kind[0].equals("car")) {
            if (kind[1].equals("start")) {
                this.pos6 = "Pick Up";
            } else if (kind[1].equals("end")) {
                this.pos6 = "Drop Off";
            }

        }else{
            if (kind[1].equals("start")) {
                this.pos6 = "Departure";
            } else if (kind[1].equals("end")) {
                this.pos6 = "Arrival";
            }
        }
        this.img1AssetName = "R.drawable."+kind[0]+"_icon";
        this.pos5 = cursor.getString(cursor.getColumnIndex("startPlaceName"));

        ((TextView)view.findViewById(R.id.pos4)).setText(this.pos4);
        ((TextView)view.findViewById(R.id.pos5)).setText(this.pos5);
        ((TextView)view.findViewById(R.id.pos6)).setText(this.pos6);

        switch (kind[0]){
            case "car": ((ImageView) view.findViewById(R.id.img1)).setImageResource
                    (R.drawable.car_icon); break;
            case "flight": ((ImageView) view.findViewById(R.id.img1)).setImageResource
                    (R.drawable.flight_icon); break;
            case "bus": ((ImageView) view.findViewById(R.id.img1)).setImageResource
                    (R.drawable.bus_icon); break;
            case "train" : ((ImageView) view.findViewById(R.id.img1)).setImageResource
                    (R.drawable.train_icon); break;
        }

    }

    public void viewReservationDisplay(int resId){
        ReservationsInfo resInfo = tripDbHelper.getReservation(resId);

        titleOrPlaceName = resInfo.titleOrName;
        String resType = resInfo.kind.split("_")[0];

        if(titleOrPlaceName == null || titleOrPlaceName.isEmpty()){
            titleOrPlaceName = resType.toUpperCase() +" Reservation Details ";
        }

        String resTimelineInfo = resInfo.kind.split("_")[1];
        String tempStKind = "";
        String tempEndKind = "";
        switch (resType){
            case "car":     tempStKind = "Pick-Up"; tempEndKind = "Drop Off" ; break;
            case "flight":  tempStKind = "Depart"; tempEndKind = "Arrive"; break;
            case "bus":     tempStKind = "Depart"; tempEndKind = "Arrive"; break;
            case "train":   tempStKind = "Depart"; tempEndKind = "Arrive"; break;
        }
        startPlaceNameAndKind = tempStKind + " "+ resInfo.startPlaceName;

        startPlaceAddress = resInfo.startPlaceAddress;

        endPlaceNameAndKind = tempEndKind + " " +resInfo.endPlaceName;

        endPlaceAddress = resInfo.endPlaceAddress;

        if(resTimelineInfo.equals("end")){
            String temp = startPlaceAddress;
            startPlaceAddress = endPlaceAddress;
            endPlaceAddress = temp;
            temp = startPlaceNameAndKind;
            startPlaceNameAndKind = endPlaceNameAndKind;
            endPlaceNameAndKind = temp;
        }


    }

    public String getReservationItinerary(final ReservationsInfo info){
        String tempStWord = "";
        String tempEndWord = "";
        String notes = info.getNotes();
        String confNo = info.getConfNo();
        String title = info.getTitleOrName();
        if(notes == null){
            notes = "-";
        }
        if(confNo == null){
            confNo = "-";
        }
        String kind = info.kind.split("_")[0];


        Log.d("ItineraryMistakeTrans",kind);
        if (kind.equals("car")){
            tempStWord = "Pick-Up";
            tempEndWord = "Drop-Off";
        }else{
            tempStWord = "Depart";
            tempEndWord = "Arrive";
        }
        String transportResevationDetails = kind +" Reservation Details " +"<br>"+
                title + "<br>" +
                tripDbHelper.getTimeFromMilli(info.getStTimeMillis(),"hh:mm a")+ "    "+ tempStWord +" " + info.getStartPlaceName() + "<br>" +
                tripDbHelper.getDateFromMilli(info.getStTimeMillis(),"MMM dd")+ "        "+info.getStartPlaceAddress() + "<br> " +
                tripDbHelper.getTimeFromMilli(info.getEndTimeMillis(),"hh:mm a")+ "    "+ tempEndWord +" " + info.getEndPlaceName() + "<br>" +
                tripDbHelper.getDateFromMilli(info.getEndTimeMillis(),"MMM dd")+ "        "+info.getEndPlaceName() + "<br> " +
                "Confirmation #: " + confNo +"<br> "+
                "Notes: " + notes;
        return transportResevationDetails;
    }

    public String getPos4() {
        return pos4;
    }

    public String getPos5() {
        return pos5;
    }

    public String getPos6() {
        return pos6;
    }

    public String getImg1AssetName() {
        return img1AssetName;
    }
}
