package com.trios.gradetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ProgressActivity extends AppCompatActivity {

    private SQLiteDatabase db = null;
    private final String DB_NAME = "gradeTrackerDB";
    int termID;

    ArrayList<String> courseNames = new ArrayList<>();
    ArrayList<Integer> grades = new ArrayList<>();
    String dummyName = "Dummy";
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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_grade);
        fab.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {

                    }
                });

        GetGrades();
        showTermContent();

        // Example of a call to a native method
        String hello = stringFromJNI();
    }

    public void GetGrades() {
        db = this.openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);
/*
        Cursor n = db.rawQuery(
                "SELECT courses FROM tblTerm WHERE termID = " +
                        termID, null);

        if(n != null) {
            if(n.moveToFirst()) {
                totalCourses = n.getInt(n.getColumnIndex("courses"));
            }
        }
*/
        int id = termID + 1; // termID is index of array from main activity
        Cursor c = db.rawQuery(
                "SELECT * FROM tblGrade WHERE termID = " +
                        id, null);

        if(c != null) {
            if(c.moveToFirst()) {
                do {
                    courseNames.add(c.getString(c.getColumnIndex("courseName")));
                    grades.add(c.getInt(c.getColumnIndex("grade")));
                }while(c.moveToNext());
            }
        }
/*
        for(int i = 0; i < totalCourses - courseNames.size(); i++) {
            courseNames.add(dummyName);
            // call the C++ lib fun stuff to calc the new target goal thing ;)
        }
*/
        db.close();
    } // GetGrades()

    public void showTermContent() {
        TableLayout tl = (TableLayout) findViewById(R.id.displayTerm);
        //TableRow.LayoutParams layoutParams = new TableRow.LayoutParams();
        //TableRow.LayoutParams.MATCH_PARENT,
        //TableRow.LayoutParams.MATCH_PARENT);
        //layoutParams.gravity = Gravity.CENTER;
        //layoutParams.leftMargin = 30;
        //layoutParams.bottomMargin = 10;
        //layoutParams.topMargin = 10;

        TableRow tbrow0 = new TableRow(this);
        tbrow0.setBackgroundColor(Color.LTGRAY);
        TextView tv0 = new TextView(this);
        tv0.setText(" Term ");
        tv0.setTextColor(Color.BLACK);
        tv0.setGravity(Gravity.CENTER);
        tv0.setTextSize(20);
        tv0.setPadding(15, 15, 15, 15);
        tbrow0.addView(tv0);

        TextView tv1 = new TextView(this);
        tv1.setText(" Course Name ");
        tv1.setTextColor(Color.BLACK);
        tv1.setGravity(Gravity.CENTER);
        tv1.setTextSize(20);
        tv1.setPadding(15, 15, 15, 15);
        tbrow0.addView(tv1);

        TextView tv2 = new TextView(this);
        tv2.setText(" Grade ");
        tv2.setTextColor(Color.BLACK);
        tv2.setGravity(Gravity.CENTER);
        tv2.setTextSize(20);
        tv2.setPadding(15, 15, 15, 15);
        tbrow0.addView(tv2);

        tl.addView(tbrow0);


        for (int i = 0; i < courseNames.size(); i++) {

            TableRow tableRow = new TableRow(this);
            tableRow.setClickable(true);

            TextView t1v = new TextView(this);
            int id = termID + 1;
            t1v.setText("Term " + id);
            t1v.setTextColor(Color.BLACK);
            t1v.setGravity(Gravity.CENTER);
            t1v.setTextSize(20);
            t1v.setPadding(15, 15, 15, 15);
            tableRow.addView(t1v);

            TextView t2v = new TextView(this);
            t2v.setText(courseNames.get(i));
            if(courseNames.get(i).equals("Dummy")){
                t2v.setTextColor(Color.BLUE);
            }else{
                t2v.setTextColor(Color.BLACK);
            }
            t2v.setGravity(Gravity.CENTER);
            t2v.setTextSize(20);
            t2v.setPadding(15, 15, 15, 15);
            tableRow.addView(t2v);

            TextView t3v = new TextView(this);
            t3v.setText(grades.get(i).toString());
            if(courseNames.get(i).equals("Dummy")){
                t3v.setTextColor(Color.BLUE);
            }else{
                t3v.setTextColor(Color.BLACK);
            }
            t3v.setGravity(Gravity.CENTER);
            t3v.setTextSize(20);
            t3v.setPadding(15, 15, 15, 15);
            tableRow.addView(t3v);

            tableRow.setId(i);
            tl.addView(tableRow);


            //tableRow.addView(t1v, layoutParams);
            if (i % 2 != 0) {
                tableRow.setBackgroundColor(Color.LTGRAY);
            } else {
                tableRow.setBackgroundColor(Color.WHITE);
            }

            final int n = i;
            tableRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int tableId = v.getId();
                }
            });

        }
    }


    // Used to load the 'grade-calc' library on application startup.
    static {
        System.loadLibrary("gradetracker");
    }
    /**
     * A native method that is implemented by the 'grade-calc' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
} // class