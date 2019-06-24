package shobaky.studientsecretary;

import android.app.AlarmManager;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.TextView;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.List;


public class alarmDialog extends DialogFragment {
    private Ringtone myTone;
    MediaPlayer mediaPlayer;


    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String REMINDER = "alarm";
    private static final String ID = "id";


    private Parcelable mReminder;
    private Long mId;


    public alarmDialog() {
        // Required empty public constructor
    }


    public static alarmDialog newInstance(Parcelable reminder,Long id) {
        alarmDialog fragment = new alarmDialog();
        Bundle args = new Bundle();
        args.putParcelable(REMINDER,reminder);
        args.putLong(ID,id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mediaPlayer = new MediaPlayer();
        SharedPreferences ringPref = getActivity().getSharedPreferences(Settings.settingsPref,Context.MODE_PRIVATE);
        String ringName = ringPref.getString(preferenceFragment.ringPref,"");

        AssetFileDescriptor afd = null;
        try {
            afd = getActivity().getAssets().openFd("ringtones/".concat(ringName));
            Log.d("Ringtone name",ringName);
            mediaPlayer.setDataSource(afd.getFileDescriptor(),afd.getStartOffset()+10,afd.getLength());
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setVolume(100,100);
        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        if (getArguments() != null) {
            mReminder = getArguments().getParcelable(REMINDER);
            mId = getArguments().getLong(ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_alarm_dialog, container, false);
        final reminder currentAlarm = (reminder) mReminder;

        ((TextView) view.findViewById(R.id.materialAlarm)).setText("Material name : ".concat(currentAlarm.getMaterialName()));
        ((TextView) view.findViewById(R.id.bookAlarm)).setText("From book : ".concat(currentAlarm.getMaterialBook()));
        ((TextView) view.findViewById(R.id.pageAlarm)).setText("Page : ".concat(currentAlarm.getMaterialPages()));
        ((TextView) view.findViewById(R.id.notesAlarm)).setText("Notes : ".concat(currentAlarm.getMaterialNotes()));
        Button ok = view.findViewById(R.id.okAlarm);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RoomDatabase reminderDB = Room.databaseBuilder(getActivity(),reminderRoom.class,"reminders").build();
                syncRemind sR = new syncRemind(((reminderRoom) reminderDB).getRemindDao(), false, true, new asyncRespone() {
                    @Override
                    public void processFinish(List<Material> materials) {
                    }

                    @Override
                    public void processRemindFinish(List<reminder> reminders) {
                        dismiss();
                    }
                });
                sR.getID(mId);
                sR.execute(currentAlarm);

            }
        });
        return view;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.reset();
        }


    }

    @Override
    public void onResume() {
        super.onResume();

        Window window = getDialog().getWindow();
        Point size = new Point();

        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);

        int width = size.x;
        int height = size.y;

        window.setLayout((int) (width * 0.9), (int) (height*0.9));
        window.setGravity(Gravity.CENTER);
    }
}
