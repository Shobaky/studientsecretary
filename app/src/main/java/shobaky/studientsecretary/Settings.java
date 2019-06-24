package shobaky.studientsecretary;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class Settings extends AppCompatActivity {
    public static String settingsPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Intent intent = getIntent();
        if(intent.getStringArrayExtra("ringtones") != null){
            Bundle bundle = new Bundle();
            bundle.putStringArray("ringtones",intent.getStringArrayExtra("ringtones"));
            preferenceFragment pF = new preferenceFragment();
            pF.setArguments(bundle);
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.settings_container,pF).commit();
        }else{
            Log.e("Ringtones error","Couldn't load ringtones");
            preferenceFragment pF = new preferenceFragment();
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.settings_container,pF).commit();

        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 3 && resultCode == RESULT_OK){
            Log.d("ACTIVITY RESULT",data.getDataString());
        }
    }

}
