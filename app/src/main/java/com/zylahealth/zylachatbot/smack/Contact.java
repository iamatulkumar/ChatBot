package com.zylahealth.zylachatbot.smack;

public class Contact {
    private String jid;

    public Contact(String contactJid )
    {
        jid = contactJid;
    }

    public String getJid()
    {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }
}
