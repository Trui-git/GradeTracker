package com.trios.gradetracker;

import androidx.appcompat.app.AppCompatActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import java.util.ArrayList;

public class ProgressActivity extends AppCompatActivity {

    private SQLiteDatabase db = null;
    private final String DB_NAME = "gradeTrackerDB";
    int termID;

    ArrayList<String> courseNames = new ArrayList<>();
    ArrayList<Integer> grades = new ArrayList<>();
    String dummyName = "dummy";
    int dummyGrade = 67;
    int totalCourses = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        Bundle extras = getIntent().getExtras();
        if(extras != null)
        {
            termID = extras.getInt("termID");
        }

        GetGrades();

        // Example of a call to a native method
        String hello = stringFromJNI();
    }

    public void GetGrades() {
        db = this.openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);

        Cursor n = db.rawQuery(
                "SELECT courses FROM tblTerm WHERE termID = " +
                        termID, null);

        if(n != null) {
            if(n.moveToFirst()) {
                totalCourses = n.getInt(n.getColumnIndex("courses"));
            }
        }

        Cursor c = db.rawQuery(
                "SELECT courseName FROM tblGrade WHERE termID = " +
                        termID, null);

        if(c != null) {
            if(c.moveToFirst()) {
                do {
                    courseNames.add(c.getString(c.getColumnIndex("courseName")));
                    grades.add(c.getInt(c.getColumnIndex("grade")));
                }while(c.moveToNext());
            }
        }

        for(int i = 0; i < totalCourses - courseNames.size(); i++) {
            courseNames.add(dummyName);
            // call the C++ lib fun stuff to calc the new target goal thing ;)
        }

        db.close();
    } // GetGrades()


    // Used to load the 'grade-calc' library on application startup.
    static {
        System.loadLibrary("gradetracker-calc");
    }
    /**
     * A native method that is implemented by the 'grade-calc' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
} // class