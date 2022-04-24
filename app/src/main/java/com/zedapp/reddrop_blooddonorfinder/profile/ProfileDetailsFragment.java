package com.zedapp.reddrop_blooddonorfinder.profile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zedapp.reddrop_blooddonorfinder.MessageActivity;
import com.zedapp.reddrop_blooddonorfinder.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileDetailsFragment extends Fragment {

    private CircleImageView profileImg;
    private TextView name,blood,city,address,email,cnumber,enumber,lastdonate,ability;
    private String userId,myId;
    private Button emergency,contact,sendMsg,editProfile;
    private FirebaseUser fUser;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_details, container, false);

        fUser = FirebaseAuth.getInstance().getCurrentUser();

        myId = fUser.getUid();
        userId = getActivity().getIntent().getStringExtra("userId");

        editProfile = view.findViewById(R.id.editProfile);
        profileImg = view.findViewById(R.id.profile_image);
        name = view.findViewById(R.id.name);
        blood = view.findViewById(R.id.bloodgroup);
        city = view.findViewById(R.id.city);
        address = view.findViewById(R.id.address);
        email = view.findViewById(R.id.email);
        cnumber = view.findViewById(R.id.cnumber);
        enumber = view.findViewById(R.id.enumber);
        lastdonate = view.findViewById(R.id.ld);
        emergency = view.findViewById(R.id.emergency);
        contact = view.findViewById(R.id.contact);
        ability = view.findViewById(R.id.ability);
        sendMsg = view.findViewById(R.id.sendMsg);
        if (userId.equals(myId)){
            sendMsg.setVisibility(View.GONE);
            editProfile.setVisibility(View.VISIBLE);
            editProfile.setOnClickListener(v -> {
                startActivity(new Intent(getContext(), EditMyProfileActivity.class));
            });
        }
        sendMsg.setOnClickListener(v -> {
            Intent i = new Intent(getContext(), MessageActivity.class);
            i.putExtra("userId", userId);
            startActivity(i);
        });
        getUserInfomation(userId);

        return view;
    }

    private void getUserInfomation(String key) {

        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(key);
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if(snapshot.child("ability").getValue()!=null) {
                        ability.setVisibility(View.VISIBLE);
                        ability.setText("Ability : "+snapshot.child("ability").getValue().toString()); }
                    if(snapshot.child("BloodGroup").getValue()!=null) {
                        blood.setVisibility(View.VISIBLE);
                        blood.setText("Blood Group : "+snapshot.child("BloodGroup").getValue().toString()); }
                    if(snapshot.child("city").getValue()!=null) {
                        city.setVisibility(View.VISIBLE);
                        city.setText("City :"+snapshot.child("city").getValue().toString()); }
                    if(snapshot.child("fullname").getValue()!=null) { name.setText(snapshot.child("fullname").getValue().toString()); }
                    if(snapshot.child("contactNO").getValue()!=null) {
                        cnumber.setVisibility(View.VISIBLE);
                        contact.setVisibility(View.VISIBLE);
                        cnumber.setText(snapshot.child("contactNO").getValue().toString());
                     contact.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", cnumber.getText().toString(), null)));
                            }
                        });
                    }
                    if(snapshot.child("emergencyNO").getValue()!=null) {
                        enumber.setVisibility(View.VISIBLE);
                        emergency.setVisibility(View.VISIBLE);
                        enumber.setText(snapshot.child("emergencyNO").getValue().toString());
                        emergency.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", emergency.getText().toString(), null)));
                            }
                        });
                    }
                    if(snapshot.child("address").getValue()!=null) {
                        address.setVisibility(View.VISIBLE);
                        address.setText(snapshot.child("address").getValue().toString()); }
                    if(snapshot.child("lastDonate").getValue()!=null) {
                        lastdonate.setVisibility(View.VISIBLE);
                        lastdonate.setText("L/D: "+snapshot.child("lastDonate").getValue().toString()); }
                    if(snapshot.child("email").getValue()!=null) {
                        email.setVisibility(View.VISIBLE);
                        email.setText(snapshot.child("email").getValue().toString()); }
                    if(snapshot.child("image").getValue()!=null){
                        if (snapshot.child("sex").getValue()!=null && snapshot.child("sex").getValue().equals("Female")){
                            Glide.with(getContext()).load(snapshot.child("image").getValue()).placeholder(R.drawable.female_user).into(profileImg);
                        }

                        else{
                            Glide.with(getContext()).load(snapshot.child("image").getValue()).placeholder(R.drawable.male_user).into(profileImg);
                        }


                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

}