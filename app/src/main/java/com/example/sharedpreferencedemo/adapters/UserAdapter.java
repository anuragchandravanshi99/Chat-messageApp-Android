package com.example.sharedpreferencedemo.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharedpreferencedemo.R;
import com.example.sharedpreferencedemo.listener.UserListener;
import com.example.sharedpreferencedemo.models.User;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private final List<User> users;
    private final UserListener userListener;
    Context ctx;
    public UserAdapter(Context ctx, List<User> users,UserListener userListener){
        this.users = users;
        this.ctx = ctx;
        this.userListener = userListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.item_container_user,parent,false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.name.setText(users.get(position).name);
        holder.email.setText(users.get(position).email);
        holder.profile.setImageBitmap(getUserImage(users.get(position).image));
        holder.cl.setOnClickListener(v -> {userListener.onUserClicked(users.get(position));});
    }

    @Override
    public int getItemCount() {
        return users.size();
    }


    class UserViewHolder extends RecyclerView.ViewHolder{
        TextView name,email;
        ImageView profile;
        ConstraintLayout cl;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            name =itemView.findViewById(R.id.textName);
            email = itemView.findViewById(R.id.textEmail);
            profile= itemView.findViewById(R.id.imageProfile);
            cl = itemView.findViewById(R.id.root);
        }
    }

    private Bitmap getUserImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }
}
