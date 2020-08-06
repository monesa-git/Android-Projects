package com.helloworld.finalexam;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ScheduleCreate extends AppCompatActivity {

    private static TextView textViewDate;
    private static TextView textViewTime;
    private static Button buttonDate;
    private static Button buttonTime;
    private FirebaseFirestore db;
    private EditText editTextMeeting;
    String globalPlace = "";
    static String globalTime = "";
    static String globalDate = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_create);
        setTitle("Schedule a meeting");

        globalTime = "";
        globalDate = "";
        textViewDate = findViewById(R.id.textViewDate);
        buttonDate = findViewById(R.id.buttonDate);
        db = FirebaseFirestore.getInstance();
        editTextMeeting = findViewById(R.id.editTextMeeting);

        buttonTime = findViewById(R.id.buttonTime);
        textViewTime = findViewById(R.id.textViewTime);

        findViewById(R.id.buttonTime).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new ScheduleCreate.TimePickerFragment();
                newFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });

        findViewById(R.id.buttonDate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new ScheduleCreate.DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        findViewById(R.id.buttonAddPlace).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScheduleCreate.this, SearchPlace.class);
                startActivityForResult(intent, 1000);
            }
        });

        findViewById(R.id.buttonSaveActivity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkValidation(editTextMeeting)){
                    final ScheduleClass scheduleClass = new ScheduleClass();
                    scheduleClass.meeting_name = editTextMeeting.getText().toString().trim();
                    if(globalPlace.equals("")){
                        Toast.makeText(ScheduleCreate.this, "Please add the meeting place!", Toast.LENGTH_SHORT).show();
                    }else {
                        scheduleClass.meeting_location = globalPlace;
                        if(globalDate.equals("") || globalTime.equals("")){
                            Toast.makeText(ScheduleCreate.this, "Please specify the date and time for the meeting!", Toast.LENGTH_SHORT).show();
                        }else{
                            scheduleClass.meeting_date = "On "+globalDate+" at "+globalTime;
                            String mAlertDateTime = globalDate + " " + globalTime;
                            SimpleDateFormat  dft = new SimpleDateFormat("MMMM d, yyyy HH : mm a");
                            try {
                                scheduleClass.date = dft.parse(mAlertDateTime);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            db.collection("ScheduleList").document(scheduleClass.meeting_name)
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if(!documentSnapshot.exists()){
                                                //Saving the added todo_list to the firebase
                                                Intent intent = new Intent();
                                                intent.putExtra("schedule",scheduleClass);
                                                setResult(200, intent);
                                                finish();
                                            }else{
                                                AlertDialog.Builder builder1 = new AlertDialog.Builder(ScheduleCreate.this);
                                                builder1.setMessage("A schedule with same name already exists, Please give a different schedule name or delete the old task");

                                                builder1.setPositiveButton(
                                                        "OK",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                dialog.cancel();
                                                            }
                                                        });

                                                AlertDialog alert11 = builder1.create();
                                                alert11.show();
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                        }
                    }
                }else{
                    Toast.makeText(ScheduleCreate.this, "Title cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean checkValidation(EditText editText){
        if(editText.getText().toString().trim().equals("")){
            return false;
        }
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000 && resultCode == 2000 && data!=null){
            Button buttonAddPlace = findViewById(R.id.buttonAddPlace);
            buttonAddPlace.setVisibility(Button.INVISIBLE);
            TextView textViewPlacesmeetingPlace = findViewById(R.id.textViewPlacesmeetingPlace);
            globalPlace = data.getExtras().getString("meetingPlace");
            textViewPlacesmeetingPlace.setText("Places : "+globalPlace);
            textViewPlacesmeetingPlace.setVisibility(TextView.VISIBLE);
        }else{
            Toast.makeText(ScheduleCreate.this, "Some error occured. Please try again", Toast.LENGTH_SHORT).show();
        }
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            Log.d("demo",hourOfDay + " ");
            buttonTime.setVisibility(Button.INVISIBLE);

            String time = "";

            if(hourOfDay>=0 && hourOfDay<12){
                time = hourOfDay + " : " + minute + " AM";
            } else {
                if(hourOfDay == 12){
                    time = hourOfDay + " : " + minute + " PM";
                } else{
                    hourOfDay = hourOfDay -12;
                    time = hourOfDay + " : " + minute + " PM";
                }
            }

            globalTime = time;

            Log.d("time",globalTime);
            textViewTime.setText("Time : "+time);
            textViewTime.setVisibility(TextView.VISIBLE);

        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(0);
            cal.set(year, month, day, 0, 0, 0);
            String date = new SimpleDateFormat("MMM dd, YYYY").format(cal.getTime());

            globalDate = date;


            Log.d("date",globalDate);
            buttonDate.setVisibility(Button.INVISIBLE);
            textViewDate.setText("Date : "+ date);
            textViewDate.setVisibility(TextView.VISIBLE);
        }
    }


}
