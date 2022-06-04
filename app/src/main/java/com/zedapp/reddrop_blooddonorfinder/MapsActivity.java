package com.zedapp.reddrop_blooddonorfinder;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;



import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener,
        SeekBar.OnSeekBarChangeListener,
        GoogleMap.OnInfoWindowLongClickListener,
        GoogleMap.OnInfoWindowCloseListener{

    TextView textView;
    ProgressDialog progressDialog;
    String bgroup="",city="";

    int COUNT = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapsActivity.this);
        bgroup = getIntent().getStringExtra("bgroup");
        city = getIntent().getStringExtra("city");
        textView = findViewById(R.id.info);
        getSupportActionBar().setTitle(city+" - "+bgroup);
        if(!bgroup.equals("")&& !city.equals("")){
            readFilterdata(bgroup,city);
            textView.setText(COUNT+" Donor Found at "+city);

        }else {
            Toast.makeText(this,"Something Went Wrong!",Toast.LENGTH_LONG).show();
        }





    }
    private void readFilterdata(String bgroup, String locationn) {
        progressDialog = new ProgressDialog(MapsActivity.this);
        progressDialog.setMessage("Loading..."); // Setting Message
        progressDialog.setTitle("ProgressDialog"); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.show(); // Display Progress Dialog
        progressDialog.setCancelable(false);
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        FetchMatchInformation(snapshot.getKey(),bgroup,locationn);
                        progressDialog.dismiss();
                    }

                }else{
                    progressDialog.dismiss();
                    Toast.makeText(MapsActivity.this, "No Match found",Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void FetchMatchInformation(String key,String bgroup, String locationn) {

        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(key);
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    String donor,city,address,cnumber,bloodgroup,lastdonate,latitude,longitude;
                    double loclatitude,loclongitude;
                    LatLng cod;
                    bloodgroup ="";
                    donor ="";
                    cnumber ="";
                    lastdonate ="";
                    city ="";
                    address ="";

                    if(snapshot.child("BloodGroup").getValue()!=null) { bloodgroup = snapshot.child("BloodGroup").getValue().toString(); }
                    if(snapshot.child("city").getValue()!=null) { city = snapshot.child("city").getValue().toString(); }
                    if(snapshot.child("fullname").getValue()!=null) { donor = snapshot.child("fullname").getValue().toString(); }
                    if(snapshot.child("contactNO").getValue()!=null) { cnumber = snapshot.child("contactNO").getValue().toString(); }
                    if(snapshot.child("address").getValue()!=null) { address = snapshot.child("address").getValue().toString(); }
                    if(snapshot.child("lastdonate").getValue()!=null) { lastdonate = snapshot.child("lastdonate").getValue().toString(); }
                    if(snapshot.child("latitude").getValue()!=null && snapshot.child("longitude").getValue()!=null)
                    {
                        latitude = snapshot.child("latitude").getValue().toString();
                        longitude =  snapshot.child("longitude").getValue().toString();
                        loclatitude = Double.parseDouble(latitude);
                        loclongitude = Double.parseDouble(longitude);
                        cod = new LatLng(loclatitude, loclongitude);

                        if (bgroup.equals(bloodgroup) && locationn.equals(city)){
                            COUNT++;

                            mMap.addMarker(new MarkerOptions()
                                    .position(cod)
                                    .title(donor)
                                    .snippet("Blood: "+bloodgroup+
                                            "\nAddress :"+address+
                                            "\nContact :"+cnumber+
                                            "\nL/D :"+lastdonate
                                            )
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.blood)));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(cod));
                            textView.setText(COUNT+" Donor Found at "+city);

                        }
                        else if (bgroup.equals("All") && locationn.equals(city)){
                            COUNT++;
                            mMap.addMarker(new MarkerOptions()
                                    .position(cod)
                                    .title(donor)
                                    .snippet("Blood: "+bloodgroup+
                                            "\nAddress :"+address+
                                            "\nContact :"+cnumber+
                                            "\nL/D :"+lastdonate

                                    )
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.blood)));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(cod));
                            textView.setText(COUNT+" Donor Found at "+city);

                        }
                        else if (bgroup.equals(bloodgroup) && locationn.equals("All")){
                            COUNT++;
                            mMap.addMarker(new MarkerOptions()
                                    .position(cod)
                                    .title(donor)
                                    .snippet("Blood: "+bloodgroup+
                                            "\nAddress :"+address+
                                            "\nContact :"+cnumber+
                                            "\nL/D :"+lastdonate

                                    )
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.blood)));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(cod));
                            textView.setText(COUNT+" Donor Found at "+city);

                        }
                        else if (bgroup.equals("All") && locationn.equals("All")){
                            COUNT++;
                            mMap.addMarker(new MarkerOptions()
                                    .position(cod)
                                    .title(donor)
                                    .snippet("Blood: "+bloodgroup+
                                            "\nAddress :"+address+
                                            "\nContact :"+cnumber+
                                            "\nL/D :"+lastdonate

                                    )
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.blood)));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(cod));
                            textView.setText(COUNT+" Donor Found at "+city);

                        }


                      }
                    }

                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }

        });

    }


    GoogleMap mMap;
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {


        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnInfoWindowCloseListener(this);
        mMap.setOnInfoWindowLongClickListener(this);
        mMap.setMinZoomPreference(10.5F);
        mMap.setContentDescription("Map with lots of markers.");

        SharedPreferences sh = getSharedPreferences("MY_LOCATION", MODE_PRIVATE);
        String latitude = sh.getString("latitude", "0.0");
        String longitude = sh.getString("longitude", "0.0");
        double loclatitude = Double.parseDouble(latitude);
        double loclongitude = Double.parseDouble(longitude);
        LatLng cod = new LatLng(loclatitude, loclongitude);
        mMap.addMarker(new MarkerOptions()
                .position(cod)
                .title("My Location")
                .snippet("")
        );
        mMap.moveCamera(CameraUpdateFactory.newLatLng(cod));






    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!checkReady()) {
            return;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // Do nothing.
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // Do nothing.
    }


    @Override
    public void onInfoWindowClick(Marker marker) {

       // Toast.makeText(this, "Click Info Window", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInfoWindowClose(Marker marker) {
       // Toast.makeText(this, "Close Info Window", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInfoWindowLongClick(Marker marker) {
      //  Toast.makeText(this, "Info Window long click", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

        // Markers have a z-index that is settable and gettable.
        float zIndex = marker.getZIndex() + 1.0f;
        marker.setZIndex(zIndex);
//        Toast.makeText(this, marker.getTitle() + " z-index set to " + zIndex,
//                Toast.LENGTH_SHORT).show();

        // We return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }





    private boolean checkReady() {
        if (mMap == null) {
            Toast.makeText(this, "Map Not Ready", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /** Called when the Clear button is clicked. */
    public void onClearMap(View view) {
        if (!checkReady()) {
            return;
        }
        mMap.clear();
    }






    class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        // These are both viewgroups containing an ImageView with id "badge" and two TextViews with id
        // "title" and "snippet".
        private final View mWindow;

        private final View mContents;

        CustomInfoWindowAdapter() {
            mWindow = getLayoutInflater().inflate(R.layout.custom_info_window, null);
            mContents = getLayoutInflater().inflate(R.layout.custom_info_contents, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            render(marker, mWindow);
            return mWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {
            render(marker, mContents);
            return mContents;
        }

        private void render(Marker marker, View view) {
            int badge =0;

            ((ImageView) view.findViewById(R.id.badge)).setImageResource(badge);

            String title = marker.getTitle();
            TextView titleUi = ((TextView) view.findViewById(R.id.title));
            if (title != null) {
                // Spannable string allows us to edit the formatting of the text.
                SpannableString titleText = new SpannableString(title);
                titleUi.setText(titleText);
            } else {
                titleUi.setText("");
            }

            String snippet = marker.getSnippet();
            TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
            if (snippet != null) {
                SpannableString snippetText = new SpannableString(snippet);
                snippetUi.setText(snippetText);
            } else {
                snippetUi.setText("");
            }
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