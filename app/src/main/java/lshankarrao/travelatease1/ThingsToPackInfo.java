package lshankarrao.travelatease1;


public class ThingsToPackInfo {

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    String item;
    Boolean checked;



    public ThingsToPackInfo(String item, Boolean checked) {
        this.item = item;
        this.checked = checked;
    }
}
