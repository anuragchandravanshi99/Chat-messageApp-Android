package com.example.sharedpreferencedemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.sharedpreferencedemo.adapters.ChatAdapter;
import com.example.sharedpreferencedemo.models.ChatMessage;
import com.example.sharedpreferencedemo.models.User;
import com.example.sharedpreferencedemo.utilities.Constants;
import com.example.sharedpreferencedemo.utilities.PerferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ChatActivity extends BaseActivity {
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;
    private PerferenceManager perferenceManager;
    private FirebaseFirestore database;
    Boolean isReceiverAvailable = false;
    private User receiverUser;
    private String conversationId = null;
    RecyclerView rv;
    EditText mess;
    ProgressBar load;
    TextView name,onlineTxt ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        name = findViewById(R.id.username);
        ImageView iv = findViewById(R.id.back);
        ImageView send = findViewById(R.id.send);
        mess = findViewById(R.id.inputMessage);
        rv = findViewById(R.id.recycle);
        load = findViewById(R.id.load);
        onlineTxt = findViewById(R.id.textAvailability);
        iv.setOnClickListener(v -> {onBackPressed();});
        receiverUser =(User) getIntent().getExtras().getSerializable(Constants.KEY_USER);
        name.setText(receiverUser.getName());
        init();
        listMessage();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mess.getText().toString().trim().equals("")){
                    sendMessage();
                }
            }
        });
    }

    private void listenAvailabilityOfReceiver(){
        database.collection(Constants.KEY_COLLECTION_USERS).document(receiverUser.id)
                .addSnapshotListener(ChatActivity.this,(value, error) -> {
                   if(error != null){
                       return;
                   }
                   if(value != null){
                       if(value.getLong(Constants.KEY_AVAILABILITY) != null){
                           int availability = Objects.requireNonNull(value.getLong(Constants.KEY_AVAILABILITY)).intValue();
                           isReceiverAvailable = availability==1;
                       }
                   }
                   if(isReceiverAvailable){
                       onlineTxt.setVisibility(View.VISIBLE);
                   }else{
                       onlineTxt.setVisibility(View.GONE);
                   }
                });
    }
    private void listMessage(){
        database.collection(Constants.KEY_COLLECTION_CHAT).whereEqualTo(Constants.KEY_SENDER_ID,perferenceManager.getString(Constants.KEY_USER_ID))
                .whereEqualTo(Constants.KEY_RECEIVER_ID,receiverUser.id).addSnapshotListener(eventListener);
        database.collection(Constants.KEY_COLLECTION_CHAT).whereEqualTo(Constants.KEY_SENDER_ID,receiverUser.id)
                .whereEqualTo(Constants.KEY_RECEIVER_ID,perferenceManager.getString(Constants.KEY_USER_ID)).addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if(error != null){
            return;
        }
        if(value != null){
            int count = chatMessages.size();
            for(DocumentChange documentChange : value.getDocumentChanges()){
                if(documentChange.getType() == DocumentChange.Type.ADDED){
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.userId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    chatMessage.receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    chatMessage.message = documentChange.getDocument().getString(Constants.KEY_MESSAGE);
                    chatMessage.dateTime = getReadableDate(documentChange.getDocument().getDate(Constants.KEY_TIME_STAMP));
                    chatMessage.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIME_STAMP);
                    chatMessages.add(chatMessage);
                }
            }
            Collections.sort(chatMessages,(obj1, obj2) -> obj1.dateObject.compareTo(obj2.dateObject));
            if(count == 0){
                chatAdapter.notifyDataSetChanged();
            }else{
                chatAdapter.notifyItemRangeInserted(chatMessages.size(),chatMessages.size());
                rv.smoothScrollToPosition(chatMessages.size()-1);
            }
            rv.setVisibility(View.VISIBLE);
            load.setVisibility(View.INVISIBLE);

            if(conversationId==null){
                checkForConversation();
            }
        }
    };
    private void addConversation(HashMap<String,Object> conversion){
        database.collection(Constants.KEY_COLLECTION_CONVERSATION).add(conversion)
                .addOnSuccessListener(documentReference -> conversationId = documentReference.getId());
    }

    private void updateConversation(String message){
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_CONVERSATION).document(conversationId);
        documentReference.update(
                Constants.KEY_LAST_MESSAGE,message,
                Constants.KEY_TIME_STAMP,new Date()
        );
    }
    private void checkForConversation(){
        if(chatMessages.size()!=0){
            checkForConversationRemotely(perferenceManager.getString(Constants.KEY_USER_ID),receiverUser.id);
            checkForConversationRemotely(receiverUser.id,perferenceManager.getString(Constants.KEY_USER_ID));

        }
    }
    private void init(){
        perferenceManager = new PerferenceManager(ChatActivity.this);
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(ChatActivity.this,chatMessages,getBitmap(receiverUser.image),perferenceManager.getString(Constants.KEY_USER_ID));
        rv.setAdapter(chatAdapter);
        database = FirebaseFirestore.getInstance();
    }

    public void sendMessage(){
        HashMap<String,Object> message = new HashMap<>();
        message.put(Constants.KEY_SENDER_ID,perferenceManager.getString(Constants.KEY_USER_ID));
        message.put(Constants.KEY_RECEIVER_ID,receiverUser.id);
        message.put(Constants.KEY_MESSAGE,mess.getText().toString());
        message.put(Constants.KEY_TIME_STAMP,new Date());
        database.collection(Constants.KEY_COLLECTION_CHAT).add(message);
        if(conversationId != null){
            updateConversation(mess.getText().toString());
        }else{
            HashMap<String,Object> conversation = new HashMap<>();
            conversation.put(Constants.KEY_SENDER_ID,perferenceManager.getString(Constants.KEY_USER_ID));
            conversation.put(Constants.KEY_SENDER_NAME,perferenceManager.getString(Constants.KEY_NAME));
            conversation.put(Constants.KEY_SENDER_IMAGE,perferenceManager.getString(Constants.KEY_IMAGE));
            conversation.put(Constants.KEY_RECEIVER_ID,receiverUser.id);
            conversation.put(Constants.KEY_RECEIVER_NAME,receiverUser.name);
            conversation.put(Constants.KEY_RECEIVER_IMAGE,receiverUser.image);
            conversation.put(Constants.KEY_LAST_MESSAGE,mess.getText().toString());
            conversation.put(Constants.KEY_TIME_STAMP,new Date());
            addConversation(conversation);
        }
        mess.setText(null);
    }
    private Bitmap getBitmap(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }
    private String getReadableDate(Date date){
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm:ss", Locale.getDefault()).format(date);
    }

    private void checkForConversationRemotely(String senderId,String receiverId){
        database.collection(Constants.KEY_COLLECTION_CONVERSATION).whereEqualTo(Constants.KEY_RECEIVER_ID,receiverId).whereEqualTo(Constants.KEY_SENDER_ID,senderId)
                .get().addOnCompleteListener(conversationOnCompleteListener);
    }

    private final OnCompleteListener<QuerySnapshot> conversationOnCompleteListener = task -> {
        if(task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size()>0){
            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
            conversationId = documentSnapshot.getId();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        listenAvailabilityOfReceiver();
    }
}