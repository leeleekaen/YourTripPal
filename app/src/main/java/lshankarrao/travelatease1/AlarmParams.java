package lshankarrao.travelatease1;

import android.content.Context;

public class AlarmParams {
    String purpose;
    int tripId;
    String userMessage;
    long reminderTime;
    long startTimeMilli;
    String startAddress;
    Context context;
    int slotNum;

    public AlarmParams(String purpose, int tripId, String userMessage, long reminderTime,
                       long startTimeMilli, String startAddress, Context context, int slotNum){
        this.purpose = purpose;
        this.tripId = tripId;
        this.userMessage = userMessage;
        this.reminderTime = reminderTime;
        this.startTimeMilli = startTimeMilli;
        this.startAddress = startAddress;
        this.context = context;
        this.slotNum = slotNum;
    }

}
