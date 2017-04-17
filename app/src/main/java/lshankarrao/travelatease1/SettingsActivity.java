package lshankarrao.travelatease1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;


public class SettingsActivity extends ActionBarActivity2 {
    CheckBox disablePlanRem;
    CheckBox disableLocRem;
    TripDbHelper tripDbHelper;
    SettingsInfo settingsInfo;
    TextView textViewUninstall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setTitle("Settings");

        disablePlanRem = (CheckBox) findViewById(R.id.checkBoxSettingsDisablePlanRem);
        disableLocRem = (CheckBox) findViewById(R.id.checkBoxSettingsDisableLocRem);
        textViewUninstall = (TextView)findViewById(R.id.textViewSettingsUninstall);

        tripDbHelper = new TripDbHelper(this);
        settingsInfo = tripDbHelper.getSettingsInfo();

        if (settingsInfo != null) {

            Log.d("disableLocRem",settingsInfo.disableLocRem+"");
            Log.d("disablePlanRem",settingsInfo.disablePlanRem+"");

            if (settingsInfo.disableLocRem == 1) {
                disableLocRem.setChecked(true);
            } else {
                disableLocRem.setChecked(false);
            }

            if (settingsInfo.disablePlanRem == 1) {
                disablePlanRem.setChecked(true);
            } else {
                disablePlanRem.setChecked(false);
            }
        }else {
            settingsInfo = new SettingsInfo(0,0);
        }

        disableLocRem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (disableLocRem.isChecked() == true) {
                    settingsInfo.disableLocRem = 1;
                } else {
                    settingsInfo.disableLocRem = 0;
                }
                tripDbHelper.updateSettings(settingsInfo);
            }
        });

        disablePlanRem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (disablePlanRem.isChecked() == true) {
                    settingsInfo.disablePlanRem = 1;
                } else {
                    settingsInfo.disablePlanRem = 0;
                }
                tripDbHelper.updateSettings(settingsInfo);
            }
        });

        textViewUninstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri packageURI = Uri.parse("package:"+SettingsActivity.class.getPackage().getName());
                Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
                startActivity(uninstallIntent);
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Log.d("disableLocRemBack",settingsInfo.disableLocRem+"");
        Log.d("disablePlanRemBack",settingsInfo.disablePlanRem+"");

        tripDbHelper.deleleAllEntriesInTable("settings");
        tripDbHelper.addSettings(settingsInfo);

        SettingsInfo updatedOne = tripDbHelper.getSettingsInfo();
        Log.d("disableLocRemUpd",updatedOne.disableLocRem+"");
        Log.d("disablePlanRemUpd",updatedOne.disablePlanRem+"");

        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        finish();
        startActivity(intent);
    }
}
