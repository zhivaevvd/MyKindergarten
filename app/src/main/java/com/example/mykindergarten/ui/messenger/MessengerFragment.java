package com.example.mykindergarten.ui.messenger;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.mykindergarten.R;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import Modules.Message;


public class MessengerFragment extends Fragment {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference messagesRef = database.getReference().child("Messages");
    private DatabaseReference usersRef = database.getReference().child("Users");
    private FloatingActionButton sendButton;
    private FirebaseAuth mAuth;
    private String name;
    private ListView listView;
    private Context context;
    private FirebaseUser mUser;
    private FirebaseListAdapter<Message> adapter;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_messenger, container, false);
        listView = root.findViewById(R.id.messages_list);
        context = getContext();
        sendButton = root.findViewById(R.id.btnSend);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        String Uid = mUser.getUid();

        usersRef.child(Uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name = snapshot.child("name").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


        adapter = new FirebaseListAdapter<Message>((Activity) context, Message.class, R.layout.list_item, messagesRef) {
            @Override
            protected void populateView(View v, Message model, int position) {
                TextView messUser, messTime, messText;
                messUser = v.findViewById(R.id.message_user);
                messTime = v.findViewById(R.id.message_time);
                messText = v.findViewById(R.id.message_text);

                messUser.setText(model.getUserName());
                messTime.setText(DateFormat.format("dd.mm.yyyy HH:MM", model.getMessageTime()));
                messText.setText(model.getTextMessage());
            }
        };

        listView.setAdapter(adapter);


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputEditText textField = root.findViewById(R.id.messageField);
                if (textField.getText().toString().trim().isEmpty())
                    return;

                messagesRef.push().setValue(new Message(name, textField.getText().toString().trim()));

                textField.setText("");
            }
        });

        return root;
    }


}