package lshankarrao.travelatease1;

import android.content.Context;
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


public class ContactRVAdapter extends RecyclerView.Adapter<ContactRVAdapter.PersonViewHolder> {

    public static class PersonViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView personName;
        TextView personEmail;
        ImageView delete;
        CheckBox favourite;
        CheckBox checkBox;

        PersonViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            personName = (TextView)itemView.findViewById(R.id.textViewAffrowName);
            personEmail = (TextView)itemView.findViewById(R.id.textViewAffrowEmail);
            //delete = (ImageView)itemView.findViewById(R.id.imageViewDelete);
            favourite = (CheckBox)itemView.findViewById(R.id.checkBoxAFFfavourite);
            checkBox = (CheckBox)itemView.findViewById(R.id.checkBoxAFFrow);

        }
    }

    List<ContactInfo> contactInfos;
    Context context;
    int contactId;
    HashSet<String> contactEmailsHashSet;
    TextView textViewAFFGeneralInfo;

    ContactRVAdapter(List<ContactInfo> contactInfos, HashSet<String> contactEmailsHashSet,
                     Context context, TextView textViewAFFGeneralInfo){
        this.contactInfos = contactInfos;
        this.context = context;
        this.textViewAFFGeneralInfo = textViewAFFGeneralInfo;
        this.contactEmailsHashSet = contactEmailsHashSet;
        if(contactInfos != null) {
            Log.d("# of Contacts", this.contactInfos.size() + "");
            printAllContacts(this.contactInfos);
        }
    }

    private void printAllContacts(List<ContactInfo> contactInfos) {
        int index = 0;
        for(ContactInfo ci : contactInfos){
            Log.d("#",index+"");
            Log.d("name, email ",ci.name+", "+ci.email);
            index++;
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.contact_rv_custom_row, viewGroup, false);
        PersonViewHolder pvh = new PersonViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(final PersonViewHolder personViewHolder, int i) {
        contactId = i;
        Log.d("i = ", i+"");


        if(contactInfos.get(i).checked == 1){
            personViewHolder.checkBox.setChecked(true);
        }else if(contactInfos.get(i).checked == 0){
            personViewHolder.checkBox.setChecked(false);
        }
        personViewHolder.personName.setText(contactInfos.get(i).name);
        personViewHolder.personEmail.setText(contactInfos.get(i).email);
        personViewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(personViewHolder.checkBox.isChecked() == true){
                    Log.d("cccc", "true");
                    contactInfos.get(personViewHolder.getAdapterPosition()).checked = 1;
                }else{
                    Log.d("cccc", "false");
                    contactInfos.get(personViewHolder.getAdapterPosition()).checked = 0;
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        if(contactInfos != null){
            return contactInfos.size();
        }
        return 0;
    }

    public void onItemDismissed(int position) {
        Log.d("pos onItemDismissed: ", position+"");
        if (contactInfos != null) {
            printAllContacts(contactInfos);
            String email = contactInfos.get(position).email;
            contactInfos.remove(position);
            contactEmailsHashSet.remove(email.toUpperCase());
            if(contactInfos.size() == 0){
                textViewAFFGeneralInfo.setText("No contacts to show.");
            }
            TripDbHelper tripDbHelper = new TripDbHelper(context);
            tripDbHelper.toast("Contact deleted", context);
            printAllContacts(contactInfos);

        }


        notifyItemRemoved(position);
    }

    public boolean onItemMove(int fromPosition, int toPosition) {
        ContactInfo temp = contactInfos.get(fromPosition);
        contactInfos.set(fromPosition, contactInfos.get(toPosition));
        contactInfos.set(toPosition, temp);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }
}


