package com.helloworld.inclass11;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.MyViewHolder> {
    private ArrayList<Expense> mDataset;
    public static InteractWithRecyclerView interact;

    // Provide a suitable constructor (depends on the kind of dataset)
    public ExpenseAdapter(ArrayList<Expense> myDataset, Context ctx) {
        mDataset = myDataset;
        interact = (InteractWithRecyclerView) ctx;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ExpenseAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_expense, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Expense expense = mDataset.get(position);
        holder.expenseName.setText(expense.title);
        holder.expenseAmount.setText("$" + expense.cost);

        holder.constraintLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                interact.selectedItem(position);
                return false;
            }
        });

        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interact.getDetails(mDataset.get(position),position);
            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        // each data item is just a string in this case
        TextView expenseName;
        TextView expenseAmount;
        ConstraintLayout constraintLayout;
        public MyViewHolder(View view) {
            super(view);
            expenseName = view.findViewById(R.id.textViewExpenseName);
            expenseAmount = view.findViewById(R.id.textViewExpenseAmount);
            constraintLayout = view.findViewById(R.id.expense_layout);
        }

    }
//
    public interface InteractWithRecyclerView{
        public void selectedItem(int position);
        public void getDetails(Expense expense, int position);
    }
}
