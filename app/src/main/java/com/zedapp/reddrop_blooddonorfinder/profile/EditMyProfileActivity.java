package com.zedapp.reddrop_blooddonorfinder.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.zedapp.reddrop_blooddonorfinder.MainActivity;
import com.zedapp.reddrop_blooddonorfinder.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditMyProfileActivity extends AppCompatActivity {
    final Calendar myCalendar= Calendar.getInstance();
    String fullname,ability,dateofbirth,ld,city,address,email,cnumber,enumber,bloodgroup;
    TextInputLayout addresset,emailet,cnumberet,enumberet,fullnameet;
    RadioGroup bloodgrouprd,ailityrb;
    @SuppressLint("StaticFieldLeak")
    TextView cityet,dateofbirthet,lastDonate;
    Button update;
    FirebaseAuth mAuth;
    String userId;
    String[] districts;
    Dialog dialog;
    CircleImageView profile_image;
    int Image_Request_Code = 7;
    Uri FilePathUri;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_my_profile);

        ability = "";
        bloodgroup = "";


        fullnameet = findViewById(R.id.fullname);
        dateofbirthet = findViewById(R.id.dob);
        lastDonate = findViewById(R.id.lastDonate);
        cityet = findViewById(R.id.city);
        addresset = findViewById(R.id.address);
        emailet = findViewById(R.id.email);
        cnumberet = findViewById(R.id.cnumber);
        enumberet = findViewById(R.id.enumber);
        profile_image = findViewById(R.id.profile_image);

        update = findViewById(R.id.update);
        ailityrb = findViewById(R.id.ailityrb);
        bloodgrouprd = findViewById(R.id.bloodgroup);
        districts = getResources().getStringArray(R.array.bd_districts);
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference("ProfileImage");
        getUserInfomation(userId);
        cityet.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                dialog =  new Dialog(EditMyProfileActivity.this);
                dialog.setContentView(R.layout.dialogue_search_box_dark);
                dialog.getWindow().setLayout(700,1200);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));

                dialog.show();

                EditText searcet = dialog.findViewById(R.id.search_text);
                ListView listViewd = dialog.findViewById(R.id.sarch_list);

                ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(EditMyProfileActivity.this,R.layout.support_simple_spinner_dropdown_item,districts);
                stringArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                listViewd.setAdapter(stringArrayAdapter);
                searcet.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        stringArrayAdapter.getFilter().filter(s);

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }


                });

                listViewd.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        cityet.setText(stringArrayAdapter.getItem(position));
                        dialog.dismiss();
                    }
                });


            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadImage();
            }
        });
        DatePickerDialog.OnDateSetListener date = (view, year, month, day) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH,month);
            myCalendar.set(Calendar.DAY_OF_MONTH,day);
            updateLabeldb();
        };
        DatePickerDialog.OnDateSetListener lastlddate = (view, year, month, day) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH,month);
            myCalendar.set(Calendar.DAY_OF_MONTH,day);
            updateLabelld();
        };
        dateofbirthet.setOnClickListener(v -> {
                new DatePickerDialog(EditMyProfileActivity.this,date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();


        });
        lastDonate.setOnClickListener(v -> {
            new DatePickerDialog(EditMyProfileActivity.this,lastlddate,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();


        });


        ailityrb.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton abrp = (RadioButton) group.findViewById(checkedId);
            if (null != abrp && checkedId > -1) {
                ability =abrp.getText().toString();
            }
        });
        bloodgrouprd.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton bgrp = (RadioButton) group.findViewById(checkedId);
            if (null != bgrp && checkedId > -1) {
                bloodgroup =bgrp.getText().toString();
            }

        });

    }

    private void getUserInfomation(String key) {

        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(key);
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    if(snapshot.child("ability").getValue()!=null){
                        ability = snapshot.child("ability").getValue().toString();
                        switch (ability) {
                            case "Yes":
                                ailityrb.check(R.id.Yes);
                                break;
                            case "No":
                                ailityrb.check(R.id.No);
                                break;
                        }

                    }
                    if(snapshot.child("BloodGroup").getValue()!=null){
                        bloodgroup = snapshot.child("BloodGroup").getValue().toString();
                        switch (bloodgroup) {
                            case "A+":
                                bloodgrouprd.check(R.id.ap);
                                break;
                            case "A-":
                                bloodgrouprd.check(R.id.an);
                                break;
                            case "B+":
                                bloodgrouprd.check(R.id.bp);
                                break;
                            case "B-":
                                bloodgrouprd.check(R.id.bn);
                                break;
                            case "AB+":
                                bloodgrouprd.check(R.id.abp);
                                break;
                            case "AB-":
                                bloodgrouprd.check(R.id.abn);
                                break;
                            case "O+":
                                bloodgrouprd.check(R.id.op);
                                break;
                            case "O-":
                                bloodgrouprd.check(R.id.on);
                                break;
                        }
                    }
                    if(snapshot.child("emergencyNO").getValue()!=null){
                        enumberet.getEditText().setText(snapshot.child("emergencyNO").getValue().toString());
                    }
                    if(snapshot.child("image").getValue()!=null){
                        Picasso.get()
                                .load(snapshot.child("image").getValue().toString())
                                .placeholder(R.drawable.male_user)
                                .placeholder(R.drawable.female_user)
                                .into(profile_image);
                    }
                    if(snapshot.child("contactNO").getValue()!=null){
                        cnumberet.getEditText().setText(snapshot.child("contactNO").getValue().toString());
                    }
                    if(snapshot.child("address").getValue()!=null){
                        addresset.getEditText().setText(snapshot.child("address").getValue().toString());
                    }
                    if(snapshot.child("city").getValue()!=null){
                        cityet.setText(snapshot.child("city").getValue().toString());
                    }
                    if(snapshot.child("dateofbirth").getValue()!=null){
                        dateofbirthet.setText(snapshot.child("dateofbirth").getValue().toString());
                    }
                    if(snapshot.child("lastDonate").getValue()!=null){
                        lastDonate.setText(snapshot.child("lastDonate").getValue().toString());
                    }

                    if(snapshot.child("fullname").getValue()!=null){
                        fullnameet.getEditText().setText(snapshot.child("fullname").getValue().toString());
                    }
                    if(snapshot.child("email").getValue()!=null){
                        emailet.getEditText().setText(snapshot.child("email").getValue().toString());
                    }



                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }


    private void updateLabeldb(){
        String myFormat="MM/dd/yy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        dateofbirthet.setText(dateFormat.format(myCalendar.getTime()));
    }
    private void updateLabelld(){
        String myFormat="MM/dd/yy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        lastDonate.setText(dateFormat.format(myCalendar.getTime()));
    }
    public void cancel(View view) {
        finish();
    }

    public void changeImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), Image_Request_Code);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Image_Request_Code && resultCode == RESULT_OK && data != null && data.getData() != null) {

            FilePathUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), FilePathUri);
                profile_image.setImageBitmap(bitmap);
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

            final ProgressDialog pd = new ProgressDialog(EditMyProfileActivity.this);
            pd.setMessage("Image Uploading..");
            pd.show();
            final StorageReference ref = storageReference.child(System.currentTimeMillis() + "." + GetFileExtension(FilePathUri));

            profile_image.setDrawingCacheEnabled(true);
            profile_image.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) profile_image.getDrawable()).getBitmap();
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
            uploadInfo("default");
            //Toast.makeText(AddBloodRequestActivity.this, "Please Select Image or Add Image Name", Toast.LENGTH_LONG).show();

        }
    }

    private void uploadInfo(String image) {
        if (ability.isEmpty()){
            Toast.makeText(EditMyProfileActivity.this,"Please Select ability Option",Toast.LENGTH_LONG).show();
        }
        else if (fullnameet.getEditText().getText().toString().isEmpty()){
            fullnameet.setError("Empty Field!");
        }

        else if(dateofbirthet.getText().toString().equals("Select Date of Birth")){
            Toast.makeText(EditMyProfileActivity.this,"Please Select Date of Birth",Toast.LENGTH_LONG).show();
        }
        else if(cityet.getText().toString().equals("Select One")){
            Toast.makeText(EditMyProfileActivity.this,"Please Select District",Toast.LENGTH_LONG).show();
        }
        else if(addresset.getEditText().getText().toString().isEmpty()){
            addresset.setError("Empty Field!");
        }
        else if(emailet.getEditText().getText().toString().isEmpty()){
            emailet.setError("Empty Field!");
        }
        else if(cnumberet.getEditText().getText().toString().isEmpty()){
            cnumberet.setError("Empty Field!");
        }
        else if(enumberet.getEditText().getText().toString().isEmpty()){
            enumberet.setError("Empty Field!");
        }

        else if (bloodgroup.equals("")){
            Toast.makeText(EditMyProfileActivity.this,"Please Select Blood Group",Toast.LENGTH_LONG).show();
        }
        else if(lastDonate.getText().toString().equals("Select Date of Last Donate")){
            Toast.makeText(EditMyProfileActivity.this,"Please Select Date of Last Donate",Toast.LENGTH_LONG).show();
        }
        else {
            fullname = fullnameet.getEditText().getText().toString();
            dateofbirth = dateofbirthet.getText().toString();
            city = cityet.getText().toString();
            address = addresset.getEditText().getText().toString();
            email = emailet.getEditText().getText().toString();
            cnumber = cnumberet.getEditText().getText().toString();
            enumber = enumberet.getEditText().getText().toString();
            ld = lastDonate.getText().toString();


            final ProgressDialog pd = new ProgressDialog(EditMyProfileActivity.this);
            pd.setMessage("Please wait..");
            pd.show();


            String userId = mAuth.getCurrentUser().getUid();
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
            Map<String, Object> userInfo = new HashMap<>();

            userInfo.put("fullname",fullname);
            userInfo.put("userid",userId);
            userInfo.put("email",email);
            userInfo.put("ability",ability);
            userInfo.put("dateofbirth",dateofbirth);
            userInfo.put("city",city);
            userInfo.put("address",address);
            userInfo.put("contactNO",cnumber);
            userInfo.put("emergencyNO",enumber);
            userInfo.put("BloodGroup",bloodgroup);
            userInfo.put("lastDonate",ld);
            userInfo.put("image",image);

            dbRef.updateChildren(userInfo);
            pd.dismiss();
            Toast.makeText(EditMyProfileActivity.this,"Update Successfully",Toast.LENGTH_LONG).show();
            finish();

        }
    }
}