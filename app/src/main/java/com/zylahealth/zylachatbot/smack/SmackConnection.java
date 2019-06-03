package com.zylahealth.zylachatbot.smack;

import android.util.Log;

import com.zylahealth.zylachatbot.realm.chat.model.ChatBot;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;


public class SmackConnection {

    private static volatile SmackConnection instance;
    private static final String TAG = "RoosterConnection";

    private PublishSubject<Integer> statusStream;
    private final String serverName = "chat.poc.zyla.in";


    private ConnectionListener connectionListener;
    private XMPPTCPConnection xmpptcpConnection;
    ChatMessageListener messageListener;

    public static enum ConnectionState {
        CONNECTED, AUTHENTICATED, CONNECTING, DISCONNECTING, DISCONNECTED;
    }

    public static enum LoggedInState {
        LOGGED_IN, LOGGED_OUT;
    }

    private SmackConnection() {
        if (instance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
       // getConnectionListener();
        statusStream = PublishSubject.create();


    }

    public void createXmppConnection() {
        Single.fromCallable(() -> getXmppConfig(serverName))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((config) -> {
                    xmpptcpConnection = new XMPPTCPConnection(config);
                });
    }

    public static SmackConnection getInstance() {
        if (instance == null) {
            synchronized (SmackConnection.class) {
                if (instance == null) {
                    instance = new SmackConnection();
                }
            }
        }
        return instance;
    }

    public Observable<Integer> getStatusStream() {
        return statusStream;
    }

    public void setStatusStream(Integer statusStream) {
        this.statusStream.onNext(statusStream);
    }

    public XMPPTCPConnectionConfiguration getXmppConfig(String servername) {
        try {
            XMPPTCPConnectionConfiguration conf = XMPPTCPConnectionConfiguration.builder()
                    .setXmppDomain(servername)
                    .setHost(servername)
                    .setResource("android")

                    //Was facing this issue
                    //https://discourse.igniterealtime.org/t/connection-with-ssl-fails-with-java-security-keystoreexception-jks-not-found/62566
                    .setKeystoreType(null) //This line seems to get rid of the problem

                    .setSecurityMode(ConnectionConfiguration.SecurityMode.required)
                    .setCompressionEnabled(true).build();
            return conf;
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void connect(String userName, String password) throws IOException, XMPPException, SmackException {
        if (xmpptcpConnection != null) {
            xmpptcpConnection.addConnectionListener(connectionListener);
            Log.d("d1", "Calling connect() ");
            Maybe.fromCallable(() -> xmpptcpConnection.connect())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((config) -> {
                        xmpptcpConnection.login(userName, password, Resourcepart.fromOrThrowUnchecked("android"));
                        ReconnectionManager reconnectionManager = ReconnectionManager.getInstanceFor(xmpptcpConnection);
                        reconnectionManager.setEnabledPerDefault(true);
                        reconnectionManager.enableAutomaticReconnection();
                    }, throwable -> {
                        Log.d("d1", "error thgrow ");
                    });
            Log.d("d1", " login() Called ");
        }
    }


    public XMPPTCPConnection getXmpptcpConnection() {
        return xmpptcpConnection;
    }

//    private void getConnectionListener() {
//        connectionListener = new ConnectionListener() {
//            @Override
//            public void connected(XMPPConnection connection) {
//                SmackConnection.getInstance().setStatusStream(1);
//                RoosterConnectionService.sConnectionState = RoosterConnection.ConnectionState.CONNECTED;
//                Log.d(TAG, "Connected Successfully");
//            }
//
//            @Override
//            public void authenticated(XMPPConnection connection, boolean resumed) {
//                SmackConnection.getInstance().setStatusStream(2);
//                RoosterConnectionService.sConnectionState = RoosterConnection.ConnectionState.CONNECTED;
//                Log.d(TAG, "Authenticated Successfully");
//            }
//
//            @Override
//            public void connectionClosed() {
//                SmackConnection.getInstance().setStatusStream(3);
//                RoosterConnectionService.sConnectionState = RoosterConnection.ConnectionState.DISCONNECTED;
//                Log.d(TAG, "Connectionclosed()");
//                ReconnectionManager manager = ReconnectionManager.getInstanceFor(SmackConnection.getInstance().getXmpptcpConnection());
//                manager.enableAutomaticReconnection();
//            }
//
//            @Override
//            public void connectionClosedOnError(Exception e) {
//                SmackConnection.getInstance().setStatusStream(4);
//                RoosterConnectionService.sConnectionState = RoosterConnection.ConnectionState.DISCONNECTED;
//                Log.d(TAG, "ConnectionClosedOnError, error " + e.toString());
//
//            }
//        };
//    }

    public void sendMessage(ChatBot chatBot){

    }

}
