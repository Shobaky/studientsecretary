package shobaky.studientsecretary;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.IOException;
import java.util.ArrayList;
/*
Main homework's page adapter
*/

public class homeworkAdapter extends RecyclerView.Adapter<homeworkAdapter.Holder> {
    private Context context;
    private ArrayList<homeWork> homeWorks;
    private todoDoneFragment.dismisser dismisser;
    private homeworkSerializer serializer;

    homeworkAdapter(Context cxt,ArrayList<homeWork> homeWorks,todoDoneFragment.dismisser dismisser){
        this.context = cxt;
        this.homeWorks = homeWorks;
        this.dismisser = dismisser;
        this.serializer = new homeworkSerializer(context,newHomeWork.path);
    }
    @NonNull
    @Override
    public homeworkAdapter.Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Holder(LayoutInflater.from(context).inflate(R.layout.homework_item,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder,  int i) {
        holder.homework.setText(homeWorks.get(i).getTodo());

        if(!homeWorks.get(i).getImgUri().equals("")){

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(homeWorks.get(i).getImgUri()));
                holder.icon.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            holder.icon.setImageResource(homeWorks.get(i).getImg());
        }
        holder.title.setText(homeWorks.get(i).getTitle());
        holder.done.setChecked(homeWorks.get(i).isDone());
        int priority = homeWorks.get(i).getPriority();
        switch (priority){
            case 100:
                holder.parent.setBackgroundColor(context.getResources().getColor(android.R.color.holo_red_dark));
                break;
            case 50:
                holder.parent.setBackgroundColor(context.getResources().getColor(android.R.color.holo_blue_dark));
                break;
            default:
                holder.parent.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));

        }
        holder.done.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    todoDoneFragment tDF = todoDoneFragment.newInstance(homeWorks.get(holder.getAdapterPosition()).getDiff(),holder.getAdapterPosition(),dismisser);
                    if(context instanceof AppCompatActivity){
                        tDF.show(((AppCompatActivity) context).getSupportFragmentManager(),"rate");
                    }

                }else{

                    SharedPreferences scorePref = context.getSharedPreferences(profileFragment.SCORE_PREF, Context.MODE_PRIVATE);
                    float currentScore = scorePref.getFloat(finishStudyFragment.SCORE,0);
                    float homeworkScore = homeWorks.get(holder.getAdapterPosition()).getScore();
                    int level = scorePref.getInt(profileFragment.LEVEL,0);
                    if(currentScore-homeworkScore <= 0 ){

                        scorePref.edit().putFloat(finishStudyFragment.SCORE,0).apply();
                        if(level > 1) {

                            scorePref.edit().putInt(profileFragment.LEVEL, level - 1).apply();
                        }
                    }else{
                        scorePref.edit().putFloat(finishStudyFragment.SCORE,currentScore-homeworkScore).apply();

                    }
                    try {
                        serializer.checked(false,0,holder.getAdapterPosition());

                    } catch (IOException e) {

                        e.printStackTrace();
                    }
                }


            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.titleDelete);
                builder.setMessage(R.string.messageDelete);
                builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        homeWorks.remove(holder.getAdapterPosition());
                        homeworkSerializer saver = new homeworkSerializer(context,newHomeWork.path);
                        try {
                            saver.save(homeWorks);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        notifyDataSetChanged();

                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });
                builder.show();
            }

        });


    }



    @Override
    public int getItemCount() {
        return homeWorks.size();

    }

    class Holder extends RecyclerView.ViewHolder {
        public TextView title;
        private TextView homework;
        private ImageView icon;
        private ConstraintLayout parent;
        private CheckBox done;
        private ImageView delete;
        Holder(@NonNull View itemView) {
            super(itemView);

            homework = itemView.findViewById(R.id.homework);
            icon = itemView.findViewById(R.id.homework_icon);
            parent  = itemView.findViewById(R.id.homework_parent);
            title = itemView.findViewById(R.id.homework_item_title);
            done = itemView.findViewById(R.id.home_item_check);
            delete = itemView.findViewById(R.id.home_item_delete);

        }
    }
    void dataObserve(ArrayList<homeWork> newList){
        homeWorks.clear();
        homeWorks.addAll(newList);
        this.notifyDataSetChanged();
    }
    void homeworkDone(int pos,float score){

        homeWork currentTodo = homeWorks.get(pos);
        currentTodo.setScore(score);
        currentTodo.setDone(true);
        try {
            serializer.checked(true,score,pos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.notifyDataSetChanged();
    }
}
