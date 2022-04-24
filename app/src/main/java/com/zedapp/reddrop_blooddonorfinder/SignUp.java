package com.zedapp.reddrop_blooddonorfinder;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.CollationElementIterator;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SignUp extends AppCompatActivity {
    String donor,fname,lname,dateofbirth,city,address,email,cnumber,enumber,bloodgroup,password;
    TextInputLayout fnameet,lnameet,addresset,emailet,cnumberet,enumberet,passwordet;
    RadioGroup donorrd,bloodgrouprd;
    @SuppressLint("StaticFieldLeak")
    static  TextView cityet,dateofbirthet;
    Button signup, emergency;


    FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    FirebaseAuth mAuth;

    String[] districts;
    Dialog dialog;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        SharedPreferences sharedPreference=getSharedPreferences("Users",MODE_PRIVATE);
        String user = sharedPreference.getString("UserId","");
        if(!user.isEmpty()){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        mAuth =FirebaseAuth.getInstance();
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final String user =mAuth.getCurrentUser().getUid();
                if (user != null) {
                    Intent intent = new Intent(SignUp.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                } }
        };

        donor = "";
        bloodgroup = "";

        fnameet = findViewById(R.id.fname);
        lnameet = findViewById(R.id.lname);
        dateofbirthet = findViewById(R.id.dob);
        cityet = findViewById(R.id.city);
        addresset = findViewById(R.id.address);
        emailet = findViewById(R.id.email);
        cnumberet = findViewById(R.id.cnumber);
        enumberet = findViewById(R.id.enumber);
        passwordet = findViewById(R.id.password);
        signup = findViewById(R.id.signup);
        districts = getResources().getStringArray(R.array.bd_districts);

        cityet.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                dialog =  new Dialog(SignUp.this);
                dialog.setContentView(R.layout.dialogue_search_box);
                dialog.getWindow().setLayout(700,1200);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));

                dialog.show();

                EditText searcet = dialog.findViewById(R.id.search_text);
                ListView listViewd = dialog.findViewById(R.id.sarch_list);

                ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(SignUp.this,R.layout.support_simple_spinner_dropdown_item,districts);
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

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (donor.equals("")){
                    Toast.makeText(SignUp.this,"Please Select Donor Option",Toast.LENGTH_LONG).show();
                }
                else if (fnameet.getEditText().getText().toString().isEmpty()){
                    fnameet.setError("Empty Field!");
                }
                else if(lnameet.getEditText().getText().toString().isEmpty()){
                    lnameet.setError("Empty Field!");
                }
                else if(dateofbirthet.getText().toString().equals("Select Date of Birth")){
                    Toast.makeText(SignUp.this,"Please Select Date of Birth",Toast.LENGTH_LONG).show();
                }
                else if(cityet.getText().toString().equals("Select One")){
                    Toast.makeText(SignUp.this,"Please Select District",Toast.LENGTH_LONG).show();

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
                else if(passwordet.getEditText().getText().toString().isEmpty()){
                    passwordet.setError("Empty Field!");
                }
                else if (bloodgroup.equals("")){
                    Toast.makeText(SignUp.this,"Please Select Blood Group",Toast.LENGTH_LONG).show();
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

                    password = passwordet.getEditText().getText().toString();
                    final ProgressDialog pd = new ProgressDialog(SignUp.this);
                    pd.setMessage("Please wait..");
                    pd.show();

                    mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                pd.dismiss();
                                emailet.setError(task.getException().getMessage().toString());
                            }else {
                                String userId = mAuth.getCurrentUser().getUid();
                                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
                                Map userInfo = new HashMap();

                                userInfo.put("fname",fname);
                                userInfo.put("lname",lname);
                                userInfo.put("fullname",fname+" "+lname);
                                userInfo.put("userid",userId);
                                userInfo.put("email",email);
                                userInfo.put("donor",donor);
                                userInfo.put("password",password);
                                userInfo.put("dateofbirth",dateofbirth);
                                userInfo.put("city",city);
                                userInfo.put("address",address);
                                userInfo.put("contactNO",cnumber);
                                userInfo.put("emergencyNO",enumber);
                                userInfo.put("BloodGroup",bloodgroup);

                                dbRef.updateChildren(userInfo);
                                pd.dismiss();
                                Toast.makeText(SignUp.this,"Registration Completed",Toast.LENGTH_LONG).show();

                                SharedPreferences sharedPreferences = getSharedPreferences("Users", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("UserId", mAuth.getCurrentUser().getUid());
                                editor.putString("UserEmail", email);
                                editor.commit();
                                startActivity(new Intent(SignUp.this,MainActivity.class));
                                finish();

                            }
                        }
                    });



                }



            }
        });

        dateofbirthet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");

            }
        });
        donorrd = findViewById(R.id.isdonor);
        donorrd.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton dnr = (RadioButton) group.findViewById(checkedId);
                if (null != dnr && checkedId > -1) {
                    donor = dnr.getText().toString();
                }

            }
        });
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



    }

    public void login(View view) {
        startActivity(new Intent(SignUp.this,LoginActivity.class));
        finish();
    }


    public class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {



        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user


            dateofbirthet.setText(day+"/"+month+"/"+year);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}