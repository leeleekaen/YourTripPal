package lshankarrao.travelatease1;

import android.database.Cursor;

public class StdPackingItemsInfo {
    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    public int getChecked() {
        return checked;
    }

    public void setChecked(int checked) {
        this.checked = checked;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    String item;
    String category;
    int id;
    int checked;


    StdPackingItemsInfo(String item, String category, int checked) {
        this.item = item;
        this.category = category;
        this.checked = checked;
    }

    public StdPackingItemsInfo(Cursor c) {
        id = c.getInt(c.getColumnIndex("_id"));
        item =        getVal(c, "item");
        category =        getVal(c, "category");
        checked = 1;
    }

    String getVal(Cursor c, String field) {
        if (c.getColumnIndex(field) != -1) return c.getString(c.getColumnIndex(field));
        return null;
    }

}
