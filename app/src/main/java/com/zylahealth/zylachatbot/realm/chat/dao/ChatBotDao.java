package com.zylahealth.zylachatbot.realm.chat.dao;

import com.zylahealth.zylachatbot.application.ChatBotApplication;
import com.zylahealth.zylachatbot.realm.chat.model.ChatBot;
import com.zylahealth.zylachatbot.util.MessageType;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

public class ChatBotDao {

    public static void saveMessage(ChatBot chatBot) {
        try (Realm realm = ChatBotApplication.getRealmChatBotModule()) {
            realm.executeTransaction((realmInstance) -> {
                if (chatBot != null) {
                    RealmResults<ChatBot> chatBot1 = realm.where(ChatBot.class).findAllAsync().sort("id", Sort.DESCENDING);
                    if (chatBot1 != null && chatBot1.size() > 0) {
                        ChatBot chatBot2 = chatBot1.first();
                        chatBot.setId(chatBot2.getId() + 1);
                    }
                    realmInstance.insertOrUpdate(chatBot);
                }
            });
        }
    }

    public static void saveOfflineMessageMessage(ChatBot chatBot) {
        try (Realm realm = ChatBotApplication.getRealmChatBotModule()) {
            realm.executeTransaction((realmInstance) -> {
                if (chatBot != null) {
                    chatBot.setSendReport(2);
                    realmInstance.insertOrUpdate(chatBot);
                }
            });
        }
    }

    public static RealmList<ChatBot> getOffLineMessage() {
        Realm realm = ChatBotApplication.getRealmChatBotModule();
        RealmResults<ChatBot> chatBot = realm.where(ChatBot.class).
                equalTo("type", MessageType.SENT)
                .equalTo("sendReport", MessageType.OFFLINE)
                .findAll();

        RealmList<ChatBot> chatBotRealmList = new RealmList<ChatBot>();
        chatBotRealmList.addAll(chatBot.subList(0, chatBot.size()));
        return chatBotRealmList;
    }

    public static RealmList<ChatBot> getAllMessage() {
        Realm realm = ChatBotApplication.getRealmChatBotModule();
        RealmResults<ChatBot> chatBot = realm.where(ChatBot.class)
                .findAll();

        RealmList<ChatBot> chatBotRealmList = new RealmList<ChatBot>();
        chatBotRealmList.addAll(chatBot.subList(0, chatBot.size()));
        return chatBotRealmList;
    }

}
