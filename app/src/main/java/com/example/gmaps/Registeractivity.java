package com.example.gmaps;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Objects;

public class  Registeractivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    EditText useremail,userpassword,userconfirmpassword,username,userage;
    Button register,chooseimage;
    TextView uslogin;
    double longitude,latitude;
    Uri mImageUri;
    ImageView mImageView;
    private FirebaseAuth firebaseAuth;
    String password,confpassword,name,emailid,age;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private Instant Picasso;
    private StorageReference mStorageRef;
    private FirebaseDatabase firebaseDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registeractivity);
        chooseimage=(Button)findViewById(R.id.chooseimage);
        mImageView=(ImageView)findViewById(R.id.imageview);
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        SetUpViews();
        requestlocationupdates();
        callpermissions();

        firebaseAuth=FirebaseAuth.getInstance();
        register=(Button)findViewById(R.id.btregister);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    String email=useremail.getText().toString().trim();
                    String password=userpassword.getText().toString().trim();
                    firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                // sendverificationmail();
                                senduserdata();
                                uploadFile();

                               // Toast.makeText(Registeractivity.this,"Successfully send, mail send",Toast.LENGTH_SHORT).show();
                                  firebaseAuth.signOut();
                                finish();
                                startActivity(new Intent(Registeractivity.this,loginactivity.class));
                                firebaseAuth.signOut();
                            }
                            else{
                                Toast.makeText(Registeractivity.this,"Not successfull",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });
        uslogin=findViewById(R.id.login);
        uslogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Registeractivity.this,MapsActivity.class);
                startActivity(intent);
            }
        });
        chooseimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Openfilechooser();
            }
        });

    }

    /**
     * Dispatch incoming result to the correct fragment.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
           mImageUri = data.getData();
            mImageView.setImageURI(mImageUri);


        }
    }

    private void Openfilechooser(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }
    public void SetUpViews(){
        useremail=(EditText)findViewById(R.id.etemail);
        userpassword=(EditText)findViewById(R.id.Password);
        username=(EditText)findViewById(R.id.Name);
        userage=(EditText)findViewById(R.id.age);
        userconfirmpassword=(EditText)findViewById(R.id.confPassword);
    }
    private Boolean validate(){
        Boolean result=false;
        emailid=useremail.getText().toString().trim();
        password=userpassword.getText().toString().trim();
        confpassword=userconfirmpassword.getText().toString().trim();
        name=username.getText().toString().trim();
        age=userage.getText().toString().trim();
        if(emailid.isEmpty()){
            useremail.setError("Mail Id required");
            useremail.requestFocus();
        }
        else if(name.isEmpty()){
            username.setError("Username is needed");
            username.requestFocus();
        }
        else if(password.isEmpty()){
            userpassword.setError("Password required");
            userpassword.requestFocus();
        }
        else if(emailid.isEmpty()|| password.isEmpty() || confpassword.isEmpty() || name.isEmpty()){
            Toast.makeText(Registeractivity.this,"Registration failed,please enter all the details",Toast.LENGTH_SHORT).show();
        }
        else{

            result=true;

        }
        return result;
    }
    private void sendverificationmail(){
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
        if(firebaseUser!=null){
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        senduserdata();
                        Toast.makeText(Registeractivity.this,"Successfully send, mail send",Toast.LENGTH_SHORT).show();
                        //  firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(Registeractivity.this,MapsActivity.class));
                    }
                }
            });
        }
    }

    public void requestlocationupdates() {
        fusedLocationProviderClient = new FusedLocationProviderClient(this);
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(2000);
        locationRequest.setInterval(4000);
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
             latitude=locationResult.getLastLocation().getLatitude();
             longitude=locationResult.getLastLocation().getLongitude();
                Log.e("Mainactivity", "lat" + locationResult.getLastLocation().getLatitude());
                Log.e("Mainactivity", "Long" + locationResult.getLastLocation().getLongitude());
            }
        }, getMainLooper());
    }

    public void callpermissions() {
        String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        Permissions.check(this/*context*/, permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
            @Override
            public void onGranted() {
                requestlocationupdates();
                // do your task.
            }

            /**
             * This method will be called if some of the requested permissions have been denied.
             *
             * @param context           The application context.
             * @param deniedPermissions The list of permissions which have been denied.
             */
            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                super.onDenied(context, deniedPermissions);
                callpermissions();
            }

        });


    }

    private void senduserdata(){


       firebaseDatabase=FirebaseDatabase.getInstance();
        DatabaseReference myref=firebaseDatabase.getReference((firebaseAuth.getUid())).child("Name");
        myref.setValue(name);
        DatabaseReference myref1=firebaseDatabase.getReference((firebaseAuth.getUid())).child("Age");
        myref1.setValue(age);
        DatabaseReference myref2=firebaseDatabase.getReference((firebaseAuth.getUid())).child("lat");
        myref2.setValue(latitude);
        DatabaseReference myref3=firebaseDatabase.getReference((firebaseAuth.getUid())).child("lat");
        myref3.setValue(latitude);
        DatabaseReference myref4=firebaseDatabase.getReference((firebaseAuth.getUid())).child("long");
        myref4.setValue(longitude);

    }
    private void uploadFile(){
        if(mImageUri!=null){
            StorageReference fileReference = mStorageRef.child(firebaseAuth.getUid()
                    + "." + getFileExtension(mImageUri));

           String url= mStorageRef.child(firebaseAuth.getUid()
                    + "." + getFileExtension(mImageUri)).getDownloadUrl().toString();
            DatabaseReference myref5=firebaseDatabase.getReference((firebaseAuth.getUid())).child("url");
            myref5.setValue(url);
          fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
              @Override
              public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Toast.makeText(Registeractivity.this,"Succes",Toast.LENGTH_SHORT).show();

              }
          }).addOnFailureListener(new OnFailureListener() {
              @Override
              public void onFailure(@NonNull Exception e) {
                  Toast.makeText(Registeractivity.this,"error",Toast.LENGTH_SHORT).show();
              }
          });
        }
    }
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}
