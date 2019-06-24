package shobaky.studientsecretary;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;


public class fragmentTodoTexts extends Fragment  {


    public static fragmentTodoTexts.OnFragmentInteractionListener mListener;
    private EditText title;
    private EditText homework;
    public fragmentTodoTexts(){
        // Required empty public constructor
    }


    public static fragmentTodoTexts newInstance() {
        fragmentTodoTexts fragment = new fragmentTodoTexts();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        View texts =inflater.inflate(R.layout.fragment_todo_texts_land,container,false);
        title = texts.findViewById(R.id.title_input_land);
        homework = texts.findViewById(R.id.homework_input_land);
        return texts;


    }
    public void onButtonPressed() {
        if (mListener != null) {
            mListener.onFragmentInteraction(title.getText().toString(),homework.getText().toString());

        }
    }





    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String title , String homework);
    }

}
