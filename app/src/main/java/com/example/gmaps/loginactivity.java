package com.example.gmaps;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class loginactivity extends AppCompatActivity {
    private EditText email, password;
    private Button login;
    private TextView register;
    private FirebaseAuth firebaseAuth;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private Boolean result;
    private StorageReference storageReference,ref;
    private FirebaseStorage firebaseStorage;
    private Button resetpassword;
    private int counter = 5;
    private ImageView imageview;
    StorageReference fileReference;
    StorageReference mStorageRef;
     FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginactivity);
        email = (EditText) findViewById(R.id.etname);
        password = (EditText) findViewById(R.id.etepassword);
        firebaseAuth = FirebaseAuth.getInstance();

        final FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            finish();
            startActivity(new Intent(loginactivity.this, MapsActivity.class));
        }

        login = (Button) findViewById(R.id.btlogin);
        register = (TextView) findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(loginactivity.this, Registeractivity.class);
                startActivity(intent);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mailid = email.getText().toString().trim();
                String userpass = password.getText().toString().trim();
                if (mailid.isEmpty() || userpass.isEmpty()) {
                    Toast.makeText(loginactivity.this, "Please enter details", Toast.LENGTH_SHORT).show();
                } else {
                    validate(mailid, userpass);
                }
            }

        });
    }


    private void validate(final String userName, final String userPassword) {
        firebaseAuth.signInWithEmailAndPassword(userName, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    startActivity(new Intent(loginactivity.this, MapsActivity.class));
                    //startActivity(new Intent(MainActivity.this,SecondActivity.class));
                    //  checkemailverification();
                } else {
                    Toast.makeText(loginactivity.this, "Login failed" + userName + userPassword, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkemailverification() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        Boolean emailflag = firebaseUser.isEmailVerified();
        if (emailflag) {
            startActivity(new Intent(loginactivity.this, MapsActivity.class));
        } else {
            Toast.makeText(loginactivity.this, "Verify your mail", Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
        }
    }
            private void update(){
                fileReference = mStorageRef.child(firebaseAuth.getUid()
                        +"."+ "jpg");
                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        DatabaseReference myref5=firebaseDatabase.getReference((firebaseAuth.getUid())).child("url");
                        myref5.setValue(uri.toString());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
    }




