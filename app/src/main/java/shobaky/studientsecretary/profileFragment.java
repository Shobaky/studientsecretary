package shobaky.studientsecretary;


import android.app.AlertDialog;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Templates;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class profileFragment extends Fragment {

    public static final String SCORE_PREF = "scorePref";
    public static final String LEVEL = "lvl";
    private  CircleImageView profileImg;
    private AdView mAd;


    public profileFragment() {
        // Required empty public constructor
    }

    public static profileFragment newInstance() {
        profileFragment fragment = new profileFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        setHasOptionsMenu(true);
        SharedPreferences ourPref = getActivity().getSharedPreferences(Settings.settingsPref,Context.MODE_PRIVATE);
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        mAd = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAd.loadAd(adRequest);
        profileImg = view.findViewById(R.id.profileImage);
        TextView userName = view.findViewById(R.id.username);
        String name = ourPref.getString(preferenceFragment.namePref,"");
        userName.setText(name);
        String imgURL = ourPref.getString(preferenceFragment.imagePref,"");
        if(URLUtil.isValidUrl(imgURL)){
            Uri imgUri = Uri.parse(imgURL);
            try {
                Bitmap profileBitMap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),imgUri);
                profileImg.setImageBitmap(profileBitMap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        final ImageView settings = view.findViewById(R.id.settingsProf);
        if(settings != null) {
            settings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AssetManager assetManager = getActivity().getAssets();
                    Intent settingsIntent = new Intent(getActivity(), Settings.class);
                    try {
                        String[] ringNames = assetManager.list("ringtones");
                        settingsIntent.putExtra("ringtones", ringNames);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    startActivity(settingsIntent);

                }
            });
        }
        final PieChart pC = view.findViewById(R.id.myChart);
        ProgressBar scoreProf = view.findViewById(R.id.scoreProg);
        SharedPreferences profPref
                = getActivity().getSharedPreferences(profileFragment.SCORE_PREF,Context.MODE_PRIVATE);
        float prog = profPref.getFloat(finishStudyFragment.SCORE,0);
        int level = profPref.getInt(LEVEL,1);

        if((prog/scoreProf.getMax())%scoreProf.getMax() >= level){
            level++;
            profPref.edit().putInt(LEVEL,level+1).apply();
            profPref.edit().putFloat(finishStudyFragment.SCORE,0).apply();
            prog = 0;
        }

        TextView lvlText = view.findViewById(R.id.level);
        TextView scrPrecent = view.findViewById(R.id.scorePrecent);


        scoreProf.setProgress((int)prog);
        scrPrecent.setText(String.valueOf(prog).concat("%"));
        lvlText.setText("lvl : ".concat(String.valueOf(level)));


        RoomDatabase db = Room.databaseBuilder(getActivity(),materialRoom.class,"materials").build();
        final SharedPreferences studyData = getActivity().getSharedPreferences(finishStudyFragment.STUDY_PREF,Context.MODE_PRIVATE);
        final long total = studyData.getLong(finishStudyFragment.TOTAL_TIME,0);
        pC.setUsePercentValues(true);
        syncMat mats = new syncMat(((materialRoom) db).getMatDao(), false, new asyncRespone() {
            @Override
            public void processFinish(List<Material> materials) {
                List<PieEntry> pE = new ArrayList<>();
                ArrayList<String> labels = new ArrayList<>();
                for(Material m : materials){
                    labels.add(m.getName());
                    long time = studyData.getLong(m.getName(),0);

                    if(time != 0) {
                        pE.add(new PieEntry(((float) time / total) ,m.getName()));

                    }
                }
                PieDataSet PDS = new PieDataSet(pE,"Study Intervals");
                PDS.setValueFormatter(new PercentFormatter());
                PDS.setColors(ColorTemplate.JOYFUL_COLORS);
                pC.setData(new PieData(PDS));
                pC.invalidate();
            }

            @Override
            public void processRemindFinish(List<reminder> reminders) {

            }
        },getActivity());
        if(total != 0) {
            mats.execute();
        }

        return view;
    }




}
