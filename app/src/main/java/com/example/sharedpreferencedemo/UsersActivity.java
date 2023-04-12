package com.example.sharedpreferencedemo;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.sharedpreferencedemo.adapters.UserAdapter;
import com.example.sharedpreferencedemo.listener.UserListener;
import com.example.sharedpreferencedemo.models.User;
import com.example.sharedpreferencedemo.utilities.Constants;
import com.example.sharedpreferencedemo.utilities.PerferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends BaseActivity implements UserListener {
    PerferenceManager perferenceManager;
    RecyclerView recyclerView;
    ProgressBar pbr;
    ImageView back;
    TextView error;
    Context ctx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        pbr = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.userRecyclerView);
        error = findViewById(R.id.textErrorMessage);
        back = findViewById(R.id.imageBack);
        back.setOnClickListener(v -> {onBackPressed();});
        perferenceManager = new PerferenceManager(this);
        getUsers();
    }

    public void getUsers(){
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS).get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    String currentUserId = perferenceManager.getString(Constants.KEY_USER_ID) ;
                    if(task.isSuccessful() && task.getResult()!=null){
                        List<User> users = new ArrayList<>();
                        for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                            if(currentUserId.equals(queryDocumentSnapshot.getId())){
                                continue;
                            }else{
                                User user = new User();
                                user.name = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                                user.email = queryDocumentSnapshot.getString(Constants.KEY_EMAIL);
                                user.image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                                user.token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                                user.id = queryDocumentSnapshot.getId();
                                users.add(user);
                            }
                        }
                        if(users.size()>0){
                            UserAdapter userAdapter = new UserAdapter(UsersActivity.this,users,this);
                            recyclerView.setAdapter(userAdapter);
                            recyclerView.setVisibility(View.VISIBLE);
                        }else {
                            showErrorMessage();
                        }
                    }else {
                        showErrorMessage();
                    }
                });
    }

    public void showErrorMessage(){
        error.setText(String.format("%s","No user found"));
        error.setVisibility(View.VISIBLE);
    }

    public void loading(Boolean isloading){
        if(isloading){
            pbr.setVisibility(View.VISIBLE);
        }else{
            pbr.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onUserClicked(User user) {
        Intent intent = new Intent(UsersActivity.this,ChatActivity.class);
        intent.putExtra(Constants.KEY_USER,user);
        startActivity(intent);
        finish();
    }
}