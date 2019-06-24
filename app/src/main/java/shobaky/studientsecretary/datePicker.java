package shobaky.studientsecretary;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import java.util.Calendar;
import java.util.Locale;


public class datePicker extends DialogFragment {
    private static final String TIMEPICKING = "TIME";
    private boolean time;

    private onDatePick mDatePick;
    public static datePicker newInstance(boolean Time){
        Bundle bundle = new Bundle();
        bundle.putBoolean(TIMEPICKING,Time);
        datePicker dP = new datePicker();
        dP.setArguments(bundle);
        return dP;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            this.time = getArguments().getBoolean(TIMEPICKING);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View picker =inflater.inflate(R.layout.date_picker_dialog,container,false);
        final DatePicker DP = picker.findViewById(R.id.date_calender);
        final TimePicker TP = picker.findViewById(R.id.time_calender);
        TP.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                Log.d("TIMETOTO",""+hourOfDay);
            }
        });
        Button done = picker.findViewById(R.id.done_picking);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String interact="";
                if(DP.getVisibility() == View.VISIBLE){
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(DP.getYear(),DP.getMonth(),DP.getDayOfMonth());
                    mDatePick.datePick(calendar.getTime().toString());
                }else{
                    StringBuilder timeBuilder = new StringBuilder();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        timeBuilder.append(String.format(Locale.getDefault(),"%02d",TP.getHour())).append(":").append(String.format(Locale.getDefault(),"%02d",TP.getMinute()));


                    }else{
                        timeBuilder.append(String.format(Locale.getDefault(),"%02d",TP.getCurrentHour())).append(":").append(String.format(Locale.getDefault(),"%02d",TP.getCurrentMinute()));
                    }
                    interact = timeBuilder.toString();
                }
                mDatePick.datePick(interact);
                dismiss();

            }
        });
        if (time){
            TP.setVisibility(View.VISIBLE);
            DP.setVisibility(View.GONE);
        }
        return picker;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(mDatePick != null){
            mDatePick = null;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(!(context instanceof onDatePick)){
            throw new RuntimeException();
        }else{
            mDatePick = (onDatePick)context;
        }
    }

    public interface onDatePick{
         void datePick(String dT);
    }

}
