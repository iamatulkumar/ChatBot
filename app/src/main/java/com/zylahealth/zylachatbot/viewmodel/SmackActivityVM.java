package com.zylahealth.zylachatbot.viewmodel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.databinding.BaseObservable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.wang.avi.AVLoadingIndicatorView;
import com.zylahealth.zylachatbot.adapter.ChatAdapter;
import com.zylahealth.zylachatbot.databinding.ActivitySmackBinding;
import com.zylahealth.zylachatbot.realm.chat.dao.ChatBotDao;
import com.zylahealth.zylachatbot.realm.chat.model.ChatBot;
import com.zylahealth.zylachatbot.worker.OfflineMessageWorker;
import com.zylahealth.zylachatbot.smack.RoosterConnection;
import com.zylahealth.zylachatbot.smack.RoosterConnectionService;
import com.zylahealth.zylachatbot.smack.SmackActivity;
import com.zylahealth.zylachatbot.util.DateTimeUtils;
import com.zylahealth.zylachatbot.util.NetworkUtils;

import java.util.concurrent.TimeUnit;

import io.realm.RealmList;

public class SmackActivityVM extends BaseObservable implements LifecycleObserver {

    private SmackActivity activity;
    private ActivitySmackBinding binding;

    private ChatAdapter adapter;
    private BroadcastReceiver networkReceiver;
    private boolean sendEnabled;
    private String messageContent;
    private RecyclerView recyclerView;
    private AVLoadingIndicatorView loader;
    private BroadcastReceiver mBroadcastReceiver;

    private String contactJid;

    private static final String TAG = "RoosterConnection";

    public SmackActivityVM(SmackActivity activity, ActivitySmackBinding binding, Lifecycle lifecycle) {
        this.activity = activity;
        this.binding = binding;
        lifecycle.addObserver(this);

        Constraints constraints = new Constraints.Builder().setRequiredNetworkType
                (NetworkType.CONNECTED).build();

        Data data = new Data.Builder()
                .putString("xmpp_jid", "atul1@chat.poc.zyla.in")
                .putString("xmpp_password", "12345").build();

        PeriodicWorkRequest locationWork = new PeriodicWorkRequest.Builder(OfflineMessageWorker
                .class, 15, TimeUnit.MINUTES)
                .setInputData(data)
                .setConstraints(constraints).build();

        WorkManager.getInstance().enqueue(locationWork);

//        WorkManager.getInstance().getWorkInfoByIdLiveData(locationWork.getId()).observe((LifecycleOwner) activity, workInfo -> {
//            if (workInfo != null && workInfo.getState().isFinished()) {
//                Log.d("worker", "Completed ");
//            }
//        });

        onCreate();
    }

    private void onCreate() {
        setSendEnabled(false);
        initLayout();
        stopAnim();

        Intent intent = activity.getIntent();
        contactJid = intent.getStringExtra("EXTRA_CONTACT_JID");

        networkReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, Intent intent) {
                if (NetworkUtils.isConnectingToInternet(context)) {
                    //sendOffLineMessage();
                }
            }
        };
    }

