package com.zedapp.reddrop_blooddonorfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zedapp.reddrop_blooddonorfinder.adapter.CustomAdapter;
import com.zedapp.reddrop_blooddonorfinder.models.CustomModels;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CustomAdapter customAdapter;
    private List<CustomModels> customModelsList;
    private ProgressBar pb;
    String[] districts;
    Dialog dialog;
    TextView locationtv, check;
    String dataType;
    int icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_list);

        dataType = getIntent().getStringExtra("dateType");
        getSupportActionBar().setTitle( dataType);

        if (dataType.equals("Blood Bank")){
            icon = R.drawable.blood_bank;
        }else if(dataType.equals("Ambulance")){
            icon = R.drawable.ambulance;
        }
        else if(dataType.equals("Hospitals")){
            icon = R.drawable.hospital;
        }
        else if(dataType.equals("Foundation")){
            icon = R.drawable.find_doner;
        }


        check = findViewById(R.id.check);
        locationtv = findViewById(R.id.search_location);
        districts = getResources().getStringArray(R.array.bd_districts);
        pb = findViewById(R.id.progress_circular);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ListActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        customModelsList = new ArrayList<>();
        customAdapter = new CustomAdapter(ListActivity.this, customModelsList);
        recyclerView.setAdapter(customAdapter);
    }
    private void ReadData(String filter) {

        customModelsList.clear();
        customAdapter.notifyDataSetChanged();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(dataType);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    String name, city, address, hotline, phonenumber, location, key;

                    name = "";
                    phonenumber = "";
                    hotline = "";
                    location = "";
                    city = "";
                    address = "";
                    key = "";

                    if (snapshot.child("name").getValue() != null) {
                        name = snapshot.child("name").getValue().toString();
                    }
                    if (snapshot.child("phonenumber").getValue() != null) {
                        phonenumber = snapshot.child("phonenumber").getValue().toString();
                    }
                    if (snapshot.child("hotline").getValue() != null) {
                        hotline = snapshot.child("hotline").getValue().toString();
                    }
                    if (snapshot.child("location").getValue() != null) {
                        location = snapshot.child("location").getValue().toString();
                    }
                    if (snapshot.child("city").getValue() != null) {
                        city = snapshot.child("city").getValue().toString();
                    }
                    if (snapshot.child("address").getValue() != null) {
                        address = snapshot.child("address").getValue().toString();
                    }
                    if (snapshot.child("key").getValue() != null) {
                        key = snapshot.child("key").getValue().toString();
                    }


                    CustomModels obj = new CustomModels(icon, name, city, address, hotline, phonenumber, location, key,dataType);
                    if (filter.equals("All")) {
                        customModelsList.add(obj);
                        customAdapter.notifyDataSetChanged();
                    } else if (city.equals(filter)) {
                        customModelsList.add(obj);
                        customAdapter.notifyDataSetChanged();

                    }

                    pb.setVisibility(View.GONE);
                }
                if (customModelsList.size() < 1) {
                    check.setVisibility(View.VISIBLE);
                    pb.setVisibility(View.GONE);
                } else {
                    check.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.location:
                dialog = new Dialog(ListActivity.this);
                dialog.setContentView(R.layout.dialogue_search_box);
                dialog.getWindow().setLayout(700, 1200);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
                dialog.show();

                EditText searcet = dialog.findViewById(R.id.search_text);
                ListView listViewd = dialog.findViewById(R.id.sarch_list);

                ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(ListActivity.this, R.layout.support_simple_spinner_dropdown_item, districts);
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
                        String filter = stringArrayAdapter.getItem(position);
                        ReadData(filter);
                        locationtv.setText("Search Location by " + filter);

                        dialog.dismiss();


                    }
                });

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_items2, menu);

        return super.onCreateOptionsMenu(menu);

    }



    @Override
    protected void onStart() {
        ReadData("All");
        super.onStart();
    }
}