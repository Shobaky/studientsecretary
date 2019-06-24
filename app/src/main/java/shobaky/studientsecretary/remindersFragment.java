package shobaky.studientsecretary;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;



public class remindersFragment extends Fragment {
    private reminderAdapter adapter;
    private ArrayList<reminder> remindersArray;


    public remindersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     ]
     * @return A new instance of fragment remindersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static remindersFragment newInstance() {
        remindersFragment fragment = new remindersFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reminders, container, false);

        remindersArray = new ArrayList<>();
        final RecyclerView reminderRec = view.findViewById(R.id.reminderRecycle);
        FloatingActionButton addAlarm = view.findViewById(R.id.reminder_add_alarm);
        addAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new dialogNewReminder().show(getActivity().getSupportFragmentManager(),"new alarm");
            }
        });
        reminderRoom db = Room.databaseBuilder(getActivity(),reminderRoom.class,"reminders").build();
        syncRemind reminder = new syncRemind(db.getRemindDao(), new asyncRespone() {
            @Override
            public void processFinish(List<Material> materials) {

            }

            @Override
            public void processRemindFinish(List<shobaky.studientsecretary.reminder> reminders) {
                remindersArray.addAll(reminders);
                adapter = new reminderAdapter(getActivity(),remindersArray,getActivity().getSupportFragmentManager());
                reminderRec.setAdapter(adapter);
                reminderRec.setLayoutManager(new LinearLayoutManager(getActivity()));
            }
        },false);
        reminder.execute();
        return view;

    }
    public void getReminder(reminder newOne){
        remindersArray.add(newOne);
        adapter.notifyDataSetChanged();
    }


}
