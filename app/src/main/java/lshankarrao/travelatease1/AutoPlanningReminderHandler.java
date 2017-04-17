package lshankarrao.travelatease1;

import android.content.Context;
import android.util.Log;

import java.util.Calendar;


public class AutoPlanningReminderHandler {

    AutoPlanningReminderHandler(int tripId, long stTimeMillis, Context context, int recvdSlotNum) {

        AutoPlanRemParams autoPlanRemParams = getTripReminderDateTime(stTimeMillis, recvdSlotNum);
        Log.d("namonamonamo", "");
        Log.d("AET planRemTime=", autoPlanRemParams.reminderTime + "");
        if (autoPlanRemParams.slotNum > recvdSlotNum && autoPlanRemParams.reminderTime > -1) {

            AlarmParams autoReminderAlarmParams = new AlarmParams("autoPlanningReminder", tripId, null,
                    autoPlanRemParams.reminderTime, stTimeMillis, null, context, autoPlanRemParams.slotNum);

            SetTripPlanningReminderActivity planningReminder = new SetTripPlanningReminderActivity();
            planningReminder.setAlarm(autoReminderAlarmParams);
        }
    }

    public AutoPlanRemParams getTripReminderDateTime(long stTimeMillis, int recvdSlotNum) {
        int one = 1;
        int two = 2;
        int three = 3;
        int four = 4;
        int fourtyEight = 48;
        int slotNum = 0;

        Log.d("recvdSlotNum", recvdSlotNum + "");
        Calendar tripDateTime = Calendar.getInstance();
        tripDateTime.setTimeInMillis(stTimeMillis);
        long tripDateTimeInMillis = tripDateTime.getTimeInMillis();
        Log.d("tripDateTime", tripDateTime.getTimeInMillis() + "");

        Calendar currentDateTime = Calendar.getInstance();
        long currentTimeInMilli = currentDateTime.getTimeInMillis();
        Log.d("currentTimeInMilli", currentTimeInMilli + "");

        Calendar reminder3months = Calendar.getInstance();
        reminder3months.setTimeInMillis(tripDateTimeInMillis);
        Log.d("rtimesJustAfterGiven", reminder3months.getTimeInMillis() + "--> 1");

        reminder3months.add(Calendar.MONTH, -three);
        Log.d("rtimes", reminder3months.getTimeInMillis() + "--> 1");

        Calendar reminder2months = Calendar.getInstance();
        reminder2months.setTimeInMillis(tripDateTimeInMillis);
        reminder2months.add(Calendar.MONTH, -two);

        Calendar reminder1month = Calendar.getInstance();
        reminder1month.setTimeInMillis(tripDateTimeInMillis);
        reminder1month.add(Calendar.MONTH, -one);

        Calendar reminder4weeks = Calendar.getInstance();
        reminder4weeks.setTimeInMillis(tripDateTimeInMillis);
        reminder4weeks.add(Calendar.WEEK_OF_MONTH, -four);

        Calendar reminder3weeks = Calendar.getInstance();
        reminder3weeks.setTimeInMillis(tripDateTimeInMillis);
        reminder3weeks.add(Calendar.WEEK_OF_MONTH, -three);

        Calendar reminder2weeks = Calendar.getInstance();
        reminder2weeks.setTimeInMillis(tripDateTimeInMillis);
        reminder2weeks.add(Calendar.WEEK_OF_MONTH, -two);


        Calendar reminder1week = Calendar.getInstance();
        reminder1week.setTimeInMillis(tripDateTimeInMillis);
        reminder1week.add(Calendar.WEEK_OF_MONTH, -one);

        Calendar reminder3days = Calendar.getInstance();
        reminder3days.setTimeInMillis(tripDateTimeInMillis);
        reminder3days.add(Calendar.DAY_OF_MONTH, -three);

        Calendar reminder48HrShareItinerary = Calendar.getInstance();
        reminder48HrShareItinerary.setTimeInMillis(tripDateTimeInMillis);
        reminder48HrShareItinerary.add(Calendar.HOUR_OF_DAY, -fourtyEight);

        Log.d("rtimes", reminder2months.getTimeInMillis() + "--> 2");
        Log.d("rtimes", reminder1month.getTimeInMillis() + "--> 3");
        Log.d("rtimes", reminder4weeks.getTimeInMillis() + "--> 4");
        Log.d("rtimes", reminder3weeks.getTimeInMillis() + "--> 5");
        Log.d("rtimes", reminder2weeks.getTimeInMillis() + "--> 6");
        Log.d("rtimes", reminder1week.getTimeInMillis() + "--> 7");
        Log.d("rtimes", reminder3days.getTimeInMillis() + "--> 8");
        Log.d("rtimes", reminder48HrShareItinerary.getTimeInMillis() + "--> 9");

        Calendar reminderCalendar = Calendar.getInstance();

        if (recvdSlotNum == 0) {

            if (currentTimeInMilli < reminder3months.getTimeInMillis()) {
                Log.d("rtInside", reminder3months.getTimeInMillis() + "--> 1");
                slotNum = 1;
                reminderCalendar.setTimeInMillis(reminder3months.getTimeInMillis());
            } else if (currentTimeInMilli < reminder2months.getTimeInMillis()) {
                Log.d("rtInside", reminder2months.getTimeInMillis() + "--> 2");
                slotNum = 2;
                reminderCalendar.setTimeInMillis(reminder2months.getTimeInMillis());
            } else if (currentTimeInMilli < reminder1month.getTimeInMillis()) {
                Log.d("rtInside", reminder1month.getTimeInMillis() + "--> 3");
                slotNum = 3;
                reminderCalendar.setTimeInMillis(reminder1month.getTimeInMillis());
            } else if (currentTimeInMilli < reminder4weeks.getTimeInMillis()) {
                Log.d("rtInside", reminder4weeks.getTimeInMillis() + "--> 4");
                slotNum = 4;
                reminderCalendar.setTimeInMillis(reminder4weeks.getTimeInMillis());
            } else if (currentTimeInMilli < reminder3weeks.getTimeInMillis()) {
                Log.d("rtInside", reminder3weeks.getTimeInMillis() + "--> 5");
                slotNum = 5;
                reminderCalendar.setTimeInMillis(reminder3weeks.getTimeInMillis());
            } else if (currentTimeInMilli < reminder2weeks.getTimeInMillis()) {
                Log.d("rtInside", reminder2weeks.getTimeInMillis() + "--> 6");
                slotNum = 6;
                reminderCalendar.setTimeInMillis(reminder2weeks.getTimeInMillis());
            } else if (currentTimeInMilli < reminder1week.getTimeInMillis()) {
                Log.d("rtInside", reminder1week.getTimeInMillis() + "--> 7");
                slotNum = 7;
                reminderCalendar.setTimeInMillis(reminder1week.getTimeInMillis());
            } else if (currentTimeInMilli < reminder3days.getTimeInMillis()) {
                Log.d("rtInside", reminder3days.getTimeInMillis() + "--> 8");
                slotNum = 8;
                reminderCalendar.setTimeInMillis(reminder3days.getTimeInMillis());

            } else if (currentTimeInMilli < reminder48HrShareItinerary.getTimeInMillis()) {
                Log.d("rtInside", reminder48HrShareItinerary.getTimeInMillis() + "--> 9");
                slotNum = 9;
                reminderCalendar.setTimeInMillis(reminder48HrShareItinerary.getTimeInMillis());
            } else {
                reminderCalendar.setTimeInMillis(tripDateTime.getTimeInMillis());
            }

        } else {
            slotNum = recvdSlotNum + 1;
            switch (slotNum) {
                case 1:
                    reminderCalendar.setTimeInMillis(reminder3months.getTimeInMillis());
                    break;
                case 2:
                    reminderCalendar.setTimeInMillis(reminder2months.getTimeInMillis());
                    break;
                case 3:
                    reminderCalendar.setTimeInMillis(reminder1month.getTimeInMillis());
                    break;
                case 4:
                    reminderCalendar.setTimeInMillis(reminder4weeks.getTimeInMillis());
                    break;
                case 5:
                    reminderCalendar.setTimeInMillis(reminder3weeks.getTimeInMillis());
                    break;
                case 6:
                    reminderCalendar.setTimeInMillis(reminder2weeks.getTimeInMillis());
                    break;
                case 7:
                    reminderCalendar.setTimeInMillis(reminder1week.getTimeInMillis());
                    break;
                case 8:
                    reminderCalendar.setTimeInMillis(reminder3days.getTimeInMillis());
                    break;
                case 9:
                    reminderCalendar.setTimeInMillis(reminder48HrShareItinerary.getTimeInMillis());
                    break;
                default:
                    reminderCalendar.setTimeInMillis(tripDateTime.getTimeInMillis());
                    break;
            }
        }

        if (reminderCalendar.getTimeInMillis() == tripDateTime.getTimeInMillis()) {
            AutoPlanRemParams autoPlanRemParams = new AutoPlanRemParams(-1, slotNum);
            return autoPlanRemParams;

        } else {
            reminderCalendar.set(Calendar.HOUR_OF_DAY, 19);
            reminderCalendar.set(Calendar.MINUTE, 0);
            Log.d("sendingSlotNum", slotNum + "");
            AutoPlanRemParams autoPlanRemParams = new AutoPlanRemParams(reminderCalendar.getTimeInMillis(), slotNum);
            return autoPlanRemParams;
        }
    }

    public class AutoPlanRemParams {
        long reminderTime;
        int slotNum;

        AutoPlanRemParams(long reminderTime, int slotNum) {
            this.reminderTime = reminderTime;
            this.slotNum = slotNum;
        }
    }

}
