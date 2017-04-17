package lshankarrao.travelatease1;

import android.database.Cursor;

public class ReservationsInfo {
    protected int id;
    protected String kind = null;
    protected String titleOrName = null;
    protected long stTimeMillis;
    protected String startPlaceName = null;
    protected String startPlaceAddress  = null;
    protected long endTimeMillis;
    protected String endPlaceName = null;
    protected String endPlaceAddress = null;
    protected String confNo = null;
    protected String notes = null;
    protected String attachmentFilePath = null;
    protected int tripId;

    public ReservationsInfo() {

    }

    String getVal(Cursor c, String field) {
        if (c.getColumnIndex(field) != -1) return c.getString(c.getColumnIndex(field));
        return null;
    }


    public ReservationsInfo(Cursor c) {
        id = c.getInt(c.getColumnIndex("_id"));
        kind =        getVal(c, "kind");
        titleOrName =        getVal(c, "titleOrName");
        stTimeMillis =        c.getLong(c.getColumnIndex("stTimeMillis"));
        startPlaceName =        getVal(c, "startPlaceName");
        startPlaceAddress =        getVal(c, "startPlaceAddress");
        endTimeMillis =        c.getLong(c.getColumnIndex("endTimeMillis"));
        endPlaceName =        getVal(c, "endPlaceName");
        endPlaceAddress =        getVal(c, "endPlaceAddress");
        confNo =        getVal(c, "confNo");
        notes =        getVal(c, "notes");
        attachmentFilePath =        getVal(c, "attachmentFilePath");
        tripId =        c.getInt(c.getColumnIndex("tripId"));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getTitleOrName() {
        return titleOrName;
    }

    public void setTitleOrName(String titleOrName) {
        this.titleOrName = titleOrName;
    }

    public long getStTimeMillis() {
        return stTimeMillis;
    }

    public void setStTimeMillis(long stTimeMillis) {
        this.stTimeMillis = stTimeMillis;
    }

    public String getStartPlaceName() {
        return startPlaceName;
    }

    public void setStartPlaceName(String startPlaceName) {
        this.startPlaceName = startPlaceName;
    }

    public String getStartPlaceAddress() {
        return startPlaceAddress;
    }

    public void setStartPlaceAddress(String startPlaceAddress) {
        this.startPlaceAddress = startPlaceAddress;
    }

    public long getEndTimeMillis() {
        return endTimeMillis;
    }

    public void setEndTimeMillis(long endTimeMillis) {
        this.endTimeMillis = endTimeMillis;
    }

    public String getEndPlaceName() {
        return endPlaceName;
    }

    public void setEndPlaceName(String endPlaceName) {
        this.endPlaceName = endPlaceName;
    }

    public String getEndPlaceAddress() {
        return endPlaceAddress;
    }

    public void setEndPlaceAddress(String endPlaceAddress) {
        this.endPlaceAddress = endPlaceAddress;
    }

    public String getConfNo() {
        return confNo;
    }

    public void setConfNo(String confNo) {
        this.confNo = confNo;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getAttachmentFilePath() {
        return attachmentFilePath;
    }

    public void setAttachmentFilePath(String attachmentFilePath) {
        this.attachmentFilePath = attachmentFilePath;
    }

    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }

}
