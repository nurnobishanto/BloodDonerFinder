package com.zedapp.reddrop_blooddonorfinder;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zedapp.reddrop_blooddonorfinder.profile.EditMyProfileActivity;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;

public class SettingActivity extends AppCompatActivity {
    CardView cardView1;
    Button editprofile,googleConnect;
    private FirebaseAuth mAuth;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_setting);
        mAuth = FirebaseAuth.getInstance();

        cardView1 = findViewById(R.id.card1);

        editprofile= findViewById(R.id.editprofile);
        googleConnect= findViewById(R.id.googleConnect);

        SharedPreferences sharedPreference=getSharedPreferences("Users",MODE_PRIVATE);
        String user = sharedPreference.getString("UserId","");
        if(user.isEmpty() || user.equals("emergency_guest")){
            cardView1.setVisibility(View.GONE);

        }else {
            cardView1.setVisibility(View.VISIBLE);
            editprofile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(SettingActivity.this, EditMyProfileActivity.class));
                }
            });
            googleConnect.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onClick(View v) {
                    Dialog dialog;
                    dialog =  new Dialog(SettingActivity.this);
                    dialog.setContentView(R.layout.dialogue_email_pass);
                    dialog.getWindow().setLayout(700,700);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
                    dialog.show();
                    dialog.setCancelable(true);
                    EditText emailet = dialog.findViewById(R.id.email_et);
                    EditText passwordet = dialog.findViewById(R.id.password_et);
                    Button updatebtn = dialog.findViewById(R.id.update);
                    updatebtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!emailet.getText().equals("") && !passwordet.getText().equals("")){
                                AuthCredential credential = EmailAuthProvider.getCredential(emailet.getText().toString(), passwordet.getText().toString());
                                mAuth.getCurrentUser().linkWithCredential(credential)
                                        .addOnCompleteListener(SettingActivity.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "linkWithCredential:success");
                                                    dialog.dismiss();
                                                    try {
                                                        FirebaseUser user = task.getResult().getUser();
                                                        reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                                                        HashMap<String,Object> hashMap=new HashMap<>();
                                                        hashMap.put("fullname",user.getDisplayName());
                                                        hashMap.put("userid",user.getUid());
                                                        hashMap.put("email",user.getEmail());
                                                        hashMap.put("image", Objects.requireNonNull(user.getPhotoUrl()).toString());
                                                        reference.updateChildren(hashMap);
                                                        Toast.makeText(SettingActivity.this,"Success",Toast.LENGTH_LONG).show();
                                                    }catch (Exception e){
                                                        Toast.makeText(SettingActivity.this,e.toString(),Toast.LENGTH_LONG).show();
                                                    }




                                                } else {
                                                    dialog.dismiss();
                                                    Log.w(TAG, "linkWithCredential:failure", task.getException());
                                                    Toast.makeText(SettingActivity.this, "Authentication failed.",
                                                            Toast.LENGTH_SHORT).show();

                                                }
                                            }
                                        });
                            }
                        }
                    });


                }
            });

        }
    }

    public void logout(View view) {

        SharedPreferences sharedPreference=getSharedPreferences("Users",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.remove("UserId");
        editor.remove("UserEmail");
        editor.apply();
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
        startActivity(intent);
        Toast.makeText(this,"Logout",Toast.LENGTH_SHORT).show();
        deleteCache(SettingActivity.this);
       // clearAppData();
        finish();
    }
    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) { e.printStackTrace();}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
    private void clearAppData() {
        try {
            // clearing app data
            if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
                ((ActivityManager)getSystemService(ACTIVITY_SERVICE)).clearApplicationUserData(); // note: it has a return value!
            } else {
                String packageName = getApplicationContext().getPackageName();
                Runtime runtime = Runtime.getRuntime();
                runtime.exec("pm clear "+packageName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void termsPage(View view) {
        startActivity(new Intent(SettingActivity.this,terms.class));
    }

    public void privacyPage(View view) {
        startActivity(new Intent(SettingActivity.this,Privacy.class));
    }

    public void contactPage(View view) {
        startActivity(new Intent(SettingActivity.this,contact.class));
    }

    public void appVersion(View view) {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
            builder.setMessage(version);
            builder.setTitle("App Version");
            builder.setCancelable(true);

            builder.setNegativeButton(
                    "Cancel",
                    (dialog, id) -> dialog.cancel());
            AlertDialog alert11 = builder.create();
            alert11.show();

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}