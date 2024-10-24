package com.example.urgentcare.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.urgentcare.R;
import com.example.urgentcare.helper.ChatsModel;

import java.util.ArrayList;

public class ChatRVAdapter extends RecyclerView.Adapter {
    private ArrayList<ChatsModel> chatsModelArrayList;
    private Context context;

    public ChatRVAdapter(ArrayList<ChatsModel> chatsModelArrayList, Context context) {
        this.chatsModelArrayList = chatsModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        switch (viewType){
            case 0:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_right, parent, false);
                return new UserViewHolder(view);

            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.boot_msg, parent, false);
                return new BotViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatsModel chatsModel = chatsModelArrayList.get(position);

        switch (chatsModel.getSender()){
            case "user":
                ((UserViewHolder)holder).userTV.setText(chatsModel.getMessage());
//                animateMessage(holder.itemView, position, 0); // User message animation
                break;

            case "bot":
                ((BotViewHolder)holder).botMsgTv.setText(chatsModel.getMessage());
//                animateMessage(holder.itemView, position, 1);
                break;
        }
    }

//    private void animateMessage(View view, int position, int senderType) {
//        long animationDelay = 500L; // Delay in milliseconds (customize based on your needs)
//
//        // You can customize the animation properties here
//        view.setScaleX(0.9f);
//        view.setScaleY(0.9f);
//        view.setAlpha(0f);
//
//        // Using view's property animator for a simple "pop in" effect
//        view.animate()
//                .scaleX(1f)
//                .scaleY(1f)
//                .alpha(1f)
//                .setDuration(200) // Duration of the animation
//                .setStartDelay(animationDelay * position) // Delay based on item position to create staggering effect
//                .start();
//    }

    @Override
    public int getItemViewType(int position){
        switch (chatsModelArrayList.get(position).getSender()){
            case "user":
                return 0;
            case "bot":
                return 1;
            default:
                return -1;
        }
    }

    @Override
    public int getItemCount() {
        return chatsModelArrayList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder{
        TextView userTV;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            userTV = itemView.findViewById(R.id.idTVUser);
        }

    }

    public static class BotViewHolder extends RecyclerView.ViewHolder{
        TextView botMsgTv;
        public BotViewHolder(@NonNull View itemView) {
            super(itemView);

            botMsgTv = itemView.findViewById(R.id.idTVBot);
        }
    }
}
