package com.zedapp.reddrop_blooddonorfinder;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;


public class LoginActivity extends AppCompatActivity {
    private Button signin;
    private EditText iemail,ipass;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    private GoogleSignInClient mGoogleSignInClient;
    private static int RC_SIGN_IN = 101;
    private DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
                final String user = mAuth.getCurrentUser().getUid();
                if (user != null) {
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                    return;

                }
            }
        };
        iemail=  findViewById(R.id.lemailId);
        ipass= findViewById(R.id.lpassId);
        signin=findViewById(R.id.signBtnId);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(iemail.getText().toString())) {
                    iemail.setError("email address empty"); }
                else if (TextUtils.isEmpty(ipass.getText().toString())) {
                    ipass.setError("password empty");
                }
                else {
                    final ProgressDialog pd= new ProgressDialog(LoginActivity.this);
                    pd.setMessage("sign in Proccessinhg...");
                    pd.show();

                    final String mail = iemail.getText().toString();
                    final String password =ipass.getText().toString();
                    mAuth.signInWithEmailAndPassword(mail,password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful())
                            {
                                iemail.setError(task.getException().getMessage());
                                ipass.setError(task.getException().getMessage());
                                pd.dismiss();
                            }else {
                                pd.dismiss();
                                SharedPreferences sharedPreferences = getSharedPreferences("Users", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("UserId", mAuth.getCurrentUser().getUid());
                                editor.putString("UserEmail", mail);
                                editor.commit();
                                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                finish();

                            }
                        }
                    });

                }
            }
        });
        CreatGoogleRequest();
    }

    public void contactPage(View view) {
        startActivity(new Intent(LoginActivity.this,contact.class));
    }

    public void signupPage(View view) {
        startActivity(new Intent(LoginActivity.this,SignUp.class));
        finish();
    }

    public void forgotPage(View view) {
        startActivity(new Intent(LoginActivity.this,ForgotPassword.class));
    }

    public void termsPage(View view) {
        startActivity(new Intent(LoginActivity.this,terms.class));
    }

    public void privacyPage(View view) {
        startActivity(new Intent(LoginActivity.this,Privacy.class));
    }

    public void googleLogin(View view) {
        googlesignIn();

    }
    private void CreatGoogleRequest(){
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken("818931195488-n2f15n3nhoa4ltutd120pssrruv3dmui.apps.googleusercontent.com")
//                .requestEmail()
//                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }
    private void googlesignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(LoginActivity.this,"Google sign in failed",Toast.LENGTH_LONG).show();
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

    }
    private void firebaseAuthWithGoogle(String idToken) {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading");
        dialog.setTitle("Login");
        dialog.show();
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = mAuth.getCurrentUser();
                            isNewAccount(user.getUid(),user);
                            SharedPreferences sharedPreferences = getSharedPreferences("Users", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("UserId", user.getUid());
                            editor.putString("UserEmail", user.getEmail());
                            editor.commit();
                            startActivity(new Intent(LoginActivity.this,MainActivity.class));
                            finish();







                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                        }
                        dialog.dismiss();
                    }

                    private void isNewAccount(String uid, FirebaseUser user) {

                        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    if(snapshot.child("fullname").getValue()==null) {

                                        reference= FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                                        HashMap<String,Object> hashMap=new HashMap<>();
                                        hashMap.put("fullname",user.getDisplayName());
                                        hashMap.put("userid",user.getUid());
                                        hashMap.put("email",user.getEmail());
                                        hashMap.put("image", Objects.requireNonNull(user.getPhotoUrl()).toString());
                                        reference.updateChildren(hashMap);

                                    }

                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });

                    }
                });
    }


    public void Emergency(View view) {
        Dialog dialog = new Dialog(LoginActivity.this);
        dialog.setTitle("Login");
        dialog.show();
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInAnonymously:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            reference= FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                            HashMap<String,Object> hashMap=new HashMap<>();
                            NameGenerator nameGenerator = new NameGenerator(10);
                            hashMap.put("fullname",nameGenerator.getName());
                            hashMap.put("userid",user.getUid());
                            hashMap.put("user_type","guest");
                            reference.updateChildren(hashMap);
                            SharedPreferences sharedPreferences = getSharedPreferences("Users", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("UserId", user.getUid());
                            editor.putString("UserEmail", user.getEmail());
                            editor.commit();
                            startActivity(new Intent(LoginActivity.this,MainActivity.class));
                            finish();



                            dialog.dismiss();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });
    }

    public class NameGenerator {
        private static final int diffBetweenAtoZ = 25;
        private static final int charValueOfa = 97;
        private String lastGeneratedName = "";
        int length;

        char[] vowels = {
                'a', 'e', 'i', 'o', 'u'
        };

        public NameGenerator(int lengthOfName) {
            if (lengthOfName < 5 || lengthOfName > 10) {
                System.out.println("Setting default length to 7");
                lengthOfName = 7;
            }

            this.length = lengthOfName;
        }

        public String getName() {
            for (;;) {
                Random randomNumberGenerator = new Random(Calendar.getInstance()
                        .getTimeInMillis());

                char[] nameInCharArray = new char[length];

                for (int i = 0; i < length; i++) {
                    if (positionIsOdd(i)) {
                        nameInCharArray[i] = getVowel(randomNumberGenerator);
                    } else {
                        nameInCharArray[i] = getConsonant(randomNumberGenerator);
                    }
                }
                nameInCharArray[0] = (char) Character
                        .toUpperCase(nameInCharArray[0]);

                String currentGeneratedName = new String(nameInCharArray);

                if (!currentGeneratedName.equals(lastGeneratedName)) {
                    lastGeneratedName = currentGeneratedName;
                    return currentGeneratedName;
                }

            }

        }

        private boolean positionIsOdd(int i) {
            return i % 2 == 0;
        }

        private char getConsonant(Random randomNumberGenerator) {
            for (;;) {
                char currentCharacter = (char) (randomNumberGenerator
                        .nextInt(diffBetweenAtoZ) + charValueOfa);
                if (currentCharacter == 'a' || currentCharacter == 'e'
                        || currentCharacter == 'i' || currentCharacter == 'o'
                        || currentCharacter == 'u')
                    continue;
                else
                    return currentCharacter;
            }

        }

        private char getVowel(Random randomNumberGenerator) {
            return vowels[randomNumberGenerator.nextInt(vowels.length)];
        }
    }
}