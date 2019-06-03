package com.zylahealth.zylachatbot.viewmodel;

import android.content.Context;

import androidx.databinding.BaseObservable;

import com.zylahealth.zylachatbot.adapter.holder.ReceiveNormalTextVH;
import com.zylahealth.zylachatbot.realm.chat.model.ChatBot;

public class ReceiveNormalTextVM extends BaseObservable {

    private Context context;
    private ReceiveNormalTextVH receiveNormalTextVH;
    private ChatBot chatBot;

    public ReceiveNormalTextVM(Context context, ReceiveNormalTextVH receiveNormalTextVH, ChatBot chatBot) {
        this.context = context;
        this.receiveNormalTextVH = receiveNormalTextVH;
        this.chatBot = chatBot;
    }

    /**********************************************************************
     * Getters & setters
     **********************************************************************/

    public ChatBot getChatBot() {
        return chatBot;
    }

    public void setChatBot(ChatBot chatBot) {
        this.chatBot = chatBot;
        notifyChange();
    }
}
