package lshankarrao.travelatease1;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;

public class LocationBasedNotifier
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<Status> {

    GoogleApiClient googleApiClient = null;
    ArrayList<Geofence> geofenceArrayList = null;
    PendingIntent geofencingNotifyIntent = null;
    AddressToGeocode addressToGeocode;
    String tripPlace;
    TextView place;
    int tripId;
    Context context;

    LocationBasedNotifier(Context context, int tripId, String tripPlace){
    this.context = context;
        Log.d("trip Place = ",tripPlace);
        addressToGeocode = new AddressToGeocode();
        LatLng latLng = addressToGeocode.AddressToLatLng(tripPlace,
                context);

        if(latLng ==null){
            Toast.makeText(context, "Invalid place", Toast.LENGTH_SHORT);
            return;
        }
        Log.d("LatLng",latLng.latitude+" "+latLng.longitude);

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        googleApiClient.connect();

        geofenceArrayList = new ArrayList<Geofence>();
        geofenceArrayList.add(new Geofence.Builder()
                .setRequestId(tripPlace)
                .setCircularRegion(latLng.latitude, latLng.longitude, 200)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .build());

        if (geofencingNotifyIntent == null) {
            Intent intent = new Intent(context, LocationBasedNotificationHandler.class);
            intent.putExtra("tripPlace", tripPlace);
            intent.putExtra("tripId", tripId);
            geofencingNotifyIntent = PendingIntent.getService(context, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

        }
    }

    private void startGeofencing() {
        Log.d("start ", "geofencing ..."+"");
        Log.d("lux", "start geofencing ...");

        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceArrayList);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.GeofencingApi.addGeofences(
                googleApiClient,
                builder.build(),
                geofencingNotifyIntent
        ).setResultCallback(this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("onConnected()","called");
        startGeofencing();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("onConnectionFailed()", "failure"+"");
    }

    @Override
    public void onResult(@NonNull Status status) {
        Log.d("onResult(): status=" , status.toString());
        googleApiClient.disconnect();
    }
}
