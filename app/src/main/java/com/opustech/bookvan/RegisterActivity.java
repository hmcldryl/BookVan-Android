package com.opustech.bookvan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference;
    private CollectionReference conversationsReference;

    private Button btnRegister;
    private TextInputEditText inputFirstName, inputLastName, inputEmail, inputContactNumber, inputPassword, inputConfirmPassword;
    private TextView btnLogin;

    private String currentUserID;

    // BookVan ADMIN UID
    private String admin_uid = "yEali5UosERXD1wizeJGN87ffff2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        usersReference = firebaseFirestore.collection("users");
        conversationsReference = firebaseFirestore.collection("conversations");

        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);

        inputFirstName = findViewById(R.id.inputFirstName);
        inputLastName = findViewById(R.id.inputLastName);
        inputEmail = findViewById(R.id.inputEmail);
        inputContactNumber = findViewById(R.id.inputContactNumber);
        inputPassword = findViewById(R.id.inputPassword);
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnRegister.setEnabled(false);
                onRegister(v);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();
                startActivity(intent);
            }
        });
    }

    // CHECK INPUTS & REGISTER NEW USER
    private void onRegister(View v) {
        final ACProgressFlower dialog = new ACProgressFlower.Builder(RegisterActivity.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(getResources().getColor(R.color.colorAccent))
                .text("Processing...")
                .fadeColor(Color.DKGRAY).build();
        dialog.show();

        String name = inputFirstName.getText().toString() + " " + inputLastName.getText().toString();
        String email = inputEmail.getText().toString().trim();
        String contact_number = inputContactNumber.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        String confirm_password = inputConfirmPassword.getText().toString().trim();

        // CHECK INPUTS
        if (name.isEmpty()) {
            dialog.dismiss();
            btnRegister.setEnabled(true);
            inputFirstName.setError("Please enter your first name.");
            inputLastName.setError("Please enter your last name.");
        } else if (email.isEmpty()) {
            dialog.dismiss();
            btnRegister.setEnabled(true);
            inputEmail.setError("Please enter your email.");
        } else if (contact_number.isEmpty()) {
            dialog.dismiss();
            btnRegister.setEnabled(true);
            inputContactNumber.setError("Please enter your email.");
        } else if (password.isEmpty()) {
            dialog.dismiss();
            btnRegister.setEnabled(true);
            inputPassword.setError("Please enter your password.");
        } else if (confirm_password.isEmpty()) {
            dialog.dismiss();
            btnRegister.setEnabled(true);
            inputConfirmPassword.setError("Please confirm your password.");
        } else if (!confirm_password.equals(password)) {
            dialog.dismiss();
            btnRegister.setEnabled(true);
            inputPassword.setError("Passwords does not match.");
            inputConfirmPassword.setError("Passwords does not match.");
        } else {
            // REGISTER NEW USER
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                uploadInfo(name, email, contact_number);
                            } else {
                                new MaterialAlertDialogBuilder(RegisterActivity.this)
                                        .setTitle("Sign up failed. Please check your internet connection and try again later.")
                                        .setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                                startMainActivity();
                                            }
                                        })
                                        .show();
                            }
                            dialog.dismiss();
                        }
                    });
        }
    }

    //UPLOAD USER INFO
    private void uploadInfo(String name, String email, String contact_number) {
        final ACProgressFlower dialog = new ACProgressFlower.Builder(RegisterActivity.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(getResources().getColor(R.color.white))
                .text("Uploading info...")
                .fadeColor(Color.DKGRAY).build();
        dialog.show();

        currentUserID = firebaseAuth.getCurrentUser().getUid();

        // UPLOAD USER INFO TO SERVER
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", name);
        hashMap.put("email", email);
        hashMap.put("contact_number", contact_number);
        usersReference.document(currentUserID)
                .set(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            // GET BookVan WELCOME MESSAGE
                            usersReference.document(admin_uid)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                // SEND WELCOME MESSAGE TO NEW USER
                                                String message = task.getResult().getString("welcome_message");
                                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                                                String timestamp = format.format(Calendar.getInstance().getTime());
                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("uid", admin_uid);
                                                hashMap.put("message", message);
                                                hashMap.put("timestamp", timestamp);
                                                conversationsReference.document(currentUserID)
                                                        .collection("chat")
                                                        .add(hashMap)
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                                if (task.isSuccessful()) {
                                                                    // UPDATE UID AND TIMESTAMP OF USER CONVERSATION
                                                                    HashMap<String, Object> hashMap = new HashMap<>();
                                                                    hashMap.put("uid", currentUserID);
                                                                    hashMap.put("timestamp", timestamp);
                                                                    conversationsReference.document(currentUserID)
                                                                            .set(hashMap)
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        dialog.dismiss();
                                                                                        btnRegister.setEnabled(true);
                                                                                        new MaterialAlertDialogBuilder(RegisterActivity.this)
                                                                                                .setTitle("You have signed up successfully to BookVan.")
                                                                                                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                                                                                                    @Override
                                                                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                                                                        dialogInterface.dismiss();
                                                                                                        startMainActivity();
                                                                                                    }
                                                                                                })
                                                                                                .show();
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        btnRegister.setEnabled(true);
                        new MaterialAlertDialogBuilder(RegisterActivity.this)
                                .setTitle("Sign up failed. Please check your internet connection and try again later.")
                                .setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                })
                                .show();
                    }
                });

    }

    // REDIRECT USER TO HOME ACTIVITY
    private void startMainActivity() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


}