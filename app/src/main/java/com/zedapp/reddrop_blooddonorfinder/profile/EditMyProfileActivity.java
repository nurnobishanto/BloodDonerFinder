package com.zedapp.reddrop_blooddonorfinder.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zedapp.reddrop_blooddonorfinder.MainActivity;
import com.zedapp.reddrop_blooddonorfinder.R;
import com.zedapp.reddrop_blooddonorfinder.SignUp;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditMyProfileActivity extends AppCompatActivity {
    final Calendar myCalendar= Calendar.getInstance();
    String ability,fname,lname,dateofbirth,city,address,email,cnumber,enumber,bloodgroup;
    TextInputLayout fnameet,lnameet,addresset,emailet,cnumberet,enumberet,fullnameet;
    RadioGroup bloodgrouprd,ailityrb;
    @SuppressLint("StaticFieldLeak")
    TextView cityet,dateofbirthet;
    Button update;
    FirebaseAuth mAuth;
    String userId;
    String[] districts;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_my_profile);

        ability = "";
        bloodgroup = "";

        fnameet = findViewById(R.id.fname);
        lnameet = findViewById(R.id.lname);
        fullnameet = findViewById(R.id.fullname);
        dateofbirthet = findViewById(R.id.dob);
        cityet = findViewById(R.id.city);
        addresset = findViewById(R.id.address);
        emailet = findViewById(R.id.email);
        cnumberet = findViewById(R.id.cnumber);
        enumberet = findViewById(R.id.enumber);

        update = findViewById(R.id.update);
        ailityrb = findViewById(R.id.ailityrb);
        bloodgrouprd = findViewById(R.id.bloodgroup);
        districts = getResources().getStringArray(R.array.bd_districts);
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        getUserInfomation(userId);
        cityet.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                dialog =  new Dialog(EditMyProfileActivity.this);
                dialog.setContentView(R.layout.dialogue_search_box);
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
                if (ability.isEmpty()){
                    Toast.makeText(EditMyProfileActivity.this,"Please Select ability Option",Toast.LENGTH_LONG).show();
                }
                else if (fnameet.getEditText().getText().toString().isEmpty()){
                    fnameet.setError("Empty Field!");
                }
                else if(lnameet.getEditText().getText().toString().isEmpty()){
                    lnameet.setError("Empty Field!");
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
                else {
                    fname = fnameet.getEditText().getText().toString();
                    lname = lnameet.getEditText().getText().toString();
                    dateofbirth = dateofbirthet.getText().toString();
                    city = cityet.getText().toString();
                    address = addresset.getEditText().getText().toString();
                    email = emailet.getEditText().getText().toString();
                    cnumber = cnumberet.getEditText().getText().toString();
                    enumber = enumberet.getEditText().getText().toString();


                    final ProgressDialog pd = new ProgressDialog(EditMyProfileActivity.this);
                    pd.setMessage("Please wait..");
                    pd.show();


                                String userId = mAuth.getCurrentUser().getUid();
                                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
                                Map userInfo = new HashMap();

                                userInfo.put("fname",fname);
                                userInfo.put("lname",lname);
                                userInfo.put("fullname",fullnameet.getEditText().getText().toString());
                                userInfo.put("userid",userId);
                                userInfo.put("email",email);
                                userInfo.put("abilty",ability);
                                userInfo.put("dateofbirth",dateofbirth);
                                userInfo.put("city",city);
                                userInfo.put("address",address);
                                userInfo.put("contactNO",cnumber);
                                userInfo.put("emergencyNO",enumber);
                                userInfo.put("BloodGroup",bloodgroup);

                                dbRef.updateChildren(userInfo);
                                pd.dismiss();
                                Toast.makeText(EditMyProfileActivity.this,"Update Successfully",Toast.LENGTH_LONG).show();
                                startActivity(new Intent(EditMyProfileActivity.this, MainActivity.class));




                }



            }
        });
        DatePickerDialog.OnDateSetListener date = (view, year, month, day) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH,month);
            myCalendar.set(Calendar.DAY_OF_MONTH,day);
            updateLabel();
        };
        dateofbirthet.setOnClickListener(v -> {


                new DatePickerDialog(EditMyProfileActivity.this,date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();


        });

        ailityrb.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton abrp = (RadioButton) group.findViewById(checkedId);
                if (null != abrp && checkedId > -1) {
                    ability =abrp.getText().toString();
                }
            }
        });
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
                    }
                    if(snapshot.child("BloodGroup").getValue()!=null){
                        bloodgroup = snapshot.child("BloodGroup").getValue().toString();
                    }
                    if(snapshot.child("emergencyNO").getValue()!=null){
                        enumberet.getEditText().setText(snapshot.child("emergencyNO").getValue().toString());
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
                    if(snapshot.child("fname").getValue()!=null){
                        fnameet.getEditText().setText(snapshot.child("fname").getValue().toString());
                    }
                    if(snapshot.child("lname").getValue()!=null){
                        lnameet.getEditText().setText(snapshot.child("lname").getValue().toString());
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


    private void updateLabel(){
        String myFormat="MM/dd/yy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        dateofbirthet.setText(dateFormat.format(myCalendar.getTime()));
    }
    public void cancel(View view) {
        finish();
    }
}