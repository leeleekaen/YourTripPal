package lshankarrao.travelatease1;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class AlarmReceiver extends BroadcastReceiver {
    Cursor cursor;
    public final int VISIBILITY_PRIVATE = 0;
    int tripId;
    TripInfo tripInfo;


    private static int notificationId = 1111;

    @Override
    public void onReceive(Context context, Intent intent) {
        int requestCode = 1253;

        Log.d("Rcvd Req.Sending noti", "hi");


        tripId = intent.getIntExtra("tripId", -1);
        Log.d("AR alli rx tripID: ", tripId + "");
        if (tripId == -1) {
            Log.d("trip id invalid. val: ", tripId + "");
            return;
        }
        String purpose = intent.getStringExtra("purpose");

        tripInfo = getTripInfoForTripId(tripId, context);

        boolean sendNotification = false;
        sendNotification = getApproval(intent, context);
        Log.d("ARsendNoti", sendNotification+"");
        if (sendNotification) {
            if (purpose.equals("autoLocationReminder")) {
                locationBasedNotificationOption(context);
                Log.d("AR","inLocReminder");
            } else {
                Log.d("AR","inPlanReminder");
                Intent resultIntent = new Intent(context, ViewTripItineraryActivity.class);
                resultIntent.putExtra("id", intent.getIntExtra("tripId", -1));

                PendingIntent resultPendingIntent =
                        PendingIntent.getActivity(context, notificationId, resultIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT);

                Calendar calendar = Calendar.getInstance();
                long when = calendar.getTimeInMillis();

                String title = tripInfo.getTitle();
                long tripStTime = tripInfo.getStTimeMillis();
                String startDate = getDate(tripStTime, context);
                List<String> contentText = new ArrayList<>();
                String contentTitle = "Reminder to Plan your Trip";
                if(purpose.equals("autoPlanningReminder")) {
                    contentText.add(title);
                    contentText.add("Starts " + startDate);
                }else if(purpose.equals("userSetReminder")){
                    contentTitle ="Reminder for your Trip";
                    contentText.add(intent.getStringExtra("userMessage"));
                    contentText.add(title);
                }

                Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.reminder_icon)
                                .setColor(0xFF080D41)
                                .setContentTitle(contentTitle)
                                .setContentText(contentText.get(0))
                                .setContentIntent(resultPendingIntent)
                                .setWhen(when)
                                .setVisibility(VISIBILITY_PRIVATE)
                                .setAutoCancel(true)
                                .setSound(uri)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));


                mBuilder.setStyle(createBigContent(contentTitle,contentText ));

                NotificationManager mNotificationManager =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(notificationId, mBuilder.build());
                notificationId++;

                if (purpose.equals("autoPlanningReminder")) {
                    int justFinishedSlotNo = intent.getIntExtra("remTimeSlotNum", -1);
                    if (justFinishedSlotNo == -1) {
                        Log.d("Slot no = -1", "error");
                        return;
                    }

                    AutoPlanningReminderHandler autoPlanningReminderHandler = new AutoPlanningReminderHandler(
                            tripId, tripInfo.stTimeMillis, context, justFinishedSlotNo);
                }
            }
        }else{
            Log.d("AR_sendNotiFalse","");

        }
    }



    private String getDate(long tripStTime, Context context) {
        TripDbHelper tripDb = new TripDbHelper(context);
       return tripDb.getDateFromMilli(tripStTime, "EEE, MMM d, ''yy");
    }

    private TripInfo getTripInfoForTripId(int tripId, Context context) {
        TripDbHelper tripDbHelper = new TripDbHelper(context);
        TripInfo tripInfo = tripDbHelper.getTripInfo(tripId);
        return tripInfo;
    }

    private boolean getApproval(Intent intent, Context context) {
        TripDbHelper tripDb = new TripDbHelper(context);

        String purpose = intent.getStringExtra("purpose");
        Log.d("ARpurpose",purpose);
        int tripId = intent.getIntExtra("tripId", -1);
        Log.d("AR alli rx tripID: ", tripId + "");
        if (tripId == -1) {
            Log.d("trip id invalid. val: ", tripId + "");
            return false;
        }



        TripInfo tripInfo = tripDb.getTripInfo(tripId);
        if(tripInfo == null){
            return false;
        }
        Log.d("ARPlanningDone",tripInfo.planningDone+"");

        SettingsInfo settingsInfo = tripDb.getSettingsInfo();
        Log.d("ARdisableLocRem",settingsInfo.disableLocRem+"");
        Log.d("ARdisablePlanRem",settingsInfo.disablePlanRem+"");

        switch (purpose) {
            case "autoLocationReminder":
                long originalStTimeMillisLocRem = intent.getLongExtra("stTimeMilli", -1);
                if (originalStTimeMillisLocRem == -1) {
                    Log.d("Alarm Rcv: rem loc Err", "rcv stTime -ve");
                    return false;
                }
                if(intent.getStringExtra("placeAddress") == null){
                    Log.d("plAdd","null");
                }
                String originalAddress = intent.getStringExtra("placeAddress");

                if (originalAddress.equals(tripInfo.getPlaceAddress())){
                        if(originalStTimeMillisLocRem == tripInfo.getStTimeMillis()){
                            if(settingsInfo.disableLocRem == 0) {
                                return true;
                            }
                        }
                }
                break;
            case "autoPlanningReminder":
                long originalStTimeMillisPlanRem = intent.getLongExtra("stTimeMilli", -1);
                if (originalStTimeMillisPlanRem == -1) {
                    Log.d("Alarm Rcv: rem loc Err", "rcv stTime -ve");
                    return false;
                }
                if (originalStTimeMillisPlanRem == tripInfo.getStTimeMillis() &&
                        tripInfo.planningDone == 0  &&  settingsInfo.disablePlanRem == 0) {
                    return true;
                }

                break;
            case "userSetReminder": Log.d("AR_approval", "userRem");
                return true;
        }
        return false;
    }


    NotificationCompat.InboxStyle createBigContent(String contentTitle, List<String> contentText) {
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(contentTitle);
            inboxStyle.addLine(contentText.get(0));
        inboxStyle.setSummaryText(contentText.get(1));
        return inboxStyle;
    }

    private void locationBasedNotificationOption(Context context) {
        LocationBasedNotifier location = new LocationBasedNotifier(context, tripId, tripInfo.getPlaceAddress());
    }
}
