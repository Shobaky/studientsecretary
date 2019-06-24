package shobaky.studientsecretary;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class materialDialogAdapt extends BaseAdapter {
    //adapter for icons scrollview
    private Context context;
    private HashMap<String,Object> icons;
    private String uri;
    private Boolean notified;
    private ArrayList<Integer> systemImages;
    materialDialogAdapt(Context cxt, ArrayList<Integer> materialIcons){
        this.context = cxt;
        this.icons = new HashMap<>();
        for(int resource : materialIcons){
            icons.put("int".concat(String.valueOf(icons.size())),resource);

        }
        this.uri = "";
        this.notified = false;
        this.systemImages = materialIcons;
    }

    @Override
    public int getCount() {
        return icons.size();
    }

    @Override
    public Object getItem(int position) {
        if(icons.containsKey("int".concat(String.valueOf(position)))){
            return icons.get("int".concat(String.valueOf(position)));
        }else{
            return icons.get("string".concat(String.valueOf(position)));
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.icon_item,parent,false);

        ImageView icon = view.findViewById(R.id.imageView);
        int permission = context.getSharedPreferences("permission",Context.MODE_PRIVATE).getInt("READ_PERMISSION",0);
        if(notified&&position >= systemImages.size()&&permission==1){
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(icons.get("string".concat(String.valueOf(position))).toString()));
            } catch (IOException e) {
                e.printStackTrace();
            }

            icon.setImageBitmap(rotateMe(uri,bitmap));
            icon.setTag(icons.get("string".concat(String.valueOf(position))).toString());
            icon.setScaleType(ImageView.ScaleType.FIT_XY);
        }else{
            icon.setImageResource((int)icons.get("int".concat(String.valueOf(position))));
            icon.setTag(icons.get("int".concat(String.valueOf(position))));
        }

        //it may has an id in sql or something
        return view;
    }
    void notifyUserUpload(String dataUri){

        icons.put("string".concat(String.valueOf(icons.size())),dataUri);
        notified = true;
        this.notifyDataSetChanged();

    }
    //method to rotate image if it's taken by camera
    private Bitmap rotateMe(String takenByMe, Bitmap bfRot){
        try {
            //Exif information about the bitmap (JPEG,ALPHA , ORIENTATION , ETC)
            ExifInterface ei = new ExifInterface(takenByMe);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_UNDEFINED);
            Bitmap bitmap = null;
            Matrix mat;
            switch (orientation){
                case ExifInterface.ORIENTATION_ROTATE_90:
                    mat = new Matrix();
                    mat.postRotate(90);
                    bitmap = Bitmap.createBitmap(bfRot,0,0,bfRot.getWidth(),bfRot.getHeight(),mat,true);
                    return bitmap;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    mat = new Matrix();
                    mat.postRotate(180);
                    bitmap = Bitmap.createBitmap(bfRot,0,0,bfRot.getWidth(),bfRot.getHeight(),mat,true);
                    return bitmap;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    mat = new Matrix();
                    mat.postRotate(270);
                    bitmap = Bitmap.createBitmap(bfRot,0,0,bfRot.getWidth(),bfRot.getHeight(),mat,true);
                    return bitmap;
                default:
                    return bfRot;

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bfRot;

    }

}
