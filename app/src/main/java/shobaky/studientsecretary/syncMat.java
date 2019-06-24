package shobaky.studientsecretary;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.List;

//Room operations for material
public class syncMat extends AsyncTask<Material,Void,List<Material>> {
    private materialDao mDao;
    private Boolean insert;
    private asyncRespone respone;
    private WeakReference<Context> contexts;
    public syncMat(materialDao dao , Boolean insert, asyncRespone finish, Context cxt){
        this.insert = insert;
        this.mDao = dao;
        this.respone = finish;
        this.contexts = new WeakReference<>(cxt);
    }
    public syncMat(materialDao dao , Boolean insert,asyncRespone finish){
        this.insert = insert;
        this.mDao = dao;
        this.respone = finish;
    }
    @Override
    protected List<Material> doInBackground(Material... materials) {
        if(insert){
            mDao.insertMat(materials);
            return null;
        }else{
            return mDao.getAll();
        }

    }

    @Override
    protected void onPostExecute(List<Material> materials) {
        super.onPostExecute(materials);
        if(materials != null) {
            respone.processFinish(materials);

        }else{
            if(contexts != null) {
                Toast.makeText(contexts.get(), "Material added successfully", Toast.LENGTH_SHORT).show();
            }
            respone.processFinish(null);
        }
    }
}
