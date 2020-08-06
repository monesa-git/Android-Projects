package com.helloworld.inclass15;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

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


import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TodoTaskAdapter.InteractWithRecyclerView{

    private EditText todoText;
    private FirebaseFirestore db;
    ArrayList<TodoClass> globalList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private String itemSelected = "show_all";
    private RecyclerView.LayoutManager layoutManager;
    ArrayList<TodoClass> highListPending = new ArrayList<>();
    ArrayList<TodoClass> lowListPending = new ArrayList<>();
    ArrayList<TodoClass> mediumListPending = new ArrayList<>();
    ArrayList<TodoClass> highListCompleted = new ArrayList<>();
    ArrayList<TodoClass> lowListCompleted = new ArrayList<>();
    ArrayList<TodoClass> mediumListCompleted = new ArrayList<>();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_item_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.show_all:
                itemSelected = "show_all";
                setMainRecyclerView();
                return true;

            case R.id.show_completed:
                itemSelected = "show_completed";
                setMainRecyclerView();
                return true;

            case R.id.show_pending:
                itemSelected = "show_pending";
                setMainRecyclerView();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        todoText = findViewById(R.id.editText);
        db = FirebaseFirestore.getInstance();
        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_list, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);


        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        // specify an adapter (see also next example)
        mAdapter = new TodoTaskAdapter(globalList, MainActivity.this);
        recyclerView.setAdapter(mAdapter);


        //Adding snapshot listerner to the list
        db.collection("TodoTasks").addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                            Log.d("TAG", "New Msg: " + dc.getDocument().toObject(TodoClass.class));
                            TodoClass added = dc.getDocument().toObject(TodoClass.class);
                            if(added.priority.equals("High")){
                                if(added.checked==false){
                                    highListPending.add(added);
                                }else{
                                    highListCompleted.add(added);
                                }
                            }else if(added.priority.equals("Medium")){
                                if(added.checked==false){
                                    mediumListPending.add(added);
                                }else{
                                    mediumListCompleted.add(added);
                                }
                            }else if(added.priority.equals("Low")){
                                if(added.checked==false){
                                    lowListPending.add(added);
                                }else{
                                    lowListCompleted.add(added);
                                }
                            }
