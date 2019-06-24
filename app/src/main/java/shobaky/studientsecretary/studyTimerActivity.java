package shobaky.studientsecretary;


import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Chronometer;
import android.widget.TextView;


import java.util.Calendar;

import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

//This activity is open from studyDialog
public class studyTimerActivity extends AppCompatActivity implements finishStudyFragment.OnFragmentInteractionListener {
    private Chronometer Timer;

    private String pages;
    private String material;
    private int diff;

    private Material currentMat;
    private RoomDatabase db;
    private boolean notesBool;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_timer);
        final TextView oldNotes = findViewById(R.id.oldNotes);
        db = Room.databaseBuilder(this,materialRoom.class,"materials").build();
        SharedPreferences sp = getSharedPreferences(Settings.settingsPref, Context.MODE_PRIVATE);
        notesBool = sp.getBoolean(preferenceFragment.notesPref,true);
        if(notesBool) {
            syncMat loader = new syncMat(((materialRoom) db).getMatDao(), false, new asyncRespone() {
                @Override
                public void processFinish(List<Material> materials) {
                    for (Material m : materials) {
                        if (m.getName().equals(material)) {
                            currentMat = m;
                            if (m.getNotes() != null && !(m.getNotes().isEmpty())) {
                                oldNotes.setVisibility(View.VISIBLE);
                                oldNotes.setText("Don't forget \n".concat(m.getNotes()));
                            }
                            materialAdapter.deleteSync deleter = new materialAdapter.deleteSync(((materialRoom) db).getMatDao());
                            deleter.execute(m.getName());
                        }
                    }
                }

                @Override
                public void processRemindFinish(List<reminder> reminders) {

                }
            });
            loader.execute();
        }
        Bundle args = getIntent().getExtras();
        if(args != null) {
            pages = args.getString("Pages");
            material = args.getString("Name");
            diff = args.getInt("Diff");
        }
        Timer = findViewById(R.id.myTimer);
        Timer.setBase(SystemClock.elapsedRealtime());
        Timer.start();


    }



    public void finisher(View view) {
        Timer.stop();
        finishStudyFragment fsf = finishStudyFragment.newInstance(material,SystemClock.elapsedRealtime() - Timer.getBase(),diff,Integer.valueOf(pages));
        fsf.show(getSupportFragmentManager(),"finish him");
    }

    @Override
    public void onFragmentInteraction(final String notes) {
        if(notesBool){
        if(currentMat!=null&&!(notes.isEmpty())) {
            currentMat.setNotes(notes);
        }
            syncMat inserter = new syncMat(((materialRoom) db).getMatDao(), true, new asyncRespone() {
                @Override
                public void processFinish(List<Material> materials) {

                }

                @Override
                public void processRemindFinish(List<reminder> reminders) {

                }
            });
            inserter.execute(currentMat);


    }}

    @Override
    public void onBackPressed() {

    }
}
