package com.zedapp.reddrop_blooddonorfinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.zedapp.reddrop_blooddonorfinder.adapter.HelpLineAdapter;

import com.zedapp.reddrop_blooddonorfinder.models.HelpLineModels;

import java.util.ArrayList;
import java.util.List;

public class HelpLineActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    HelpLineAdapter adapter;
    List<HelpLineModels> helpLineModelsList;
    private ProgressBar pb;
    TextView check;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_help_line);

        check=findViewById(R.id.check);

        pb = findViewById(R.id.progress_circular);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(HelpLineActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        helpLineModelsList =new ArrayList<>();
        adapter =new HelpLineAdapter(HelpLineActivity.this,helpLineModelsList);
        recyclerView.setAdapter(adapter);

        helpLineModelsList.add(new HelpLineModels(R.drawable.hl333,"Information Services -তথ্য সেবা ( 333 )","333"));
        helpLineModelsList.add(new HelpLineModels(R.drawable.hl106,"ACC hotline -দুদক হটলাইন ( 106 )","106"));
        helpLineModelsList.add(new HelpLineModels(R.drawable.help_line,"Disaster warning -দুর্যোগের আগাম বার্তা ( 10941 )","10941"));
        helpLineModelsList.add(new HelpLineModels(R.drawable.help_line,"Government Legal Services -সরকারী আইন সেবা ( 16430 )","16430"));
        helpLineModelsList.add(new HelpLineModels(R.drawable.hl105,"National Identity Card ( 105 )","105"));
        helpLineModelsList.add(new HelpLineModels(R.drawable.hl109,"Violence against women and children -নারী ও শিশু নির্যাতন ( 10921 )","10921"));
        helpLineModelsList.add(new HelpLineModels(R.drawable.hl109,"Violence against women and children -নারী ও শিশু নির্যাতন ( 109 )","109"));
        helpLineModelsList.add(new HelpLineModels(R.drawable.hl1098,"Child support -শিশু সহায়তা ( 1098 )","1098"));
        helpLineModelsList.add(new HelpLineModels(R.drawable.hk16163,"Fire Service -ফায়ার সার্ভিসে ( 16163 )","16163"));
        helpLineModelsList.add(new HelpLineModels(R.drawable.hl999,"Emergency services -জরুরী সেবা ( 999 )","999"));
        helpLineModelsList.add(new HelpLineModels(R.drawable.help_line,"Red Drop Blood Donor Help","1796004569"));
       adapter.notifyDataSetChanged();


        if (helpLineModelsList.size()==0){
            check.setVisibility(View.VISIBLE);
            pb.setVisibility(View.GONE);
            Toast.makeText(HelpLineActivity.this,"No Data Found",Toast.LENGTH_LONG).show();
        }
        else {
            pb.setVisibility(View.GONE);
            check.setVisibility(View.GONE);
        }


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
                default:
                return super.onOptionsItemSelected(item);
        }

    }

}