package shobaky.studientsecretary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import java.util.ArrayList;



public class studyDialog extends DialogFragment implements Spinner.OnItemSelectedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String BOOKS = "param1";
    private static final String PAGES = "param2";
    private static final String NAME = "param3";
    private static final String ICON = "param4";
    private static final String DIFFICULTY = "param5";

    private int selectedBookPos =-1;

    // TODO: Rename and change types of parameters
    private ArrayList<String> mBooks;
    private ArrayList<String> mPages;
    private String mName;
    private String mIcon;
    private int mDiff;

    public studyDialog() {
        // Required empty public constructor
    }



    public static studyDialog newInstance(ArrayList<String> books, ArrayList<String> pages , String matName,String icon,int diff) {
        studyDialog fragment = new studyDialog();
        Bundle args = new Bundle();
        args.putStringArrayList(BOOKS, books);
        args.putStringArrayList(PAGES, pages);
        args.putString(NAME,matName);
        args.putString(ICON,icon);
        args.putInt(DIFFICULTY,diff);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mBooks = getArguments().getStringArrayList(BOOKS);
            mPages = getArguments().getStringArrayList(PAGES);
            mName = getArguments().getString(NAME);
            mIcon = getArguments().getString(ICON);
            mDiff = getArguments().getInt(DIFFICULTY);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_study_dialog, container, false);
        setCancelable(false);
        final Spinner books = view.findViewById(R.id.study_books_spinner);
        books.setOnItemSelectedListener(this);

        ArrayAdapter adapterBooks = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item,mBooks);

        ( adapterBooks).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        books.setAdapter(adapterBooks);


        final EditText fromPage = view.findViewById(R.id.study_from_pages);
        fromPage.requestFocus();
        final EditText toPage = view.findViewById(R.id.study_to_pages);
        Button start = view.findViewById(R.id.study_start_button);
        Button cancel = view.findViewById(R.id.study_cancel_button);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fromPage.getText().toString().isEmpty()){
                    fromPage.setError("From which page will you start?");
                }else if(Integer.valueOf(fromPage.getText().toString()) == 0){
                    fromPage.setError("Wrong input");
                }else if(selectedBookPos != -1 &&Integer.valueOf(fromPage.getText().toString())>Integer.valueOf(mPages.get(selectedBookPos))){
                    fromPage.setError("This page number doesn't exist in this book");

                }else if(toPage.getText().toString().isEmpty()){
                    toPage.setError("at which page will you stop?");
                }else if(Integer.valueOf(toPage.getText().toString()) == 0){
                    toPage.setError("Wrong input");
                }else if(selectedBookPos != -1 &&Integer.valueOf(toPage.getText().toString())>Integer.valueOf(mPages.get(selectedBookPos))){

                    toPage.setError("This page number doesn't exist in this book");
                }else{
                    Intent intent = new Intent(getActivity(),studyTimerActivity.class);
                    Bundle bundle = new Bundle();

                    bundle.putString("Pages",toPage.getText().toString());
                    bundle.putString("Name",mName);

                    bundle.putInt("Diff",mDiff);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }

            }
        });


        return view;

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedBookPos = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        selectedBookPos=0;

    }
}
