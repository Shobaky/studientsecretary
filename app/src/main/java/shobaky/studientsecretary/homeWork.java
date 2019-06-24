package shobaky.studientsecretary;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class homeWork {
    private boolean done;
    private String todo;
    private String materialName;
    private int img;
    private String title;
    private int priority;
    private String imgUri;
    private int Diff;
    private float score;

    public homeWork(){

    }

    public homeWork(JSONObject jo){
        try {
            this.done = jo.getBoolean("done");
            this.todo = jo.getString("todo");
            this.materialName = jo.getString("materialName");
            this.img = jo.getInt("icon");
            this.priority = jo.getInt("priority");
            this.title = jo.getString("title");
            this.Diff = jo.getInt("diff");
            Log.d("DIFFFF",this.Diff+this.title);
            this.score = jo.getInt("score");
            this.imgUri = jo.getString("uri");


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getTodo() {
        return todo;
    }

    public void setHomework(String homework) {
        this.todo = homework;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public JSONObject toJson(){
        JSONObject obj = new JSONObject();
        try {
            obj.put("done",done);
            obj.put("priority",priority);
            obj.put("icon",img);
            obj.put("title",title);
            obj.put("todo",todo);
            obj.put("materialName",materialName);
            obj.put("diff",Diff);
            obj.put("score",score);
            obj.put("uri",imgUri);

            return obj;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

    }

    public void setImgUri(String imgUri) {
        this.imgUri = imgUri;
    }

    public String getImgUri() {
        return imgUri;
    }

    public int getDiff() {
        return Diff;
    }

    public void setDiff(int diff) {
        Diff = diff;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }
}
