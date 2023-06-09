// Generated by view binder compiler. Do not edit!
package com.example.sharedpreferencedemo.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.sharedpreferencedemo.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityMainBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final CardView cardView;

  @NonNull
  public final ImageView circleImageView;

  @NonNull
  public final RecyclerView conversationRecyclerView;

  @NonNull
  public final FloatingActionButton fabNewChat;

  @NonNull
  public final AppCompatImageView imageSignOut;

  @NonNull
  public final ProgressBar progressBar;

  @NonNull
  public final TextView textName;

  private ActivityMainBinding(@NonNull ConstraintLayout rootView, @NonNull CardView cardView,
      @NonNull ImageView circleImageView, @NonNull RecyclerView conversationRecyclerView,
      @NonNull FloatingActionButton fabNewChat, @NonNull AppCompatImageView imageSignOut,
      @NonNull ProgressBar progressBar, @NonNull TextView textName) {
    this.rootView = rootView;
    this.cardView = cardView;
    this.circleImageView = circleImageView;
    this.conversationRecyclerView = conversationRecyclerView;
    this.fabNewChat = fabNewChat;
    this.imageSignOut = imageSignOut;
    this.progressBar = progressBar;
    this.textName = textName;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityMainBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityMainBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_main, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityMainBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.cardView;
      CardView cardView = ViewBindings.findChildViewById(rootView, id);
      if (cardView == null) {
        break missingId;
      }

      id = R.id.circleImageView;
      ImageView circleImageView = ViewBindings.findChildViewById(rootView, id);
      if (circleImageView == null) {
        break missingId;
      }

      id = R.id.conversationRecyclerView;
      RecyclerView conversationRecyclerView = ViewBindings.findChildViewById(rootView, id);
      if (conversationRecyclerView == null) {
        break missingId;
      }

      id = R.id.fabNewChat;
      FloatingActionButton fabNewChat = ViewBindings.findChildViewById(rootView, id);
      if (fabNewChat == null) {
        break missingId;
      }

      id = R.id.imageSignOut;
      AppCompatImageView imageSignOut = ViewBindings.findChildViewById(rootView, id);
      if (imageSignOut == null) {
        break missingId;
      }

      id = R.id.progressBar;
      ProgressBar progressBar = ViewBindings.findChildViewById(rootView, id);
      if (progressBar == null) {
        break missingId;
      }

      id = R.id.textName;
      TextView textName = ViewBindings.findChildViewById(rootView, id);
      if (textName == null) {
        break missingId;
      }

      return new ActivityMainBinding((ConstraintLayout) rootView, cardView, circleImageView,
          conversationRecyclerView, fabNewChat, imageSignOut, progressBar, textName);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
