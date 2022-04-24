package com.zedapp.reddrop_blooddonorfinder.BloodRequest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zedapp.reddrop_blooddonorfinder.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddBloodRequestActivity extends AppCompatActivity {
    String bloodgroup,hospital,message,fname,lname,cnumber,enumber,userId;
    RadioGroup bloodgrouprd;
    Button submit,upload;
    ImageView imageView;
    TextInputLayout fnameet,lnameet,cnumberet,enumberet,hospitalet,messageet;
    FirebaseAuth mAuth;
    int Image_Request_Code = 7;
    Uri FilePathUri;
    StorageReference storageReference;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("BloodRequestImage");
        setContentView(R.layout.activity_add_blood_request);
        userId = mAuth.getCurrentUser().getUid();
        bloodgroup = "";
        final String image = "no";
        imageView = findViewById(R.id.image);
        upload = findViewById(R.id.upload);

        fnameet = findViewById(R.id.fname);
        lnameet = findViewById(R.id.lname);
        hospitalet = findViewById(R.id.hospitalName);
        messageet = findViewById(R.id.message);
        cnumberet = findViewById(R.id.cnumber);
        enumberet = findViewById(R.id.enumber);



        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), Image_Request_Code);
            }
        });
        submit = findViewById(R.id.submit);
        bloodgrouprd = findViewById(R.id.bloodgroup);
        bloodgrouprd.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton bgrp = (RadioButton) group.findViewById(checkedId);
                if (null != bgrp && checkedId > -1) {
                    bloodgroup =bgrp.getText().toString();
                }

            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (bloodgroup.equals("")){
                    Toast.makeText(AddBloodRequestActivity.this,"Please Select Blood Group",Toast.LENGTH_LONG).show();
                }
               else if (fnameet.getEditText().getText().toString().isEmpty()){
                   fnameet.setError("Empty Field!");
               }
               else if(lnameet.getEditText().getText().toString().isEmpty()){
                   lnameet.setError("Empty Field!");
               }
               else if(hospitalet.getEditText().getText().toString().isEmpty()){
                   hospitalet.setError("Empty Field!");
               }
               else if(messageet.getEditText().getText().toString().isEmpty()){
                   messageet.setError("Empty Field!");
               }
               else if(cnumberet.getEditText().getText().toString().isEmpty()){
                   cnumberet.setError("Empty Field!");
               }
               else if(enumberet.getEditText().getText().toString().isEmpty()){
                   enumberet.setError("Empty Field!");
               }else {

                   UploadImage();


                   }
            }
        });


    }



    @Override
    public void onBackPressed() {
        finish();
    }

    public void back(View view) {
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Image_Request_Code && resultCode == RESULT_OK && data != null && data.getData() != null) {

            FilePathUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), FilePathUri);
                imageView.setImageBitmap(bitmap);
                imageView.setVisibility(View.VISIBLE);
            }
            catch (IOException e) {

                e.printStackTrace();
            }
        }
    }


    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;

    }

    public void UploadImage() {

        if (FilePathUri != null) {

            final ProgressDialog pd = new ProgressDialog(AddBloodRequestActivity.this);
            pd.setMessage("Image Uploading..");
            pd.show();
            final StorageReference ref = storageReference.child(System.currentTimeMillis() + "." + GetFileExtension(FilePathUri));

            imageView.setDrawingCacheEnabled(true);
            imageView.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = ref.putBytes(data);
            uploadTask = ref.putFile(FilePathUri);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        Toast.makeText(getApplicationContext(), "Image Uploaded Successfully ", Toast.LENGTH_LONG).show();
                        uploadInfo(downloadUri.toString());
                    } else {
                        // Handle failures
                        // ...
                        Toast.makeText(getApplicationContext(), "Image Uploaded Failed ", Toast.LENGTH_LONG).show();
                        pd.dismiss();
                    }
                }
            });



        }
        else {
            uploadInfo("no");
            //Toast.makeText(AddBloodRequestActivity.this, "Please Select Image or Add Image Name", Toast.LENGTH_LONG).show();

        }
    }

    private void uploadInfo(String image) {

        final ProgressDialog pd = new ProgressDialog(AddBloodRequestActivity.this);
        pd.setMessage("Please wait..");
        pd.show();
        fname = fnameet.getEditText().getText().toString();
        lname = lnameet.getEditText().getText().toString();
        hospital = hospitalet.getEditText().getText().toString();
        message = messageet.getEditText().getText().toString();
        cnumber = cnumberet.getEditText().getText().toString();
        enumber = enumberet.getEditText().getText().toString();

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("BloodRequest");
        String key = dbRef.push().getKey();
        assert key != null;
        dbRef = dbRef.child(key);
        Map userInfo = new HashMap();

        userInfo.put("userid",userId);
        userInfo.put("brKey",key);
        userInfo.put("fname",fname);
        userInfo.put("lname",lname);
        userInfo.put("image",image);
        userInfo.put("fullname",fname+" "+lname);
        userInfo.put("hospital",hospital);
        userInfo.put("message",message);
        userInfo.put("contactNO","+880"+cnumber);
        userInfo.put("emergencyNO","+880"+enumber);
        userInfo.put("BloodGroup",bloodgroup);

        dbRef.updateChildren(userInfo);
        pd.dismiss();
        Toast.makeText(AddBloodRequestActivity.this,"Blood Requested Successful",Toast.LENGTH_LONG).show();
        PushNotification(fname+" "+lname+" Need "+bloodgroup+"Blood at +"+hospital,"BR",key,"All");
        finish();

    }

    private void PushNotification(String s, String type, String typeKey, String reciber) {
        FirebaseAuth mauth = FirebaseAuth.getInstance();
        String myId = mauth.getCurrentUser().getUid();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Notifications");
        String key = dbRef.push().getKey();
        assert key != null;
        dbRef = dbRef.child(key);
        Map userInfo = new HashMap();

        userInfo.put("typeKey",typeKey);
        userInfo.put("message",s);
        userInfo.put("type",type);
        userInfo.put("nKey",key);
        userInfo.put("reciever",reciber);
        userInfo.put("sender",myId);

        dbRef.updateChildren(userInfo);

    }

}