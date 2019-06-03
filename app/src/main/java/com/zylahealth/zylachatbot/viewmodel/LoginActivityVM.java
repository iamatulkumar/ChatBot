package com.zylahealth.zylachatbot.viewmodel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.databinding.BaseObservable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.zylahealth.zylachatbot.databinding.ActivityLoginBinding;
import com.zylahealth.zylachatbot.smack.ContactListActivity;
import com.zylahealth.zylachatbot.smack.LoginActivity;
import com.zylahealth.zylachatbot.worker.OfflineMessageWorker;
import com.zylahealth.zylachatbot.smack.RoosterConnectionService;

import java.util.concurrent.TimeUnit;

public class LoginActivityVM extends BaseObservable implements LifecycleObserver {

    private static final String TAG = "LoginActivity";

    private String xmppId;
    private String password;

    private LoginActivity activity;
    private BroadcastReceiver mBroadcastReceiver;

    public LoginActivityVM(LoginActivity activity, ActivityLoginBinding binding, Lifecycle lifecycle) {
        this.activity = activity;
        lifecycle.addObserver(this);
    }

    public void onClickLogin() {
        if (TextUtils.isEmpty(getXmppId())) {
            Toast.makeText(activity, "Please provide xmpp id", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(getPassword())) {
            Toast.makeText(activity, "Please provide password id", Toast.LENGTH_SHORT).show();
            return;
        } else {
            saveCredentialsAndLogin();

        }
    }

    private void saveCredentialsAndLogin() {
        Log.d(TAG, "saveCredentialsAndLogin() called.");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        prefs.edit()
                .putString("xmpp_jid", getXmppId())
                .putString("xmpp_password", getPassword())
                .putBoolean("xmpp_logged_in", true)
                .commit();

        Constraints constraints = new Constraints.Builder().setRequiredNetworkType
                (NetworkType.CONNECTED).build();

        Data data = new Data.Builder()
                .putString("xmpp_jid", getXmppId())
                .putString("xmpp_password", getPassword()).build();

        PeriodicWorkRequest locationWork = new PeriodicWorkRequest.Builder(OfflineMessageWorker
                .class, 15, TimeUnit.MINUTES)
                .setInputData(data)
                .setConstraints(constraints).build();

        WorkManager.getInstance().enqueue(locationWork);


        //Start the service
        Intent i1 = new Intent(activity, RoosterConnectionService.class);
        activity.startService(i1);

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private void onPause() {
        activity.unregisterReceiver(mBroadcastReceiver);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    protected void onResume() {
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();
                switch (action) {
                    case RoosterConnectionService.UI_AUTHENTICATED:
                        Log.d(TAG, "Got a broadcast to show the main app window");
                        //Show the main app window

                        Intent intent1 = new Intent(activity, ContactListActivity.class);
                        activity.startActivity(intent1);
                        activity.finish();
                        break;
                }

            }
        };
        IntentFilter filter = new IntentFilter(RoosterConnectionService.UI_AUTHENTICATED);
        activity.registerReceiver(mBroadcastReceiver, filter);
    }

    public String getXmppId() {
        return xmppId;
    }

    public void setXmppId(String xmppId) {
        this.xmppId = xmppId;
        notifyChange();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        notifyChange();
    }
}
