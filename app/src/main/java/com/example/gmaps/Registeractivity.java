package com.example.gmaps;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class  Registeractivity extends AppCompatActivity {
    EditText useremail,userpassword,userconfirmpassword,username,userage;
    Button register;
    TextView uslogin;
    private FirebaseAuth firebaseAuth;
    String password,confpassword,name,emailid,age;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registeractivity);
        SetUpViews();
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
                                //senduserdata();
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
    private void senduserdata(){
        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        DatabaseReference myref=firebaseDatabase.getReference(firebaseAuth.getUid());
     //   UserProfile userProfile= new UserProfile(name,age);
      //  myref.setValue(userProfile);

    }
}
