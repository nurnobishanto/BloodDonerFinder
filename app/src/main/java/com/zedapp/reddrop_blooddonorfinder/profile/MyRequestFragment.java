package com.zedapp.reddrop_blooddonorfinder.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zedapp.reddrop_blooddonorfinder.R;
import com.zedapp.reddrop_blooddonorfinder.RequestActivity;
import com.zedapp.reddrop_blooddonorfinder.adapter.BloodRequestAdapter;
import com.zedapp.reddrop_blooddonorfinder.models.BloodRequestModel;

import java.util.ArrayList;
import java.util.List;


public class MyRequestFragment extends Fragment {

    private String userId;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private BloodRequestAdapter adapter;
    private List<BloodRequestModel> list;
    private TextView check;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_request, container, false);


        userId = getActivity().getIntent().getStringExtra("userId");

        swipeRefreshLayout = view.findViewById(R.id.swipe);
        check = view.findViewById(R.id.check);
        recyclerView = view.findViewById(R.id.recycler_view);

        swipeRefreshLayout.setRefreshing(false);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        readBloodRequests();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                readBloodRequests();
            }
        });

        return view;
    }
    private void readBloodRequests()
    {
        swipeRefreshLayout.setRefreshing(true);
        list = new ArrayList<>();
        list.clear();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("BloodRequest");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    if(snapshot.child("userid").getValue().toString().equals(userId)) {
                        BloodRequestModel obj = new BloodRequestModel();

                        if(snapshot.child("userid").getValue()!=null) obj.setUserid(snapshot.child("userid").getValue().toString());
                        if(snapshot.child("BloodGroup").getValue()!=null) obj.setBloodgroup(snapshot.child("BloodGroup").getValue().toString());
                        if(snapshot.child("hospital").getValue()!=null) obj.setHospital(snapshot.child("hospital").getValue().toString());
                        if(snapshot.child("message").getValue()!=null) obj.setMessage(snapshot.child("message").getValue().toString());
                        if(snapshot.child("brKey").getValue()!=null) obj.setBrkey(snapshot.child("brKey").getValue().toString());
                        else continue;
                        if(snapshot.child("postdate").getValue()!=null) obj.setDate(snapshot.child("postdate").getValue().toString());
                        if(snapshot.child("image").getValue()!=null) obj.setImage(snapshot.child("image").getValue().toString());
                        if(snapshot.child("emergencyNO").getValue()!=null) obj.setEnumber(snapshot.child("emergencyNO").getValue().toString());
                        if(snapshot.child("contactNO").getValue()!=null) obj.setCnumber(snapshot.child("contactNO").getValue().toString());
                        if(snapshot.child("fullname").getValue()!=null) obj.setFullname(snapshot.child("fullname").getValue().toString());

                        list.add(obj);
                    }
                }
                swipeRefreshLayout.setRefreshing(false);
                adapter = new BloodRequestAdapter(getContext(),list);
                recyclerView.setAdapter(adapter);
                if (list.size()<1){
                    check.setVisibility(View.VISIBLE);
                }else {
                    check.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}