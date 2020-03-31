package com.example.gmaps;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FirebaseAuth firebaseAuth;
    private int i,j,a;
    private static final String TAG = "Mapsactivity";
    double latarray[],longarray[];
    String name[];
    //declaring array
    private long totalusers;
    private Button bt1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        firebaseAuth= FirebaseAuth.getInstance();
        bt1=(Button)findViewById(R.id.logout);
        logout();
        i=0;
        j=0;
        a=0;
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        receive();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera

       }
    public void receive(){
        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        DatabaseReference myref=firebaseDatabase.getReference();
        myref.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    totalusers=snapshot.getChildrenCount();
                    latarray = new double[(int) totalusers];
                    longarray=new double[(int) totalusers];
                    name=new String[(int) totalusers];
                    for(DataSnapshot snapshot1:snapshot.getChildren()){
                        if(snapshot1.getKey().equals("lat")){

                          latarray[i]= Double.parseDouble(snapshot1.getValue().toString());



                            i=i+1;
                }
                        if(snapshot1.getKey().equals("long")){

                            longarray[j]= Double.parseDouble(snapshot1.getValue().toString());



                            j=j+1;
                        }
                        if(snapshot1.getKey().equals("Name")){

                            name[a]= snapshot1.getValue().toString();



                            a=a+1;
                        }
                        for(int k=0;k<totalusers;k++){
                        LatLng user = new LatLng(latarray[0], longarray[0]);
                        mMap.addMarker(new MarkerOptions().position(user).title(name[k]));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(user));

                    }}}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void logout(){
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(MapsActivity.this,loginactivity.class));
            }
        });
    }
}
