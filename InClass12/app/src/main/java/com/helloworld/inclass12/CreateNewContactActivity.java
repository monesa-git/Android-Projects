package com.helloworld.inclass12;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class CreateNewContactActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private FirebaseStorage storage;
    private FirebaseFirestore db;
    private StorageReference storageRef;
    private boolean isTakenPhoto = false;
    private ImageButton imageButton;
    private ProgressDialog progressDialog;
    private EditText editTextCn;
    private EditText editTextCe;
    private EditText editTextCp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_contact);
        setTitle("Create New Contact");
        imageButton = findViewById(R.id.imageButton);
        storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        storageRef = storage.getReference();
        db = FirebaseFirestore.getInstance();

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        findViewById(R.id.buttonSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextCn = findViewById(R.id.editTextCName);
                editTextCe = findViewById(R.id.editTextCEmail);
                editTextCp = findViewById(R.id.editTextCPhNum);
                if(checkValidations(editTextCn) && checkValidations(editTextCe) && checkValidations(editTextCp) && checkEmailValidations(editTextCe) && checkPhoneValidations(editTextCp)) {
                    Log.d("Validation", " Entered ");
                    showProgressBarDialog();
                    String imagePath = "";
                    if (isTakenPhoto) {
                        String user_id = getIntent().getExtras().getString("user");
                        imagePath = "images/"+user_id+"_ "+editTextCn.getText().toString().trim()+" "+ editTextCe.getText().toString().trim() + ".jpg";
                        final StorageReference imageRepo = storageRef.child(imagePath);
                        Bitmap bitmap = ((BitmapDrawable) imageButton.getDrawable()).getBitmap();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] data = baos.toByteArray();
                        UploadTask uploadTask = imageRepo.putBytes(data);
                        final String finalImagePath = imagePath;
                        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }
                                // Continue with the task to get the download URL
                                return imageRepo.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri downloadUri = task.getResult();
                                    storeContactDetails(finalImagePath, downloadUri.toString());
                                }else{
                                    Toast.makeText(CreateNewContactActivity.this, "Upload Failure", Toast.LENGTH_SHORT).show();
                                    hideProgressBarDialog();
                                }
                            }
                        });
                    } else {
                        imagePath = "";
                        storeContactDetails(imagePath, "");
                    }

                }
            }
        });
    }



    private void storeContactDetails(String imagePath, String url){
        //String firstname, String lastname, String email, String url, String imageName, Long phonenumber, String contactId
        String user_id = getIntent().getExtras().getString("user");
        final ContactProfile contactProfile = new ContactProfile(editTextCn.getText().toString(),
                "",
                editTextCe.getText().toString(),
                url,
                imagePath,
                Long.parseLong(editTextCp.getText().toString()),
                "");
        db.collection(user_id)
                .add(contactProfile)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("demo", "Expense Successfully added!");
                        contactProfile.contactId = documentReference.getId();
                        Intent intent = new Intent();
                        intent.putExtra("contact_profile", contactProfile);
                        setResult(200, intent);
                        hideProgressBarDialog();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("demo", "Some Error occured!");
                hideProgressBarDialog();
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageButton.setImageBitmap(imageBitmap);
            isTakenPhoto = true;
        }
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

    public boolean checkEmailValidations(EditText editText)
    {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if(!editText.getText().toString().trim().matches(emailPattern))
        {
            editText.setError("Invalid Email pattern");
            return false;
        }
        return true;
    }

    public boolean checkPhoneValidations(EditText editText)
    {
        if(editText.getText().toString().trim().length() != 10)
        {
            editText.setError("Phone number should be in 10 digits");
            return false;
        }
        return true;
    }
    public boolean checkValidations(EditText editText){
        if(editText.getText().toString().trim().equals("")){
            editText.setError("Cannot be empty");
            return false;
        }else{
            return true;
        }
    }
}
