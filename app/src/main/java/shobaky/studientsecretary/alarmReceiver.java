package shobaky.studientsecretary;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.URLUtil;

import java.io.IOException;


public class alarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {

        Intent startAct = new Intent(context,MainActivity.class);




        startAct.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if(intent.hasExtra("alarmBundle")) {
            Bundle args = intent.getBundleExtra("alarmBundle");
            assert args != null;
            reminder R = args.getParcelable("reminderAlarm");
            assert R != null;
            startAct.putExtra("bundleAlarm",args);
        }

        context.startActivity(startAct);

    }
}