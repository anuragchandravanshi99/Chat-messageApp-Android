package com.example.sharedpreferencedemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sharedpreferencedemo.utilities.Constants;
import com.example.sharedpreferencedemo.utilities.PerferenceManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignInActivity extends AppCompatActivity {
    private PerferenceManager preferenceManager;
    EditText uPassword,uEmail;
    ProgressBar process;
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        TextView tv = findViewById(R.id.textSignUp);
        uEmail = findViewById(R.id.inputEmail);
        preferenceManager = new PerferenceManager(SignInActivity.this);
        uPassword = findViewById(R.id.inputPassword);
        process = findViewById(R.id.progressBar);
        btn = findViewById(R.id.buttonSignIn);

        if(preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)){
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this,SignUpActivity.class));
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValidSignInDetail()){
                    signIn();
                }
            }
        });


    }
    public void signIn(){
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL,uEmail.getText().toString().trim())
                .whereEqualTo(Constants.KEY_PASSWORD,uPassword.getText().toString().trim())
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()&&task.getResult()!=null && task.getResult().getDocuments().size()>0){
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN,true);
                        preferenceManager.putString(Constants.KEY_USER_ID,documentSnapshot.getId());
                        preferenceManager.putString(Constants.KEY_NAME,documentSnapshot.getString(Constants.KEY_NAME));
                        preferenceManager.putString(Constants.KEY_IMAGE,documentSnapshot.getString(Constants.KEY_IMAGE));
                        Intent intent = new Intent(this,MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finishAffinity();
                    }else{
                        loading(false);
                        showToast("Unable to Login");
                    }
                });
    }
    public void loading(Boolean isLoading){
        if(isLoading){
            btn.setVisibility(View.INVISIBLE);
            process.setVisibility(View.VISIBLE);
        }else{
            process.setVisibility(View.INVISIBLE);
            btn.setVisibility(View.VISIBLE);
        }
    }
    public void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }

    private boolean isValidSignInDetail(){
        if(uEmail.getText().toString().trim().isEmpty()) {
            showToast("Enter Email");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(uEmail.getText().toString().trim()).matches()) {
            showToast("Enter Valid Email");
            return false;
        }else if(uPassword.getText().toString().isEmpty()){
            showToast("Enter Password");
            return false;
        }else{
            return true;
        }
    }
}