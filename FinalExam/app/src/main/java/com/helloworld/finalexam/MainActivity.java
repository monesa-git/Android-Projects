package com.helloworld.finalexam;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements ScheduleAdapter.InteractWithRecyclerViewMain{

    ArrayList<ScheduleClass> globalMainList = new ArrayList<>();
    private FirebaseFirestore db;
    private RecyclerView mainRecyclerView;
    private RecyclerView.Adapter mainAdapter;
    private RecyclerView.LayoutManager mainLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        setTitle("Basic Scheduler");

        mainRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mainLayoutManager = new LinearLayoutManager(MainActivity.this);
        mainRecyclerView.setLayoutManager(mainLayoutManager);
        // specify an adapter (see also next example)
        mainAdapter = new ScheduleAdapter(globalMainList, MainActivity.this);
        mainRecyclerView.setAdapter(mainAdapter);

        //Adding snapshot listener to the firestore
        db.collection("ScheduleList").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAG", "listen:error", e);
                    return;
                }

                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            Log.d("TAG", "New Msg: " + dc.getDocument().toObject(ScheduleClass.class));
                            ScheduleClass added = dc.getDocument().toObject(ScheduleClass.class);
                            globalMainList.add(dc.getDocument().toObject(ScheduleClass.class));
                            break;
                        case MODIFIED:
                            Log.d("TAG", "Modified Msg: " + dc.getDocument().toObject(ScheduleClass.class));
                            ScheduleClass current = dc.getDocument().toObject(ScheduleClass.class);
                            break;
                        case REMOVED:
                            Log.d("TAG", "Removed Msg: " + dc.getDocument().toObject(ScheduleClass.class));
                            ScheduleClass currentRemoved = dc.getDocument().toObject(ScheduleClass.class);
                            int j = 0;
                            for(ScheduleClass scheduleClass : globalMainList){
                                if(scheduleClass.meeting_name.equals(currentRemoved.meeting_name)){
                                    globalMainList.remove(j);
                                    break;
                                }
                                j++;
                            }
                            break;
                    }
                }

                //For Sorting
                Collections.sort(globalMainList, new Comparator<ScheduleClass>() {
                    @Override
                    public int compare(ScheduleClass o1, ScheduleClass o2) {
                        return o1.date.compareTo(o2.date);
                    }
                });

                mainAdapter.notifyDataSetChanged();
            }
        });




        findViewById(R.id.imageButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ScheduleCreate.class);
                startActivityForResult(intent, 100);
            }
        });





    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && resultCode == 200 && data!=null){
            final ScheduleClass scheduleClass = (ScheduleClass) data.getExtras().getSerializable("schedule");
            db.collection("ScheduleList").document(scheduleClass.meeting_name)
                    .set(scheduleClass)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(MainActivity.this, "Schedule successfully saved", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, "Some error occured. Please try again", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void selectedItem(final ScheduleClass scheduleClass) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
        builder1.setMessage("Are you sure you want to delete this schedule?");
        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        db.collection("ScheduleList").document(scheduleClass.meeting_name)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(MainActivity.this, "Schedule deleted successfully!", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("demo","It is coming to the places failure");
                                Toast.makeText(MainActivity.this, "Some error occurred. Please try to delete again", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    @Override
    public void getDetails(ScheduleClass scheduleClass, int position) {

    }
}


