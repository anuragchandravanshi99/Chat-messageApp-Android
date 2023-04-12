package com.example.sharedpreferencedemo;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sharedpreferencedemo.utilities.Constants;
import com.example.sharedpreferencedemo.utilities.PerferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {
    PerferenceManager perferenceManager;
    String encodedImage;
    EditText name,mail,password,confirmPassword;
    ProgressBar pbr;
    CardView cv;
    TextView txt;
    Button btn;
    ImageView iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        TextView tv = findViewById(R.id.textSignUp);
        name = findViewById(R.id.inputName);
        perferenceManager = new PerferenceManager(getApplicationContext());
        mail = findViewById(R.id.inputEmail);
        btn = findViewById(R.id.buttonSignUp);
        cv= findViewById(R.id.layout);
        iv = findViewById(R.id.userImage);
        txt = findViewById(R.id.addImageTxt);
        password = findViewById(R.id.inputPassword);
        pbr =findViewById(R.id.loading);
        confirmPassword = findViewById(R.id.inputConfirmPassword);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValidSignUp()){
                    signUp();
                }
            }
        });
        cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickImage.launch(intent);
            }
        });
    }
    public void signUp(){
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String,Object> user = new HashMap<>();
        user.put(Constants.KEY_NAME,name.getText().toString());
        user.put(Constants.KEY_EMAIL,mail.getText().toString());
        user.put(Constants.KEY_PASSWORD,password.getText().toString());
        user.put(Constants.KEY_IMAGE,encodedImage);
        database.collection(Constants.KEY_COLLECTION_USERS).add(user).addOnSuccessListener(documentReference ->{
                loading(false);
                perferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN,true);
                perferenceManager.putString(Constants.KEY_USER_ID,documentReference.getId());
                perferenceManager.putString(Constants.KEY_NAME,name.getText().toString());
                perferenceManager.putString(Constants.KEY_IMAGE,encodedImage);
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }).addOnFailureListener(e -> {
                loading(false);
                showToast(e.getMessage());
        });

    }
    private String encodeImage(Bitmap bitmap){
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight()*previewWidth/bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap,previewWidth,previewHeight,false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes,Base64.DEFAULT);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == RESULT_OK){
                    if(result.getData()!= null){
                        Uri imageUri = result.getData().getData();
                        try{
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            iv.setImageBitmap(bitmap);
                            txt.setVisibility(View.INVISIBLE);
                            encodedImage = encodeImage(bitmap);
                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    public void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }
    public boolean isValidSignUp(){
        if(encodedImage==null){
            showToast("Select Profile Image");
            return false;
        }else if(name.getText().toString().trim().isEmpty()){
            showToast("Enter name");
            return false;
        }else if(mail.getText().toString().trim().isEmpty()){
            showToast("Enter EmailId");
            return false;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(mail.getText().toString().trim()).matches()){
            showToast("Enter valid email");
            return false;
        }else if(password.getText().toString().trim().isEmpty()){
            showToast("Enter password");
            return false;
        }else if(confirmPassword.getText().toString().trim().isEmpty()){
            showToast("Enter confirm password");
            return false;
        }else if(!password.getText().toString().equals(confirmPassword.getText().toString())){
            showToast("Password & Confirm Password not match");
            return false;
        }else{
            return true;
        }
    }
    public void loading(Boolean load){
        if(load){
            btn.setVisibility(View.INVISIBLE);
            pbr.setVisibility(View.VISIBLE);
        }else{
            pbr.setVisibility(View.INVISIBLE);
            btn.setVisibility(View.VISIBLE);
        }
    }
}