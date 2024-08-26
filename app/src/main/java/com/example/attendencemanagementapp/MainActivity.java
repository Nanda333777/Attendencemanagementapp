package com.example.attendencemanagementapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.attendencemanagementapp.R;

public class MainActivity extends AppCompatActivity {

    EditText editTextStudentName;
    Button buttonAddStudent, buttonMarkAttendance, buttonViewAttendance;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextStudentName = findViewById(R.id.editTextStudentName);
        buttonAddStudent = findViewById(R.id.buttonAddStudent);
        buttonMarkAttendance = findViewById(R.id.buttonMarkAttendance);
        buttonViewAttendance = findViewById(R.id.buttonViewAttendance);

        // Initialize SQLite database
        db = openOrCreateDatabase("AttendanceDB", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS Students (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS Attendance (id INTEGER PRIMARY KEY AUTOINCREMENT, student_id INTEGER, date TEXT)");

        buttonAddStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addStudent();
            }
        });

        buttonMarkAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markAttendance();
            }
        });

        buttonViewAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewAttendance();
            }
        });
    }

    private void addStudent() {
        String studentName = editTextStudentName.getText().toString().trim();
        if (!studentName.isEmpty()) {
            db.execSQL("INSERT INTO Students (name) VALUES('" + studentName + "')");
            Toast.makeText(this, "Student added successfully", Toast.LENGTH_SHORT).show();
            editTextStudentName.setText("");
        } else {
            Toast.makeText(this, "Please enter a student name", Toast.LENGTH_SHORT).show();
        }
    }

    private void markAttendance() {
        String studentName = editTextStudentName.getText().toString().trim();
        if (!studentName.isEmpty()) {

            Cursor cursor = db.rawQuery("SELECT id FROM Students WHERE name = ?", new String[]{studentName});
            if (cursor.moveToFirst()) {
                int studentId = cursor.getInt(0);
                String date = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
                db.execSQL("INSERT INTO Attendance (student_id, date) VALUES(" + studentId + ", '" + date + "')");
                Toast.makeText(this, "Attendance marked successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Student not found", Toast.LENGTH_SHORT).show();
            }
            editTextStudentName.setText("");
        } else {
            Toast.makeText(this, "Please enter a student name", Toast.LENGTH_SHORT).show();
        }
    }

    private void viewAttendance() {
        String studentName = editTextStudentName.getText().toString().trim();
        if (!studentName.isEmpty()) {
            Cursor cursor = db.rawQuery("SELECT id FROM Students WHERE name = ?", new String[]{studentName});
            if (cursor.moveToFirst()) {
                int studentId = cursor.getInt(0);
                Cursor attendanceCursor = db.rawQuery("SELECT date FROM Attendance WHERE student_id = ?", new String[]{String.valueOf(studentId)});
                if (attendanceCursor.moveToFirst()) {
                    StringBuilder attendanceRecords = new StringBuilder();
                    do {
                        String date = attendanceCursor.getString(0);
                        attendanceRecords.append("Date: ").append(date).append("\n");
                    } while (attendanceCursor.moveToNext());
                    Toast.makeText(this, attendanceRecords.toString(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "No attendance records found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Student not found", Toast.LENGTH_SHORT).show();
            }
            editTextStudentName.setText("");
        } else {
            Toast.makeText(this, "Please enter a student name", Toast.LENGTH_SHORT).show();
        }
    }
}
