package com.zylahealth.zylachatbot.worker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.zylahealth.zylachatbot.realm.chat.dao.ChatBotDao;
import com.zylahealth.zylachatbot.realm.chat.model.ChatBot;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.ping.PingFailedListener;
import org.jivesoftware.smackx.ping.PingManager;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;

import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.RealmList;

public class OfflineMessageWorker extends Worker {
    boolean comnnect = false;

    static {
        try {
            Class.forName("org.jivesoftware.smack.ReconnectionManager");
        } catch (ClassNotFoundException ex) {
            Log.d("worker", " ClassNotFoundException ");
        }
    }

    public OfflineMessageWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public ListenableWorker.Result doWork() {
        try {
            XMPPTCPConnectionConfiguration conf = XMPPTCPConnectionConfiguration.builder()
                    .setXmppDomain("chat.poc.zyla.in")
                    .setHost("chat.poc.zyla.in")
                    .setResource("android")

                    //Was facing this issue
                    //https://discourse.igniterealtime.org/t/connection-with-ssl-fails-with-java-security-keystoreexception-jks-not-found/62566
                    .setKeystoreType(null) //This line seems to get rid of the problem

                    .setSecurityMode(ConnectionConfiguration.SecurityMode.required)
                    .setCompressionEnabled(true).build();

            XMPPTCPConnection mConnection = new XMPPTCPConnection(conf);

            mConnection.addConnectionListener(new ConnectionListener() {
                @Override
                public void connected(XMPPConnection connection) {
                    comnnect = true;
                    Log.d("worker", " connected ");
                }

                @Override
                public void authenticated(XMPPConnection connection, boolean resumed) {
                    comnnect = true;
                    Log.d("worker", " authenticated ");
                }

                @Override
                public void connectionClosed() {
                    comnnect = false;
                    Log.d("worker", " connectionClosed ");
                }

                @Override
                public void connectionClosedOnError(Exception e) {
                    comnnect = false;
                    Log.d("worker", " connectionClosedOnError ");
                }

                @Override
                public void reconnectionSuccessful() {
                    comnnect = true;
                    Log.d("worker", " reconnectionSuccessful ");
                }

                @Override
                public void reconnectingIn(int seconds) {
                    Log.d("worker", " reconnectingIn ");
                }

                @Override
                public void reconnectionFailed(Exception e) {
                    Log.d("worker", " reconnectionFailed ");
                }
            });

            Log.d("worker", "Calling connect() ");


            Maybe.fromCallable(() -> mConnection.connect())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((config) -> {
                        mConnection.login(getInputData().getString("xmpp_jid"), getInputData().getString("xmpp_password"), Resourcepart.fromOrThrowUnchecked("android"));
                    }, throwable -> {
                        Log.d("d1", "error thgrow ");
                    });

            try {

                ReconnectionManager reconnectionManager = ReconnectionManager.getInstanceFor(mConnection);
                reconnectionManager.setEnabledPerDefault(true);
                reconnectionManager.enableAutomaticReconnection();
                Log.d("worker", " reconnectionManager ");
            } catch (Exception ex) {
                ex.fillInStackTrace();
            }

            PingManager pingManager = PingManager.getInstanceFor(mConnection);
            pingManager.setPingInterval(300); // 2.5 min
            pingManager.registerPingFailedListener(new PingFailedListener() {
                @Override
                public void pingFailed() {
                    Log.d("worker", " pingFailed " + comnnect);
                }
            });

            Log.d("worker", " login() Called " + comnnect);

            RealmList<ChatBot> chatBots = ChatBotDao.getOffLineMessage();
            if (comnnect) {
                if (chatBots != null && chatBots.size() > 0) {
                    for (int i = 0; i < chatBots.size(); i++) {
                        ChatBot chatBot = chatBots.get(i);
                        Log.d("worker", "The client is connected to the server,Sending Message");
                        //Send the message to the server

                        EntityBareJid jid = null;

                        ChatManager chatManager = ChatManager.getInstanceFor(mConnection);

                        try {
                            jid = JidCreate.entityBareFrom("atul@chat.poc.zyla.in");
                        } catch (XmppStringprepException e) {
                            e.printStackTrace();
                        }
                        Chat chat = chatManager.chatWith(jid);
                        try {
                            Message message = new Message(jid, Message.Type.chat);
                            message.setBody(chatBot.getMessage());
                            chat.send(message);
                            ChatBotDao.saveOfflineMessageMessage(chatBot);
                        } catch (SmackException.NotConnectedException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
            if (!comnnect) {
                return Result.retry();
            } else {
                return Result.success();
            }
        } catch (IOException e) {
            Log.d("worker", "Something went wrong while connecting ,make sure the credentials are right and try again");
            e.printStackTrace();
            //Stop the service all together.
            return Result.retry();
        }

    }
}
