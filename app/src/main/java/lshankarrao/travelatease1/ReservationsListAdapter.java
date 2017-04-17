package lshankarrao.travelatease1;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;


public class ReservationsListAdapter extends CursorAdapter {
    TripDbHelper mTripDb;
    public ReservationsListAdapter(Context context, Cursor c, int flags) {

        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.reservation_list_custom_row,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        mTripDb = new TripDbHelper(context);
        Log.d("Adapter #ofRes=  ", cursor.getCount() + "");
        Log.d("reservation id ", cursor.getInt(cursor.getColumnIndex("_id"))+"");
        String kind = cursor.getString(cursor.getColumnIndex("kind"));
        Log.d("Adapter Res Kind  ", kind);

        ReservationDisplayInfo info;
        if(mTripDb.hotelSubKinds.contains(kind) == true){
            Log.d("bande hotel ", "");
            info = new HotelResSpecificDisplayInfo(context);
            info.commonDisplayInfo(cursor,view);
        }else if(mTripDb.transportSubKinds.contains(kind)== true){
            info = new TransportResSpecificDisplayInfo(context);
            info.commonDisplayInfo(cursor,view);
        }else if(mTripDb.eventResSubKinds.contains(kind.split("_")[0])== true ){
            info = new EventResSpecificDisplayInfo(context);
            info.commonDisplayInfo(cursor,view);
        }

    }
}
