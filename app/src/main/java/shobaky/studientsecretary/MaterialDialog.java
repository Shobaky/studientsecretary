package shobaky.studientsecretary;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

//Here we add new material
public class MaterialDialog  extends DialogFragment implements GridView.OnItemClickListener,asyncRespone  {
    private static final String UPLOADED_IMAGE = "upload";
    private TextInputLayout book2;
    private TextInputLayout book3;
    private TextInputLayout book1;
    private TextInputLayout page1;
    private TextInputLayout page2;
    private TextInputLayout page3;
    private ConstraintLayout constraintLayout;
    private TextInputLayout materialName;
    private View scroller;
    private SeekBar seekBar;
    //the selected icon from the grid of icons
    private int selectedIcon =0;
    //the selected and uploaded icon from the grid of icons
    private String selectedUri = "";
    //selected icon position
    private View preView;
    //Intent Gallery Code
    private final int intentCode = 0;
    //Uri of the image in gallery
    private String galleryUri = "";
    private materialDialogAdapt adapter;
    private DialogInterface.OnDismissListener dissmisser;
    private Material newMaterial;
    public static MaterialDialog.newMat adder;
    private int Difficulty;
    private boolean landscape = false;
    private View materialGrid;


    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        final View dialog = inflater.inflate(R.layout.material_add_dialog,container,false);


        if(savedInstanceState != null) {
             galleryUri = savedInstanceState.getString(UPLOADED_IMAGE);
        }

        book1 = dialog.findViewById(R.id.book1layout);
        book2 = dialog.findViewById(R.id.book2layout);
        book3 = dialog.findViewById(R.id.book3layout);
        page1 = dialog.findViewById(R.id.page1);
        page2 = dialog.findViewById(R.id.page2);
        page3 = dialog.findViewById(R.id.page3);
        materialName = dialog.findViewById(R.id.materialNameLayout);
        seekBar = dialog.findViewById(R.id.materialAddSeekBar);
        //Upload new material icon
        ImageView uploadIcon = dialog.findViewById(R.id.uploadImage);
        //add image button to add new book
        ImageButton add = dialog.findViewById(R.id.bookAdd);

        constraintLayout = dialog.findViewById(R.id.material_dialog_parent);
        if((scroller = dialog.findViewById(R.id.materialAddScroll))==null){
            scroller = dialog.findViewById(R.id.materialAddScrollLand);
        }


        //add and cancel button to add the material
        Button addMaterial = dialog.findViewById(R.id.addMaterialButton);
        Button cancelMaterial = dialog.findViewById(R.id.cancelMaterialButton);

        //DateBase
        final materialRoom db = Room.databaseBuilder(getActivity(),materialRoom.class,"materials").build();
        final materialDao mDao = db.getMatDao();

