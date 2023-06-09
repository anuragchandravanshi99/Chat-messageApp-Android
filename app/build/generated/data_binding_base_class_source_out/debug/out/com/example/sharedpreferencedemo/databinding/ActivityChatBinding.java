// Generated by view binder compiler. Do not edit!
package com.example.sharedpreferencedemo.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.sharedpreferencedemo.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityChatBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final ImageView back;

  @NonNull
  public final ImageView backview;

  @NonNull
  public final ImageView info;

  @NonNull
  public final EditText inputMessage;

  @NonNull
  public final ProgressBar load;

  @NonNull
  public final RecyclerView recycle;

  @NonNull
  public final ImageView send;

  @NonNull
  public final TextView textAvailability;

  @NonNull
  public final TextView username;

  private ActivityChatBinding(@NonNull ConstraintLayout rootView, @NonNull ImageView back,
      @NonNull ImageView backview, @NonNull ImageView info, @NonNull EditText inputMessage,
      @NonNull ProgressBar load, @NonNull RecyclerView recycle, @NonNull ImageView send,
      @NonNull TextView textAvailability, @NonNull TextView username) {
    this.rootView = rootView;
    this.back = back;
    this.backview = backview;
    this.info = info;
    this.inputMessage = inputMessage;
    this.load = load;
    this.recycle = recycle;
    this.send = send;
    this.textAvailability = textAvailability;
    this.username = username;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityChatBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityChatBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_chat, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityChatBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.back;
      ImageView back = ViewBindings.findChildViewById(rootView, id);
      if (back == null) {
        break missingId;
      }

      id = R.id.backview;
      ImageView backview = ViewBindings.findChildViewById(rootView, id);
      if (backview == null) {
        break missingId;
      }

      id = R.id.info;
      ImageView info = ViewBindings.findChildViewById(rootView, id);
      if (info == null) {
        break missingId;
      }

      id = R.id.inputMessage;
      EditText inputMessage = ViewBindings.findChildViewById(rootView, id);
      if (inputMessage == null) {
        break missingId;
      }

      id = R.id.load;
      ProgressBar load = ViewBindings.findChildViewById(rootView, id);
      if (load == null) {
        break missingId;
      }

      id = R.id.recycle;
      RecyclerView recycle = ViewBindings.findChildViewById(rootView, id);
      if (recycle == null) {
        break missingId;
      }

      id = R.id.send;
      ImageView send = ViewBindings.findChildViewById(rootView, id);
      if (send == null) {
        break missingId;
      }

      id = R.id.textAvailability;
      TextView textAvailability = ViewBindings.findChildViewById(rootView, id);
      if (textAvailability == null) {
        break missingId;
      }

      id = R.id.username;
      TextView username = ViewBindings.findChildViewById(rootView, id);
      if (username == null) {
        break missingId;
      }

      return new ActivityChatBinding((ConstraintLayout) rootView, back, backview, info,
          inputMessage, load, recycle, send, textAvailability, username);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
