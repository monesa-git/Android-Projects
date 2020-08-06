package com.helloworld.inclass11;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements ExpenseAdapter.InteractWithRecyclerView {

    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    ArrayList<Expense> expenseArrayList = new ArrayList<>();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Expense App");
        // Access a Cloud Firestore instance from Activity
        db = FirebaseFirestore.getInstance();

        showProgressBarDialog();
        db.collection("Expense")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                String title, String category, String documentId, double cost
                                Expense expense = new Expense(document.getString("title"),document.getString("category"),
                                        document.getId(),document.getDouble("cost"),document.getString("date"));
                                expenseArrayList.add(expense);
                                Log.d("demo", document.getId() + " => " + document.getData());
                            }
                            if(expenseArrayList.size()>0){
                                TextView textView = findViewById(R.id.expenseNoText);
                                textView.setVisibility(TextView.INVISIBLE);
                                recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
                                layoutManager = new LinearLayoutManager(MainActivity.this);
                                recyclerView.setLayoutManager(layoutManager);
                                // specify an adapter (see also next example)
                                mAdapter = new ExpenseAdapter(expenseArrayList, MainActivity.this);
                                recyclerView.setAdapter(mAdapter);
                            }
                            hideProgressBarDialog();
                        } else {
                            Log.d("demo", "Error getting documents: ", task.getException());
                            Toast.makeText(MainActivity.this, "Some Error Occured in retriving the documents", Toast.LENGTH_SHORT).show();
                            hideProgressBarDialog();
                        }
                    }
                });

        findViewById(R.id.imageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
                startActivityForResult(intent, 100);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && resultCode == 200){
            Expense expense = (Expense) data.getExtras().getSerializable("expense");
            Toast.makeText(this, "Expense Added!", Toast.LENGTH_SHORT).show();
            updateRecyclerView(expense);
        }else if(requestCode == 1000 && resultCode == 2000){
            Expense expense = (Expense) data.getExtras().getSerializable("expense");
            int position = data.getExtras().getInt("position");
            expenseArrayList.remove(position);
            Log.d("demo",expenseArrayList.toString());
            Toast.makeText(this, "Expense Updated!", Toast.LENGTH_SHORT).show();
            updateRecyclerView(expense);
        }
    }

    public void updateRecyclerView(Expense expense){
            TextView textView = findViewById(R.id.expenseNoText);
            textView.setVisibility(TextView.INVISIBLE);
            expenseArrayList.add(expense);
            recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
            layoutManager = new LinearLayoutManager(MainActivity.this);
            recyclerView.setLayoutManager(layoutManager);
            // specify an adapter (see also next example)
            mAdapter = new ExpenseAdapter(expenseArrayList, MainActivity.this);
            recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void selectedItem(final int position) {
        db.collection("Expense").document(expenseArrayList.get(position).documentId).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("demo", "DocumentSnapshot successfully deleted!");
                        expenseArrayList.remove(expenseArrayList.get(position));
                        Toast.makeText(MainActivity.this, "Expense Deleted!", Toast.LENGTH_SHORT).show();
                        mAdapter.notifyDataSetChanged();
                        if(expenseArrayList.size() == 0){
                            TextView textView = findViewById(R.id.expenseNoText);
                            textView.setVisibility(TextView.VISIBLE);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("demo", "Error deleting document", e);
                        Toast.makeText(MainActivity.this, "Some Error occured in deleting the document. Please try again!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void getDetails(Expense expense, int position) {
        Intent intent = new Intent(MainActivity.this, ExpenseDetailsActivity.class);
        intent.putExtra("Expense",expense);
        intent.putExtra("position",position);
        Log.d("demo",expense.toString());
        startActivityForResult(intent,1000);
    }

    public void showProgressBarDialog()
    {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void hideProgressBarDialog()
    {
        progressDialog.dismiss();
    }
}
