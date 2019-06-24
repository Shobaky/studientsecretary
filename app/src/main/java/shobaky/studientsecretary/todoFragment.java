package shobaky.studientsecretary;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.io.IOException;
import java.util.ArrayList;


/**

 */
public class todoFragment extends Fragment implements fragmentTodoTexts.OnFragmentInteractionListener, DialogInterface.OnDismissListener,todoDoneFragment.dismisser,fragmentTodoSS.OnFragmentInteractionListener {
    private String Title;
    private String Homework;
    private fragmentTodoTexts todoTexts;
    //so i can call them globally .. check newHomework dialog down there line 170
    private homeworkAdapter todoAdapter;
    private homeworkSerializer loader;
    private int priority;
    private Material material;
    private Button next;
    private Button Prev;
    private ConstraintSet constraintSet;
    private ConstraintLayout constraintLayout;
    private EditText titleInput;
    private PagerAdapter pagerAdapter;
    private ViewPager pager;

    public todoFragment() {
        // Required empty public constructor
    }

    public static todoFragment newInstance() {
        todoFragment fragment = new todoFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        todoTexts = fragmentTodoTexts.newInstance();
        fragmentTodoTexts.mListener = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment



        final View homeworks =  inflater.inflate(R.layout.fragment_todo, container, false);
        pager = homeworks.findViewById(R.id.homeworkPager);
        if(pager != null) {
            //Land orientation

            pagerAdapter = new landAdapter(getActivity().getSupportFragmentManager());
            pager.setAdapter(pagerAdapter);

            constraintLayout = homeworks.findViewById(R.id.homework_land_parent);
            titleInput = homeworks.findViewById(R.id.title_input_land);

            constraintSet = new ConstraintSet();
            next = homeworks.findViewById(R.id.addHomeworkLand);
            Prev = new Button(getActivity());
            //Prev design
            Prev.setId(R.id.prevButton);
            Prev.setBackgroundResource(R.drawable.circle_button);
            Prev.setText(getString(R.string.prev));
            Prev.setVisibility(View.INVISIBLE);

            constraintSet.clone(constraintLayout);

            //onClick return to text fragment
           setNextListener();
        }
        FloatingActionButton fab;
        if((fab = homeworks.findViewById(R.id.floatingActionButton)) != null){
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addHomework();
                }
            });
        }

        RecyclerView recyclerView = homeworks.findViewById(R.id.todo_rec);

        loader = new homeworkSerializer(getActivity(),newHomeWork.path);
        ArrayList<homeWork> todos = null;
        try {
            todos = loader.load();

        } catch (IOException e) {
            e.printStackTrace();
        }


        todoAdapter = new homeworkAdapter(getActivity(),todos,this );
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(todoAdapter);
        return homeworks;
    }
    public void addHomework(){
          newHomeWork homeWork = new newHomeWork();
          homeWork.setDismiss(this);
          homeWork.show(getActivity().getSupportFragmentManager(),"dialog");
    }

    @Override
    public void onFragmentInteraction(String title , String homework) {
          this.Homework = homework;
          this.Title = title;
    }
    //update when homework dialog is dismissed
    @Override
    public void onDismiss(DialogInterface dialog) {
        try {
            todoAdapter.dataObserve(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void dismissRate(int pos , float score) {
        todoAdapter.homeworkDone(pos,score);

    }

    @Override
    public void onFragmentInteraction(int priority, Material material, String title, String homework) {
      this.priority = priority;
      this.material = material;
      this.Title = title;
      this.Homework = homework;

    }

    private class landAdapter extends FragmentStatePagerAdapter {
        landAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int i) {
            switch (i){
                case 0:
                    return todoTexts;
                case 1:
                    fragmentTodoSS ss = fragmentTodoSS.newInstance();
                    ss.setListener(todoFragment.this);
                    return ss;
                default:
                    return todoTexts;
            }
        }



    }
    private void setNextListener(){


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                todoTexts.onButtonPressed();

                if (Title.isEmpty() || Homework.isEmpty()) {
                    titleInput.setError("Please enter empty fields");
                } else {

                    pager.setCurrentItem(2);
                    next.setText(R.string.add);


                    //Prev button set
                    //constraintSet.connect(Applied-on view , where to apply in the view , connect to which view ? , which direction in view , margin)
                    constraintSet.connect(Prev.getId(), ConstraintSet.START, R.id.hw_guide_divider_land, ConstraintSet.END, 8);
                    constraintSet.connect(Prev.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 8);
                    constraintSet.constrainHeight(Prev.getId(), ConstraintSet.WRAP_CONTENT);
                    constraintSet.constrainHeight(Prev.getId(), ConstraintSet.WRAP_CONTENT);

                    if(Prev.getParent() == null) {
                        constraintLayout.addView(Prev);
                    }
                    Prev.setVisibility(View.VISIBLE);



                    //Add button set
                    constraintSet.connect(R.id.addHomeworkLand, ConstraintSet.START, Prev.getId(), ConstraintSet.END, 8);
                    constraintSet.connect(R.id.addHomeworkLand, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 8);
                    constraintSet.connect(R.id.addHomeworkLand, ConstraintSet.TOP, R.id.homeworkPager, ConstraintSet.BOTTOM, 8);
                    constraintSet.connect(R.id.addHomeworkLand, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 8);

                    constraintSet.applyTo(constraintLayout);

                }
                setAddListener();

            }

        });


    }
    private void setAddListener(){
        setPrevListener();
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fragmentTodoSS fss = (fragmentTodoSS) pagerAdapter.instantiateItem(pager, pager.getCurrentItem());
                fss.setArgs(Title, Homework);
                fss.onButtonPressed();

                setPrevListener();
                if (material != null) {
                    homeWork hw = new homeWork();
                    hw.setMaterialName(material.getName());
                    hw.setScore(0);
                    hw.setTitle(Title);
                    hw.setHomework(Homework);
                    hw.setDone(false);
                    hw.setDiff(material.getDifficulty());
                    hw.setPriority(priority);
                    if (URLUtil.isValidUrl(material.getIconName())) {
                        hw.setImgUri(material.getIconName());
                    } else {
                        hw.setImg(getActivity().getResources().getIdentifier(material.getIconName(), "drawable", getActivity().getPackageName()));
                        hw.setImgUri("");
                    }
                    ArrayList<homeWork> homeworks;
                    try {
                        homeworkSerializer serializer = new homeworkSerializer(getActivity(), newHomeWork.path);
                        homeworks = serializer.load();
                        homeworks.add(hw);
                        serializer.save(homeworks);
                        todoAdapter.dataObserve(homeworks);
                        Prev.setVisibility(View.GONE);
                        next.setText(R.string.next);
                        setNextListener();
                        constraintSet.connect(R.id.addHomeworkLand,ConstraintSet.START,R.id.hw_guide_divider_land,ConstraintSet.END,16);
                        constraintSet.connect(R.id.addHomeworkLand,ConstraintSet.END,ConstraintSet.PARENT_ID,ConstraintSet.END,8);
                        constraintSet.connect(R.id.addHomeworkLand,ConstraintSet.TOP,R.id.homeworkPager,ConstraintSet.BOTTOM,8);
                        constraintSet.connect(R.id.addHomeworkLand,ConstraintSet.BOTTOM,ConstraintSet.PARENT_ID,ConstraintSet.BOTTOM,8);
                        pager.setCurrentItem(0);
                        Homework = "";
                        Title = "";
                        material = null;

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(getActivity(),"Please select a zeft material",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void setPrevListener(){
        Prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Prev.setVisibility(View.GONE);
                next.setText(R.string.next);
                setNextListener();
                constraintSet.connect(R.id.addHomeworkLand,ConstraintSet.START,R.id.hw_guide_divider_land,ConstraintSet.END,16);
                constraintSet.connect(R.id.addHomeworkLand,ConstraintSet.END,ConstraintSet.PARENT_ID,ConstraintSet.END,8);
                constraintSet.connect(R.id.addHomeworkLand,ConstraintSet.TOP,R.id.homeworkPager,ConstraintSet.BOTTOM,8);
                constraintSet.connect(R.id.addHomeworkLand,ConstraintSet.BOTTOM,ConstraintSet.PARENT_ID,ConstraintSet.BOTTOM,8);
                pager.setCurrentItem(0);
            }
        });


    }


}