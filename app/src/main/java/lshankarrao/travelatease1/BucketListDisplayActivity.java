package lshankarrao.travelatease1;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class BucketListDisplayActivity extends  ActionBarActivity2{
    TripDbHelper tripDbHelper;
    List<BucketListInfo> bucketListInfos;
    BucketListRVAdapter bucketListRVAdapter;
    RecyclerView rv;
    ProgressBar progressBar;
    TextView textViewStatus;
    int completed = 0;
    int noOfBuckets;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bucket_list_display);

        getSupportActionBar().setTitle("Bucket List");

        tripDbHelper = new TripDbHelper(this);
        bucketListInfos = tripDbHelper.getAllBucketListInfo();
        if (bucketListInfos == null){
            bucketListInfos = new ArrayList<BucketListInfo>();
        }

        textViewStatus = (TextView)findViewById(R.id.textViewBLStatus);
        for(BucketListInfo bli: bucketListInfos){
            if(bli.checked == 1){
                completed++;
            }
        }

        textViewStatus.setText(completed+"/"+bucketListInfos.size());

        bucketListRVAdapter = new BucketListRVAdapter(bucketListInfos,this, completed, textViewStatus);


        rv=(RecyclerView)findViewById(R.id.recyclerViewBucketListItems);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);
        rv.setAdapter(bucketListRVAdapter);

        final BucketListTouchHelperCallback callback = new BucketListTouchHelperCallback(bucketListRVAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(rv);

        if(getIntent().hasExtra("itemAdded") && getIntent().getBooleanExtra("ItemAdded", false) == true){
            bucketListRVAdapter.notifyItemInserted(bucketListInfos.size()-1);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabBucketListAdd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAndReAddToDb();
                Intent intent = new Intent(BucketListDisplayActivity.this, AddBucketListItem.class);
                startActivity(intent);
            }
        });
        fab.setBackgroundColor(Color.parseColor("#FFFF0004"));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        deleteAndReAddToDb();
        Intent intent = new Intent(BucketListDisplayActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void deleteAndReAddToDb(){
        tripDbHelper.deleleAllEntriesInTable("bucketListInfo");
        for(BucketListInfo bli: bucketListInfos){
            tripDbHelper.addBucketListInfo(bli);
        }
    }
}
