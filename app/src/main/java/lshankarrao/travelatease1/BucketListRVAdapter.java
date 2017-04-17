package lshankarrao.travelatease1;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;


public class BucketListRVAdapter extends RecyclerView.Adapter<BucketListRVAdapter.BucketListViewHolder>{
    List<BucketListInfo> bucketListInfos;
    Context context;
    int completed = 0;
    TextView statusDisplay;
    public static class BucketListViewHolder extends RecyclerView.ViewHolder {

        CardView cvBL;
        TextView placeName;
        TextView visitTime;
        ImageView themeIcon;
        CheckBox checkBox;
        Button startPlanning;
        RelativeLayout relativeLayoutBLrow;
        TextView themeName;

        BucketListViewHolder(View itemView) {
            super(itemView);
            cvBL = (CardView)itemView.findViewById(R.id.cvBL);
            placeName = (TextView)itemView.findViewById(R.id.textViewBLRowPlaceName);
            visitTime = (TextView)itemView.findViewById(R.id.textViewBLRowBestTimeToVisit);
            themeIcon = (ImageView)itemView.findViewById(R.id.imageViewBLRowthemeIcon);
            startPlanning = (Button) itemView.findViewById(R.id.buttonBLRowStartPlanning);
            checkBox = (CheckBox)itemView.findViewById(R.id.checkBoxBLrow);
            relativeLayoutBLrow = (RelativeLayout) itemView.findViewById(R.id.relativeLayoutBLrow);
            themeName = (TextView)itemView.findViewById(R.id.textViewBLRowThemeName);

        }
    }

    public BucketListRVAdapter(List<BucketListInfo> bucketListInfos, Context context,
                               int completed, TextView status) {
        this.bucketListInfos = bucketListInfos;
        this.context = context;
        this.completed = 0;
        this.statusDisplay = status;
        if(bucketListInfos != null) {
            Log.d("# of buckets", this.bucketListInfos.size() + "");
            printAllContacts(this.bucketListInfos);
        }
    }

