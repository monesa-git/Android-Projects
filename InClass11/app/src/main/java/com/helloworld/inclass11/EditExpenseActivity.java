package com.helloworld.inclass11;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class EditExpenseActivity extends AppCompatActivity {

    private String category;
    private ProgressDialog progressDialog;
    private FirebaseFirestore db;
    private Expense expense;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_expense);
        setTitle("Edit Expense");
        db = FirebaseFirestore.getInstance();

       expense = (Expense) getIntent().getExtras().getSerializable("expense");

        Log.d("demo","In Expense edit Activity : "+expense.toString());
        EditText expenseName = findViewById(R.id.editTextExpenseName);
        EditText amount = findViewById(R.id.editTextAmount);
        Spinner spinner = findViewById(R.id.spinner);

        expenseName.setText(expense.title);
        amount.setText(String.valueOf(expense.cost));

        final ArrayList<String> arrayList = new ArrayList<>();
//        Groceries, Invoice, Transportation, Shopping, Rent, Trips, Utilities, Other
        arrayList.add("Select Category");
        arrayList.add("Groceries");
        arrayList.add("Invoice");
        arrayList.add("Transportation");
        arrayList.add("Shopping");
        arrayList.add("Rent");
        arrayList.add("Trips");
        arrayList.add("Utilities");
        arrayList.add("Other");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, arrayList);
        spinner.setAdapter(arrayAdapter);
        spinner.setSelection(arrayList.indexOf(expense.category));

        category = expense.category;
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = arrayList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        findViewById(R.id.buttonAddExpense).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText expenseName = findViewById(R.id.editTextExpenseName);
                EditText amount = findViewById(R.id.editTextAmount);

                if(checkValidation(expenseName) && checkValidation(amount)){
                    showProgressBarDialog();
                    String expName = expenseName.getText().toString();
                    try{
                        Double amountValue = Double.parseDouble(amount.getText().toString());
                        if(!category.equals("Select Category")){
                            if(amountValue != 0){
                                String date = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(new Date());
                                final Expense expenseUpdate = new Expense(expName,category,expense.documentId,amountValue,date);
                                Log.d("demo",expense.documentId);
                                db.collection("Expense").document(expense.documentId)
                                        .update(expenseUpdate.toHasMap())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("demo","Expense Successfully Updated!");
                                                int position = getIntent().getExtras().getInt("position");
                                                expenseUpdate.documentId = expense.documentId;
                                                Log.d("demo",expenseUpdate.toString());
                                                Intent intent = new Intent();
                                                intent.putExtra("expense", expenseUpdate);
                                                intent.putExtra("position",position);
                                                setResult(20000, intent);
                                                hideProgressBarDialog();
                                                finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("demo","Some Error occured");
                                    }
                                });
                            }else{
                                Toast.makeText(EditExpenseActivity.this, "Expense value should be greater than 0!", Toast.LENGTH_SHORT).show();
                                hideProgressBarDialog();
                            }
                        }else{
                            Toast.makeText(EditExpenseActivity.this, "Please select a valid category!", Toast.LENGTH_SHORT).show();
                            hideProgressBarDialog();
                        }
                    }catch(Exception e){
                        Toast.makeText(EditExpenseActivity.this, "Please enter the right format!", Toast.LENGTH_SHORT).show();
                        hideProgressBarDialog();
                    }
                }
            }
        });

        findViewById(R.id.buttonCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public boolean checkValidation(EditText editText){
        if(editText.getText().toString().equals("")){
            editText.setError("It cannot be empty!");
            return false;
        }
        return true;
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
