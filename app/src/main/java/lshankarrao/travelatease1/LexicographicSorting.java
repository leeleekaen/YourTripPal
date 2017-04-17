package lshankarrao.travelatease1;

import android.util.Log;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class LexicographicSorting {

    String[] items;
    Boolean[] checkedStatus;
    int size;
    int stPtr, endPtr;

    List<ThingsToPackInfo> thingsToPackInfos;

    LexicographicSorting(List<ThingsToPackInfo> thingsToPackInfos) {

        this.size = thingsToPackInfos.size();
        items = new String[size];
        checkedStatus = new Boolean[size];
        int i = 0;
        for (ThingsToPackInfo t : thingsToPackInfos) {
            items[i] = t.getItem();
            checkedStatus[i] = t.getChecked();
            Log.d(i + " ", items[i] + " " + checkedStatus[i]);
            i++;
        }
        this.stPtr = 0;
        this.endPtr = size - 1;
        this.thingsToPackInfos = thingsToPackInfos;
    }

    public List<ThingsToPackInfo> getLexicographicallySortedList() {

        Collections.sort(thingsToPackInfos, new Comparator<ThingsToPackInfo>() {
            @Override
            public int compare(ThingsToPackInfo thingsToPackInfo, ThingsToPackInfo t1) {
                return thingsToPackInfo.getItem().toLowerCase().compareTo(t1.getItem().toLowerCase());
            }
        });
        return thingsToPackInfos;
    }
}

