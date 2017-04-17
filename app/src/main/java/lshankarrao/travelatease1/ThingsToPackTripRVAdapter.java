package lshankarrao.travelatease1;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.HashSet;
import java.util.List;


public class ThingsToPackTripRVAdapter extends RecyclerView.Adapter<ThingsToPackTripRVAdapter.TripPackListViewHolder>  {

    public class TripPackListViewHolder extends RecyclerView.ViewHolder {

        TextView item;
        CheckBox checkBox;
        CardView cvStdPack;

        public TripPackListViewHolder(View itemView) {
            super(itemView);

            cvStdPack = (CardView)itemView.findViewById(R.id.cvPack);
            item = (TextView)itemView.findViewById(R.id.textViewTTProwItem);
            checkBox = (CheckBox)itemView.findViewById(R.id.checkBoxTTProw);
        }
    }

    List<ThingsToPackInfo> itemsList;
    Context context;
    int tripId;
    TripDbHelper tripDbHelper;
    HashSet<String> tripPackingItems;

    ThingsToPackTripRVAdapter(List<ThingsToPackInfo> itemsList, Context context,
                              HashSet<String> tripPackingItems){
        this.itemsList = itemsList;
        this.context = context;

        this.tripPackingItems = tripPackingItems;
        if(itemsList != null) {
            Log.d("# of std items", this.itemsList.size() + "");
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    @Override
    public TripPackListViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.things_to_pack_rv_custom_row, viewGroup, false);
        TripPackListViewHolder tripPachListvh = new TripPackListViewHolder(v);
        return tripPachListvh;
    }

    @Override
    public void onBindViewHolder(final TripPackListViewHolder holder, int position) {
        Log.d("i = ", position+"");
        holder.item.setText(itemsList.get(position).getItem());
        if(itemsList.get(holder.getAdapterPosition()).getChecked() == true){
            Log.d("spsp outside", "true");
            holder.checkBox.setChecked(true);
            holder.item.setPaintFlags(holder.item.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.item.setTypeface(null, Typeface.ITALIC);
        }else{
            holder.checkBox.setChecked(false);
            holder.item.setPaintFlags(holder.item.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.item.setTypeface(null, Typeface.BOLD);
        }
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.checkBox.isChecked() == true){
                    Log.d("spsp", "true");
                    itemsList.get(holder.getAdapterPosition()).checked = true;
                    holder.item.setPaintFlags(holder.item.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.item.setTypeface(null, Typeface.ITALIC);
                }else{
                    Log.d("spsp", "false");
                    itemsList.get(holder.getAdapterPosition()).checked = false;
                    holder.item.setPaintFlags(holder.item.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    holder.item.setTypeface(null, Typeface.BOLD);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if(itemsList != null){
            return itemsList.size();
        }
        return 0;
    }

    public void onItemDismissed(int position) {
        Log.d("pos onItemDismissed: ", position+"");
        if (itemsList != null) {
            String itemRemoved = itemsList.get(position).getItem();
            itemsList.remove(position);
            tripPackingItems.remove(itemRemoved.toUpperCase());
            Log.d("Removed from cis","");
            Log.d("After Removing","");
            Log.d("","");
            TripDbHelper tripDbHelper = new TripDbHelper(context);
            tripDbHelper.toast("Item deleted", context);
        }


        notifyItemRemoved(position);
    }

    public boolean onItemMove(int fromPosition, int toPosition) {
        ThingsToPackInfo temp = itemsList.get(fromPosition);
        itemsList.set(fromPosition, itemsList.get(toPosition));
        itemsList.set(toPosition, temp);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }


}
