package com.example.sharedpreferencedemo.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharedpreferencedemo.databinding.ItemContainerRecentConversationBinding;
import com.example.sharedpreferencedemo.listener.ConversationListner;
import com.example.sharedpreferencedemo.models.ChatMessage;
import com.example.sharedpreferencedemo.models.User;

import java.util.List;

public class RecentConversationAdapter extends RecyclerView.Adapter<RecentConversationAdapter.ConversationViewHolder>{
    private final List<ChatMessage> chatMessages;
    private final ConversationListner conversationListner;

    public RecentConversationAdapter(List<ChatMessage> chatMessages, ConversationListner conversationListner) {
        this.chatMessages = chatMessages;
        this.conversationListner = conversationListner;
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversationViewHolder(ItemContainerRecentConversationBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
        holder.setData(chatMessages.get(position));
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    class ConversationViewHolder extends RecyclerView.ViewHolder {
        ItemContainerRecentConversationBinding binding;

        public ConversationViewHolder(@NonNull ItemContainerRecentConversationBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
        void setData(ChatMessage chatMessage){
            binding.imageProfile.setImageBitmap(getConversationImage(chatMessage.conversationImage));
            binding.textName.setText(chatMessage.conversationName);
            binding.textRecentMessage.setText(chatMessage.message);
            binding.getRoot().setOnClickListener(v -> {
                User user = new User();
                user.id = chatMessage.conversationId;
                user.image = chatMessage.conversationImage;
                user.email = chatMessage.conversationName;
                conversationListner.onConversationClicked(user);
            });
        }
    }

    private Bitmap getConversationImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }
}
