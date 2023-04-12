package com.example.sharedpreferencedemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sharedpreferencedemo.adapters.RecentConversationAdapter;
import com.example.sharedpreferencedemo.listener.ConversationListner;
import com.example.sharedpreferencedemo.models.ChatMessage;
import com.example.sharedpreferencedemo.models.User;
import com.example.sharedpreferencedemo.utilities.Constants;
import com.example.sharedpreferencedemo.utilities.PerferenceManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends BaseActivity implements ConversationListner {

    public PerferenceManager perferenceManager;
    private List<ChatMessage> conversation;
    private RecentConversationAdapter recentConversationAdapter;
    private FirebaseFirestore database;
    TextView name;
    FloatingActionButton btn1;
    RecyclerView rv;
    ProgressBar load;
    ImageView image,signout_iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        signout_iv = findViewById(R.id.imageSignOut);
        name =findViewById(R.id.textName);
        rv = findViewById(R.id.conversationRecyclerView);
        load =findViewById(R.id.progressBar);
        image = findViewById(R.id.circleImageView);
        btn1 = findViewById(R.id.fabNewChat);
        perferenceManager = new PerferenceManager(this);
        gotToken();
        loadUserDetails();
        init();
        listenConversation();
        signout_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,UsersActivity.class));
            }
        });
    }
    private void init(){
        conversation = new ArrayList<>();
        recentConversationAdapter = new RecentConversationAdapter(conversation, this);
        rv.setAdapter(recentConversationAdapter);
        database = FirebaseFirestore.getInstance();
    }

    public void loadUserDetails(){
        name.setText(perferenceManager.getString(Constants.KEY_NAME));
        byte[] bytes = Base64.decode(perferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        image.setImageBitmap(bitmap);
    }
    public void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }
    public void listenConversation(){
        database.collection(Constants.KEY_COLLECTION_CONVERSATION).whereEqualTo(Constants.KEY_SENDER_ID,perferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
        database.collection(Constants.KEY_COLLECTION_CONVERSATION).whereEqualTo(Constants.KEY_RECEIVER_ID,perferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }

    public final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if(error!= null){
            return;
        }
        if(value!= null){
            for(DocumentChange documentChange :value.getDocumentChanges()){
                if(documentChange.getType() == DocumentChange.Type.ADDED ){
                    String senderId =documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    String receiverId =documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.userId = senderId;
                    chatMessage.receiverId = receiverId;
                    if(perferenceManager.getString(Constants.KEY_USER_ID).equals(senderId)){
                        chatMessage.conversationImage = documentChange.getDocument().getString(Constants.KEY_RECEIVER_IMAGE);
                        chatMessage.conversationName = documentChange.getDocument().getString(Constants.KEY_RECEIVER_NAME);
                        chatMessage.conversationId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    }else{
                        chatMessage.conversationImage = documentChange.getDocument().getString(Constants.KEY_SENDER_IMAGE);
                        chatMessage.conversationName = documentChange.getDocument().getString(Constants.KEY_SENDER_NAME);
                        chatMessage.conversationId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    }
                    chatMessage.message = documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                    chatMessage.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIME_STAMP);
                    conversation.add(chatMessage);
                } else if (documentChange.getType() == DocumentChange.Type.MODIFIED) {
                    for (int i = 0; i < conversation.size(); i++) {
                        String senderId =documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                        String receiverId =documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                        if(conversation.get(i).userId.equals(senderId) && conversation.get(i).receiverId.equals(receiverId)){
                            conversation.get(i).message = documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                            conversation.get(i).dateObject = documentChange.getDocument().getDate(Constants.KEY_TIME_STAMP);
                            break;
                        }
                    }
                }
            }
            Collections.sort(conversation,(obj1, obj2) -> obj2.dateObject.compareTo(obj1.dateObject));
            recentConversationAdapter.notifyDataSetChanged();
            rv.smoothScrollToPosition(0);
            rv.setVisibility(View.VISIBLE);
            load.setVisibility(View.INVISIBLE);
        }
    };

    public void gotToken(){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    public void updateToken(String token){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_USERS).document(
                        perferenceManager.getString(Constants.KEY_USER_ID));
        documentReference.update(Constants.KEY_FCM_TOKEN,token)
        .addOnFailureListener(e -> {
            showToast("Unable to update token");
        });
    }

    public void signOut(){
        showToast("Signing Out....");
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_USERS).document(
                        perferenceManager.getString(Constants.KEY_USER_ID));
        HashMap<String,Object> updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates).addOnSuccessListener(unused -> {
            perferenceManager.clear();
            startActivity(new Intent(this,SignInActivity.class));
            finish();
        }).addOnFailureListener(e -> {
            showToast("Unable to SignOut");
        });
    }


    @Override
    public void onConversationClicked(User user) {
        Intent intent = new Intent(this,ChatActivity.class);
        intent.putExtra(Constants.KEY_USER,user);
        startActivity(intent);
    }
}