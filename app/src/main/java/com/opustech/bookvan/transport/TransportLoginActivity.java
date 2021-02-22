package com.opustech.bookvan.transport;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.opustech.bookvan.MainActivity;
import com.opustech.bookvan.R;
import com.opustech.bookvan.admin.AdminActivity;

import java.util.ArrayList;
import java.util.Arrays;

public class TransportLoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference;

    private TextInputLayout inputTransportName, inputAccountType, inputEmail, inputPassword;
    private MaterialButton btnLogin;
    private AutoCompleteTextView inputTransportNameACT, inputAccountTypeACT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport_login);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        usersReference = firebaseFirestore.collection("users");

        inputTransportName = findViewById(R.id.inputTransportName);
        inputAccountType = findViewById(R.id.inputAccountType);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);

        btnLogin = findViewById(R.id.btnLogin);

        inputTransportNameACT = findViewById(R.id.inputTransportNameACT);
        inputAccountTypeACT = findViewById(R.id.inputAccountTypeACT);

        populateTransportNameList();
        populateAccountTypeList();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableInput();
                inputCheck();
            }
        });
    }

    private void inputCheck() {
        String transport_name = inputTransportName.getEditText().getText().toString();
        String account_type = inputAccountType.getEditText().getText().toString();
        String email = inputEmail.getEditText().getText().toString();
        String password = inputPassword.getEditText().getText().toString();

        if (transport_name.isEmpty()) {
            enableInput();
            inputTransportName.setError("Please select your transport company.");
        } else if (account_type.isEmpty()) {
            enableInput();
            inputAccountType.setError("Please select your account type.");
        } else if (email.isEmpty()) {
            enableInput();
            inputEmail.setError("Please enter your email.");
        } else if (password.isEmpty()) {
            enableInput();
            inputEmail.setError("Please select your transport company.");
        } else {
            onLogin(transport_name, account_type, email, password);
        }
    }

    private void disableInput() {
        btnLogin.setEnabled(false);
        inputTransportName.setEnabled(false);
        inputAccountType.setEnabled(false);
        inputEmail.setEnabled(false);
        inputPassword.setEnabled(false);
    }

    private void enableInput() {
        btnLogin.setEnabled(true);
        inputTransportName.setEnabled(true);
        inputAccountType.setEnabled(true);
        inputEmail.setEnabled(true);
        inputPassword.setEnabled(true);
    }

    private void onLogin(String transport_name, String account_type, String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            checkUserSession(transport_name, account_type);
                        }
                    }
                });
    }

    private void checkUserSession(String transport_name, String account_type) {
        if (firebaseAuth.getCurrentUser() != null) {
            usersReference.document(firebaseAuth.getCurrentUser().getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                validateUserInfo(task, transport_name, account_type);
                            }
                        }
                    });
        }
    }

    private void validateUserInfo(Task<DocumentSnapshot> task, String transport_name, String account_type) {
        if (transport_name.equals(task.getResult().getString("transport_company"))) {
            if (account_type.equals(task.getResult().getString("account_type"))) {
                if (account_type.toLowerCase().equals("administrator")) {
                    startAdminActivity();
                } else {
                    startUserActivity();
                }
            } else {
                firebaseAuth.signOut();
                enableInput();
                new MaterialAlertDialogBuilder(this)
                        .setTitle("Sign in failed. Please check your account type and try again.")
                        .setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
            }
        } else {
            firebaseAuth.signOut();
            enableInput();
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Sign in failed. Please check the name of transport company you belong to and try again.")
                    .setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .show();
        }
    }

    private void startAdminActivity() {
        Intent intent = new Intent(TransportLoginActivity.this, AdminActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
        startActivity(intent);
    }

    private void startUserActivity() {
        Intent intent = new Intent(TransportLoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
        startActivity(intent);
    }

    private void populateTransportNameList() {
        ArrayList<String> transportNameList = new ArrayList<>();
        usersReference.whereEqualTo("account_type", "transport_company")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (int i = 0; i < task.getResult().getDocuments().size(); i++) {
                                transportNameList.add(i, task.getResult().getDocuments().get(i).getString("name"));
                            }
                            ArrayAdapter<String> transportNameAdapter = new ArrayAdapter<>(TransportLoginActivity.this, R.layout.support_simple_spinner_dropdown_item, transportNameList);
                            inputTransportNameACT.setAdapter(transportNameAdapter);
                            inputTransportNameACT.setThreshold(2);
                        }
                    }
                });
    }

    private void populateAccountTypeList() {
        ArrayList<String> accountTypeArray = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.account_type)));
        ArrayAdapter<String> accountTypeAdapter = new ArrayAdapter<>(TransportLoginActivity.this, R.layout.support_simple_spinner_dropdown_item, accountTypeArray);
        inputAccountTypeACT.setAdapter(accountTypeAdapter);
        inputAccountTypeACT.setThreshold(1);
    }
}