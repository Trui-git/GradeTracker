package com.trios.gradetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private SQLiteDatabase db = null;
    private final String DB_NAME = "gradeTrackerDB";

    // term name, 5/8 (courses done, courses to go), goal
    // Year 1, 5 of 8, goal: 95%
    // Year 2, 0 of 8, goal: 95%
    ArrayList<Integer> termIDs = new ArrayList<>();
    ArrayList<String> termNames = new ArrayList<>();
    ArrayList<String> takenCounts = new ArrayList<>();
    ArrayList<String> totalCourses = new ArrayList<>();
    ArrayList<String> goals = new ArrayList<>();
    private ArrayList<String> terms = new ArrayList<>();

    private static EditText termName;
    private static EditText numOfCourses;
    private static EditText termGoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(
            new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {

                }
            });
        dbSetup();
        getTerms();
        showTable();
        //listTerms();
    } // onCreate()

    public void dbSetup() {
        db = this.openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);

        ////////////////
        // tblTerm ////
        db.execSQL("CREATE TABLE IF NOT EXISTS tblTerm" +
                " (termID INTEGER PRIMARY KEY AUTOINCREMENT," +
                " name VARCHAR, goal INTEGER, courses INTEGER, status CHAR);");

        /////////////////
        // tblGrade ////
        db.execSQL("CREATE TABLE IF NOT EXISTS tblGrade" +
                " (gradeID INTEGER PRIMARY KEY AUTOINCREMENT," +
                " courseName VARCHAR, grade INTEGER, termID INTEGER);");

        db.close();
    }
/*
    public void CreateTerm(View v) {
        termName = (EditText) findViewById(R.id.edit_text_term_name);
        numOfCourses = (EditText) findViewById(R.id.edit_text_num_of_courses);
        termGoal = (EditText) findViewById(R.id.edit_text_target_grade);

        String name = termName.getText().toString();
        int number = Integer.parseInt(numOfCourses.getText().toString());
        int goal = Integer.parseInt(termGoal.getText().toString());

        db = this.openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);

        db.execSQL("INSERT INTO tblTerm VALUES(?1, '" + name + "'," +
                goal + "," + number + ", 'A');");

        db.close();
    } // CreateTerm()
*/
    public void getTerms() {
        db = this.openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);

        String name = "Science";
        int number = 8;
        int goal = 99;

        Cursor c = db.rawQuery("SELECT * FROM tblTerm WHERE status = 'A'", null);

        if(c != null) {
            if(c.moveToFirst()) {
                do {
                    termIDs.add(c.getColumnIndex("termID"));
                    termNames.add(c.getString(c.getColumnIndex("name")));
                    totalCourses.add(c.getString(c.getColumnIndex("courses")));
                    goals.add(c.getString(c.getColumnIndex("goal")));
                }while(c.moveToNext());
            }
        }

        // lastly, let's get the courses taken counts
        for(int i = 0; i < termIDs.size(); i++) {
            int termID = termIDs.get(i);
            Cursor coursesTaken = db.rawQuery(
                    "SELECT COUNT(courseName) AS courseTaken FROM tblGrade WHERE courseName != 'dummy' AND termID = " +
                            termID, null);
            if(coursesTaken != null) {
                if(coursesTaken.moveToFirst()) {
                    do {
                        takenCounts.add(coursesTaken.getString(coursesTaken.getColumnIndex("courseTaken")));
                    }while(coursesTaken.moveToNext());
                } // if we can move to first
            } // if courses taken NOT null
        } // for - termIDs

        for(int i = 0; i < termIDs.size(); i++) {
            terms.add(termNames.get(i) + " [ " + takenCounts.get(i) + "/" + totalCourses.get(i) + " ]" +
                    " goal: " + goals.get(i) + "%");
        }
        db.close();
    } // getTerms()
/*
    public void listTerms() {
        // add the terms(if any) to our listview
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, terms);
        ListView listView = (ListView) findViewById(R.id.list_view_terms);
        listView.setAdapter(adapter);

        // Create a message handling object as an anonymous class.
        listView.setOnItemClickListener(mMessageClickedHandler);
    }

    private AdapterView.OnItemClickListener mMessageClickedHandler =
            new AdapterView.OnItemClickListener()
            {
                public void onItemClick(AdapterView parent, View v,
                                        int position, long id)
                {
                    Intent gotoProgress =
                            new Intent(MainActivity.this, ProgressActivity.class);
                    gotoProgress.putExtra("termID", termIDs.get(position));
                    startActivity(gotoProgress);
                }
            };
*/
    @SuppressLint("SetTextI18n")
    public void showTable() {
        TableLayout  tl = (TableLayout) findViewById(R.id.displayTable);
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
        tv1.setText(" Course Done ");
        tv1.setTextColor(Color.BLACK);
        tv1.setGravity(Gravity.CENTER);
        tv1.setTextSize(20);
        tv1.setPadding(15, 15, 15, 15);
        tbrow0.addView(tv1);

        TextView tv2 = new TextView(this);
        tv2.setText(" Course To Go ");
        tv2.setTextColor(Color.BLACK);
        tv2.setGravity(Gravity.CENTER);
        tv2.setTextSize(20);
        tv2.setPadding(15, 15, 15, 15);
        tbrow0.addView(tv2);

        TextView tv3 = new TextView(this);
        tv3.setText(" Goal(%) ");
        tv3.setTextColor(Color.BLACK);
        tv3.setGravity(Gravity.CENTER);
        tv3.setTextSize(20);
        tv3.setPadding(15, 15, 15, 15);
        tbrow0.addView(tv3);

        tl.addView(tbrow0);


        for (int i = 0; i < termNames.size(); i++) {

            TableRow tableRow = new TableRow(this);
            tableRow.setClickable(true);

            TextView t1v = new TextView(this);
            t1v.setText(termNames.get(i));
            t1v.setTextColor(Color.BLACK);
            t1v.setGravity(Gravity.CENTER);
            t1v.setTextSize(20);
            t1v.setPadding(15, 15, 15, 15);
            tableRow.addView(t1v);

            TextView t2v = new TextView(this);
            t2v.setText(takenCounts.get(i));
            t2v.setTextColor(Color.BLACK);
            t2v.setGravity(Gravity.CENTER);
            t2v.setTextSize(20);
            t2v.setPadding(15, 15, 15, 15);
            tableRow.addView(t2v);

            TextView t3v = new TextView(this);
            t3v.setText(totalCourses.get(i));
            t3v.setTextColor(Color.BLACK);
            t3v.setGravity(Gravity.CENTER);
            t3v.setTextSize(20);
            t3v.setPadding(15, 15, 15, 15);
            tableRow.addView(t3v);

            TextView t4v = new TextView(this);
            t4v.setText(goals.get(i));
            t4v.setTextColor(Color.BLACK);
            t4v.setGravity(Gravity.CENTER);
            t4v.setTextSize(20);
            t4v.setPadding(15, 15, 15, 15);
            tableRow.addView(t4v);

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
                    Toast.makeText(getApplicationContext(),"Hello Javatpoint",Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

} // class
