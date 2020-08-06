package com.helloworld.inclass11;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ExpenseDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_details);
        setTitle("Show Expense");
        Expense expense = (Expense) getIntent().getExtras().getSerializable("Expense");
        Log.d("demo","In Expense Detail Activity : "+expense.toString());
        TextView expenseName = findViewById(R.id.textViewEN);
        TextView expenseAmount = findViewById(R.id.textViewA);
        TextView expenseCategory = findViewById(R.id.textViewEC);
        TextView expenseDate = findViewById(R.id.textViewD);

        expenseName.setText(expense.title);
        expenseAmount.setText("$"+expense.cost);
        expenseCategory.setText(expense.category);
        expenseDate.setText(expense.date);

        findViewById(R.id.buttonEditExpense).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExpenseDetailsActivity.this, EditExpenseActivity.class);
                intent.putExtra("expense",(Expense) getIntent().getExtras().getSerializable("Expense"));
                intent.putExtra("position", getIntent().getExtras().getSerializable("position"));
                startActivityForResult(intent, 10000);
            }
        });

        findViewById(R.id.buttonClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 10000 && resultCode == 20000){
            Expense expense = (Expense) data.getExtras().getSerializable("expense");
            int position = data.getExtras().getInt("position");
            Intent intent = new Intent();
            intent.putExtra("expense",expense);
            intent.putExtra("position",position);
            setResult(2000, intent);
            finish();
        }
    }
}
