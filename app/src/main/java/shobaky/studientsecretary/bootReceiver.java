package shobaky.studientsecretary;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class bootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {

        if(intent.getAction() != null && intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){
            Log.d("MOHAMED","BOOT");

            RoomDatabase reminderDb = Room.databaseBuilder(context,reminderRoom.class,"reminders").build();
            syncRemind sR = new syncRemind(((reminderRoom) reminderDb).getRemindDao(), new asyncRespone() {
                @Override
                public void processFinish(List<Material> materials) {

                }

                @Override
                public void processRemindFinish(List<reminder> reminders) {
                    AlarmManager AM = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    int i = 0;
                    for(reminder r : reminders) {
                        Calendar rC = Calendar.getInstance();
                        String reminderDate = r.getReminderDate();
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy",Locale.getDefault());
                        Date date  = null;
                        try {
                            date = sdf.parse(reminderDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if(date != null){
                            rC.setTime(date);
                            Log.d("MYDATE",date.toString());
                        }else{
                            Log.d("MYDATEEE",reminderDate);
                        }
                        String reminderTime  = r.getReminderTime();
                        rC.set(Calendar.HOUR,Integer.parseInt(reminderTime.substring(0,reminderTime.indexOf(":"))));
                        rC.set(Calendar.MINUTE,Integer.parseInt(reminderTime.substring(reminderTime.indexOf(":")+1,reminderTime.length())));
                        Intent alarmIntent = new Intent(context,alarmReceiver.class);
                        Bundle alarmBundle = new Bundle();
                        alarmBundle.putParcelable("reminderAlarm",r);
                        alarmBundle.putLong("reminderID",r.getId());
                        alarmIntent.putExtra("alarmBundle",alarmBundle);
                        PendingIntent pI = PendingIntent.getBroadcast(context,i,alarmIntent,PendingIntent.FLAG_UPDATE_CURRENT);
                        AM.set(AlarmManager.RTC_WAKEUP,rC.getTimeInMillis(),pI);
                    }

                }
            }, false);
            sR.execute();

        }
    }
}
