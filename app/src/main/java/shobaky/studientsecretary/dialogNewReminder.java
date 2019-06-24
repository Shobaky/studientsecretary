package shobaky.studientsecretary;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class dialogNewReminder extends DialogFragment implements AdapterView.OnItemSelectedListener {

    private final ArrayList<Material> materialNames = new ArrayList<>();
    private ArrayList<String> books = new ArrayList<>();
    private ArrayList<String> pgs = new ArrayList<>();
    private int pos;
    private Spinner spinBook;
    private ArrayAdapter<String> bookAdapt;
    private Material m = null;
    private reminder Remind;
    EditText year;
    EditText month;
    EditText day;
    TextView time;
    private remindReady reminderDone;

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.dialog_new_reminder,container,false);
        final LinearLayout increamenter = view.findViewById(R.id.reminder_page_number);
        final EditText pages = increamenter.findViewById(R.id.page_edit_incrementable);
        final Spinner spinner = view.findViewById(R.id.spinner_new_reminder);
        spinBook = view.findViewById(R.id.spinner_book_new_reminder);
        final ImageButton datePickerBut = view.findViewById(R.id.new_reminder_date_picker);
        final ImageButton timePicker = view.findViewById(R.id.new_reminder_time_picker);
        final Button add = view.findViewById(R.id.dialog_new_reminder_ok);
        Button cancel  = view.findViewById(R.id.dialog_new_reminder_cancel);
        view.findViewById(R.id.new_reminder_notes);
        final EditText Notes;
        final AlarmManager aM = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);

        materialRoom db = Room.databaseBuilder(getActivity(),materialRoom.class,"materials").build();
        final materialDao dao = db.getMatDao();
        final syncMat mat = new syncMat(dao, false, new asyncRespone() {
            @Override
            public void processFinish(List<Material> materials) {
                materialNames.addAll(materials);
                ArrayList<String> names = new ArrayList<>();
                ArrayList<Integer> imgs = new ArrayList<>();
                //(hints)
                names.add("Material name");
                books.add("Material book");
                //express the zero of the first choice of books adapter (Material book) , (hint)
                pgs.add("0");
                for(Material m: materialNames){

                    if(getResources().getIdentifier(m.getIconName(),"drawable",getActivity().getPackageName()) != 0) {
                        imgs.add(getResources().getIdentifier(m.getIconName(), "drawable", getActivity().getPackageName()));
                        names.add(m.getName());
                    }else{
                        imgs.add(0);
                        names.add(m.getIconName().concat("|").concat(m.getName()));
                    }

                }
                spinnerAdapter spinAdapt = new spinnerAdapter(getActivity(),R.layout.spinner_item,names,imgs,Color.BLACK);
                bookAdapt = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item,books);
                bookAdapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);



                //Materials name spinner
                spinner.setOnItemSelectedListener(dialogNewReminder.this);
                spinner.setAdapter(spinAdapt);
                spinBook.setAdapter(bookAdapt);
                spinBook.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        pos =position;
                        if(position == 0){
                            parent.getChildAt(position).setEnabled(false);
                            parent.getChildAt(position).setFocusable(false);
                            parent.getChildAt(position).setActivated(false);
                        }else{
                            pgs.add(m.getPages1());
                            if(m.getBook2() != null) {
                                pgs.add(m.getPages2());
                            }
                            if(m.getBook3() != null) {
                                pgs.add(m.getPages3());

                            }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });



            }
            @Override
            public void processRemindFinish(List<reminder> reminders) {

            }
        });
        mat.execute();

        RoomDatabase reminderDB = Room.databaseBuilder(getActivity(),reminderRoom.class,"reminders").build();
        asyncRespone mySync =  new asyncRespone() {
            @Override
            public void processFinish(List<Material> materials) {
            }

            @Override
            public void processRemindFinish(List<reminder> reminders) {

                if(reminderDone != null){
                    //add to the alarm manager
                    Calendar alarmC = Calendar.getInstance();
                    int alarmYear = Integer.valueOf(year.getText().toString());
                    int alarmMonth = Integer.valueOf(month.getText().toString());
                    int alarmDay = Integer.valueOf(day.getText().toString());

                    Calendar current = Calendar.getInstance();
                    current.setTimeInMillis(System.currentTimeMillis());

                    alarmC.set(Calendar.YEAR,alarmYear);
                    alarmC.set(Calendar.MONTH,alarmMonth-1);
                    alarmC.set(Calendar.DAY_OF_MONTH,alarmDay);
                    String sTime = time.getText().toString();

                    alarmC.set(Calendar.HOUR_OF_DAY,Integer.parseInt(sTime.substring(0,sTime.indexOf(":"))));
                    alarmC.set(Calendar.MINUTE,Integer.parseInt( sTime.substring(sTime.indexOf(":")+1,sTime.length())));


                    Intent alarmInt = new Intent(getActivity(),alarmReceiver.class);
                    Bundle alarmArgs = new Bundle();

                    alarmArgs.putParcelable("reminderAlarm",reminders.get(0));
                    alarmArgs.putLong("reminderID",reminders.get(0).getId());
                    alarmInt.putExtra("alarmBundle",alarmArgs);
                    PendingIntent pI = PendingIntent.getBroadcast(getActivity(),Integer.valueOf(reminders.get(0).getSetTime()),alarmInt,PendingIntent.FLAG_UPDATE_CURRENT);

                    aM.set(AlarmManager.RTC_WAKEUP,alarmC.getTimeInMillis(),pI);
                    reminderDone.deliver(Remind);
                    dismiss();
                }
            }
        };
        final syncRemind remindSync = new syncRemind(((reminderRoom) reminderDB).getRemindDao(),mySync,true);
        final ImageButton increasePage = view.findViewById(R.id.increase_page);
        ImageButton decreasePage = view.findViewById(R.id.decrease_page);
        increasePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!pages.getText().toString().isEmpty() && Integer.valueOf(pages.getText().toString()) <Integer.valueOf(pgs.get(pos))) {
                    int prevPage = Integer.valueOf(pages.getText().toString());
                   pages.setText(String.valueOf(prevPage+1));

                }
            }
        });
        increasePage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(v.isInTouchMode()){
                    if(!pages.getText().toString().isEmpty() && Integer.valueOf(pages.getText().toString()) <Integer.valueOf(pgs.get(pos))) {
                        int prevPage = Integer.valueOf(pages.getText().toString());
                        pages.setText(String.valueOf(prevPage+1));
                        return true;

                    }
                }
                return false;
            }
        });
        decreasePage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!pages.getText().toString().isEmpty() && Integer.valueOf(pages.getText().toString()) >0) {
                    int prevPage = Integer.valueOf(pages.getText().toString());
                    pages.setText(String.valueOf(prevPage-1));
                    return true;

                }
                return false;
            }
        });

        decreasePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!pages.getText().toString().isEmpty() && Integer.valueOf(pages.getText().toString()) >0) {
                    int prevPage = Integer.valueOf(pages.getText().toString());
                    pages.setText(String.valueOf(prevPage-1));

                }
            }
        });
        datePickerBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker dP = datePicker.newInstance(false);
                dP.show(getActivity().getSupportFragmentManager(),"date picker");

            }
        });
        timePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker dP = datePicker.newInstance(true);
                dP.show(getActivity().getSupportFragmentManager(),"time picker");
            }
        });
        year = view.findViewById(R.id.new_reminder_year);
        month = view.findViewById(R.id.new_reminder_month);
        day = view.findViewById(R.id.new_reminder_day);
        time = view.findViewById(R.id.new_reminder_time);
        Notes = view.findViewById(R.id.new_reminder_notes);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Errors
                final Boolean missingBook = (pos == 0);
                final Boolean missingDate = (year.getText().toString().isEmpty()) || month.getText().toString().isEmpty() || day.getText().toString().isEmpty();
                final Boolean missingTime = time.getText().toString().equals("00:00");
                Remind = new reminder();
                if(missingBook || missingDate  || missingTime){
                    if(missingBook){
                        Toast.makeText(getActivity(),"Please select a book",Toast.LENGTH_SHORT).show();
                    }else if(missingDate){
                        year.setError("Please enter a full date");

                    }else {
                        time.setError("Please enter time");
                    }
                }else  {
                    year.setError(null);
                    time.setError(null);

                    Remind.setMaterialName(m.getName());
                    Remind.setMaterialBook(books.get(pos));
                    Remind.setMaterialPages(pages.getText().toString());
                    Remind.setIconName(m.getIconName());

                    String date = day.getText().toString().concat("-").concat(month.getText().toString()).concat("-").concat(year.getText().toString());
                    Remind.setReminderDate(date);
                    Remind.setReminderTime(time.getText().toString());

                    Remind.setMaterialNotes(Notes.getText().toString());
                    String dayMonth = String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                    String Month =String.valueOf(Calendar.getInstance().get(Calendar.MONTH)+1);
                    String Year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
                    Remind.setSetDate(dayMonth.concat("-").concat(Month).concat("-").concat(Year));
                    Calendar c = Calendar.getInstance();
                    Remind.setSetTime(String.valueOf((c.get(Calendar.HOUR)*60*60*1000)+(c.get(Calendar.MINUTE)*60*1000)+(c.get(Calendar.SECOND)*1000)+c.get(Calendar.MILLISECOND)));
                    remindSync.execute(Remind);
                }
            }
        });





        return view;
    }
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if(position != 0){

            m = materialNames.get(position-1);
            books.add(m.getBook1());
            bookAdapt.notifyDataSetChanged();
        }else{
            pgs.clear();
            pgs.add("0");
        }





    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    //For both time and date
    public void dtPick(String s){
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(new SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yyyy",Locale.getDefault()).parse(s));
            year.setText(String.valueOf(calendar.get(Calendar.YEAR)));
            month.setText(String.valueOf(calendar.get(Calendar.MONTH)+1));
            day.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        } catch (ParseException e) {
            if(!s.isEmpty()) {
                time.setText(s);
            }
            e.printStackTrace();
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.reminderDone = null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(!(context instanceof remindReady)){

            throw new RuntimeException("Must implement remindReady");
        }else{
            this.reminderDone = (remindReady)context;
        }
    }

    //interface to deliver the new reminder
    public interface remindReady{
        void deliver(reminder Rem);
    }


}
