package shobaky.studientsecretary;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.MenuItem;

import com.google.android.gms.ads.MobileAds;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements datePicker.onDatePick,dialogNewReminder.remindReady {
    private FragmentManager fm;
    //the current fragment Tag
    private String fragTag;
    public final static int READ_PERMISSION = 703;
    public static int READ_GRANT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        MobileAds.initialize(this,"ca-app-pub-2515357852860145~7824375978");
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        //if alarmReceiver started this activity
        Intent alaramIntent = getIntent();
        if(alaramIntent!= null && alaramIntent.getBundleExtra("bundleAlarm")!= null){
            Bundle args = alaramIntent.getBundleExtra("bundleAlarm");
            alarmDialog aD = alarmDialog.newInstance(args.getParcelable("reminderAlarm"),args.getLong("reminderID"));
            aD.show(getSupportFragmentManager(),"alarmDialog");

        }
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle("P `ermission required").setMessage("We need to be able to access gallery so , we can use pictures you provide").
                        setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
                alertDialog.show();

            }else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},MainActivity.READ_PERMISSION);

            }
        }

        fm = getSupportFragmentManager();
        Fragment home;
        if(savedInstanceState == null) {
             home = todoFragment.newInstance();
             fragTag = todoFragment.class.getName();
        }else{
            //What was the fragment before the orientation change??
           fragTag = savedInstanceState.getString("Fragment Tag");
            home = fm.findFragmentByTag(fragTag);

        }
        //so it displays TODOs once it's launched

        fm.beginTransaction().replace(R.id.parentContainer, home,fragTag).addToBackStack(null).commit();
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()){
                    case R.id.Todo:
                        todoFragment todo = todoFragment.newInstance();
                        fragTag = todo.getClass().getName();
                        fm.beginTransaction().replace(R.id.parentContainer,todo,fragTag).commit();
                        return true;

                    case R.id.Study:
                        studyFragment study = studyFragment.newInstance();
                        fragTag = study.getClass().getName();
                        fm.beginTransaction().replace(R.id.parentContainer,study,fragTag).commit();
                        return true;
                    case R.id.Reminder:
                        remindersFragment reminder = remindersFragment.newInstance();
                        fragTag = reminder.getClass().getName();
                        fm.beginTransaction().replace(R.id.parentContainer,reminder,fragTag).commit();
                        return true;
                    case R.id.Profile:
                        profileFragment profile = profileFragment.newInstance();
                        fragTag = profile.getClass().getName();
                        fm.beginTransaction().replace(R.id.parentContainer,profile,fragTag).commit();
                        return true;
                    case R.id.Settings:
                        AssetManager assetManager = getAssets();
                        Intent settingsIntent = new Intent(MainActivity.this, Settings.class);
                        try {
                            String[] ringNames = assetManager.list("ringtones");
                            settingsIntent.putExtra("ringtones", ringNames);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        startActivity(settingsIntent);
                        return true;
                    default:
                        return true;

                }

            }
        });



    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Save the name of the current fragment
        outState.putString("Fragment Tag",fragTag);

    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case READ_PERMISSION:
                if(grantResults.length>0&&grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    READ_GRANT = 1;
                    SharedPreferences.Editor editor = getSharedPreferences("permission", Context.MODE_PRIVATE).edit();
                    editor.putInt("READ_PERMISSION",READ_GRANT).apply();

                }else{
                    SharedPreferences.Editor editor = getSharedPreferences("permission",Context.MODE_PRIVATE).edit();
                    editor.putInt("READ_PERMISSION",READ_GRANT).apply();
                }
        }
    }

    @Override
    public void datePick(String dT) {
        ((dialogNewReminder)getSupportFragmentManager().findFragmentByTag("new alarm")).dtPick(dT);

    }

    @Override
    public void deliver(reminder Rem) {
        ((remindersFragment) getSupportFragmentManager().findFragmentByTag(remindersFragment.class.getName())).getReminder(Rem);


    }



}
