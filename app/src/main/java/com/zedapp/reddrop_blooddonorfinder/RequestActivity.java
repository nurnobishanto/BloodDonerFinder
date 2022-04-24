package com.zedapp.reddrop_blooddonorfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zedapp.reddrop_blooddonorfinder.BloodRequest.AddBloodRequestActivity;
import com.zedapp.reddrop_blooddonorfinder.adapter.BloodRequestAdapter;
import com.zedapp.reddrop_blooddonorfinder.models.BloodRequestModel;

import java.util.ArrayList;
import java.util.List;

public class RequestActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private BloodRequestAdapter adapter;
    private List<BloodRequestModel> list;
    private TextView check;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_request);

        recyclerView = findViewById(R.id.recycler_view);
        swipeRefreshLayout = findViewById(R.id.swipe);
        check = findViewById(R.id.check);
        swipeRefreshLayout.setRefreshing(false);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(RequestActivity.this);
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
                if (list.isEmpty()) check.setVisibility(View.VISIBLE);
                else check.setVisibility(View.GONE);
                adapter =new BloodRequestAdapter(RequestActivity.this,list);
                recyclerView.setAdapter(adapter);
                swipeRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_items3, menu);
        return super.onCreateOptionsMenu(menu);

    }



    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.add:
                startActivity(new Intent(RequestActivity.this, AddBloodRequestActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }


}