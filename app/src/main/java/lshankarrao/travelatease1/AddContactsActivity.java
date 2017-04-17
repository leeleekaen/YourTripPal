package lshankarrao.travelatease1;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class AddContactsActivity extends ActionBarActivity2 implements View.OnClickListener {
    int tripId, contactId;
    EditText name, email;
    CheckBox checkBox;
    Button buttonAdd, buttonDone;
    TripDbHelper tripDbHelper;
    Cursor cursor;
    ContactRVAdapter contactRVAdapter;
    List<ContactInfo> contactInfos;
    RecyclerView rv;
    LinearLayout linearLayoutShare, linearLayoutPack;
    List<String> toSendEmailIds;
    String subject = "";
    String detailedItinerary = "";
    String tripTitle ="";
    String tripTimings = "";
    String tripPlace = "";
    String tripNotes;
    String purpose;
    CheckBox checkBoxAFFselectAll;
    TextView textViewAFFGeneralInfo;
    HashSet <String> contactEmailsHashSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_family_friends);

        Intent tripIntent = getIntent();
        if(tripIntent.hasExtra("tripId")) {
            tripId = tripIntent.getIntExtra("tripId", -1);
            if (tripId == -1) {
                Log.d("invalid trip id", "Add Family friends");
                return;
            }
            Log.d("tripId: AFF ", tripId + "");
        }

        name = (EditText) findViewById(R.id.editTextAFFName);
        email = (EditText) findViewById(R.id.editTextAFFEmailId);
        buttonAdd = (Button)findViewById(R.id.buttonAFFAdd);
        buttonDone = (Button)findViewById(R.id.buttonAFFDone);
        textViewAFFGeneralInfo = (TextView)findViewById(R.id.textViewAFFGeneralInfo);

        linearLayoutShare = (LinearLayout)findViewById(R.id.linearLayoutAFFShare);
        linearLayoutPack = (LinearLayout)findViewById(R.id.linearLayoutAFFThingsToPack);
        linearLayoutPack.setOnClickListener(this);


        tripDbHelper = new TripDbHelper(this);
        contactInfos = tripDbHelper.getAllContactInfo();
        contactEmailsHashSet = new HashSet<>();
        if (contactInfos == null){
            contactInfos = new ArrayList<>();
        }else {
            for(ContactInfo c: contactInfos){
                contactEmailsHashSet.add(c.getEmail().toUpperCase());
            }
        }

        checkBoxAFFselectAll = (CheckBox)findViewById(R.id.checkBoxAFFselectAll);
        checkBoxAFFselectAll.setOnClickListener(this);
        if(tripIntent.hasExtra("contactsSelectionStatus")){
            String selectionStatus = tripIntent.getStringExtra("contactsSelectionStatus");
            Log.d("selectionStatus", selectionStatus+" nodappa");
            if (selectionStatus.equals("deselect")) {
                Log.d("insideSel","deselect" );
                makeAllEntries(false);
                checkBoxAFFselectAll.setText("Select all");
            }else if(selectionStatus.equals("select")){
                Log.d("insideSel","select" );
                makeAllEntries(true);
                checkBoxAFFselectAll.setText("Deselect all");
            }
        }

        contactRVAdapter = new ContactRVAdapter(contactInfos,contactEmailsHashSet, this, textViewAFFGeneralInfo);

        rv=(RecyclerView)findViewById(R.id.recyclerViewContacts);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);
        rv.setAdapter(contactRVAdapter);

        final TouchHelperCallback callback = new TouchHelperCallback(contactRVAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(rv);

        purpose = getIntent().getStringExtra("purpose");

        buttonAdd.setOnClickListener(this);
        buttonDone.setOnClickListener(this);

        linearLayoutShare.setOnClickListener(this);



        if(getIntent().hasExtra("purpose") && purpose.equals("share")){
            getSupportActionBar().setTitle("Share Itinerary");

            buttonDone.setVisibility(View.INVISIBLE);
            if(contactInfos.size() > 3){
                textViewAFFGeneralInfo.setText("Scroll below to select contacts to share your itinerary with.");
                checkBoxAFFselectAll.setVisibility(View.VISIBLE);

            }else{
                if(contactInfos.size() > 1){
                textViewAFFGeneralInfo.setText("Select contacts to share your itinerary with.");
                    checkBoxAFFselectAll.setVisibility(View.VISIBLE);

                }else{
                    textViewAFFGeneralInfo.setText("");
                    checkBoxAFFselectAll.setVisibility(View.INVISIBLE);
                }
            }
        }else if(getIntent().hasExtra("purpose") && (purpose.equals("addContact") || purpose.equals("addContactMain"))){
            getSupportActionBar().setTitle("Add Friends");

            linearLayoutShare.setVisibility(View.INVISIBLE);
            linearLayoutPack.setVisibility(View.INVISIBLE);
            if(contactInfos.size() == 0){
                textViewAFFGeneralInfo.setText("No contacts to show.");
            }else{
                textViewAFFGeneralInfo.setText("Contacts view.");
            }
            checkBoxAFFselectAll.setVisibility(View.INVISIBLE);
        }else if(getIntent().hasExtra("purpose") && purpose.equals("sayHello")){
            getSupportActionBar().setTitle("Say Hello");

            buttonDone.setVisibility(View.INVISIBLE);
            linearLayoutPack.setVisibility(View.INVISIBLE);
            if(contactInfos.size() > 3){
                textViewAFFGeneralInfo.setText("Scroll below to select contacts to share your itinerary with.");
                checkBoxAFFselectAll.setVisibility(View.VISIBLE);

            }else{
                if(contactInfos.size() > 1){
                    textViewAFFGeneralInfo.setText("Select contacts to share your itinerary with.");
                    checkBoxAFFselectAll.setVisibility(View.VISIBLE);

                }else{
                    textViewAFFGeneralInfo.setText("");
                    checkBoxAFFselectAll.setVisibility(View.INVISIBLE);
                }
            }
        }



    }

    private void makeAllEntries(boolean b) {
        int val;
        if(b){
            val = 1;
        }else {
            val = 0;
        }
        for(int i = 0; i< contactInfos.size(); i++){
            contactInfos.get(i).checked = val;
        }

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.buttonAFFAdd:
                String contactName = name.getText().toString();
                String contactEmail = email.getText().toString();
                if(contactName == null || contactEmail == null ||
                        contactName.isEmpty() || contactEmail.isEmpty()) {
                    toast("Please enter both name and email fields");
                    return;
                }else if(!isEmailValid(contactEmail)) {
                    toast("Invalid email id");
                    return;
                } else if(contactEmailsHashSet.contains(contactEmail.toUpperCase())) {
                    toast("Email id Already present in your contacts");
                    return;
                }else {
                    int x = 0;
                    ContactInfo contactInfo = new ContactInfo();
                    contactInfo.name = contactName;
                    contactInfo.email = contactEmail;
                    contactInfo.favourite = x;


                    contactInfos.add(contactInfo);
                    contactEmailsHashSet.add(contactEmail.toUpperCase());



                    contactRVAdapter.notifyItemInserted(contactInfos.size()-1);
                    Log.d("addFamilyFriends", "contact id : " + contactId);

                    if( textViewAFFGeneralInfo.getText().toString().equals("No contacts to show.")){
                        textViewAFFGeneralInfo.setText("Contacts view.");
                    }

                    updateDb();

                    if(getIntent().hasExtra("purpose") && (purpose.equals("sayHello") || purpose.equals("share"))){
                        if(contactInfos.size() >1){
                            checkBoxAFFselectAll.setVisibility(View.VISIBLE);
                        }else{
                            checkBoxAFFselectAll.setVisibility(View.INVISIBLE);

                        }
                    }
                    name.setText(null);
                    email.setText(null);
                }
                break;
            case R.id.buttonAFFDone:
                updateDb();
                if(purpose.equals("addContact") || purpose.equals("share")) {
                    Intent intent = new Intent(AddContactsActivity.this, ViewTripItineraryActivity.class);
                    intent.putExtra("id", tripId);
                    startActivity(intent);
                    finish();
                }else if(purpose.equals("addContactMain")){
                    Intent intent = new Intent(AddContactsActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }

                break;

            case R.id.linearLayoutAFFShare:
                updateDb();
                toSendEmailIds = new ArrayList<String>();
                for(ContactInfo ci : contactInfos){
                    Log.d("sakala email ",ci.getEmail());
                    Log.d("checked status", ci.checked+"");
                    if(ci.checked == 1){
                        Log.d("checked ",ci.getEmail());
                        toSendEmailIds.add(ci.getEmail());
                    }
                }
                if(purpose.equals("share")) {
                    String fullItinerary = getItinerary();
                    String packingList = getThingsToPackListForTrip(tripId);
                    shareItinerary(subject, fullItinerary+packingList, tripTitle, tripTimings, tripPlace, tripNotes);
                }else if(purpose.equals("sayHello")){
                    sayHello();
                }
                break;

            case R.id.checkBoxAFFselectAll:
                if(checkBoxAFFselectAll.getText().toString().toLowerCase().equals("deselect all")){
                    getIntent().putExtra("contactsSelectionStatus", "deselect");

                    startActivity(getIntent());
                    finish();
                }else if (checkBoxAFFselectAll.getText().toString().toLowerCase().equals("select all")){
                    getIntent().putExtra("contactsSelectionStatus", "select");

                    startActivity(getIntent());
                    finish();
                }
                break;

            case R.id.linearLayoutAFFThingsToPack:
                updateDb();
               Intent intent = new Intent(AddContactsActivity.this, ThingsToPackActivity.class);
                intent.putExtra("tripId", tripId);
                intent.putExtra("purpose", "review");
                startActivity(intent);
                finish();
                break;

        }

    }
    public void toast(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public void updateDb(){
        tripDbHelper.deleleAllEntriesInTable("contactInfo");
        for(ContactInfo ci: contactInfos){
            tripDbHelper.addContactInfo(ci);
        }
    }

    public String getThingsToPackListForTrip(int tripId){
        TripDbHelper tripDbHelper = new TripDbHelper(this);
        String packingListString = tripDbHelper.getPackingListStringForTrip(tripId);
        JSONObjHandler jsonObjHandler = new JSONObjHandler();
        List<ThingsToPackInfo> thingsToPackInfosDb = new ArrayList<>();
        List<String> tripPackingItems = new ArrayList<>();
        if (packingListString != null) {

            try {
                thingsToPackInfosDb = jsonObjHandler.ConvertStringToList(packingListString);
                if (thingsToPackInfosDb != null) {
                    for (ThingsToPackInfo tp : thingsToPackInfosDb) {
                        tripPackingItems.add(tp.item);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        String tripPackingList = "<br><br>";
        if(tripPackingItems.size() >0){
            tripPackingList = tripPackingList + "<h3>" + "Things to Pack for Trip" + "</h3>" ;
        }
        for(String items: tripPackingItems){
            tripPackingList = tripPackingList + items + "<br>";
        }
        return tripPackingList;
    }

    public String getItinerary(){
        TripDbHelper tripDbHelper = new TripDbHelper(this);
        TripInfo tripInfo = tripDbHelper.getTripInfo(tripId);
        tripTitle = tripInfo.getTitle();
        subject = tripTitle + " Itinerary";
        tripTimings = tripDbHelper.getDateFromMilli(tripInfo.stTimeMillis, "MMM/dd/yyyy")+
                " to " +tripDbHelper.getDateFromMilli(tripInfo.endTimeMillis, "MMM/dd/yyyy");
        tripPlace = tripInfo.getPlaceName() +", "+ tripInfo.getPlaceAddress();
        tripNotes = tripInfo.getNotes() ;

        List<ReservationsInfo> reservationsInfoList = tripDbHelper.getAllReservationsInfo(tripId);
        if(reservationsInfoList != null) {
            for (ReservationsInfo reservationsInfo : reservationsInfoList) {
                if (reservationsInfo.kind.split("_")[1].equals("start")) {
                    ReservationDisplayInfo reservationDisplayInfo;
                    String kind = reservationsInfo.kind.split("_")[0];
                    if(kind.equals("hotel")) {
                        reservationDisplayInfo = new HotelResSpecificDisplayInfo(this);
                    }else if(tripDbHelper.transportSubKinds.contains(reservationsInfo.kind)){
                            reservationDisplayInfo = new TransportResSpecificDisplayInfo(this);
                    }else{
                            reservationDisplayInfo = new EventResSpecificDisplayInfo(this);
                    }
                    detailedItinerary = detailedItinerary + "<br><br>" +
                            reservationDisplayInfo.getReservationItinerary(reservationsInfo) + "<br>";
                }
            }
        }

        return detailedItinerary;
    }

    private void sayHello() {

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("application/image");
        i.putExtra(Intent.EXTRA_EMAIL, toSendEmailIds.toArray(new String[0]));
        i.putExtra(Intent.EXTRA_SUBJECT, "Location Update");
        i.putExtra(Intent.EXTRA_TEXT, "Saying hello from " +getIntent().getStringExtra("placeAddress"));
        if(getIntent().hasExtra("withPic") && getIntent().getStringExtra("withPic").equals("true")) {
            i.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + getIntent().getStringExtra("filepath")));
        }
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getApplicationContext(),
                    "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }



    public void shareItinerary(String subject, String detailedItinerary, String tripTitle,
                               String tripTimings, String tripPlace, String tripNotes) {

        String[] emails = toSendEmailIds.toArray(new String[0]);
        for(String s: emails){
            Log.d("email nodu: ",s);
        }

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, toSendEmailIds.toArray(new String[0]));
        i.putExtra(Intent.EXTRA_SUBJECT,subject );
        i.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(new StringBuilder()
                .append("<h1>" + tripTitle + "</h1>")
                .append("<b>" + tripPlace + "</b>")
                .append("<p><b>" + tripTimings + "</b></p>")
                .append("Notes: " + tripNotes + "")
                .append(detailedItinerary)
                .toString()));
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getApplicationContext(),
                    "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        updateDb();
        if(purpose.equals("addContact") || purpose.equals("share") || purpose.equals("sayHello")) {
            Intent intent = new Intent(AddContactsActivity.this, ViewTripItineraryActivity.class);
            intent.putExtra("id", tripId);
            startActivity(intent);
            finish();
        }else if(purpose.equals("addContactMain")){
            Intent intent = new Intent(AddContactsActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
