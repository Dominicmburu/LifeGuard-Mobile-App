package com.example.urgentcare.bot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.urgentcare.R;
import com.example.urgentcare.SecondBotActivity;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_USER = 0;
    private static final int VIEW_TYPE_BOT = 1;
    private static final int VIEW_TYPE_RESPONSE = 2;

    private List<ChatMessage> chatMessages;
    private Context context;

    public ChatAdapter(List<ChatMessage> chatMessages, Context context) {
        this.chatMessages = chatMessages;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = chatMessages.get(position);
        if (message.isResponse()) {
            return VIEW_TYPE_RESPONSE;
        } else if (message.isUser()) {
            return VIEW_TYPE_USER;
        } else {
            return VIEW_TYPE_BOT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_USER) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_user_message, parent, false);
            return new UserViewHolder(view);
        } else if (viewType == VIEW_TYPE_RESPONSE) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_response_message, parent, false);
            return new ResponseViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_bot_message, parent, false);
            return new BotViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = chatMessages.get(position);
        if (holder instanceof UserViewHolder) {
            ((UserViewHolder) holder).bind(message);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((SecondBotActivity) context).fetchResponse(message.getText(), position + 1);
                }
            });
        } else if (holder instanceof ResponseViewHolder) {
            ((ResponseViewHolder) holder).bind(message);
        } else {
            ((BotViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;

        UserViewHolder(View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
        }

        void bind(ChatMessage message) {
            messageTextView.setText(message.getText());
        }
    }

    static class BotViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;

        BotViewHolder(View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
        }

        void bind(ChatMessage message) {
            messageTextView.setText(message.getText());
        }
    }

    static class ResponseViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;

        ResponseViewHolder(View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
        }

        void bind(ChatMessage message) {
            messageTextView.setText(message.getText());
        }
    }
}
