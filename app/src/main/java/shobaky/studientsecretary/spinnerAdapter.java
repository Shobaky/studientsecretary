package shobaky.studientsecretary;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

public class spinnerAdapter extends ArrayAdapter {
    private ArrayList<String> names;
    private ArrayList<Integer> imgs;
    private Context cxt;
    private int mColor;
    public spinnerAdapter(Context context, int resource, ArrayList<String> names , ArrayList<Integer> images) {
        super(context, resource);
        this.imgs = images;
        this.names = names;
        this.cxt = context;

    }
    public spinnerAdapter(Context context, int resource, ArrayList<String> names , ArrayList<Integer> images,int color) {
        super(context, resource);
        this.imgs = images;
        this.names = names;
        this.cxt = context;
        this.mColor = color;
    }


    @Override
    public View getView(int position,  View convertView,ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(cxt);
        View view = inflater.inflate(R.layout.spinner_dropdown_item,parent,false);

        ImageView materialImg = view.findViewById(R.id.spinner_materialItem_image);
        TextView materialTxt = view.findViewById(R.id.spinner_materialItem_name);
        if(position > 0) {

            String path = names.get(position);

            if(path.contains("|")&&URLUtil.isValidUrl(path.substring(0,path.indexOf("|")))){
                String uriPath = path.substring(0,path.indexOf("|"));
                Uri uri = Uri.parse(uriPath);
                String name = path.replaceAll(uriPath,"").replaceAll("\\|","");
                Bitmap bitmap = null;
                int permission = cxt.getSharedPreferences("permission",Context.MODE_PRIVATE).getInt("READ_PERMISSION",0);
                if (permission == 1) {
                    try {
                        if (ContextCompat.checkSelfPermission(cxt, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_DENIED) {
                            bitmap = MediaStore.Images.Media.getBitmap(cxt.getContentResolver(), uri);
                        }
                        materialImg.setImageBitmap(bitmap);
                        materialImg.setTag(names.get(position));
                        materialTxt.setText(name);




                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (bitmap == null) {
                        materialImg.setImageResource(R.drawable.circle_button);
                    }
                }
            }else{

                materialImg.setImageResource(imgs.get(position-1));
                materialImg.setTag(cxt.getResources().getResourceEntryName(imgs.get(position-1)));
                materialTxt.setText(names.get(position));
            }



        }else{
            materialTxt.setText(names.get(position));
        }

        materialTxt.setTextColor(cxt.getResources().getColor(R.color.colorPrimaryDark));
        if(position == 0 && cxt.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE){
            materialTxt.setTextColor(Color.DKGRAY);
            materialTxt.setTextSize(18);
            ((ImageView)view.findViewById(R.id.spinner_materialItem_divider)).setVisibility(View.GONE);
        }else if(position == 0 && cxt.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            materialTxt.setTextColor(Color.WHITE);
            materialTxt.setTextSize(18);
            ((ImageView)view.findViewById(R.id.spinner_materialItem_divider)).setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView,  ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(cxt);
        View view = inflater.inflate(R.layout.spinner_dropdown_item,parent,false);
        ImageView materialImg = view.findViewById(R.id.spinner_materialItem_image);
        TextView materialTxt = view.findViewById(R.id.spinner_materialItem_name);
        if(position > 0) {

            String path = names.get(position);
            if(path.contains("|")&&URLUtil.isValidUrl(path.substring(0,path.indexOf("|")))){
                //parse the concat string (material name + uri)
                String pathUri = path.substring(0,path.indexOf("|"));
                String name = path.substring(path.indexOf("|")+1,path.length());
                Uri uri = Uri.parse(pathUri);
                Bitmap bitmap = null;
                int permission = cxt.getSharedPreferences("permission",Context.MODE_PRIVATE).getInt("READ_PERMISSION",0);
                if (permission == 1) {
                    try {
                        if (ContextCompat.checkSelfPermission(cxt, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_DENIED) {

                            bitmap = MediaStore.Images.Media.getBitmap(cxt.getContentResolver(), uri);
                        }
                        materialImg.setImageBitmap(bitmap);
                        materialImg.setTag(names.get(position));
                        materialTxt.setText(name);


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (bitmap == null) {
                        materialImg.setImageResource(R.drawable.circle_button);
                        materialTxt.setText(name);


                    }
                }
            }else{

                materialImg.setImageResource(imgs.get(position-1));
                materialImg.setTag(cxt.getResources().getResourceEntryName(imgs.get(position-1)));
                materialTxt.setText(names.get(position));
            }}

        if(mColor == Color.BLACK) {
            materialTxt.setTextColor(Color.BLACK);
        }

        if(position == 0){
            materialTxt.setVisibility(View.GONE);
            ((ImageView)view.findViewById(R.id.spinner_materialItem_divider)).setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public int getCount() {
        return names.size();
    }

}
