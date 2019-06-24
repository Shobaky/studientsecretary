package shobaky.studientsecretary;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@Entity
public class reminder implements Parcelable {
    public reminder(){}
    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "Name")
    private String materialName;

    @ColumnInfo(name = "Date")
    private String reminderDate;

    @ColumnInfo(name = "Time")
    private String reminderTime;

    @ColumnInfo(name = "Pages")
    private String materialPages;

    @ColumnInfo(name = "Book")
    private String materialBook;

    @ColumnInfo(name = "Notes")
    private String materialNotes;

    @ColumnInfo(name = "setDate")
    private String setDate;

    @ColumnInfo(name = "Icon")
    private String iconName;

    @ColumnInfo(name = "setTime")
    private String setTime;

    public long getId() {
        return id;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getReminderDate() {
        return reminderDate;
    }

    public void setReminderDate(String reminderDate) {
        this.reminderDate = reminderDate;
    }

    public String getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(String reminderTime) {
        this.reminderTime = reminderTime;
    }

    public String getMaterialPages() {
        return materialPages;
    }

    public void setMaterialPages(String materialPages) {
        this.materialPages = materialPages;
    }

    public String getMaterialBook() {
        return materialBook;
    }

    public void setMaterialBook(String materialBook) {
        this.materialBook = materialBook;
    }

    public String getMaterialNotes() {
        return materialNotes;
    }

    public void setMaterialNotes(String materialNotes) {
        this.materialNotes = materialNotes;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSetDate() {
        return setDate;
    }

    public void setSetDate(String setDate) {
        this.setDate = setDate;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public String getSetTime() {
        return setTime;
    }

    public void setSetTime(String setTime) {
        this.setTime = setTime;
    }

    public reminder(Parcel in){
        this.materialName = in.readString();
        this.materialBook = in.readString();
        this.materialPages = in.readString();
        this.materialNotes = in.readString();
        this.reminderDate = in.readString();
        this.setDate = in.readString();
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.materialName);
        dest.writeString(this.materialBook);
        dest.writeString(this.materialPages);
        dest.writeString(this.materialNotes);
        dest.writeString(this.reminderDate);
        dest.writeString(this.setDate);
    }
    public static final Creator<reminder> CREATOR = new Creator<reminder>() {
        @Override
        public reminder createFromParcel(Parcel source) {

            return new reminder(source);
        }

        @Override
        public reminder[] newArray(int size) {
            return new reminder[size];
        }
    };
}
