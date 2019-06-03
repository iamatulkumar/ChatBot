package com.zylahealth.zylachatbot.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.zylahealth.zylachatbot.R;
import com.zylahealth.zylachatbot.adapter.holder.ChatVH;
import com.zylahealth.zylachatbot.adapter.holder.ChatViewProvider;
import com.zylahealth.zylachatbot.adapter.holder.ReceiveNormalTextVH;
import com.zylahealth.zylachatbot.adapter.holder.SendNormalTextVH;
import com.zylahealth.zylachatbot.realm.chat.model.ChatBot;
import com.zylahealth.zylachatbot.viewmodel.ReceiveNormalTextVM;
import com.zylahealth.zylachatbot.viewmodel.SendNormalTextVM;

import io.realm.RealmList;

public class ChatAdapter extends RecyclerView.Adapter<ChatVH> {

    private Context context;
    private ChatViewProvider viewProvider;
    private RealmList<ChatBot> chatBots;

    public ChatAdapter(Context context, RealmList<ChatBot> chatBots) {
        this.context = context;
        this.chatBots = chatBots;
        this.viewProvider = new ChatViewProvider(context, chatBots);
    }

    @NonNull
    @Override
    public ChatVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        switch (viewType) {
            case ChatViewProvider.View.SEND_NORMAL_TEXT:
                view = LayoutInflater.from(context).inflate(R.layout.layout_chat_send_normal_text, parent, false);

                return new SendNormalTextVH(view);
            case ChatViewProvider.View.RECEIVE_NORMAL_TEXT:
                view = LayoutInflater.from(context).inflate(R.layout.layout_chat_receive_normal_text, parent, false);

                return new ReceiveNormalTextVH(view);
            default:
                view = LayoutInflater.from(context).inflate(R.layout.layout_chat_send_normal_text, parent, false);

                return new SendNormalTextVH(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull ChatVH holder, int position) {
        try {
            switch (getItemViewType(position)) {
                case ChatViewProvider.View.SEND_NORMAL_TEXT:
                    SendNormalTextVM sendNormalTextVM = new SendNormalTextVM(context, ((SendNormalTextVH) holder), chatBots.get(position));

                    ((SendNormalTextVH) holder).getBinding().setViewModel(sendNormalTextVM);
                    ((SendNormalTextVH) holder).getBinding().executePendingBindings();

                    break;
                case ChatViewProvider.View.RECEIVE_NORMAL_TEXT:
                    ReceiveNormalTextVM receiveNormalTextVM = new ReceiveNormalTextVM(context, ((ReceiveNormalTextVH) holder), chatBots.get(position));

                    ((ReceiveNormalTextVH) holder).getBinding().setViewModel(receiveNormalTextVM);
                    ((ReceiveNormalTextVH) holder).getBinding().executePendingBindings();

                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if (chatBots !=null) {
            return chatBots.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(final int position) {
        return viewProvider.getViewType(position);
    }

    public RealmList<ChatBot> getChatBots() {
        return chatBots;
    }

    public void setChatBots(RealmList<ChatBot> chatBots) {
        this.chatBots = chatBots;
        this.viewProvider.setChatBots(chatBots);
    }
}