//    private void sendOffLineMessage() {
//        RealmList<ChatBot> chatBots = ChatBotDao.getOffLineMessage();
//        if (RoosterConnectionService.getState().equals(RoosterConnection.ConnectionState.CONNECTED)) {
//            if (chatBots != null && chatBots.size() > 0) {
//                for (int i = 0; i < chatBots.size(); i++) {
//                    ChatBot chatBot = chatBots.get(i);
//                    ChatBotDao.saveOfflineMessageMessage(chatBot);
//                    setChatData();
//
//                    Log.d(TAG, "The client is connected to the server,Sending Message");
//                    //Send the message to the server
//
//                    Intent intent = new Intent(RoosterConnectionService.SEND_MESSAGE);
//                    intent.putExtra(RoosterConnectionService.BUNDLE_MESSAGE_BODY,
//                            chatBot.getMessage());
//                    intent.putExtra(RoosterConnectionService.BUNDLE_TO, contactJid);
//
//                    activity.sendBroadcast(intent);
//
//                }
//            }
//        } else {
//            Toast.makeText(activity,
//                    "Client not connected to server ,Message not sent!",
//                    Toast.LENGTH_LONG).show();
//        }
//    }

    private void initLayout() {
        recyclerView = binding.rvMainChat;
        loader = binding.indicator;
        setChatData();

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                switch (action) {
                    case RoosterConnectionService.NEW_MESSAGE:
                        String from = intent.getStringExtra(RoosterConnectionService.BUNDLE_FROM_JID);
                        String body = intent.getStringExtra(RoosterConnectionService.BUNDLE_MESSAGE_BODY);

                        if (from.equals(contactJid)) {
                            setReceiveMessage(body);
                        } else {
                            Log.d(TAG, "Got a message from jid :" + from);
                        }
                        return;
                }
            }
        };

        IntentFilter filter = new IntentFilter(RoosterConnectionService.NEW_MESSAGE);
        activity.registerReceiver(mBroadcastReceiver, filter);
    }

    private void setReceiveMessage(String message) {
        ChatBot chatBot = new ChatBot();
        chatBot.setMessage(message);
        chatBot.setType(2);
        chatBot.setTime(DateTimeUtils.getCurrentTimeUsingDate());
        ChatBotDao.saveMessage(chatBot);

        setChatData();
    }

    private void setChatData() {
        RealmList<ChatBot> chatBots = ChatBotDao.getAllMessage();

        if (recyclerView.getLayoutManager() == null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        }

        if (adapter == null) {
            adapter = new ChatAdapter(activity, chatBots);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.setChatBots(chatBots);
            adapter.notifyDataSetChanged();
        }

        recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
    }

    /**********************************************************************
     * Exposed methods
     **********************************************************************/

    public void onTextChanged(CharSequence text, int start, int before, int count) {
        final String messageContent = text.toString();

        if (messageContent.isEmpty()) {
            setSendEnabled(false);
        } else {
            setSendEnabled(true);
        }

        notifyChange();
    }

    public void onClickSend() {
        if (TextUtils.isEmpty(getMessageContent()) || !sendEnabled) {
            Toast.makeText(activity, "Please type your message", Toast.LENGTH_SHORT).show();
            return;
        } else {
            setSendMessage();
            if (NetworkUtils.isConnectingToInternet(activity)) {

                if (RoosterConnectionService.getState().equals(RoosterConnection.ConnectionState.CONNECTED)) {
                    Log.d(TAG, "The client is connected to the server,Sending Message");
                    //Send the message to the server

                    Intent intent = new Intent(RoosterConnectionService.SEND_MESSAGE);
                    intent.putExtra(RoosterConnectionService.BUNDLE_MESSAGE_BODY,
                            getMessageContent());
                    intent.putExtra(RoosterConnectionService.BUNDLE_TO, contactJid);

                    activity.sendBroadcast(intent);

                } else {
                    Toast.makeText(activity,
                            "Client not connected to server ,Message not sent!",
                            Toast.LENGTH_LONG).show();
                }
                setMessageContent("");
            } else {
                Toast.makeText(activity, "Please check your internet connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setSendMessage() {
        ChatBot chatBot = new ChatBot();
        chatBot.setMessage(getMessageContent());
        chatBot.setTime(DateTimeUtils.getCurrentTimeUsingDate());
        chatBot.setType(1);

        if (NetworkUtils.isConnectingToInternet(activity)) {
            chatBot.setSendReport(2);
        } else {
            chatBot.setSendReport(1);
            setMessageContent("");
        }

        ChatBotDao.saveMessage(chatBot);
        setChatData();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private void start() {
        registerNetworkReceiver();
    }

    /**
     * On Stop lifecycle event
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private void stop() {
        unregisterNetworkReceiver();
    }

    /**
     * Register network broadcast receiver
     */
    private void registerNetworkReceiver() {
        activity.getApplicationContext().registerReceiver(networkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    /**
     * Unregister network broadcast receiver
     */
    private void unregisterNetworkReceiver() {
        activity.getApplicationContext().unregisterReceiver(networkReceiver);
    }

    /**********************************************************************
     * Getters & setters
     **********************************************************************/

    public boolean isSendEnabled() {
        return sendEnabled;
    }

    public void setSendEnabled(boolean sendEnabled) {
        this.sendEnabled = sendEnabled;
        notifyChange();
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
        notifyChange();
    }

    void startAnim() {
        loader.show();
    }

    void stopAnim() {
        loader.hide();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onDestroy() {
        activity.unregisterReceiver(mBroadcastReceiver);
    }

}
