package com.example.mykindergarten;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.regex.Pattern;

import Modules.User;

public class RegisterActivity extends AppCompatActivity {

    public String email, password, name, ageGroup;
    private SharedPreferences pref;
    private TextInputEditText txtEmail;
    private TextInputEditText txtPass;
    private TextInputLayout inputLayoutEmail;
    private TextInputLayout inputLayoutPass;
    private Button btnSignOrRegister;
    private TextView version;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mRefUsers = database.getReference().child("Users");
    private DatabaseReference mRefCodes = database.getReference().child("invitation_code");
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ConstraintLayout root;
    private DatabaseReference versionRef = database.getReference().child("version");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        root = findViewById(R.id.constrLayout);

        txtEmail = findViewById(R.id.txtEmail);
        txtPass = findViewById(R.id.txtPass);
        inputLayoutEmail = findViewById(R.id.email);
        inputLayoutPass = findViewById(R.id.password);
        btnSignOrRegister = findViewById(R.id.btnSignOrRegister);
        version = findViewById(R.id.txtVersion);

        versionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                version.setText(snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RegisterActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        //working with visualization of TextInputLayout
        txtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                inputLayoutEmail.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String string = s.toString();
                if (!Pattern.matches("^([a-z0-9_-]+\\.)*[a-z0-9_-]+@[a-z0-9_-]+(\\.[a-z0-9_-]+)*\\.[a-z]{2,6}$", string)){
                    inputLayoutEmail.setError("Некорректный E-mail");
                } else {
                    inputLayoutEmail.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txtPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                inputLayoutPass.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String string = s.toString();

                if (string.length() < 6){
                    inputLayoutPass.setError("Не менее 6 символов");
                } else {
                    inputLayoutPass.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnSignOrRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputLayoutEmail.getError() == null && inputLayoutPass.getError() == null){
                    email = txtEmail.getText().toString();
                    password = txtPass.getText().toString();
                } else {
                    Toast.makeText(RegisterActivity.this, "Некорректный Email или пароль", Toast.LENGTH_LONG).show();
                    return;
                }

                // checking the existence of an email
                mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        if (task.getResult().getSignInMethods().size() == 0){
                           // Toast.makeText(RegisterActivity.this, "not exist", Toast.LENGTH_LONG).show();
                            ShowRegisterWindow();
                        }else {
                            //Toast.makeText(RegisterActivity.this, "exist", Toast.LENGTH_LONG).show();
                            mAuth.signInWithEmailAndPassword(email, password)
                                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                        @Override
                                        public void onSuccess(AuthResult authResult) {
                                            startMainActivity();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Snackbar.make(root, "Ошибка авторизации. " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    //saving email and password after exit
    @Override
    protected void onPause() {
        super.onPause();
        txtEmail = findViewById(R.id.txtEmail);
        txtPass = findViewById(R.id.txtPass);
        pref = getPreferences(MODE_PRIVATE);


        SharedPreferences.Editor editor = pref.edit();
        editor.putString("email", txtEmail.getText().toString());
        editor.putString("pass", txtPass.getText().toString());
        editor.apply();

    }

    //saving email and password after exit
    @Override
    protected void onStart() {
        super.onStart();

        txtEmail = findViewById(R.id.txtEmail);
        txtPass = findViewById(R.id.txtPass);

        pref = getPreferences(MODE_PRIVATE);

        txtEmail.setText(pref.getString("email", ""));
        txtPass.setText(pref.getString("pass", ""));
    }

    private void ShowRegisterWindow(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Регистрация");
        dialog.setMessage("Введите данные для регистрации");

        LayoutInflater inflater = LayoutInflater.from(this);
        View register_window = inflater.inflate(R.layout.dialog_register, null);
        dialog.setView(register_window);

        TextInputEditText txtName = register_window.findViewById(R.id.txtName);
        TextInputEditText txtAgeGroup = register_window.findViewById(R.id.txtAgeGroup);
        TextInputEditText txtCode = register_window.findViewById(R.id.txtCode);
        ArrayList<String> codes = new ArrayList<>();


        mRefCodes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()) {
                    codes.add(ds.child("code").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        txtAgeGroup.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                txtAgeGroup.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String string = s.toString();
                int value = Integer.valueOf(string);

                if (value <= 0 && value > 5) txtAgeGroup.setError("Некорректный номер группы ");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        dialog.setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });

        dialog.setPositiveButton("Зарегистрироваться", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if (TextUtils.isEmpty(txtName.getText().toString())) {
                    Snackbar.make(root, "Введите ваше имя", Snackbar.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(txtAgeGroup.getText().toString()) || Integer.valueOf(txtAgeGroup.getText().toString()) <= 0 || Integer.valueOf(txtAgeGroup.getText().toString()) > 5) {
                    Snackbar.make(root, "Неправильный номер группы", Snackbar.LENGTH_LONG).show();
                    return;
                }

                if (!codes.contains(txtCode.getText().toString())){
                    Snackbar.make(root, "Неверный код", Snackbar.LENGTH_LONG).show();
                    return;
                }


                mAuth.createUserWithEmailAndPassword(txtEmail.getText().toString(), txtPass.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                User user = new User();
                                user.setEmail(email);
                                user.setPassword(password);
                                user.setName(txtName.getText().toString());
                                user.setAgeGroup(txtAgeGroup.getText().toString());
                                FirebaseUser us = FirebaseAuth.getInstance().getCurrentUser();
                                mRefUsers.child(us.getUid()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Snackbar.make(root, "Успешно", Snackbar.LENGTH_LONG).show();
                                        startMainActivity();
                                    }
                                });
                            }
                        });
            }
        });

        dialog.show();
    }

    private void startMainActivity(){
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    //Позже удалить методы для подсветки ввода неправильных значений в диалоговом окне регистрации. Все равно не работают
    //Так же сделать удаление введенного кода в бд
}