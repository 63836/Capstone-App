package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ChatMessage> messages;
    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_BOT = 2;

    public ChatAdapter(List<ChatMessage> messages){
        this.messages = messages;
    }

    @Override
    public int getItemViewType(int position){
        if(messages.get(position).getRole().equalsIgnoreCase("user")){
            return VIEW_TYPE_USER;
        } else {
            return VIEW_TYPE_BOT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        if(viewType == VIEW_TYPE_USER){
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_right, parent, false);
            return new UserMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_left, parent, false);
            return new BotMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position){
        ChatMessage message = messages.get(position);
        if(holder instanceof UserMessageViewHolder){
            ((UserMessageViewHolder) holder).messageTextView.setText(message.getContent());
        } else if(holder instanceof BotMessageViewHolder){
            ((BotMessageViewHolder) holder).messageTextView.setText(message.getContent());
        }
    }

    @Override
    public int getItemCount(){
        return messages.size();
    }

    static class UserMessageViewHolder extends RecyclerView.ViewHolder{
        TextView messageTextView;
        public UserMessageViewHolder(@NonNull View itemView){
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
        }
    }

    static class BotMessageViewHolder extends RecyclerView.ViewHolder{
        TextView messageTextView;
        public BotMessageViewHolder(@NonNull View itemView){
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
        }
    }
}
