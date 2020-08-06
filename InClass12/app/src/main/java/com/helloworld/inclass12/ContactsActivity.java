package com.helloworld.inclass12;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ContactsActivity extends AppCompatActivity implements ContactListAdapter.InteractWithRecyclerView{

    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ProgressDialog progressDialog;
    ArrayList<ContactProfile> contactProfileArrayList = new ArrayList<>();
    private FirebaseAuth mAuth;

    @Override
    protected void onStart() {

        super.onStart();
        Log.d("demo","Coming to Onstart");
        String user_id = getIntent().getExtras().getString("user");
        contactProfileArrayList.clear();
        getContactList(user_id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        setTitle("Contacts");
        Log.d("demo","Coming to OnCreate");

        // getContactList(user_id);

        findViewById(R.id.createNewContact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactsActivity.this, CreateNewContactActivity.class);
                intent.putExtra("user",getIntent().getExtras().getString("user"));
                startActivityForResult(intent,100);
            }
        });

        findViewById(R.id.imageButtonLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                FirebaseAuth.getInstance().signOut();
                setResult(2000, intent);
                finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && resultCode == 200){
            ContactProfile contactProfile = (ContactProfile) data.getExtras().getSerializable("contact_profile");
            if(contactProfile!=null){
                contactProfileArrayList.add(contactProfile);
                recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
                layoutManager = new LinearLayoutManager(ContactsActivity.this);
                recyclerView.setLayoutManager(layoutManager);
                mAdapter = new ContactListAdapter(contactProfileArrayList, ContactsActivity.this);
                recyclerView.setAdapter(mAdapter);
            }else{
                Toast.makeText(this, "Some error occured", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getContactList(String user_id) {
        showProgressBarDialog();
        //Getting all the contacts based on the user id
        db = FirebaseFirestore.getInstance();
        db.collection(user_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
//                               String firstname, String lastname, String email, String url, int phonenumber, String contactId
                                ContactProfile contactProfile = new ContactProfile(document.getString("firstname"),
                                        document.getString("lastname"),
                                        document.getString("email"),
                                        document.getString("url"),
                                        document.getString("imageName"),
                                        document.getLong("phonenumber"),
                                        document.getId());
                                contactProfileArrayList.add(contactProfile);
                                Log.d("demo", document.getId() + " => " + document.getData());
                            }
                            if(contactProfileArrayList.size()>0){
                                recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
                                layoutManager = new LinearLayoutManager(ContactsActivity.this);
                                recyclerView.setLayoutManager(layoutManager);
                                // specify an adapter (see also next example)
                                mAdapter = new ContactListAdapter(contactProfileArrayList, ContactsActivity.this);
                                recyclerView.setAdapter(mAdapter);
                            }
                            hideProgressBarDialog();
                        } else {
                            Log.d("demo", "Error getting documents: ", task.getException());
                            Toast.makeText(ContactsActivity.this, "Some Error Occured in retriving the documents", Toast.LENGTH_SHORT).show();
                            hideProgressBarDialog();
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }).setNegativeButton("No", null).show();
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

    @Override
    public void selectedItem(final int position) {
        //delete_code
        String user_id = getIntent().getExtras().getString("user");
        db.collection(user_id).document(contactProfileArrayList.get(position).contactId).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("demo", "DocumentSnapshot successfully deleted!");
                        if(!contactProfileArrayList.get(position).imageName.equals("")){
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            // Create a storage reference from our app
                            StorageReference storageRef = storage.getReference();

                            // Create a reference to the file to delete
                            StorageReference desertRef = storageRef.child(contactProfileArrayList.get(position).imageName);

                            // Delete the file
                            desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // File deleted successfully
                                    //Toast.makeText(ContactsActivity.this, "Contact Image Successfully!", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Uh-oh, an error occurred!
                                    Toast.makeText(ContactsActivity.this, "Some error occured in deleting the image", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        contactProfileArrayList.remove(position);
                        Toast.makeText(ContactsActivity.this, "Contact Deleted Successfully!", Toast.LENGTH_SHORT).show();
                        mAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("demo", "Error deleting document", e);
                        Toast.makeText(ContactsActivity.this, "Some Error occured in deleting the contact. Please try again!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
