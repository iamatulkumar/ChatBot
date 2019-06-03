package com.zylahealth.zylachatbot.adapter.holder;

import android.content.Context;
import com.zylahealth.zylachatbot.realm.chat.model.ChatBot;
import com.zylahealth.zylachatbot.util.MessageType;

import io.realm.RealmList;

public class ChatViewProvider {

    private Context context;
    private RealmList<ChatBot> chatBots;

    public static class View {
        public static final int SEND_NORMAL_TEXT = 1;
        public static final int RECEIVE_NORMAL_TEXT = 2;
    }

    public ChatViewProvider(Context context, RealmList<ChatBot> chatBots) {
        this.context = context;
        this.chatBots = chatBots;
    }

    public int getViewType(final int position) {
        ChatBot chatBot = chatBots.get(position);
        int type = 1;
        if (chatBot != null) {
            switch (chatBot.getType()) {
                case 1:
                    if (isSent(chatBot.getType())) {
                        type = View.SEND_NORMAL_TEXT;
                    } else {
                        type = View.RECEIVE_NORMAL_TEXT;
                    }
                    break;
                case 2:
                    if (isSent(chatBot.getType())) {
                        type = View.SEND_NORMAL_TEXT;
                    } else {
                        type = View.RECEIVE_NORMAL_TEXT;
                    }
                    break;

                default:
                    if (isSent(chatBot.getType())) {
                        type = View.SEND_NORMAL_TEXT;
                    } else {
                        type = View.RECEIVE_NORMAL_TEXT;
                    }
                    break;
            }
        }

        return type;
    }

    public boolean isSent(int type) {
        return type == MessageType.SENT;
    }

    public RealmList<ChatBot> getChatBots() {
        return chatBots;
    }

    public void setChatBots(RealmList<ChatBot> chatBots) {
        this.chatBots = chatBots;
    }

}
