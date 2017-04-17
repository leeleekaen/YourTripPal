package lshankarrao.travelatease1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class ToDoListActivity extends ActionBarActivity2 {

    ImageButton imageButtonTTPSortLexicographically;
    TripDbHelper tripDbHelper;
    int tripId;
    RecyclerView rv;
    ToDoListRVAdapter toDoListRVAdapter;
    ImageButton imageButtonAddItem;
    EditText editTextItem;
    List<ToDoInfo> toDoListInfosDb;
    JSONObjHandler jsonObjHandler;
    CheckBox checkBoxPlanningDone;
    TextView textViewPlanningStatus;
    TripInfo tripInfo;
    int planStatus;
    HashSet<String> toDoListItems;
    LinearLayout linearLayoutTodoShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list);
        getSupportActionBar().setTitle("Trip To-do List");

        imageButtonTTPSortLexicographically = (ImageButton) findViewById(R.id.imageButtonToDoSortLexicographically);
        imageButtonTTPSortLexicographically.setVisibility(View.INVISIBLE);

        linearLayoutTodoShare = (LinearLayout)findViewById(R.id.linearLayoutTodoShare);
        linearLayoutTodoShare.setVisibility(View.INVISIBLE);

        if (getIntent().hasExtra("tripId")) {
            tripId = getIntent().getIntExtra("tripId", -1);
            if (tripId == -1) {
                Log.d("invalid trip id", "TTP activity");
                return;
            }
        }
        tripDbHelper = new TripDbHelper(this);
        toDoListItems = new HashSet<>();
        String toDoListString = tripDbHelper.getToDoListForTrip(tripId);
        toDoListInfosDb = new ArrayList<>();

        linearLayoutTodoShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareItineraryIntent = new Intent(ToDoListActivity.this, AddContactsActivity.class);
                shareItineraryIntent.putExtra("tripId",tripId);
                shareItineraryIntent.putExtra("purpose","share");
                startActivity(shareItineraryIntent);
                finish();
            }
        });

        jsonObjHandler = new JSONObjHandler();

        if (toDoListString != null) {

            try {
                toDoListInfosDb = jsonObjHandler.ConvertStringToListToDo(toDoListString);
                if (toDoListInfosDb != null) {
                    for (ToDoInfo tp : toDoListInfosDb) {
                        toDoListItems.add(tp.toDoItem.toUpperCase());
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        toDoListRVAdapter = new ToDoListRVAdapter(toDoListInfosDb, this,toDoListItems );

        rv = (RecyclerView) findViewById(R.id.recyclerViewToDoList);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(false);
        rv.setAdapter(toDoListRVAdapter);

        final ToDoTouchHelperCallBack callbackToDo = new ToDoTouchHelperCallBack(toDoListRVAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callbackToDo);
        touchHelper.attachToRecyclerView(rv);

        editTextItem = (EditText) findViewById(R.id.editTextToDoItem);
        editTextItem.setInputType(InputType.TYPE_CLASS_TEXT);
        imageButtonAddItem = (ImageButton) findViewById(R.id.buttonToDoAddItem);
        imageButtonAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String itemName = editTextItem.getText().toString();
                if(itemName == null || itemName.isEmpty()){
                    toast("Please enter a valid item");
                    return;
                }
                if (!toDoListItems.contains(itemName.toUpperCase())) {
                    ToDoInfo t = new ToDoInfo(itemName, false);
                    toDoListInfosDb.add(t);
                    toDoListItems.add(itemName.toUpperCase());
                    updateOrAddListToDb();
                    finish();
                    startActivity(getIntent());
                    toast("Added");
                } else {
                    toast("Item already present in the list");
                }
            }
        });

        textViewPlanningStatus = (TextView)findViewById(R.id.textViewPlanningStatus);
        checkBoxPlanningDone = (CheckBox) findViewById(R.id.checkboxToDoPlanningDone);
        tripInfo = tripDbHelper.getTripInfo(tripId);
        planStatus = tripInfo.planningDone;
        Log.d("planStatus", planStatus+"");
        if(planStatus == 1){
            checkBoxPlanningDone.setChecked(true);
            textViewPlanningStatus.setText("Planning Status: Completed");
            textViewPlanningStatus.setBackgroundColor(0xFF91FFDE);
            linearLayoutTodoShare.setVisibility(View.VISIBLE);

        }else {
            checkBoxPlanningDone.setChecked(false);
            textViewPlanningStatus.setText("Planning Status: In Progress");
            textViewPlanningStatus.setBackgroundColor(0xFFd1d0d3);
            linearLayoutTodoShare.setVisibility(View.INVISIBLE);

        }
        checkBoxPlanningDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkBoxPlanningDone.isChecked() == true){
                    planStatus = 1;
                    textViewPlanningStatus.setText("Planning Status: Completed");
                    textViewPlanningStatus.setBackgroundColor(0xFF91FFDE);

                    linearLayoutTodoShare.setVisibility(View.VISIBLE);
                }else {
                    planStatus = 0;
                    textViewPlanningStatus.setText("Planning Status: In Progress");
                    textViewPlanningStatus.setBackgroundColor(0xFFd1d0d3);
                    linearLayoutTodoShare.setVisibility(View.INVISIBLE);

                }
            }
        });
    }

    private void toast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        updateOrAddListToDb();
        Intent intent = new Intent(ToDoListActivity.this, ViewTripItineraryActivity.class);
        intent.putExtra("id", tripId);
        startActivity(intent);
        finish();
    }

    private void updateOrAddListToDb() {
        Log.d("planStatusInUpdateFn", planStatus+"");
        tripInfo.planningDone = planStatus;
        tripDbHelper.updateTripInfoSanitized(tripInfo,tripId);
        Log.d("planningDoneInUpdateFn", tripInfo.planningDone+"");
        Log.d("tripId", tripId+"");
        Log.d("title", tripInfo.getTitle());

        try {

            String toDoItemsString = jsonObjHandler.ConvertToDoListToString(toDoListInfosDb);
            if (toDoItemsString != null) {
                if (tripDbHelper.entryExistsInToDoListTable(tripId) == true) {
                    int id = (int) tripDbHelper.getToDoListIdForTrip(tripId);
                    tripDbHelper.deleteEntry("todoList", id);
                    tripDbHelper.addToDoItemsForTrip(toDoItemsString, tripId);
                } else {
                    tripDbHelper.addToDoItemsForTrip(toDoItemsString, tripId);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_things_to_pack_activity, menu);
        MenuItem item = menu.findItem(R.id.action_deleteList);
        item.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_setReminder){
            Intent intent = new Intent(ToDoListActivity.this, SetTripPlanningReminderActivity.class);
            intent.putExtra("purpose", "toDoReminder");
            intent.putExtra("id", tripId);
            startActivity(intent);
            finish();
        }
        return true;
    }

}
