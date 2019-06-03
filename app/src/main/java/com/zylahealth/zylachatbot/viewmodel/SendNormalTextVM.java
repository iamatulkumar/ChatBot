package com.zylahealth.zylachatbot.viewmodel;

import android.content.Context;

import androidx.databinding.BaseObservable;

import com.zylahealth.zylachatbot.adapter.holder.SendNormalTextVH;
import com.zylahealth.zylachatbot.realm.chat.model.ChatBot;

public class SendNormalTextVM extends BaseObservable {

    private Context context;
    private SendNormalTextVH sendNormalTextVH;
    private ChatBot chatBot;

    public SendNormalTextVM(Context context, SendNormalTextVH sendNormalTextVH, ChatBot chatBot) {
        this.context = context;
        this.chatBot = chatBot;
        this.sendNormalTextVH = sendNormalTextVH;
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

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

}
