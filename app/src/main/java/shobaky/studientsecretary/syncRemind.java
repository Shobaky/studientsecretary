package shobaky.studientsecretary;

import android.os.AsyncTask;
import android.util.Log;


import java.util.ArrayList;
import java.util.List;

public class syncRemind extends AsyncTask<reminder,Void,List<reminder>> {
    private reminderDao ourDao;
    private asyncRespone ourResponse;
    private Boolean ourInsert;
    private Boolean delete;
    private Long mId;

    syncRemind(reminderDao dao, asyncRespone finish, Boolean insert){
        this.ourDao = dao;
        this.ourResponse = finish;
        this.ourInsert = insert;
        this.delete = false;

    }

    syncRemind(reminderDao dao, Boolean insert,Boolean delete){
        this.ourDao = dao;
        this.ourInsert = insert;
        this.delete = delete;

    }
    syncRemind(reminderDao dao, Boolean insert,Boolean delete,asyncRespone deleteDone){
        this.ourDao = dao;
        this.ourInsert = insert;
        this.delete = delete;
        this.ourResponse = deleteDone;

    }
    @Override
    protected List<reminder> doInBackground(reminder... reminders) {
        if(ourInsert){
            long[] id = ourDao.insertReminder(reminders);
            reminders[0].setId(id[0]);
            List<reminder> inserted = new ArrayList<>();
            inserted.add(reminders[0]);
            return inserted;
        }else if(delete){
            if(mId != null) {
                ourDao.delete(mId);
                Log.d("HOPS",""+mId);
            }else{
                ourDao.delete(reminders[0].getId());
            }

            return null;
        } else{
            return ourDao.reminders();
        }

    }

    @Override
    protected void onPostExecute(List<reminder> reminders) {
        super.onPostExecute(reminders);
            if (!ourInsert && !delete) {
                ourResponse.processRemindFinish(reminders);
            }else if(ourResponse != null){
                ourResponse.processRemindFinish(reminders);
            }

    }
    public void getID(Long id){
        mId = id;
    }
}
