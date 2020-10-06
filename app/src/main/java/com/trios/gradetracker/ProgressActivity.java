package com.trios.gradetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ProgressActivity extends AppCompatActivity {

    private SQLiteDatabase db = null;
    private final String DB_NAME = "gradeTrackerDB";
    int termID, termGoal;
    private int visible = 0;

    ArrayList<String> courseNames = new ArrayList<>();
    ArrayList<Double> grades = new ArrayList<>();
    //String dummyName = "Dummy";
    //int dummyGrade = 67;
    //int totalCourses = 0;

    private static EditText courseName;
    private static EditText grade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        // restore visibility of edit text fields in linearlayout after rotation
        if (savedInstanceState != null) {
            int mVisible = savedInstanceState.getInt("visible");
            if (mVisible == 1){
                ((EditText) findViewById(R.id.edit_text_course_name)).setVisibility(View.VISIBLE);
                ((EditText) findViewById(R.id.edit_text_grade)).setVisibility(View.VISIBLE);
                ((Button)findViewById(R.id.create_term_content_button)).setVisibility(View.VISIBLE);
                ((Button)findViewById(R.id.cancel_term_content_button)).setVisibility(View.VISIBLE);
                visible = 1;
            }
        }

        Bundle extras = getIntent().getExtras();
        if(extras != null)
        {
            termID = extras.getInt("termID");
            termGoal = extras.getInt("goal");
        }

        // hide the keyboard after rotation
        Window window = getWindow();
        if (window != null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        } // hide the keyboard after rotation

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_grade);
        fab.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        ((EditText) findViewById(R.id.edit_text_course_name)).setVisibility(View.VISIBLE);
                        ((EditText) findViewById(R.id.edit_text_grade)).setVisibility(View.VISIBLE);
                        ((Button)findViewById(R.id.create_term_content_button)).setVisibility(View.VISIBLE);
                        ((Button)findViewById(R.id.cancel_term_content_button)).setVisibility(View.VISIBLE);
                        visible = 1;
                    }
                });

        GetGrades();
        showTermContent();
    }

    @Override
    // close app
    public void onBackPressed() {
        Intent backPage = new Intent(ProgressActivity.this, MainActivity.class);
        startActivity(backPage);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("visible", visible);
    }  // save visibility of edit text fields in linearlayout

    public void GetGrades() {
        db = this.openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);
        int id = termID + 1; // termID is index of array from main activity
        Cursor c = db.rawQuery(
                "SELECT * FROM tblGrade WHERE termID = " +
                        id, null);

        if(c != null) {
            if(c.moveToFirst()) {
                do {
                    courseNames.add(c.getString(c.getColumnIndex("courseName")));
                    grades.add(c.getDouble(c.getColumnIndex("grade")));
                }while(c.moveToNext());
            }
        }
        db.close();
    } // GetGrades()

    public void CancelTermContent(View v) {
        ((EditText) findViewById(R.id.edit_text_course_name)).setVisibility(View.INVISIBLE);
        ((EditText) findViewById(R.id.edit_text_grade)).setVisibility(View.INVISIBLE);
        ((Button)findViewById(R.id.create_term_content_button)).setVisibility(View.INVISIBLE);
        ((Button)findViewById(R.id.cancel_term_content_button)).setVisibility(View.INVISIBLE);
        visible = 0;
        return;
    }

    public void CreateTermContent(View v) {
        Cursor c;
        int id = termID + 1;
        double goal = termGoal;
        courseName = (EditText) findViewById(R.id.edit_text_course_name);
        grade = (EditText) findViewById(R.id.edit_text_grade);

        if (TextUtils.isEmpty(courseName.getText().toString())) {
            Toast.makeText(this, "course name is empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(grade.getText().toString())) {
            Toast.makeText(this, "grade is empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = courseName.getText().toString();
        int number = Integer.parseInt(grade.getText().toString());

        db = this.openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);
        //find total number of subject in this term
        c = db.rawQuery("SELECT count(gradeID) FROM tblGrade WHERE termID == "+ id +" ", null);
        if (c != null)
            c.moveToFirst();
        int total = Integer.parseInt(c.getString(0));

        c = db.rawQuery("SELECT count(gradeID) FROM tblGrade WHERE termID == "+ id +" AND courseName == 'Dummy' ", null);
        if (c != null)
            c.moveToFirst();
        int totalDummy = Integer.parseInt(c.getString(0));

        // SELECT min(gradeID) FROM tblGrade where courseName = 'Dummy' AND termID = 9
        // find the smallest grade ID to update
        c = db.rawQuery("SELECT min(gradeID) FROM tblGrade WHERE courseName == 'Dummy' AND termID == "+ id +" ", null);
        if (c != null)
            c.moveToFirst();
        int min_term_id = Integer.parseInt(c.getString(0));
        if (totalDummy <= total){
            //example: UPDATE tblGrade SET courseName = 'Biology', grade = '85' WHERE gradeID == 12
            db.execSQL("UPDATE tblGrade SET courseName = '"+ name +"', grade = "+ number +" WHERE gradeID == "+ min_term_id +"");
        }
        else{
            Toast.makeText(ProgressActivity.this,"no more place for course！", Toast.LENGTH_LONG).show();
        }

        c = db.rawQuery("SELECT sum(grade) FROM tblGrade WHERE termID == "+ id +" AND courseName != 'Dummy' ", null);
        if (c != null)
            c.moveToFirst();
        int takenTotal = Integer.parseInt(c.getString(0));
        int takenCount = total - (totalDummy-1);
        int toGocount = totalDummy - 1;
        //call to a native method
        //private native String CalcGrades(double goal, int takenCount, int takenTotal, int toGoCount);
        String newTarget = CalcGrades(goal, takenCount, takenTotal, toGocount);
        db.execSQL("UPDATE tblGrade SET grade = "+ Float.parseFloat(newTarget) +" WHERE termID == "+ id +" AND courseName == 'Dummy'");

        db.close();

        Intent refreshPage =
                new Intent(ProgressActivity.this, ProgressActivity.class);
        refreshPage.putExtra("termID", termID);
        refreshPage.putExtra("goal", termGoal);
        startActivity(refreshPage);

    } // CreateTermContent()


    public void showTermContent() {
        TableLayout tl = (TableLayout) findViewById(R.id.displayTerm);

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

            //final int n = i;
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
    private native String CalcGrades(double goal, int takenCount, int takenTotal, int toGoCount);

} // class