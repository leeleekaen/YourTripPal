package lshankarrao.travelatease1;

public class TripInfo {
    protected int id;
    protected String title;
    protected String placeName;
    protected String placeAddress;
    protected long stTimeMillis;
    protected long endTimeMillis;
    protected String themes;
    protected String notes;

    public int getPlanningDone() {
        return planningDone;
    }

    public void setPlanningDone(int planningDone) {
        this.planningDone = planningDone;
    }

    protected int planningDone;

    public TripInfo() {

    }

    public TripInfo(String title, String placeName, String placeAddress, long stTimeMillis,
                    long endTimeMillis, String themes, String notes, int planningDone) {
        //this.id = id;
        this.title = title;
        this.placeName = placeName;
        this.placeAddress = placeAddress;
        this.stTimeMillis= stTimeMillis;
        this.endTimeMillis= endTimeMillis;
        this.themes = themes;
        this.notes = notes;
        this.planningDone = planningDone;

    }
/*
    public int getId() {

        return id;
    }

    public void setId(int id) {
        this.id = id;
    }*/

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlaceName() {
        return placeName;
    }
    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPlaceAddress() {
        return placeAddress;
    }
    public void setPlaceAddress(String placeAddress) {
        this.placeAddress = placeAddress;
    }

    public long getStTimeMillis() {
        return stTimeMillis;
    }
    public void setStTimeMillis(long stTimeMillis) {
        this.stTimeMillis = stTimeMillis;
    }

    public long getEndTimeMillis() {
        return endTimeMillis;
    }
    public void setEndTimeMillis(long endTimeMillis) {
        this.endTimeMillis = endTimeMillis;
    }

    public String getThemes() {
        return themes;
    }

    public void setThemes(String themes) {
        this.themes = themes;
    }
    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }


    @Override
    public String toString(){
        return "";
    }
}
