package shobaky.studientsecretary;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import com.warkiz.widget.IndicatorSeekBar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


public class finishStudyFragment extends DialogFragment {

    private static final String ARG_PARAM1 = "internal";
    private static final String ARG_PARAM2 = "studyMaterial";
    private static final String ARG_PARAM3 = "materialDifficulty";
    private static final String ARG_PARAM4 = "materialPages";

    public static final String TOTAL_TIME = "totalTime" ;
    public static final String SCORE = "score";
    public static final String STUDY_PREF = "studyPref";


    private long mInternal;
    private OnFragmentInteractionListener mListener;
    private String mMaterial;
    private float rate;
    private int mDiff;
    private int mPage;


    public finishStudyFragment() {
        // Required empty public constructor
    }


    public static finishStudyFragment newInstance(String material,long internal,int difficulty,int pages) {
        finishStudyFragment fragment = new finishStudyFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PARAM1, internal);
        args.putString(ARG_PARAM2,material);
        args.putInt(ARG_PARAM3,difficulty);
        args.putInt(ARG_PARAM4,pages);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE,R.style.finishStudyDialog);
        if (getArguments() != null) {
            mInternal = getArguments().getLong(ARG_PARAM1);
            mMaterial = getArguments().getString(ARG_PARAM2);
            mDiff = getArguments().getInt(ARG_PARAM3);
            mPage = getArguments().getInt(ARG_PARAM4);

        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_finish_study, container, false);
        final EditText notes = view.findViewById(R.id.finish_study_notes);
        Button finish = view.findViewById(R.id.finish_study_button);
        final SharedPreferences pref = getActivity().getSharedPreferences(finishStudyFragment.STUDY_PREF,Context.MODE_PRIVATE);
        RatingBar studyRate = view.findViewById(R.id.studyRate);
        studyRate.setProgress(0);
        studyRate.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                rate = rating;
            }
        });
        final IndicatorSeekBar pageSeekBar = view.findViewById(R.id.pageSeekBar);
        pageSeekBar.setMax(mPage);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Long intern = pref.getLong(mMaterial,0);
                Long totalTime = pref.getLong(TOTAL_TIME,0);
                pref.edit().putLong(mMaterial,mInternal+intern).apply();
                pref.edit().putLong(finishStudyFragment.TOTAL_TIME,totalTime+mInternal).apply();
                SharedPreferences scorePref = getActivity().getSharedPreferences(profileFragment.SCORE_PREF, Context.MODE_PRIVATE);
                float oldRate = scorePref.getFloat(finishStudyFragment.SCORE,0);
                int pageProg = pageSeekBar.getProgress();
                scorePref.edit().putFloat(finishStudyFragment.SCORE,oldRate + (rate*(mInternal/1000)*mDiff)*(pageProg)/10).apply();

                mListener.onFragmentInteraction(notes.getText().toString());
                Intent intent  = new Intent(getActivity(),MainActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(!(context instanceof OnFragmentInteractionListener)){
            throw new RuntimeException(context.toString()+"must implement el class ");
        }else{
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String notes);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }
}
