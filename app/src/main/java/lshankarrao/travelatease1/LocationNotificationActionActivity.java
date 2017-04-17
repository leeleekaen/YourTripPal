package lshankarrao.travelatease1;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LocationNotificationActionActivity extends ActionBarActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    Bitmap photoBitmap;
    boolean photoTaken = false;
    CheckBox cb;
    int tripId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_notification_action);

        Intent captureImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (captureImageIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(captureImageIntent, REQUEST_IMAGE_CAPTURE);
        }

        cb = (CheckBox) findViewById(R.id.checkBoxLNAtakePic);


        tripId = getIntent().getIntExtra("tripId", -1);
        if(tripId == -1){
            Log.d("LocNotAction","Err");
            Intent goHome = new Intent(LocationNotificationActionActivity.this, MainActivity.class);
            startActivity(goHome);
        }

        Button send = (Button) findViewById(R.id.buttonLNAsend);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cb.isChecked()){
                    Intent captureImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (captureImageIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(captureImageIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }
                else{
                    maybeSendEmail(getIntent().getStringExtra("tripPlace"));
                }
            }
        });

        Button itinerary = (Button) findViewById(R.id.buttonLNAgoToItinerary);
        itinerary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tripId = getIntent().getIntExtra("tripId", -1);
                if (tripId > 0) {
                    Intent goToItinerary = new Intent(LocationNotificationActionActivity.this, ViewTripItineraryActivity.class);
                    goToItinerary.putExtra("id", tripId);
                    startActivity(goToItinerary);
                    finish();
                } else {
                    Intent goHome = new Intent(LocationNotificationActionActivity.this, MainActivity.class);
                    startActivity(goHome);
                    finish();
                }
            }
        });

    }

    private void maybeSendEmail(final String place) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter your friends email ids below(comma separated)" + "\nYour Location updates will be sent to them.");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String emailAddrs = input.getText().toString();

                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, emailAddrs.split(","));
                i.putExtra(Intent.EXTRA_SUBJECT, "Location Update");
                i.putExtra(Intent.EXTRA_TEXT, "Reached Safely. Saying hello from " + place);
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getApplicationContext(),
                            "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void maybeSendEmailWithPhoto(final String place, final String filepath){

        Intent sayHello = new Intent(LocationNotificationActionActivity.this, AddContactsActivity.class);
        sayHello.putExtra("tripId",tripId);
        sayHello.putExtra("purpose","sayHello");
        sayHello.putExtra("placeAddress",place );
        sayHello.putExtra("filepath", filepath);
        sayHello.putExtra("withPic", "true");
        startActivity(sayHello);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            photoBitmap = (Bitmap) data.getExtras().get("data");
        }
        if(photoBitmap != null){
            String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
            String imgFileName = "imageTrip" + ".jpeg";
            String directoryPathImage = saveImageToExternalStorage(photoBitmap, imgFileName)+"/"+imgFileName;
            Log.d("image path: ", directoryPathImage);
            maybeSendEmailWithPhoto(getIntent().getStringExtra("tripPlace"), directoryPathImage);
        }
        else {
            Log.d("photo bitmap null", "");
        }
    }

    private String saveImageToExternalStorage(Bitmap finalBitmap, String fname) {
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        File myDir = new File(root);
        myDir.mkdirs();

        File file = new File(myDir, fname);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return root ;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (menu != null) {

            menu.findItem(R.id.action_addtrip).setVisible(false);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(LocationNotificationActionActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}

