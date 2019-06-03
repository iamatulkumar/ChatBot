package com.zylahealth.zylachatbot.realm.chat.model;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

public class ChatBot extends RealmObject {

    @Index
    @PrimaryKey
    private int id;

    private String message;

    private String time;

    private int sendReport;

    private int type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getSendReport() {
        return sendReport;
    }

    public void setSendReport(int sendReport) {
        this.sendReport = sendReport;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
