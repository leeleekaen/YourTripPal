package lshankarrao.travelatease1;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class EventResSpecificDisplayInfo extends ReservationDisplayInfo {
    TripDbHelper tripDbHelper = new TripDbHelper(context);

    String pos4, pos5, pos6, img1AssetName;

    public EventResSpecificDisplayInfo(Context context) {
        super(context);
    }

    public void SpecificResBasedDisplayInfo(Context context, Cursor cursor, View view) {

        this.pos5 = cursor.getString(cursor.getColumnIndex("titleOrName"));
        this.pos6 = "";
        String temp  = cursor.getString(cursor.getColumnIndex("kind"));
        String endPlace = cursor.getString(cursor.getColumnIndex("endPlaceName"));
        String kind;
        String[] kindInfo = temp.split("_");
        kind = kindInfo[0];
        this.pos6 = kind;

        this.pos4 = cursor.getString(cursor.getColumnIndex("startPlaceName"));

        ((TextView)view.findViewById(R.id.pos4)).setText(this.pos4);
        ((TextView)view.findViewById(R.id.pos5)).setText(this.pos5);
        ((TextView)view.findViewById(R.id.pos6)).setText(this.pos6);

        switch (kind){
            case "shows": ((ImageView) view.findViewById(R.id.img1)).setImageResource
                    (R.drawable.shows_icon); break;
            case "restaurant": ((ImageView) view.findViewById(R.id.img1)).setImageResource
                    (R.drawable.restaurant_icon); break;
            case "cruise": ((ImageView) view.findViewById(R.id.img1)).setImageResource
                    (R.drawable.cruise_icon); break;
            case "adventure" : ((ImageView) view.findViewById(R.id.img1)).setImageResource
                    (R.drawable.adventure_icon); break;
            case "sightseeing": ((ImageView) view.findViewById(R.id.img1)).setImageResource
                    (R.drawable.sightseeing_icon); break;
            case "water activity": ((ImageView) view.findViewById(R.id.img1)).setImageResource
                    (R.drawable.water_activity_icon); break;
            case "snow activity": ((ImageView) view.findViewById(R.id.img1)).setImageResource
                    (R.drawable.snow_activity_icon); break;
            case "art/culture" : ((ImageView) view.findViewById(R.id.img1)).setImageResource
                    (R.drawable.art_culture_icon); break;
            case "educational/official": ((ImageView) view.findViewById(R.id.img1)).setImageResource
                    (R.drawable.business_educational_icon); break;
            case "party": ((ImageView) view.findViewById(R.id.img1)).setImageResource
                    (R.drawable.party_icon); break;
            case "other": ((ImageView) view.findViewById(R.id.img1)).setImageResource
                    (R.drawable.general_event_icon); break;

        }
    }

    public void viewReservationDisplay(int resId){
        ReservationsInfo resInfo = tripDbHelper.getReservation(resId);

        titleOrPlaceName = resInfo.titleOrName;
        placeNameAddress = resInfo.startPlaceName +"\n"+resInfo.startPlaceAddress;
        startMsg = "Starts";
        endMsg = "Ends";

        if(resInfo.endPlaceName != null){
            String resTimelineInfo = resInfo.kind.split("_")[1];

            startPlaceNameAndKind = resInfo.startPlaceName;

            startPlaceAddress = resInfo.startPlaceAddress;

            endPlaceNameAndKind = resInfo.endPlaceName;

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
    }

    public String getReservationItinerary(final ReservationsInfo info){
        String tempStWord = "Starts: ";
        String tempEndWord = "Ends: ";
        String notes = info.getNotes();
        String confNo = info.getConfNo();
        if(notes == null){
            notes = "-";
        }
        if(confNo == null){
            confNo = "-";
        }
        if(info.endPlaceName == null){
            String eventResevationDetails = info.getTitleOrName()+ "<br>" +
                    info.getStartPlaceName() + "<br>" +
                    info.getStartPlaceAddress() + "<br> " +
                    tempStWord + tripDbHelper.getDateAndTimeInStdFormat(info.getStTimeMillis())+"<br> "+
                    tempEndWord + tripDbHelper.getDateAndTimeInStdFormat(info.getEndTimeMillis())+"<br> "+
                    "Confirmation #: " + confNo +"<br> "+
                    "Notes: " + notes;
            return eventResevationDetails;

        }else {

        String eventResevationDetails = "Event Reservation Details " +"<br>"+
                info.getTitleOrName()+ "<br>" +
                tripDbHelper.getTimeFromMilli(info.getStTimeMillis(),"hh:mm a")+ "    "+ tempStWord + info.getStartPlaceName() + "<br>" +
                tripDbHelper.getDateFromMilli(info.getStTimeMillis(),"MMM dd")+ "        "+info.getStartPlaceAddress() + "<br> " +
                tripDbHelper.getTimeFromMilli(info.getEndTimeMillis(),"hh:mm a")+ "    "+ tempEndWord + info.getEndPlaceName() + "<br>" +
                tripDbHelper.getDateFromMilli(info.getEndTimeMillis(),"MMM dd")+ "        "+info.getEndPlaceName() + "<br> " +
                "Confirmation #: " + confNo +"<br> "+
                "Notes: " + notes;
        return eventResevationDetails;  }
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
