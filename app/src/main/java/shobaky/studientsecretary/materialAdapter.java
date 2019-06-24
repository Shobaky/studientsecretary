package shobaky.studientsecretary;

import android.Manifest;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import java.io.IOException;
import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;


//adapter of the study fragment main screen
public class materialAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> iconIds;
    private ArrayList<Material> materials;
    materialAdapter(Context cxt , ArrayList<Material> materialIcons){
        this.context = cxt;
        this.materials = materialIcons;
        if(iconIds == null) {
            iconIds = new ArrayList<>();
            for(Material m : materialIcons){
                iconIds.add(m.getIconName());
            }
        }

        if(!iconIds.contains(context.getResources().getResourceEntryName(R.drawable.ic_add_white_24dp))) {
            iconIds.add(context.getResources().getResourceEntryName(R.drawable.ic_add_white_24dp));
        }

    }

    @Override
    public int getCount() {
        //to add the plus icon

        return iconIds.size();
    }

    @Override
    public Object getItem(int position) {
        return iconIds.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.material_item,parent,false);
        CircleImageView icon = view.findViewById(R.id.materialImage);
        final ImageButton delete = view.findViewById(R.id.removeMaterialButton);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 new AlertDialog.Builder(context).setTitle(materials.get(position).getName().concat("will be deleted!"))
                        .setMessage("Are you sure you want to delete this material").setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                RoomDatabase db = Room.databaseBuilder(context,materialRoom.class,"materials").build();
                                deleteSync deleter = new deleteSync(((materialRoom) db).getMatDao());
                                deleter.execute(materials.get(position).getName());
                                materialAdapter.this.iconIds.remove(position);
                                materialAdapter.this.materials.remove(position);
                                materialAdapter.this.notifyDataSetChanged();
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
        if(position < iconIds.size()-1) {
            int id = context.getResources().getIdentifier(iconIds.get(position), "drawable", context.getPackageName());
            String path = iconIds.get(position);

            if (URLUtil.isValidUrl(path)) {

                Uri uri = Uri.parse(path);
                Bitmap bitmap = null;
                int permission = context.getSharedPreferences("permission",Context.MODE_PRIVATE).getInt("READ_PERMISSION",0);
                if (permission == 1) {
                    try {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_DENIED) {
                            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                        }
                        icon.setImageBitmap(bitmap);
                        view.setTag(materials.get(position).getIconName());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (bitmap == null) {
                        icon.setImageResource(R.drawable.circle_button);
                        view.setTag(materials.get(position).getIconName());
                    }
                }
            } else {


                icon.setImageResource(id);
                view.setTag(materials.get(position).getIconName());

            }
        }else {
            //it may has an id in sql or something
            //the add button
            icon.setImageResource(R.drawable.ic_add_white_24dp);
            view.setTag( "ADD");
            icon.setCircleBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
            delete.setVisibility(View.INVISIBLE);



        }
        return view;
    }
    void notifyMaterial(Material news){
        iconIds.remove(iconIds.size()-1);
        iconIds.add(news.getIconName());
        materials.add(news);
        iconIds.add((context.getResources().getResourceEntryName(R.drawable.ic_add_white_24dp)));

        this.notifyDataSetChanged();
    }
    public static class deleteSync extends AsyncTask<String,Void,Void>{

        private final materialDao dao;

        public deleteSync(materialDao dao){
            this.dao = dao;
        }
        @Override
        protected Void doInBackground(String... strings) {
            if(strings.length >0) {
                dao.deleteMaterial(strings[0]);
            }
            return null;
        }
    }
}