    private void printAllContacts(List<BucketListInfo> bucketListInfos) {
        int index = 0;
        for(BucketListInfo bi : bucketListInfos){
            Log.d("#",index+"");
            Log.d("place, time ",bi.placeName+", "+bi.startMonth+ " - " + bi.endMonth);
            index++;
        }
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    int tripId;

    @Override
    public BucketListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.bucket_list_custom_row, parent, false);
        BucketListViewHolder bvh = new BucketListViewHolder(v);
        return bvh;
    }

    @Override
    public void onBindViewHolder(final BucketListViewHolder holder, int i) {

        Log.d("bucket_i = ", i+"");
        tripId = bucketListInfos.get(holder.getAdapterPosition()).tripId;
        Log.d("bktAdpTripId", tripId+"");

        holder.checkBox.setChecked(false);

        holder.relativeLayoutBLrow.setBackgroundColor(0xFFD8F0FF);


        final TripDbHelper tripDbHelper = new TripDbHelper(context);
        if(tripId < 0 || tripDbHelper.tripExists(tripId) == false ) {

            Log.d("tripId", "NotThereOrNeg");
            bucketListInfos.get(holder.getAdapterPosition()).checked = 0;
            holder.relativeLayoutBLrow.setBackgroundColor(0xFFD8F0FF);
            holder.startPlanning.setText("Start Planning");

        }else{
            String timeRange = tripDbHelper.getTimeRangeInfoOfTrip(tripId);

            if(timeRange == null || timeRange.isEmpty() ){
                Log.d("timeRangenullide", timeRange);
                holder.startPlanning.setText("Start Planning");
            }else {
                holder.startPlanning.setText(timeRange);
                if(timeRange.equals("Woohoo Completed !!")){
                    holder.relativeLayoutBLrow.setBackgroundColor(0xFF91FFDE);
                    completed++;
                    statusDisplay.setText(completed + "/" + bucketListInfos.size());
                }
            }
        }


        String themeNm = bucketListInfos.get(i).theme;
        holder.themeName.setText(themeNm);
        switch (bucketListInfos.get(i).theme){
            case "Outdoor Adventure": holder.themeIcon.setImageResource(R.drawable.adventure_icon);break;
            case "Sightseeing": holder.themeIcon.setImageResource(R.drawable.sightseeing_icon); break;
            case "Camping": holder.themeIcon.setImageResource(R.drawable.camping_icon); break;
            case "Business/Educational": holder.themeIcon.setImageResource(R.drawable.business_educational_icon); break;
            case "Indoor Activities": holder.themeIcon.setImageResource(R.drawable.indoor_activity_icon); break;
            case "Art/Culture": holder.themeIcon.setImageResource(R.drawable.art_culture_icon); break;
            case "Water Activities": holder.themeIcon.setImageResource(R.drawable.water_activity_icon); break;
            case "Snow Activities": holder.themeIcon.setImageResource(R.drawable.snow_activity_icon); break;
            default:  holder.themeIcon.setImageResource(R.mipmap.trip); break;
        }

        holder.placeName.setText(bucketListInfos.get(i).placeName);
        Log.d("bobo"+bucketListInfos.get(i).startMonth,bucketListInfos.get(i).endMonth);
        if(bucketListInfos.get(i).startMonth.equals("allYearLong") || bucketListInfos.get(i).endMonth.equals("allYearLong")){
            holder.visitTime.setText("Suitable to Visit all year long");
        }else {
            holder.visitTime.setText("Best time to Visit: " + bucketListInfos.get(i).startMonth + " - " + bucketListInfos.get(i).endMonth);
        }

        holder.startPlanning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                String textOnStatusButton = holder.startPlanning.getText().toString().toUpperCase();
                if(textOnStatusButton.equals("START PLANNING")) {
                    Intent intent = new Intent(context, AddEditTripActivity.class);
                    intent.putExtra("purpose", "bucketList");
                    intent.putExtra("placeAddress", bucketListInfos.get(holder.getAdapterPosition()).placeAddress);
                    intent.putExtra("placeName", bucketListInfos.get(holder.getAdapterPosition()).placeName);
                    context.startActivity(intent);
                }else if(textOnStatusButton.equals("UPCOMING") || textOnStatusButton.equals("ON-GOING")){
                    int bktId = tripDbHelper.getBucketListId(bucketListInfos.get(
                            holder.getAdapterPosition()).placeAddress);
                    BucketListInfo bucketListInfo = tripDbHelper.getBucketListInfo(bktId);
                    tripId = bucketListInfo.tripId;
                    Intent intent = new Intent(context, ViewTripItineraryActivity.class);
                    intent.putExtra("id", tripId);
                    context.startActivity(intent);
                }

            }
        });

        statusDisplay.setText(completed + "/" + bucketListInfos.size());


    }

    @Override
    public int getItemCount() {
        if(bucketListInfos != null){
            return bucketListInfos.size();
        }
        return 0;
    }

    public void onItemDismissed(int position) {
        Log.d("pos onItemDismissed: ", position+"");
        if (bucketListInfos != null) {
            printAllContacts(bucketListInfos);
            int wasChecked = bucketListInfos.get(position).checked;
            bucketListInfos.remove(position);
            Log.d("Removed from cis","");
            Log.d("After Removing","");
            Log.d("","");
            printAllContacts(bucketListInfos);
            if(wasChecked == 1){
                completed--;
            }
            statusDisplay.setText(completed+"/"+bucketListInfos.size());
        }


        notifyItemRemoved(position);
    }

    public boolean onItemMove(int fromPosition, int toPosition) {
        BucketListInfo temp = bucketListInfos.get(fromPosition);
        bucketListInfos.set(fromPosition, bucketListInfos.get(toPosition));
        bucketListInfos.set(toPosition, temp);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

}
