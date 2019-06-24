package shobaky.studientsecretary;


import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import java.util.ArrayList;
import java.util.List;

public class fragmentTodoSS extends Fragment implements AdapterView.OnItemSelectedListener {
    String materialName;
    int materialImg;
    int priority;
    Material m;
    String imgUri;
    ArrayList<Material> materials;
    fragmentTodoSS.OnFragmentInteractionListener mListener;



    private String title;
    private String homework;
    public fragmentTodoSS(){
        // Required empty public constructor
    }


    public static fragmentTodoSS newInstance() {
        fragmentTodoSS fragment = new fragmentTodoSS();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_todo_seekscroll_land,container,false);

        final Spinner spinner = view.findViewById(R.id.hw_dialog_material_spinner);
        //scroll names
        materials = new ArrayList<>();
        materialRoom db = Room.databaseBuilder(getActivity(),materialRoom.class,"materials").build();
        final materialDao dao = db.getMatDao();
        syncMat mat = new syncMat(dao, false, new asyncRespone() {
            @Override
            public void processFinish(List<Material> materials) {
                fragmentTodoSS.this.materials.addAll(materials);
                ArrayList<String> names = new ArrayList<>();
                ArrayList<Integer> imgs = new ArrayList<>();
                names.add("Material name");
                for(Material m: materials){
                    if(!(URLUtil.isValidUrl(m.getIconName()))) {
                        names.add(m.getName());
                        imgs.add(getResources().getIdentifier(m.getIconName(), "drawable", getActivity().getPackageName()));
                    }else{
                        names.add(m.getIconName().concat("|").concat(m.getName()));
                        imgs.add(0);
                    }
                }
                spinnerAdapter spinAdapt = new spinnerAdapter(getActivity(),R.layout.spinner_item,names,imgs);

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(),R.layout.spinner_item,names);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);



                //Materials name spinner

                spinner.setOnItemSelectedListener(fragmentTodoSS.this);

                spinner.setAdapter(spinAdapt);

                //The arrow of the spinner
                ImageView arrow = view.findViewById(R.id.hw_dialog_material_spinner_arrow);
                //when the arrow is clicked , open the spinner
                arrow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        spinner.performClick();

                    }
                });
            }

            @Override
            public void processRemindFinish(List<reminder> reminders) {

            }
        });
        mat.execute();
        final IndicatorSeekBar seekBar = view.findViewById(R.id.prioritySeekBar);

        seekBar.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
                fragmentTodoSS.this.priority = seekParams.progress;
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

            }
        });


        seekBar.setIndicatorTextFormat("Priority ${TICK_TEXT}");
        return view;

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ((TextView) view.findViewById(R.id.spinner_materialItem_name)).setTextColor(getActivity().getResources().getColor(android.R.color.white));
        if (materials.size() > 0 && view != null) {
            if (position == 0 ) {
                view.setEnabled(false);

            }
            this.materialName = ((TextView) view.findViewById(R.id.spinner_materialItem_name)).getText().toString();

            ImageView img = view.findViewById(R.id.spinner_materialItem_image);
            if (position > 0 && !(URLUtil.isValidUrl(img.getTag().toString()))) {
                this.m = materials.get(position-1);


                this.materialImg = getActivity().getResources().getIdentifier(img.getTag().toString(), "drawable", getActivity().getPackageName());
            } else if(position >0) {
                this.m = materials.get(position-1);

                this.imgUri = img.getTag().toString();
            }
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {


    }

    public void setListener(OnFragmentInteractionListener listener){
        this.mListener = listener;

    }
    public void setArgs(String title , String homework){
        this.title = title;
        this.homework = homework;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void onButtonPressed() {
        if (mListener != null) {
            mListener.onFragmentInteraction(priority,m,title,homework);
        }
    }

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(int priority , Material material,String title,String homework);
    }
}