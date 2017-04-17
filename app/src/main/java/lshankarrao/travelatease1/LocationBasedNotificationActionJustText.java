package lshankarrao.travelatease1;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;

public class LocationBasedNotificationActionJustText extends ActionBarActivity {
    int tripId;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent temp = getIntent();
        String s = temp.getStringExtra("tripPlace");
        Log.d("nodu ", s);
        tripId = getIntent().getIntExtra("tripId", -1);
        if(tripId == -1){
            Log.d("LocNotAction","Err");
            Intent goHome = new Intent(LocationBasedNotificationActionJustText.this, MainActivity.class);
            startActivity(goHome);
        }
        maybeSendEmail(getIntent().getStringExtra("tripPlace"));

    }

    private void maybeSendEmail(final String place) {

        Intent sayHello = new Intent(LocationBasedNotificationActionJustText.this, AddContactsActivity.class);
        sayHello.putExtra("tripId",tripId);
        sayHello.putExtra("purpose","sayHello");
        sayHello.putExtra("placeAddress",place );
        sayHello.putExtra("withPic", "false");
        startActivity(sayHello);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (menu != null) {

            menu.findItem(R.id.action_addtrip).setVisible(false);
        }
        return true;
    }
}
