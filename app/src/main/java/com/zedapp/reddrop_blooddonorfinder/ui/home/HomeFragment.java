package com.zedapp.reddrop_blooddonorfinder.ui.home;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.zedapp.reddrop_blooddonorfinder.FindDonorActivity;
import com.zedapp.reddrop_blooddonorfinder.HelpLineActivity;
import com.zedapp.reddrop_blooddonorfinder.ListActivity;
import com.zedapp.reddrop_blooddonorfinder.MapsActivity;
import com.zedapp.reddrop_blooddonorfinder.MenuList;
import com.zedapp.reddrop_blooddonorfinder.NewsFeedActivity;
import com.zedapp.reddrop_blooddonorfinder.profile.ProfileActivity;
import com.zedapp.reddrop_blooddonorfinder.R;
import com.zedapp.reddrop_blooddonorfinder.RequestActivity;
import com.zedapp.reddrop_blooddonorfinder.SettingActivity;
import com.zedapp.reddrop_blooddonorfinder.adapter.FeedAdapter;
import com.zedapp.reddrop_blooddonorfinder.databinding.FragmentHomeBinding;
import com.zedapp.reddrop_blooddonorfinder.models.FeedModels;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

   // private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    public ArrayList<MenuList> al = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    GridView gridView;
    TextView donorCount,requestCount;
    RecyclerView recyclerView;
    FeedAdapter adapter;
    List<FeedModels> list;

    TextView check;
    FirebaseAuth mAuth;

    String[] districts;
    Dialog dialog;
    String bgroup="All",city="All";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        districts = getResources().getStringArray(R.array.bd_districts);

        mAuth = FirebaseAuth.getInstance();
        donorCount = root.findViewById(R.id.donorCount);
        requestCount = root.findViewById(R.id.requestCount);
        swipeRefreshLayout = root.findViewById(R.id.swipe);
        swipeRefreshLayout.setRefreshing(false);
        gridView = root.findViewById(R.id.gridview);
        gridView.setAdapter(new myadapter());
        al.clear();
        al.add(new MenuList("Find Donor",R.drawable.find_doner));
        al.add(new MenuList("Nearby",R.drawable.nearby));
        al.add(new MenuList("Blood Bank",R.drawable.blood_bank));
        al.add(new MenuList("News Feed",R.drawable.news_feed));
        al.add(new MenuList("Ambulance",R.drawable.ambulance));
        al.add(new MenuList("Hospitals",R.drawable.hospital));
        al.add(new MenuList("Foundation",R.drawable.find_doner));
        al.add(new MenuList("Request",R.drawable.request));
        al.add(new MenuList("Help Line",R.drawable.help_line));
        al.add(new MenuList("Profile",R.drawable.profile_dark));
        al.add(new MenuList("Setting",R.drawable.setting));


        TotalDonor(donorCount);
        TotalRequest(requestCount);
        check = root.findViewById(R.id.check);

        recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        readFeedrdata();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                TotalDonor(donorCount);
                TotalRequest(requestCount);
                readFeedrdata();
            }
        });


        return root;
    }
    private void readFeedrdata() {
        swipeRefreshLayout.setRefreshing(true);
        list =new ArrayList<>();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("News Feed");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {



                    FeedModels obj = new FeedModels();

                    if(snapshot.child("userid").getValue()!=null) { obj.setUserid(snapshot.child("userid").getValue().toString()); }
                    else continue;
                    if(snapshot.child("nfKey").getValue()!=null) { obj.setNfKey(snapshot.child("nfKey").getValue().toString()); }
                    else continue;
                    if(snapshot.child("image").getValue()!=null) { obj.setImage(snapshot.child("image").getValue().toString()); }
                    if(snapshot.child("desc").getValue()!=null) { obj.setDesc(snapshot.child("desc").getValue().toString()); }
                    if(snapshot.child("date").getValue()!=null) { obj.setDate(snapshot.child("date").getValue().toString()); }
                    list.add(obj);

                }
                swipeRefreshLayout.setRefreshing(false);
                adapter =new FeedAdapter(getContext(),list);
                recyclerView.setAdapter(adapter);
                if (list.isEmpty()){
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
    private void TotalRequest(TextView requestCount) {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("BloodRequest");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                requestCount.setText(dataSnapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void TotalDonor(TextView donorCount) {

            DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Users");
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    donorCount.setText(dataSnapshot.getChildrenCount()+"");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    class myadapter extends BaseAdapter
    {


        @Override
        public int getCount() {
            return al.size();
        }

        @Override
        public Object getItem(int position) {
            return al.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.menuitem,parent,false);
            final  MenuList ml = al.get(position);

            ImageView imageView;
            TextView textView;
            LinearLayout layout;
            layout = convertView.findViewById(R.id.menuly);
            imageView = convertView.findViewById(R.id.menuIcon);
            textView = convertView.findViewById(R.id.menuName);
            Picasso.get().load(al.get(position).getMenuImg()).into(imageView);
            textView.setText(al.get(position).getMenuName());
            layout.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onClick(View v) {
                    if(textView.getText().equals("Find Donor")){
                        startActivity(new Intent(getContext(), FindDonorActivity.class));
                    }
                    else if (textView.getText().equals("Blood Bank")){
                        Intent i = new Intent(getContext(), ListActivity.class);
                        i.putExtra("dateType", "Blood Bank");
                        startActivity(i);
                    }
                    else if (textView.getText().equals("News Feed")){
                        startActivity(new Intent(getContext(), NewsFeedActivity.class));
                    }
                    else if (textView.getText().equals("Ambulance")){
                        Intent i = new Intent(getContext(), ListActivity.class);
                        i.putExtra("dateType", "Ambulance");
                        startActivity(i);
                    }
                    else if (textView.getText().equals("Hospitals")){
                        Intent i = new Intent(getContext(), ListActivity.class);
                        i.putExtra("dateType", "Hospitals");
                        startActivity(i);
                    }
                    else if (textView.getText().equals("Foundation")){
                        Intent i = new Intent(getContext(), ListActivity.class);
                        i.putExtra("dateType", "Foundation");
                        startActivity(i);
                    }
                    else if (textView.getText().equals("Request")){
                        startActivity(new Intent(getContext(), RequestActivity.class));
                    }
                    else if (textView.getText().equals("Help Line")){
                        startActivity(new Intent(getContext(), HelpLineActivity.class));
                    }
                    else if (textView.getText().equals("Profile")){
                        Intent i = new Intent(getContext(), ProfileActivity.class);
                        i.putExtra("userId",mAuth.getCurrentUser().getUid().toString());
                        i.putExtra("userName", "My Profile");
                        startActivity(i);
                    }
                    else if (textView.getText().equals("Setting")){
                        startActivity(new Intent(getContext(), SettingActivity.class));
                    }  else if (textView.getText().equals("Nearby")){

                        dialog =  new Dialog(getContext());
                        dialog.setContentView(R.layout.filter_box2);
                        dialog.getWindow().setTitle("Select District and Blood Group");
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
                        bloodgrouprd.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @SuppressLint("ResourceType")
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                RadioButton bgrp = (RadioButton) group.findViewById(checkedId);
                                if (null != bgrp && checkedId > -1) {

                                    bgroup = bgrp.getText().toString();
                                    result.setText("Blood Group: "+bgroup+"\nLocation : "+city);
                                }

                            }
                        });

                        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(getContext(),R.layout.support_simple_spinner_dropdown_item,districts);
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
                                city = stringArrayAdapter.getItem(position);
                                searcet.setText(stringArrayAdapter.getItem(position));
                                result.setText("Blood Group: "+bgroup+"\nLocation : "+city);



                            }
                        });

                        filter.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(getContext(), MapsActivity.class);
                                i.putExtra("bgroup",bgroup);
                                i.putExtra("city", city);
                                startActivity(i);
                                dialog.dismiss();
                                bgroup="All";
                                city="All";

                            }
                        });

                    }

                }
            });






            return convertView;
        }
    }
}