package lshankarrao.travelatease1;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class AddressToGeocode {

    public LatLng AddressToLatLng(String userGivenAddress, Context context){
        Log.d("hi hello ","how r u");
        Geocoder coder = new Geocoder(context);
        List<Address> addresses;
        LatLng latlong = null;

        try {
            addresses = coder.getFromLocationName(userGivenAddress, 1);
            if (addresses == null) {
                return null;
            }
            Address location = addresses.get(0);
            location.getLatitude();
            location.getLongitude();

            latlong = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return latlong;

    }
}
