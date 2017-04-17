package lshankarrao.travelatease1;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;


public class StdPackRVAdapter extends RecyclerView.Adapter<StdPackRVAdapter.StdPackListViewHolder> {

    public static class StdPackListViewHolder extends RecyclerView.ViewHolder {

        CardView cvStdPack;
        TextView item;
        CheckBox checkBox;

        StdPackListViewHolder(View itemView) {
            super(itemView);
            cvStdPack = (CardView)itemView.findViewById(R.id.cvStdPack);
            item = (TextView)itemView.findViewById(R.id.textViewStdTTProwItem);
            checkBox = (CheckBox)itemView.findViewById(R.id.checkBoxStdTTProw);
        }

    }

    List<StdPackingItemsInfo> itemsList;
    Context context;
    CheckBox checkboxSelectAllItems;
    Boolean[] itemsSelectionStatus;


    StdPackRVAdapter(List<StdPackingItemsInfo> itemsList, Context context,
                     Boolean[] itemsSelectionStatus, CheckBox checkboxSelectAllItems){
        this.itemsList = itemsList;
        this.context = context;
        this.checkboxSelectAllItems = checkboxSelectAllItems;
        this.itemsSelectionStatus = itemsSelectionStatus;
        if(itemsList != null) {
            Log.d("# of std items", this.itemsList.size() + "");
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public StdPackListViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.std_things_to_pack_rv_custom_row, viewGroup, false);
        StdPackListViewHolder stdPachListvh = new StdPackListViewHolder(v);
        return stdPachListvh;
    }

    @Override
    public void onBindViewHolder(final StdPackListViewHolder holder, int i) {
        Log.d("i = ", i+"");
        holder.item.setText(itemsList.get(i).getItem());


        if(itemsList.get(i).checked == 1){
            holder.checkBox.setChecked(true);
        }else if(itemsList.get(i).checked == 0){
            holder.checkBox.setChecked(false);
        }

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.checkBox.isChecked() == true){
                    Log.d("spsp", "true");
                    itemsList.get(holder.getAdapterPosition()).checked = 1;
                }else{
                    Log.d("spsp", "false");
                    itemsList.get(holder.getAdapterPosition()).checked = 0;
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


}
