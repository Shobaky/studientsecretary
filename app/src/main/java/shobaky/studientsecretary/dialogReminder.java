package shobaky.studientsecretary;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.IOException;





public class dialogReminder extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String MATERIAL = "matName";
    private static final String ICON = "matIcon";
    private static final String BOOK = "bookName";
    private static final String PAGE = "pages";
    private static final String DATE = "date";
    private static final String NOTES = "notes";

    // TODO: Rename and change types of parameters
    private String mMaterial;
    private String mIcon;
    private String mPage;
    private String mBook;
    private String mNotes;
    private String mDate;


    public dialogReminder() {
        // Required empty public constructor
    }

    public static dialogReminder newInstance(String name, String book, String icon, String page, String date, String notes) {
        dialogReminder fragment = new dialogReminder();
        Bundle args = new Bundle();
        args.putString(MATERIAL, name);
        args.putString(DATE, date);
        args.putString(ICON, icon);
        args.putString(BOOK, book);
        args.putString(PAGE, page);
        args.putString(NOTES, notes);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mNotes = getArguments().getString(NOTES);
            mPage = getArguments().getString(PAGE);
            mBook = getArguments().getString(BOOK);
            mMaterial = getArguments().getString(MATERIAL);
            mIcon = getArguments().getString(ICON);
            mNotes = getArguments().getString(NOTES);
            mDate = getArguments().getString(DATE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dialog_reminder, container, false);
        TextView materialName = view.findViewById(R.id.dialog_reminder_material);
        //Date when the reminder set not when to remind
        TextView date = view.findViewById(R.id.dialog_reminder_date);
        TextView book = view.findViewById(R.id.dialog_reminder_book);
        TextView pages = view.findViewById(R.id.dialog_reminder_pages);
        TextView notes = view.findViewById(R.id.dialog_reminder_notes);
        ImageView icon = view.findViewById(R.id.dialog_reminder_image);
        Button ok = view.findViewById(R.id.dialog_reminder_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        //the following is for having different size for the first and second strings ex(Material name && mMaterial)
        StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
        SpannableString spanName = new SpannableString("Material name : ");
        SpannableString spanDate = new SpannableString("Reminder was added on : ");
        SpannableString spanBook = new SpannableString("Notes are from book : ");
        SpannableString spanPages = new SpannableString("Notes are from pages : ");
        SpannableString spanNotes = new SpannableString("Notes : ");

        spanName.setSpan(new AbsoluteSizeSpan(40),0,spanName.length(),Spanned.SPAN_USER);
        spanName.setSpan(bss,0,spanName.length(),0);

        spanDate.setSpan(new AbsoluteSizeSpan(40),0,spanDate.length(),Spanned.SPAN_USER);
        spanDate.setSpan(bss,0,spanDate.length(),0);

        spanBook.setSpan(new AbsoluteSizeSpan(40),0,spanBook.length(),Spanned.SPAN_USER);
        spanBook.setSpan(bss,0,spanBook.length(),0);

        spanPages.setSpan(new AbsoluteSizeSpan(40),0,spanPages.length(),Spanned.SPAN_USER);
        spanPages.setSpan(bss,0,spanPages.length(),0);

        spanNotes.setSpan(new AbsoluteSizeSpan(40),0,spanNotes.length(),Spanned.SPAN_USER);
        spanNotes.setSpan(bss,0,spanNotes.length(),0);

        materialName.setText(TextUtils.concat(spanName,mMaterial));
        date.setText(TextUtils.concat(spanDate,mDate));
        book.setText(TextUtils.concat(spanBook,mBook));
        pages.setText(TextUtils.concat(spanPages,mPage));
        notes.setText(TextUtils.concat(spanNotes,mNotes));

        //Resource id
        int id = getActivity().getResources().getIdentifier(mIcon, "drawable", getActivity().getPackageName());
        //if the user uploaded the pic , then the url of the img is saved as if its name

        if (URLUtil.isValidUrl(mIcon)) {

            Uri uri = Uri.parse(mIcon);
            Bitmap bitmap = null;
            int permission = getActivity().getSharedPreferences("permission", Context.MODE_PRIVATE).getInt("READ_PERMISSION", 0);
            if (permission == 1) {
                try {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_DENIED) {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                        icon.setImageBitmap(bitmap);
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (bitmap == null) {
                    icon.setImageResource(R.drawable.circle_button);

                }
            }
        }else {
            icon.setImageResource(id);
        }
        return view;
    }
    public void onResume() {
        super.onResume();

        Window window = getDialog().getWindow();
        Point size = new Point();

        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);

        int width = size.x;
        int height = size.y;

        window.setLayout((int) (width * 0.9), (int) (height*0.9));
        window.setGravity(Gravity.CENTER);
    }
}









