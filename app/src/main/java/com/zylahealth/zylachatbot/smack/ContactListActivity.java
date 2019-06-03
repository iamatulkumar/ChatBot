package com.zylahealth.zylachatbot.smack;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zylahealth.zylachatbot.R;

import java.util.List;

public class ContactListActivity extends AppCompatActivity {

    private static final String TAG = "ContactListActivity";

    private RecyclerView contactsRecyclerView;
    private ContactAdapter mAdapter;
    private Button button;
    private List<Contact> contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        contactsRecyclerView = (RecyclerView) findViewById(R.id.contact_list_recycler_view);
        contactsRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));

        button = findViewById(R.id.button_add);

        ContactModel model = ContactModel.get(getBaseContext());
        contacts = model.getContacts();

        mAdapter = new ContactAdapter(contacts);
        contactsRecyclerView.setAdapter(mAdapter);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog();
            }
        });
    }

    private void alertDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_alert, null);
        dialogBuilder.setView(dialogView);

        EditText editText = (EditText) dialogView.findViewById(R.id.etComments);
        Button btnAdd = dialogView.findViewById(R.id.button_add);


        editText.setText("Add chat user");
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(editText.getText().toString())) {
                    Contact contact1 = new Contact(editText.getText().toString());
                    contacts.add(contact1);
                    mAdapter.setmContacts(contacts);
                    mAdapter.notifyDataSetChanged();
                    alertDialog.cancel();
                }
            }
        });
    }

    private class ContactHolder extends RecyclerView.ViewHolder {
        private TextView contactTextView;
        private Contact mContact;

        public ContactHolder(View itemView) {
            super(itemView);

            contactTextView = (TextView) itemView.findViewById(R.id.contact_jid);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Inside here we start the chat activity
                    Intent intent = new Intent(ContactListActivity.this
                            , SmackActivity.class);
                    intent.putExtra("EXTRA_CONTACT_JID", mContact.getJid());
                    startActivity(intent);
                }
            });
        }

        public void bindContact(Contact contact) {
            mContact = contact;
            if (mContact == null) {
                Log.d(TAG, "Trying to work on a null Contact object ,returning.");
                return;
            }
            contactTextView.setText(mContact.getJid());
        }
    }


    private class ContactAdapter extends RecyclerView.Adapter<ContactHolder> {
        private List<Contact> mContacts;

        public ContactAdapter(List<Contact> contactList) {
            mContacts = contactList;
        }

        @Override
        public ContactHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater
                    .inflate(R.layout.list_item_contact, parent,
                            false);
            return new ContactHolder(view);
        }

        @Override
        public void onBindViewHolder(ContactHolder holder, int position) {
            Contact contact = mContacts.get(position);
            holder.bindContact(contact);

        }

        @Override
        public int getItemCount() {
            return mContacts.size();
        }

        public List<Contact> getmContacts() {
            return mContacts;
        }

        public void setmContacts(List<Contact> mContacts) {
            this.mContacts = mContacts;
        }
    }
}
