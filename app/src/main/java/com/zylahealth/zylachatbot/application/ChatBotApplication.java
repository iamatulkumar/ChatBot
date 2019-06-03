package com.zylahealth.zylachatbot.application;

import android.app.Application;

import com.zylahealth.zylachatbot.realm.module.RealmChatBotModule;

import io.reactivex.plugins.RxJavaPlugins;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class ChatBotApplication extends Application {

    private static ChatBotApplication instance;

    public static ChatBotApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        RxJavaPlugins.setErrorHandler(throwable -> {
        });
        try {
            Realm.init(this);

            // The RealmConfiguration is created using the builder pattern.
            // The Realm file will be located in Context.getFilesDir() with Realm.DEFAULT_REALM_NAME
            // Use the config
            RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                    .name(Realm.DEFAULT_REALM_NAME)
                    .schemaVersion(1)
                    .build();
            Realm.setDefaultConfiguration(realmConfiguration);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ChatBotApplication.instance = this;
    }

    public static Realm getRealmChatBotModule() {
        RealmConfiguration realmChatBotConfiguration = new RealmConfiguration.Builder()
                .name("zyla.chat")
                .schemaVersion(1)
                .modules(new RealmChatBotModule())
                .build();

        return Realm.getInstance(realmChatBotConfiguration);
    }

}
