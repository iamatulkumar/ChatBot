package com.zylahealth.zylachatbot.smack;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class ContactModel {

    private static ContactModel sContactModel;
    private List<Contact> mContacts;

    public static ContactModel get(Context context)
    {
        if(sContactModel == null)
        {
            sContactModel = new ContactModel(context);
        }
        return  sContactModel;
    }

    private ContactModel(Context context)
    {
        mContacts = new ArrayList<>();
        populateWithInitialContacts(context);

    }

    public void addUser(Contact contact) {
        mContacts.add(contact);
    }

    private void populateWithInitialContacts(Context context)
    {
        //Create the Foods and add them to the list;

        Contact contact1 = new Contact("atul@chat.poc.zyla.in");
        mContacts.add(contact1);
        Contact contact2 = new Contact("mrigesh1@chat.poc.zyla.in");
        mContacts.add(contact2);
//        Contact contact3 = new Contact("mrigesh@chat.poc.zyla.in");
//        mContacts.add(contact3);
//        Contact contact4 = new Contact("testzyla2@chat.poc.zyla.in");
//        mContacts.add(contact4);
    }

    public List<Contact> getContacts()
    {
        return mContacts;
    }

}
