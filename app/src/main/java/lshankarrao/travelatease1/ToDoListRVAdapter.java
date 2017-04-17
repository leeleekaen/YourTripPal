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
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashSet;
import java.util.List;


public class ToDoListRVAdapter extends RecyclerView.Adapter<ToDoListRVAdapter.ToDoListViewHolder>   {
    public class ToDoListViewHolder extends RecyclerView.ViewHolder {

        TextView todoItem;
        CheckBox checkBox;
        CardView cvStdToDo;
        ImageView delete;

        public ToDoListViewHolder(View itemView) {
            super(itemView);

            cvStdToDo = (CardView)itemView.findViewById(R.id.cvTodo);
            todoItem = (TextView)itemView.findViewById(R.id.textViewTodoRowItem);
            checkBox = (CheckBox)itemView.findViewById(R.id.checkBoxTodoRow);
            delete = (ImageView)itemView.findViewById(R.id.imageButtonTodoDelete);
        }
    }

    List<ToDoInfo> todoItemsList;
    Context context;
    HashSet<String> toDoListItems;

    ToDoListRVAdapter(List<ToDoInfo> todoItemsList, Context context, HashSet<String> toDoListItems){
        this.todoItemsList = todoItemsList;
        this.context = context;
        this.toDoListItems = toDoListItems;
        if(todoItemsList != null) {
            Log.d("# of std todoItems", this.todoItemsList.size() + "");
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    @Override
    public ToDoListViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.todo_rv_custom_row, viewGroup, false);
        ToDoListViewHolder tripPachListvh = new ToDoListViewHolder(v);
        return tripPachListvh;
    }

    @Override
    public void onBindViewHolder(final ToDoListViewHolder holder, int position) {
        Log.d("i = ", position+"");
        holder.todoItem.setText(todoItemsList.get(position).getToDoItem());
        if(todoItemsList.get(holder.getAdapterPosition()).getChecked() == true){
            Log.d("spsp outside", "true");
            holder.checkBox.setChecked(true);
            holder.todoItem.setPaintFlags(holder.todoItem.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.todoItem.setTypeface(null, Typeface.ITALIC);
        }else{
            holder.checkBox.setChecked(false);
            holder.todoItem.setPaintFlags(holder.todoItem.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.todoItem.setTypeface(null, Typeface.BOLD);
        }
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.checkBox.isChecked() == true){
                    Log.d("spsp", "true");
                    todoItemsList.get(holder.getAdapterPosition()).checked = true;
                    holder.todoItem.setPaintFlags(holder.todoItem.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.todoItem.setTypeface(null, Typeface.ITALIC);
                }else{
                    Log.d("spsp", "false");
                    todoItemsList.get(holder.getAdapterPosition()).checked = false;
                    holder.todoItem.setPaintFlags(holder.todoItem.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    holder.todoItem.setTypeface(null, Typeface.BOLD);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        if(todoItemsList != null){
            return todoItemsList.size();
        }
        return 0;
    }

    public void onItemDismissed(int position) {
        Log.d("pos onItemDismissed: ", position+"");
        if (todoItemsList != null) {
            String itemRemoved = todoItemsList.get(position).getToDoItem();
            todoItemsList.remove(position);
            toDoListItems.remove(itemRemoved.toUpperCase());
            Log.d("Removed from cis","");
            Log.d("After Removing","");
            Log.d("","");
            TripDbHelper tripDbHelper = new TripDbHelper(context);
            tripDbHelper.toast("Item deleted", context);
        }


        notifyItemRemoved(position);
    }

    public boolean onItemMove(int fromPosition, int toPosition) {
        ToDoInfo temp = todoItemsList.get(fromPosition);
        todoItemsList.set(fromPosition, todoItemsList.get(toPosition));
        todoItemsList.set(toPosition, temp);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

}
