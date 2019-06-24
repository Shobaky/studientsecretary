package shobaky.studientsecretary;

import android.app.Dialog;
import android.arch.persistence.room.Room;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



/*Copyright (C) 2017 zhuangguangquan(庄广权)

        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.*/
public class newHomeWork extends DialogFragment implements Spinner.OnItemSelectedListener {
    private String materialName;
    private int materialImg;
    public static String path = "TODOS";
    private String materialUri="";
    private DialogInterface.OnDismissListener dissmisser;
    private int pos;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE,R.style.DialogHomework);
        //so when changing from portrait to landscape it closes
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        final View dialog = inflater.inflate(R.layout.homework_dialog,container,false);
        final TextInputLayout homeworkLayout = dialog.findViewById(R.id.hw_dialog_homework_layout);
        TextInputLayout titleLayout = dialog.findViewById(R.id.hw_dialog_title_layout);
        final EditText homeworkEdit = homeworkLayout.getEditText();
        final EditText titleEdit = titleLayout.getEditText();

        final Spinner spinner = dialog.findViewById(R.id.hw_dialog_material_spinner);
        final IndicatorSeekBar priority = dialog.findViewById(R.id.prioritySeekBar);


        //Material names adapter
        final ArrayList<Material> materialNames = new ArrayList<>();
        materialRoom db = Room.databaseBuilder(getActivity(),materialRoom.class,"materials").build();
        final materialDao dao = db.getMatDao();
        syncMat mat = new syncMat(dao, false, new asyncRespone() {
            @Override
            public void processFinish(List<Material> materials) {
             materialNames.addAll(materials);
                ArrayList<String> names = new ArrayList<>();
                ArrayList<Integer> imgs = new ArrayList<>();
                names.add("Material name");
                for(Material m: materialNames){
                    if(!URLUtil.isValidUrl(m.getIconName())) {
                        names.add(m.getName());

                        imgs.add(getResources().getIdentifier(m.getIconName(), "drawable", getActivity().getPackageName()));
                    }else{
                        //i concat the uri with the material name then i parse them in the adapter ان شاء الله
                        names.add(m.getIconName().concat("|").concat(m.getName()));
                        imgs.add(0);
                    }
                }
                spinnerAdapter spinAdapt = new spinnerAdapter(getActivity(),R.layout.spinner_item,names,imgs);
                //Materials name spinner
                spinner.setOnItemSelectedListener(newHomeWork.this);
                spinner.setAdapter(spinAdapt);


                //The arrow of the spinner
                ImageView arrow = dialog.findViewById(R.id.hw_dialog_material_spinner_arrow);
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



        Toolbar toolbar = dialog.findViewById(R.id.toolBar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimaryDark));
        TextView textView =toolbar.findViewById(R.id.homeworkDialogTitle);

        //Priority hint
        priority.setIndicatorTextFormat("Priority ${TICK_TEXT}");
        priority.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
                switch (seekParams.thumbPosition){
                    case 0:
                        seekParams.seekBar.thumbColor(getActivity().getResources().getColor(R.color.colorAccent));
                        break;
                    case 1:
                        seekParams.seekBar.thumbColor(getActivity().getResources().getColor(android.R.color.holo_blue_dark));
                        break;
                    case 2:
                        seekParams.seekBar.thumbColor(getActivity().getResources().getColor(android.R.color.holo_red_dark));
                        break;

                }
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

            }
        });



        textView.setText("Add homework");
        Button add = dialog.findViewById(R.id.addHomework);
        Button cancel = dialog.findViewById(R.id.cancelHomework);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean noHomework = homeworkEdit.getText().toString().isEmpty();
                Boolean noTitle = homeworkEdit.getText().toString().isEmpty();
                Boolean noMaterial = (materialName.equals("Material name"));
                if(spinner.getAdapter().getCount() ==1){
                    Toast.makeText(getActivity(),"Please add a material first",Toast.LENGTH_LONG).show();

                }else if(noHomework){
                    homeworkEdit.setError("Please enter your homework");
                }else if(noTitle){
                    titleEdit.setError("Please enter homework's title");
                }else if(noMaterial){
                    spinner.performClick();
                    Toast.makeText(getActivity(),"Please select a material",Toast.LENGTH_LONG).show();
                }
                else{
                    homeWork hw = new homeWork();
                    hw.setHomework(homeworkEdit.getText().toString());
                    hw.setTitle(titleEdit.getText().toString());
                    hw.setPriority(priority.getProgress());
                    hw.setDone(false);
                    hw.setMaterialName(materialName);
                    hw.setImg(materialImg);
                    hw.setImgUri(materialUri);
                    hw.setDiff(materialNames.get(pos-1).getDifficulty());
                    hw.setScore(0);
                    homeworkSerializer serializer = new homeworkSerializer(getActivity(),newHomeWork.path);
                    ArrayList<homeWork> homeWorks = null;
                    Toast.makeText(getActivity(),"Homework successfully added",Toast.LENGTH_LONG).show();
                    dismiss();
                    try {
                        homeWorks = serializer.load();
                        homeWorks.add(hw);
                        serializer.save(homeWorks);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }




                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        Dialog dialog =  super.onCreateDialog(savedInstanceState);


        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
             if(position == 0){
                 view.setEnabled(false);
             }
             this.materialName = ((TextView)view.findViewById(R.id.spinner_materialItem_name)).getText().toString();
             ImageView img = view.findViewById(R.id.spinner_materialItem_image);
             if(position >0&&URLUtil.isValidUrl(img.getTag().toString()) ){
                 this.materialUri = img.getTag().toString();
             }
             else if(position > 0 ) {
                 this.pos = position;
                 this.materialImg = getActivity().getResources().getIdentifier(img.getTag().toString(), "drawable", getActivity().getPackageName());
             }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    //dismiss listener
    public void setDismiss(DialogInterface.OnDismissListener listener){
        this.dissmisser = listener;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if(dissmisser != null){
            dissmisser.onDismiss(dialog);
        }
    }
}