        //upload new icon
        uploadIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent,intentCode);

            }
        });
        ArrayList<Integer> images = new ArrayList<>();

        images.add(R.drawable.math_icon);
        images.add(R.drawable.ic_biology);
        images.add(R.drawable.ic_geo);
        images.add(R.drawable.ic_history);
        images.add(R.drawable.ic_philisophy);
        images.add(R.drawable.ic_arabic);
        images.add(R.drawable.ic_eng);
        images.add(R.drawable.ic_chemistry);

        adapter = new materialDialogAdapt(getActivity(),images);


        //check portrait or landscape(Linear or Grid)
        //Until we decide , Linear or Grid

        if((materialGrid = dialog.findViewById(R.id.iconsGrid)) != null){

            if(galleryUri  != null && !galleryUri.isEmpty()){
                adapter.notifyUserUpload(galleryUri);
            }
            ((GridView) materialGrid).setAdapter(adapter);
            ((GridView) materialGrid).setOnItemClickListener(this);

            //prevents the keyboard from taking the whole screen
            EditorInfo editorInfo = new EditorInfo();
            editorInfo.imeOptions = EditorInfo.IME_FLAG_NO_EXTRACT_UI;
            book1.getEditText().onCreateInputConnection(editorInfo);
            book2.onCreateInputConnection(editorInfo);
            book3.onCreateInputConnection(editorInfo);

            //animate the text inputs
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)  {
                    animatePortrait();
                }
            });

        }else{
            materialGrid = dialog.findViewById(R.id.iconsLinear);
            landscape = true;
            //it isn't grid , it's linear
            if(galleryUri != null && !galleryUri.isEmpty()){

                addImg(null,galleryUri);
            }


            addImg(images,null);
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    animateLandScape();
                }
            });
            getDialog().getWindow().setGravity(Gravity.CENTER_VERTICAL);

        }

        //change seekbar icon on changing value
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1 ){
            Log.d("CURRENTBUILDING",""+Build.VERSION.SDK_INT);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Drawable drawable;
                switch (progress){
                    case 0:
                        Difficulty = 1;
                        drawable = ContextCompat.getDrawable(getActivity(),R.drawable.ic_smile);
                        break;
                    case 1:
                        Difficulty = 2;
                        drawable = ContextCompat.getDrawable(getActivity(),R.drawable.ic_difficulty_2);
                        break;
                    case 2:
                        Difficulty = 3;
                        drawable = ContextCompat.getDrawable(getActivity(),R.drawable.ic_einstein);
                        break;
                    default:
                        Difficulty = 1;
                        drawable = ContextCompat.getDrawable(getActivity(),R.drawable.ic_smile);
                        break;
                }
                seekBar.setThumb(drawable);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });}
        cancelMaterial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

            }
        });
        addMaterial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Error icoN
                Drawable drawable;
                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
                     drawable = getResources().getDrawable(R.drawable.ic_error_black_24dp);
                }else{
                     drawable = getResources().getDrawable(R.drawable.ic_error_black_24dp);
                }

                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

                //Error booleans to check
                Boolean iconMissing = (selectedIcon == 0 && selectedUri.isEmpty());
                Boolean nameMissing = (materialName.getEditText().getText().toString().isEmpty());
                Boolean book1Missing = book1.getEditText().getText().toString().isEmpty();
                Boolean page1Missing = page1.getEditText().getText().toString().isEmpty();
                //book2 exists but without it's pages
                Boolean book2Page2Missing = (!book2.getEditText().getText().toString().isEmpty() && page2.getEditText().getText().toString().isEmpty());
                //book3 exists but without it's pages
                Boolean book3Page3Missing = (!book3.getEditText().getText().toString().isEmpty() && page3.getEditText().getText().toString().isEmpty());

                //if any of the above error exists provide an error else, execute and add the material
                if(iconMissing || nameMissing|| book1Missing || page1Missing || book2Page2Missing || book3Page3Missing){
                    //No Icon!
                   if(iconMissing){
                     //put a yellow border around the icons scroller
                     scroller.setBackgroundResource(R.drawable.no_icon_selected_scroll);
                     materialName.getEditText().setError("Please select an icon",drawable);
                   }else if(nameMissing) {
                       //remove the yellow border from the scroller if exists
                       scroller.setBackgroundResource(R.drawable.material_add_scroll_background);
                       materialName.getEditText().setError("Please enter the material name", drawable);
                   }else if(book1Missing){
                       //remove the yellow border from the scroller if exists
                       scroller.setBackgroundResource(R.drawable.material_add_scroll_background);
                       book1.getEditText().setError("Please enter material's book name",drawable);
                   }else if(page1Missing){
                       //remove the yellow border from the scroller if exists
                       scroller.setBackgroundResource(R.drawable.material_add_scroll_background);
                       page1.getEditText().setError("Please enter number of pages",drawable);
                   }else if(book2Page2Missing){
                       //remove the yellow border from the scroller if exists
                       scroller.setBackgroundResource(R.drawable.material_add_scroll_background);
                       page2.getEditText().setError("Enter Book'pages number , Please!",drawable);
                   }else {
                       //remove the yellow border from the scroller if exists
                       scroller.setBackgroundResource(R.drawable.material_add_scroll_background);
                       page3.getEditText().setError("Enter Book's pages number , Please!",drawable);
                   }
                }else{

                //Collect Material Information
                newMaterial = new Material();
                if(Difficulty == 0){
                    Difficulty++;
                }
                newMaterial.setDifficulty(Difficulty);
                newMaterial.setName(materialName.getEditText().getText().toString());
                //selectedIcon is specified in spinner onClick method of the dialog (down there)
                //if user didn't upload image
                if(galleryUri.isEmpty()) {
                    newMaterial.setIconName(getResources().getResourceEntryName(selectedIcon));

                } else{//else use and save it
                    newMaterial.setIconName(galleryUri);
                }
                newMaterial.setBook1(book1.getEditText().getText().toString());
                newMaterial.setPages1(page1.getEditText().getText().toString());

                syncMat insertMat = new syncMat(mDao, true, MaterialDialog.this,getActivity());
                insertMat.execute(newMaterial);
                Toast.makeText(getActivity(),"Material is successfully added",Toast.LENGTH_SHORT).show();
                dismiss();
                }}
        });

        return dialog;

    }
    @Override
    public void onResume() {
        super.onResume();

      if(getDialog().getWindow() != null) {
         getDialog().getWindow().setLayout(getResources().getDimensionPixelSize(R.dimen.dialog_width),getResources().getDimensionPixelSize(R.dimen.dialog_height));
         getDialog().getWindow().setGravity(Gravity.CENTER_VERTICAL);



      }


    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == intentCode){
                galleryUri = data.getData().toString();
                int permission = getActivity().getSharedPreferences("permission",Context.MODE_PRIVATE).getInt("READ_PERMISSION",0);
                if(permission== 1) {
                    adapter.notifyUserUpload(galleryUri);
                    if(landscape){
                        addImg(null,galleryUri);
                    }
                }else{
                    new AlertDialog.Builder(getActivity()).setMessage("You denied our request to access your gallery :(").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                }
            }
        }
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ImageView selectedImage = view.findViewById(R.id.imageView);



        if(preView != null){

            preView.setBackgroundResource(R.drawable.icon_border);

        }
        view.setBackgroundResource(R.drawable.selected_icon_border);
        if(selectedImage.getTag() instanceof Integer) {
            selectedIcon = (int) selectedImage.getTag();
        }else{
            selectedUri = selectedImage.getTag().toString();
        }
        preView = view;


    }



    //Animation methods
    private void animatePortrait(){
        if(book2.getVisibility() == View.VISIBLE) {
            ConstraintSet set  = new ConstraintSet();

            ObjectAnimator animator = ObjectAnimator.ofFloat(book3,"translationY",300f);
            ObjectAnimator animatorPage3 = ObjectAnimator.ofFloat(page3,"translationY",300f);


            animator.setDuration(200);

            animator.start();
            animatorPage3.start();



            set.clone(constraintLayout);
            set.connect(R.id.materialDif,ConstraintSet.TOP,R.id.book3layout,ConstraintSet.BOTTOM,300);


            set.applyTo(constraintLayout);
            book3.setVisibility(View.VISIBLE);
            page3.setVisibility(View.VISIBLE);
        }else if(book2.getVisibility() == View.INVISIBLE){
            ObjectAnimator animator = ObjectAnimator.ofFloat(book2,"translationY",150f);
            ObjectAnimator animatorPage2 = ObjectAnimator.ofFloat(page2,"translationY",150f);

            animator.setDuration(200);

            animator.start();
            animatorPage2.start();


            ConstraintSet set  = new ConstraintSet();

            set.clone(constraintLayout);
            set.connect(R.id.materialDif,ConstraintSet.TOP,R.id.book2layout,ConstraintSet.BOTTOM,150);

            set.applyTo(constraintLayout);
            book2.setVisibility(View.VISIBLE);
            page2.setVisibility(View.VISIBLE);
        }

    }
    private void animateLandScape(){
        if(book2.getVisibility() == View.VISIBLE) {
            Animation animation = AnimationUtils.loadAnimation(getActivity(),R.anim.material_book_animation_scale_reduce);
            animation.setDuration(150);
            animation.setFillAfter(true);
            book2.startAnimation(animation);



            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    book2.setVisibility(View.INVISIBLE);
                    Animation animator = AnimationUtils.loadAnimation(getActivity(),R.anim.material_book_animation_scale_increase);

                    animator.setFillAfter(true);
                    animator.setDuration(150);
                    book3.startAnimation(animator);
                    book3.setVisibility(View.VISIBLE);

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }else if(book2.getVisibility() == View.INVISIBLE){

            Animation animation = AnimationUtils.loadAnimation(getActivity(),R.anim.material_book_animation_scale_reduce);
            animation.setDuration(150);
            animation.setFillAfter(true);
            book1.startAnimation(animation);

            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    book1.setVisibility(View.INVISIBLE);
                    Animation animator = AnimationUtils.loadAnimation(getActivity(),R.anim.material_book_animation_scale_increase);

                    animator.setFillAfter(true);
                    animator.setDuration(150);
                    book2.startAnimation(animator);
                    book2.setVisibility(View.VISIBLE);

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });





        }

    }

    //useless
    @Override
    public void processFinish(List<Material> materials) {


    }

    @Override
    public void processRemindFinish(List<reminder> reminders) {

    }

    public void setDismiss(DialogInterface.OnDismissListener dismiss){
        this.dissmisser = dismiss;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if(dissmisser != null){
            dissmisser.onDismiss(dialog);
            adder.sendMat(newMaterial);
        }

    }
    public interface newMat{
         void sendMat(Material m);
    }
    public void addImg(@Nullable ArrayList<Integer> systemData,@Nullable  String url) {
        if (systemData != null) {
            for(int data : systemData) {

                final FrameLayout frameLayout = new FrameLayout(getActivity());
                ImageView imageView = new ImageView(getActivity());

                frameLayout.setTag(data);
                frameLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (preView != null) {
                            preView.setBackgroundResource(R.drawable.icon_border);
                        }
                        preView = v;
                        v.setBackgroundResource(R.drawable.selected_icon_border);
                    }
                });

                frameLayout.setBackgroundResource(R.drawable.icon_border);
                //Params of frame layout in the linear layout
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(105, 105);

                layoutParams.setMargins(20, 10, 0, 10);
                //Params of image in the frame layout
                FrameLayout.LayoutParams imageParams = new FrameLayout.LayoutParams(100, 100, Gravity.CENTER);
                frameLayout.addView(imageView, imageParams);
                ((LinearLayout) materialGrid).addView(frameLayout, layoutParams);
                imageView.setImageResource(data);
            }
        }else if(url != null){
            final FrameLayout frameLayout = new FrameLayout(getActivity());
            ImageView img = new ImageView(getActivity());

            frameLayout.setTag(url);
            frameLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(preView != null){
                        preView.setBackgroundResource(R.drawable.icon_border);
                    }
                    preView = v;
                    v.setBackgroundResource(R.drawable.selected_icon_border);

                }
            });
            frameLayout.setBackgroundResource(R.drawable.icon_border);
            //Params of frame layout in the linear layout
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(105, 105);

            layoutParams.setMargins(20, 10, 0, 10);
            //Params of image in the frame layout
            FrameLayout.LayoutParams imageParams = new FrameLayout.LayoutParams(100, 100, Gravity.CENTER);
            frameLayout.addView(img, imageParams);
            ((LinearLayout) materialGrid).addView(frameLayout, layoutParams);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),Uri.parse(url));
                img.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(galleryUri != null) {
            outState.putString(UPLOADED_IMAGE,galleryUri);
        }
    }


}
