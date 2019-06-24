package shobaky.studientsecretary;

import android.content.Context;
import android.util.Log;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

class homeworkSerializer {
    private Context context;
    private String path;
    homeworkSerializer(Context cxt, String filename){
        this.context = cxt;
        this.path = filename;
    }
    void save(ArrayList<homeWork> homeWorks) throws IOException {

        JSONArray jsonArray = new JSONArray();
        for(homeWork hw : homeWorks){
            jsonArray.put(hw.toJson());

        }
        Writer writer = null;
        try {
            OutputStream os = context.openFileOutput(path,Context.MODE_PRIVATE);



            writer = new OutputStreamWriter(os);
            writer.write(jsonArray.toString());

        } finally {
            if(writer != null){
                writer.close();
            }
        }

    }
    ArrayList<homeWork> load() throws IOException {
        ArrayList<homeWork> hws = new ArrayList<>();
        BufferedReader bf= null;
        try {
            InputStream is = context.openFileInput(path);

            bf = new BufferedReader(new InputStreamReader(is));
            StringBuilder builder = new StringBuilder();
            String line;
            while((line = bf.readLine())!=null){
                builder.append(line);


            }

            JSONArray jsonArray = (JSONArray) new JSONTokener(builder.toString()).nextValue();
            for(int i = 0 ; i <jsonArray.length() ; i++){
                hws.add(new homeWork(jsonArray.getJSONObject(i)));

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }finally {
            if(bf != null) {
                bf.close();
            }
        }
        return hws;
    }
    void checked(boolean checked ,float score, int i ) throws IOException {
        ArrayList<homeWork> homeworks = load();
        homeworks.get(i).setDone(checked);
        homeworks.get(i).setScore(score);
        save(homeworks);
    }

}
