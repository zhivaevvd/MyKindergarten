package com.example.mykindergarten.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.mykindergarten.MainActivity;
import com.example.mykindergarten.R;
import com.example.mykindergarten.RegisterActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import Modules.User;

public class HomeFragment extends Fragment {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference usersRef = database.getReference().child("Users");
    private FirebaseAuth mAuth;
    private Context context;
    private FirebaseUser mUser;
    private TextView name, email, ageGroup, password;
    private String Uid;
    private Button btnExit;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        context = getContext();

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        Uid = mUser.getUid();

        name = root.findViewById(R.id.tvName);
        password = root.findViewById(R.id.tvPass);
        email = root.findViewById(R.id.tvEmail);
        ageGroup = root.findViewById(R.id.tvAgeGroup);

        btnExit = root.findViewById(R.id.btnExit);

        usersRef.child(Uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = new User();
                user = snapshot.getValue(User.class);
                name.setText(name.getText() + user.getName());
                password.setText(password.getText() + user.getPassword());
                email.setText(email.getText() + user.getEmail());
                ageGroup.setText(ageGroup.getText() + user.getAgeGroup());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();

                Intent intent = new Intent(getActivity(), RegisterActivity.class);

                startActivity(intent);
            }
        });

        return root;
    }
}