package shobaky.studientsecretary;

import android.arch.persistence.room.Room;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import java.util.ArrayList;
import java.util.List;



public class studyFragment extends Fragment implements DialogInterface.OnDismissListener,MaterialDialog.newMat {
    private materialAdapter adapt;
    private GridView materialsGrid;
    private MaterialDialog addMat;




    public studyFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static studyFragment newInstance() {

        return new studyFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View study = inflater.inflate(R.layout.fragment_study, container, false);
        materialsGrid = study.findViewById(R.id.materialsGrid);
        MaterialDialog.adder = this;
        materialRoom room = Room.databaseBuilder(getActivity(),materialRoom.class,"materials").build();
        final materialDao dao = room.getMatDao();
        syncMat mat = new syncMat(dao, false, new asyncRespone() {
            @Override
            public void processFinish(List<Material> materials) {
                final ArrayList<Material> myList = new ArrayList<>(materials);
                adapt = new materialAdapter(getActivity(), myList);
                materialsGrid.setAdapter(adapt);
                materialsGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        switch (view.getTag().toString()) {
                            case "ADD":
                                addMat = new MaterialDialog();
                                addMat.setDismiss(studyFragment.this);
                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                addMat.show(fm, "addMaterial");
                                break;
                            case "None":
                                break;
                            default:
                                Material m = myList.get(position);
                                ArrayList<String> books = new ArrayList<>();
                                ArrayList<String> pages = new ArrayList<>();
                                books.add(m.getBook1());
                                if(m.getBook2() != null){
                                    pages.add(m.getPages2());
                                    books.add(m.getBook2());

                                }
                                if(m.getBook3() != null) {
                                    books.add(m.getBook3());
                                    pages.add(m.getPages3());
                                }
                                pages.add(m.getPages1());
                                studyDialog dialog = studyDialog.newInstance(books,pages,m.getName(),m.getIconName(),m.getDifficulty());
                                dialog.show(getActivity().getSupportFragmentManager(),"study_dialog");

                        }
                    }
                });

            }

            @Override
            public void processRemindFinish(List<reminder> reminders) {

            }
        }, getActivity());
        mat.execute();




        return study;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    @Override
    public void onDismiss(DialogInterface dialog) {

    }

    @Override
    public void sendMat(Material m) {
        if(m != null){
            adapt.notifyMaterial(m);
        }
    }
}
