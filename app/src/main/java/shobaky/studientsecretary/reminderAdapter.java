package shobaky.studientsecretary;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class reminderAdapter extends RecyclerView.Adapter<reminderAdapter.Holder> {
    private ArrayList<reminder> reminders;
    private Context cxt;
    private FragmentManager fm;
    public reminderAdapter(Context context , ArrayList<reminder> reminders,FragmentManager FM){
        this.reminders = reminders;
        this.cxt = context;
        this.fm = FM;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Holder(LayoutInflater.from(cxt).inflate(R.layout.reminder_item,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {

        holder.date.setText("Date : ".concat(reminders.get(i).getReminderDate()));
        holder.time.setText("Time : ".concat(reminders.get(i).getReminderTime()));
        holder.name.setText("Material name : ".concat(reminders.get(i).getMaterialName()));

    }

    @Override
    public int getItemCount() {

        return reminders.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        private TextView date;
        private TextView time;
        private TextView name;
        private ImageView delete;
        public Holder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.reminderDate);
            time = itemView.findViewById(R.id.reminderTime);
            name = itemView.findViewById(R.id.reminderMaterialName);
            delete = itemView.findViewById(R.id.deleteRemind);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = reminders.get(getAdapterPosition()).getMaterialName();
                    String book = reminders.get(getAdapterPosition()).getMaterialBook();
                    String page = reminders.get(getAdapterPosition()).getMaterialPages();
                    String date = reminders.get(getAdapterPosition()).getSetDate();
                    String icon = reminders.get(getAdapterPosition()).getIconName();
                    String notes = reminders.get(getAdapterPosition()).getMaterialNotes();
                    dialogReminder reminder = dialogReminder.newInstance(name,book,icon,page,date,notes);
                    reminder.show(fm,"sea");
                }
            });
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                     new AlertDialog.Builder(cxt).setTitle("Deletion warning!").setMessage("This reminder will be deleted")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    RoomDatabase dbReminder = Room.databaseBuilder(cxt,reminderRoom.class,"reminders").build();
                                    syncRemind deleter = new syncRemind(((reminderRoom) dbReminder).getRemindDao(),false,true);
                                    deleter.execute(reminders.get(getAdapterPosition()));
                                    AlarmManager am = (AlarmManager)cxt.getSystemService(Context.ALARM_SERVICE);
                                    PendingIntent pI = PendingIntent.getBroadcast(cxt,Integer.valueOf(reminders.get(getAdapterPosition()).getSetTime().replaceAll("-","")),new Intent(cxt,alarmReceiver.class),0);
                                    am.cancel(pI);
                                    reminders.remove(reminders.get(getAdapterPosition()));
                                    reminderAdapter.this.notifyDataSetChanged();
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();


                }
            });
        }
    }
    public interface cancelAlarm{
         void cancel();
    }
}
