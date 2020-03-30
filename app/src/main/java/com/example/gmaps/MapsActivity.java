package com.example.gmaps;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;
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
    private int i,j;
    double latarray[],longarray[];    //declaring array
    private long totalusers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        latarray = new double[20];
        longarray=new double[20];
        firebaseAuth= FirebaseAuth.getInstance();
        i=0;
        j=0;
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


       for(i=0;i<totalusers;i++){
        mMap.addMarker(new MarkerOptions().position( new LatLng(latarray[i], longarray[i])));
        mMap.moveCamera(CameraUpdateFactory.newLatLng( new LatLng(latarray[i], longarray[i])));

    }}
    public void receive(){
        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        DatabaseReference myref=firebaseDatabase.getReference();
        myref.addValueEventListener(new ValueEventListener() {
            private static final String TAG = "Mapsactivity";

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    totalusers=dataSnapshot.getChildrenCount();
                    for(DataSnapshot snapshot1:snapshot.getChildren()){
                        if(snapshot1.getKey().equals("lat")){

                          latarray[i]= Double.parseDouble(snapshot1.getValue().toString());

                        Log.d(TAG,"lat is "+ latarray[i]);

                            i=i+1;
                }
                        if(snapshot1.getKey().equals("long")){

                            longarray[j]= Double.parseDouble(snapshot1.getValue().toString());

                            Log.d(TAG,"lat is "+ longarray[j]);

                            j=j+1;
                        }


                    }}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
