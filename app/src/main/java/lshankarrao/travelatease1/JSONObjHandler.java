package lshankarrao.travelatease1;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class JSONObjHandler {

    public List<ThingsToPackInfo> ConvertStringToList(String packList) throws JSONException {
        List<ThingsToPackInfo> ItemsList = new ArrayList<>();
        JSONObject newObj = new JSONObject(packList);
        JSONArray newJsonArray = newObj.getJSONArray("itemsList");
        for(int i=0; i< newJsonArray.length(); i++){
            String item = newJsonArray.getJSONObject(i).getString("item");
            Boolean checked = newJsonArray.getJSONObject(i).getBoolean("checked");
            Log.d(item,"  packed="+checked);
            ThingsToPackInfo thingsToPackInfo = new ThingsToPackInfo(item,checked);
            ItemsList.add(thingsToPackInfo);
        }
        return ItemsList;
    }

    public String ConvertListToString(List<ThingsToPackInfo> thingsToPackInfos) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for(ThingsToPackInfo ttpI: thingsToPackInfos){
            JSONObject obj = new JSONObject();
            obj.put("item", ttpI.getItem());
            obj.put("checked", ttpI.getChecked());
            jsonArray.put(obj);
        }
        JSONObject full = new JSONObject();
        full.put("itemsList",jsonArray );

        String packList = full.toString();
        return packList;
    }

    public List<ToDoInfo> ConvertStringToListToDo(String packList) throws JSONException {
        List<ToDoInfo> ItemsList = new ArrayList<>();
        JSONObject newObj = new JSONObject(packList);
        JSONArray newJsonArray = newObj.getJSONArray("itemsList");
        for(int i=0; i< newJsonArray.length(); i++){
            String item = newJsonArray.getJSONObject(i).getString("item");
            Boolean checked = newJsonArray.getJSONObject(i).getBoolean("checked");
            Log.d(item,"  packed="+checked);
            ToDoInfo toDoInfo = new ToDoInfo(item,checked);
            ItemsList.add(toDoInfo);
        }
        return ItemsList;
    }

    public String ConvertToDoListToString(List<ToDoInfo> toDoInfos) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for(ToDoInfo ttpI: toDoInfos){
            JSONObject obj = new JSONObject();
            obj.put("item", ttpI.getToDoItem());
            obj.put("checked", ttpI.getChecked());
            jsonArray.put(obj);
        }
        JSONObject full = new JSONObject();
        full.put("itemsList",jsonArray );

        String todoList = full.toString();
        return todoList;
    }

}
