package shobaky.studientsecretary;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;



@Entity
public class Material {
    @PrimaryKey(autoGenerate = true)
    private int Id;

    @ColumnInfo(name = "Name")
    private String name;
    @ColumnInfo(name = "Book1")
    private String book1;

    @ColumnInfo(name = "Book2")
    private String book2;

    @ColumnInfo(name = "Book3")
    private String book3;


    @ColumnInfo(name = "Icon")
    private String iconName;

    @ColumnInfo(name = "Difficulty")
    private int Difficulty;

    @ColumnInfo(name = "book1Pages")
    private String pages1;


    @ColumnInfo(name = "book2Pages")
    private String pages2;


    @ColumnInfo(name = "book3Pages")
    private String pages3;

    @ColumnInfo(name = "notesReminder")
    private String notes;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBook1() {
        return book1;
    }
    public String getBook2() {
        return book2;
    }
    public String getBook3() {
        return book3;
    }

    public void setBook1(String book1) {
        this.book1 = book1;
    }

    public void setBook2(String book2) {
        this.book2 = book2;
    }

    public void setBook3(String book3) {
        this.book3 = book3;
    }


    public String getPages1() {
        return pages1;
    }

    public String getPages2() {
        return pages2;
    }

    public String getPages3() {
        return pages3;
    }

    public void setPages1(String pages) {
        this.pages1 = pages;
    }
    public void setPages2(String pages) {
        this.pages2 = pages;
    }
    public void setPages3(String pages) {
        this.pages3 = pages;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public int getDifficulty() {
        return Difficulty;
    }

    public void setDifficulty(int difficulty) {
        Difficulty = difficulty;
    }




    public int getId() {
        return Id;
    }

    public void setId(int mId) {
        this.Id = mId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