//                            globalList.add(dc.getDocument().toObject(TodoClass.class));
                            break;
                        case MODIFIED:
                            Log.d("TAG", "Modified Msg: " + dc.getDocument().toObject(TodoClass.class));
                            TodoClass current = dc.getDocument().toObject(TodoClass.class);
                            int i = 0;
                            if(current.priority.equals("High")){
                                if(current.checked == true){
                                    for(TodoClass todoClass : highListPending){
                                        if(todoClass.task_name.equals(current.task_name)){
                                            todoClass.date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                                            todoClass.priority = current.priority;
                                            todoClass.checked = current.checked;
                                            highListPending.remove(i);
                                            highListCompleted.add(todoClass);
                                            break;
                                        }else{
                                            i++;
                                        }
                                    }
                                }else{
                                    for(TodoClass todoClass : highListCompleted){
                                        if(todoClass.task_name.equals(current.task_name)){
                                            todoClass.date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                                            todoClass.priority = current.priority;
                                            todoClass.checked = current.checked;
                                            highListCompleted.remove(i);
                                            highListPending.add(todoClass);
                                            break;
                                        }else{
                                            i++;
                                        }
                                    }
                                }
                            }else if(current.priority.equals("Medium")){
                                if(current.checked == true){
                                    for(TodoClass todoClass : mediumListPending){
                                        if(todoClass.task_name.equals(current.task_name)){
                                            todoClass.date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                                            todoClass.priority = current.priority;
                                            todoClass.checked = current.checked;
                                            mediumListPending.remove(i);
                                            mediumListCompleted.add(todoClass);
                                            break;
                                        }else{
                                            i++;
                                        }
                                    }
                                }else{
                                    for(TodoClass todoClass : mediumListCompleted){
                                        if(todoClass.task_name.equals(current.task_name)){
                                            todoClass.date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                                            todoClass.priority = current.priority;
                                            todoClass.checked = current.checked;
                                            mediumListCompleted.remove(i);
                                            mediumListPending.add(todoClass);
                                            break;
                                        }else{
                                            i++;
                                        }
                                    }
                                }
                            }else if(current.priority.equals("Low")){
                                if(current.checked == true){
                                    for(TodoClass todoClass : lowListPending){
                                        if(todoClass.task_name.equals(current.task_name)){
                                            todoClass.date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                                            todoClass.priority = current.priority;
                                            todoClass.checked = current.checked;
                                            lowListPending.remove(i);
                                            lowListCompleted.add(todoClass);
                                            break;
                                        }else{
                                            i++;
                                        }
                                    }
                                }else{
                                    for(TodoClass todoClass : lowListCompleted){
                                        if(todoClass.task_name.equals(current.task_name)){
                                            todoClass.date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                                            todoClass.priority = current.priority;
                                            todoClass.checked = current.checked;
                                            lowListCompleted.remove(i);
                                            lowListPending.add(todoClass);
                                            break;
                                        }else{
                                            i++;
                                        }
                                    }
                                }
                            }
                            break;
                        case REMOVED:
                            Log.d("TAG", "Removed Msg: " + dc.getDocument().toObject(TodoClass.class));
                            TodoClass currentRemoved = dc.getDocument().toObject(TodoClass.class);
                            int j = 0;

                            if(currentRemoved.priority.equals("High")){
                                if(currentRemoved.checked == false){
                                    for(TodoClass todoClass : highListPending){
                                        if(todoClass.task_name.equals(currentRemoved.task_name)){
                                            highListPending.remove(j);
                                            break;
                                        }else{
                                            j++;
                                        }
                                    }
                                }else{
                                    for(TodoClass todoClass : highListCompleted){
                                        if(todoClass.task_name.equals(currentRemoved.task_name)){
                                            highListCompleted.remove(j);
                                            break;
                                        }else{
                                            j++;
                                        }
                                    }
                                }
                            }else if(currentRemoved.priority.equals("Medium")){
                                if(currentRemoved.checked == false){
                                    for(TodoClass todoClass : mediumListPending){
                                        if(todoClass.task_name.equals(currentRemoved.task_name)){
                                            mediumListPending.remove(j);
                                            break;
                                        }else{
                                            j++;
                                        }
                                    }
                                }else{
                                    for(TodoClass todoClass : mediumListCompleted){
                                        if(todoClass.task_name.equals(currentRemoved.task_name)){
                                            mediumListCompleted.remove(j);
                                            break;
                                        }else{
                                            j++;
                                        }
                                    }
                                }
                            }else if(currentRemoved.priority.equals("Low")){
                                if(currentRemoved.checked == false){
                                    for(TodoClass todoClass : lowListPending){
                                        if(todoClass.task_name.equals(currentRemoved.task_name)){
                                            lowListPending.remove(j);
                                            break;
                                        }else{
                                            j++;
                                        }
                                    }
                                }else{
                                    for(TodoClass todoClass : lowListCompleted){
                                        if(todoClass.task_name.equals(currentRemoved.task_name)){
                                            lowListCompleted.remove(j);
                                            break;
                                        }else{
                                            j++;
                                        }
                                    }
                                }
                            }

                            break;
                    }
                }
                setMainRecyclerView();
            }
        });


        Log.d("demo", "Spinner value is : "+spinner.getSelectedItem());
        findViewById(R.id.button_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkValidation(todoText)){
                    if(!spinner.getSelectedItem().equals("Priority")){

                        final TodoClass todoClass = new TodoClass();
                        todoClass.task_name = todoText.getText().toString();
                        todoClass.checked = false;
                        todoClass.priority = spinner.getSelectedItem().toString();
                        todoClass.date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

                        db.collection("TodoTasks").document(todoClass.task_name)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if(!documentSnapshot.exists()){
                                    //Saving the added todo_list to the firebase
                                    db.collection("TodoTasks").document(todoClass.task_name)
                                            .set(todoClass)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(MainActivity.this, "Todo task successfully saved", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(MainActivity.this, "Some error occured. Please try again", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }else{
                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                                    builder1.setMessage("A task with same name already exists, Please give a different task name or delete the old task");

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
                    }else{
                        Toast.makeText(MainActivity.this, "Please set a priority for the Todo list!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(MainActivity.this, "Todo list cannot be empty!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void setMainRecyclerView(){
        globalList.clear();
        //For Recycler View:

        if(itemSelected.equals("show_pending")){
            globalList.addAll(highListPending);
            globalList.addAll(mediumListPending);
            globalList.addAll(lowListPending);
        }else if(itemSelected.equals("show_completed")){
            globalList.addAll(highListCompleted);
            globalList.addAll(mediumListCompleted);
            globalList.addAll(lowListCompleted);
        }else{
            globalList.addAll(highListPending);
            globalList.addAll(mediumListPending);
            globalList.addAll(lowListPending);

            globalList.addAll(highListCompleted);
            globalList.addAll(mediumListCompleted);
            globalList.addAll(lowListCompleted);
        }

        mAdapter.notifyDataSetChanged();
    }

    private boolean checkValidation(EditText editText){
        if(editText.getText().toString().trim().equals("")){
            return false;
        }
        return true;
    }

    //For deleting the task in the Firebase
    @Override
    public void selectedItem(final TodoClass todoClass) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
        builder1.setMessage("Are you sure you want to delete this task?");
        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        db.collection("TodoTasks").document(todoClass.task_name)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(MainActivity.this, "Task deleted successfully!", Toast.LENGTH_SHORT).show();
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

    //For updating the task in the firebase
    @Override
    public void getDetails(TodoClass todoClass, int position) {
        db.collection("TodoTasks").document(todoClass.task_name)
                .update("checked",!todoClass.checked)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(MainActivity.this, "Task status successfully updated!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("demo","It is coming to the places failure");
                Toast.makeText(MainActivity.this, "Some error occurred. Please try to delete again", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
