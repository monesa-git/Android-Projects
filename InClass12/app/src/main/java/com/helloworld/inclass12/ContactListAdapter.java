package com.helloworld.inclass12;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.MyViewHolder> {
    private ArrayList<ContactProfile> mDataset;
    public static InteractWithRecyclerView interact;

    // Provide a suitable constructor (depends on the kind of dataset)
    public ContactListAdapter(ArrayList<ContactProfile> myDataset, Context ctx) {
        mDataset = myDataset;
        interact = (InteractWithRecyclerView) ctx;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ContactListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_list, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        ContactProfile contactProfile = mDataset.get(position);
        holder.name.setText(contactProfile.firstname+" "+contactProfile.lastname);
        holder.phNum.setText(String.valueOf(contactProfile.phonenumber));
        holder.email.setText(contactProfile.email);

        if(!contactProfile.imageName.equals("")){
            Picasso.get()
                    .load(contactProfile.url)
                    .into(holder.imageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
        }

        holder.constraintLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                interact.selectedItem(position);
                return false;
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
        TextView name, phNum, email;
        ImageView imageView;
        ConstraintLayout constraintLayout;
        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.flName);
            phNum = view.findViewById(R.id.phNumber);
            email = view.findViewById(R.id.email);
            imageView = view.findViewById(R.id.imageView);
            constraintLayout = view.findViewById(R.id.constraintLayout);
        }
    }

    public interface InteractWithRecyclerView{
        public void selectedItem(int position);
//        public void getDetails(Notes notes);
    }

}
