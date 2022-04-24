package com.zedapp.reddrop_blooddonorfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SearchView;


import com.google.firebase.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;
import com.zedapp.reddrop_blooddonorfinder.adapter.DonorAdapter;

import com.zedapp.reddrop_blooddonorfinder.adapter.FeedAdapter;
import com.zedapp.reddrop_blooddonorfinder.models.DonorModels;
import com.zedapp.reddrop_blooddonorfinder.models.FeedModels;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FindDonorActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DonorAdapter adapter;
    private List<DonorModels> list;

    String[] districts;
    Dialog dialog;
    String bgroup="All",city="All";
    private TextView check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_find_donor);
        check=findViewById(R.id.check);
        districts = getResources().getStringArray(R.array.bd_districts);


        recyclerView = findViewById(R.id.recycler_view);
        swipeRefreshLayout = findViewById(R.id.swipe);
        recyclerView.setHasFixedSize(true);
        swipeRefreshLayout.setRefreshing(false);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(FindDonorActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);




        readDonorData("All","All");

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                readDonorData(bgroup,city);
            }
        });

    }
    private void readDonorData(String bgroup, String city) {
        swipeRefreshLayout.setRefreshing(true);
        list = new ArrayList<>();
        list.clear();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {

                    DonorModels obj = new DonorModels();

                    if(snapshot.child("BloodGroup").getValue()!=null) { obj.setBloodgroup(snapshot.child("BloodGroup").getValue().toString()); }

                    else {
                        obj.setBloodgroup("N/A");
                        //continue;
                    }
                    if(snapshot.child("city").getValue()!=null) { obj.setCity(snapshot.child("city").getValue().toString()); }
                    else obj.setCity("Not Added Yet!");
                    if(snapshot.child("fullname").getValue()!=null) { obj.setFullName(snapshot.child("fullname").getValue().toString()); }
                    if(snapshot.child("contactNO").getValue()!=null) { obj.setCnumber(snapshot.child("contactNO").getValue().toString()); }
                    else obj.setCnumber("Not Added Yet!");
                    if(snapshot.child("emergencyNO").getValue()!=null) { obj.setEnumber(snapshot.child("emergencyNO").getValue().toString());  }
                    else obj.setEnumber("Not Added Yet!");
                    if(snapshot.child("userid").getValue()!=null) { obj.setUserid(snapshot.child("userid").getValue().toString()); }
                    else continue;
                    if(snapshot.child("address").getValue()!=null) { obj.setAddress(snapshot.child("address").getValue().toString());}
                    else obj.setAddress("Not Added Yet!");
                    if(snapshot.child("lastdonate").getValue()!=null) { obj.setLastdonate(snapshot.child("lastdonate").getValue().toString()); }
                    else obj.setLastdonate("Not Added Yet!");

                    if (bgroup.equals("All") && city.equals("All")){
                        list.add(obj);
                    }else if(snapshot.child("BloodGroup").getValue()!=null && bgroup.equals(snapshot.child("BloodGroup").getValue().toString())){
                        list.add(obj);
                    }else if(snapshot.child("city").getValue()!=null && city.equals(snapshot.child("city").getValue().toString())){
                        list.add(obj);
                    }
                }
                swipeRefreshLayout.setRefreshing(false);
                adapter = new DonorAdapter(FindDonorActivity.this,list);
                recyclerView.setAdapter(adapter);
                if (list.isEmpty()) check.setVisibility(View.VISIBLE);
                else check.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.filter:
                dialog =  new Dialog(FindDonorActivity.this);
                dialog.setContentView(R.layout.filter_box);
                dialog.getWindow().setLayout(1000,2000);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
                dialog.show();


                EditText searcet = dialog.findViewById(R.id.search_text);
                ListView listViewd = dialog.findViewById(R.id.sarch_list);
                TextView result = dialog.findViewById(R.id.select);
                RadioGroup bloodgrouprd;
                Button filter = dialog.findViewById(R.id.filter);
                bloodgrouprd = dialog.findViewById(R.id.bloodgroup);
                result.setText("Blood Group: "+bgroup+"\nLocation : "+city);
                bloodgrouprd.setOnCheckedChangeListener((group, checkedId) -> {
                    RadioButton bgrp = (RadioButton) group.findViewById(checkedId);
                    if (null != bgrp && checkedId > -1) {

                        bgroup = bgrp.getText().toString();
                        result.setText("Blood Group: "+bgroup+"\nLocation : "+city);
                    }

                });

                ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(FindDonorActivity.this,R.layout.support_simple_spinner_dropdown_item,districts);
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
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        city = stringArrayAdapter.getItem(position);
                        searcet.setText(stringArrayAdapter.getItem(position));
                        result.setText("Blood Group: "+bgroup+"\nLocation : "+city);



                    }
                });

                filter.setOnClickListener(v -> {
                    readDonorData(bgroup,city);
                    dialog.dismiss();
                });


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }



    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_items1, menu);

       /* MenuItem searchViewItem = menu.findItem(R.id.app_bar_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                searcData(query.toLowerCase());
              *//*  if(donorModelsList.contains(query)){
                    donorAdapter.getFilter().filter(query);
                }else{
                    Toast.makeText(FindDonorActivity.this, "No Match found",Toast.LENGTH_LONG).show();
                }*//*
                return false;

            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searcData(newText.toLowerCase().trim());
              // donorAdapter.getFilter().filter(newText);
                return true;
            }
        });*/
        return super.onCreateOptionsMenu(menu);

    }

/*    private void searcData(String s) {
        pb.setVisibility(View.VISIBLE);
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("fullname")
                .startAt(s)
                .endAt(s + "\uf8ff");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                donorModelsList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        FetchMatchInformation(snapshot.getKey(),bgroup2,city);

                    }
                }else{
                    Toast.makeText(FindDonorActivity.this, "No Match found",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }*/


}