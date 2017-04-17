package lshankarrao.travelatease1;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;


public class LocationBasedNotificationHandler extends IntentService implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<Status> {

    public LocationBasedNotificationHandler() {
        super("LocationBasedNotificationHandler");
    }

    GoogleApiClient googleApiClient = null;

    public final int VISIBILITY_PRIVATE = 0;

    private static int notificationId = 2345;
    String tripPlace;
    int tripId;
    String requestId;

    public LocationBasedNotificationHandler(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        googleApiClient.connect();
        tripPlace = intent.getStringExtra("tripPlace");
        tripId = intent.getIntExtra("tripId", -1);
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.e("jsun", "We have an error in geofencing intent.");
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

        Geofence target = triggeringGeofences.get(0);
        requestId = target.getRequestId();

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            Log.d("Entering geofencing of ", requestId);
            sendNotification(target);
        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            Log.d("Travel@Ease", "Leaving geofencing of " + requestId);
        } else {
            Log.w("Attention", "unknown/unexpected geofencing event : " + geofenceTransition);
        }

    }

    private void sendNotification(Geofence target) {
        Intent resultIntent = new Intent(LocationBasedNotificationHandler.this,
                LocationNotificationActionActivity.class);
        resultIntent.putExtra("tripPlace", tripPlace);
        resultIntent.putExtra("tripId", tripId);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(getApplicationContext(), notificationId,
                        resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent rIntent = new Intent(LocationBasedNotificationHandler.this,
                LocationBasedNotificationActionJustText.class);
        rIntent.putExtra("tripPlace", tripPlace);
        rIntent.putExtra("tripId", tripId);
        PendingIntent rPendingIntent =
                PendingIntent.getActivity(getApplicationContext(), notificationId,
                        rIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        android.support.v4.app.NotificationCompat.Builder mBuilder =
                new android.support.v4.app.NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.ic_location)
                        .setColor(0xFF080D41)
                        .setContentTitle("Location Update...!")
                        .setContentText("Say hello to Friends/Family ")
                        .setContentIntent(resultPendingIntent)
                        .setVisibility(VISIBILITY_PRIVATE)
                        .setAutoCancel(true)
                .addAction(R.drawable.camera_icon, "With Picture", resultPendingIntent)
                .addAction(R.drawable.edit_icon, "Just Text", rPendingIntent)
                .setSound(uri)
                .setLargeIcon(BitmapFactory.decodeResource(
                                getApplicationContext().getResources(), R.mipmap.ic_launcher));

        NotificationManager mNotificationManager =
                (NotificationManager) getApplicationContext().getSystemService(
                        Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(notificationId, mBuilder.build());
        notificationId++;
    }

    private void stopGeofencing() {
        Log.d("stop ", "geofencing ..."+"");

        List<String> geofenceReqIDs = new ArrayList<>();
        geofenceReqIDs.add(requestId);
        LocationServices.GeofencingApi.removeGeofences(googleApiClient, geofenceReqIDs).setResultCallback(this);

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("onConnected()", "called"+"");
        Log.d("connect aaythu","");
        stopGeofencing();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("onConnectionFailed()", "called"+"");
    }

    @Override
    public void onResult(@NonNull Status status) {
        Log.i("onResult()status=" , status.toString()+"");
        googleApiClient.disconnect();
    }
}
