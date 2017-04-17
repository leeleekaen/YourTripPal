package lshankarrao.travelatease1;


public class BucketListInfo {
    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
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

    public String getStartMonth() {
        return startMonth;
    }

    public void setStartMonth(String startMonth) {
        this.startMonth = startMonth;
    }

    public String getEndMonth() {
        return endMonth;
    }

    public void setEndMonth(String endMonth) {
        this.endMonth = endMonth;
    }

    public int getChecked() {
        return checked;
    }

    public void setChecked(int checked) {
        this.checked = checked;
    }
    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }

    int _id;
    String theme;
    String placeName;
    String placeAddress;
    String startMonth;
    String endMonth;
    int tripId;
    int checked;

    BucketListInfo(String theme, String placeName, String placeAddress, String startMonth,
                   String endMonth, int tripId, int checked){

        this.theme = theme;
        this.placeName = placeName;
        this.placeAddress = placeAddress;
        this.startMonth = startMonth;
        this.endMonth = endMonth;
        this.tripId = tripId;
        this.checked = checked;

    }

}
