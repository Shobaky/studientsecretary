package shobaky.studientsecretary;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;





public class todoDoneFragment extends DialogFragment {
    private  int mDiff;
    private int mPos;
    private static final String DIFF = "DIFFICULTY";
    private static final String POS = "POSITION";
    private static dismisser rateDismiss;
    public static todoDoneFragment newInstance(int diff,int pos,dismisser rater) {
        Bundle args = new Bundle();

        args.putInt(DIFF,diff);
        args.putInt(POS,pos);
        todoDoneFragment fragment = new todoDoneFragment();
        fragment.setArguments(args);
        rateDismiss = rater;
        return fragment;
    }

    private float rate;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            mDiff = getArguments().getInt(DIFF);
            mPos = getArguments().getInt(POS);
        }
    }

    public todoDoneFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_todo_done, container, false);
        RatingBar homeworkRate = view.findViewById(R.id.homework_rate);
        homeworkRate.setProgress(0);
        homeworkRate.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                rate = rating;
                Log.d("Before i rate","i want to sy"+rate);
            }
        });
        return view;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        rateDismiss.dismissRate(mPos,(rate*mDiff)*5);
        SharedPreferences scorePref = getActivity().getSharedPreferences(profileFragment.SCORE_PREF, Context.MODE_PRIVATE);
        float oldRate = scorePref.getFloat(finishStudyFragment.SCORE,0);
        scorePref.edit().putFloat(finishStudyFragment.SCORE,oldRate+(rate*mDiff*5)).apply();
        Log.d("Before i dismiss","i want to say "+rate+mDiff);

    }
    public interface dismisser{
        void dismissRate(int pos , float score);
    }

}
