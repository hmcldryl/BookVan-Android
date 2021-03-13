package com.opustech.bookvan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.opustech.bookvan.ui.user.UserHomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
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
    private CollectionReference usersReference, conversationsReference;
    private DocumentReference systemReference;

    private TextInputLayout inputFirstName, inputLastName, inputEmail, inputContactNumber, inputPassword, inputConfirmPassword;
    private MaterialButton btnRegister;
    private TextView btnLogin;

    private final String admin_uid = "yEali5UosERXD1wizeJGN87ffff2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        usersReference = firebaseFirestore.collection("users");
        conversationsReference = firebaseFirestore.collection("conversations");
        systemReference = firebaseFirestore.collection("system").document("data");

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
                disableInput();
                inputCheck();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void enableInput() {
        btnRegister.setEnabled(true);
        inputFirstName.setEnabled(true);
        inputLastName.setEnabled(true);
        inputEmail.setEnabled(true);
        inputContactNumber.setEnabled(true);
        inputPassword.setEnabled(true);
        inputConfirmPassword.setEnabled(true);
    }

    private void disableInput() {
        btnRegister.setEnabled(false);
        inputFirstName.setEnabled(false);
        inputLastName.setEnabled(false);
        inputEmail.setEnabled(false);
        inputContactNumber.setEnabled(false);
        inputPassword.setEnabled(false);
        inputConfirmPassword.setEnabled(false);
    }

    private void inputCheck() {
        String name = inputFirstName.getEditText().getText().toString() + " " + inputLastName.getEditText().getText().toString();
        String email = inputEmail.getEditText().getText().toString().trim();
        String contact_number = inputContactNumber.getEditText().getText().toString().trim();
        String password = inputPassword.getEditText().getText().toString().trim();
        String confirm_password = inputConfirmPassword.getEditText().getText().toString().trim();

        if (name.isEmpty()) {
            enableInput();
            inputFirstName.setError("Please enter your first name.");
            inputLastName.setError("Please enter your last name.");
        } else if (email.isEmpty()) {
            enableInput();
            inputEmail.setError("Please enter your email.");
        } else if (contact_number.isEmpty()) {
            enableInput();
            inputContactNumber.setError("Please enter your email.");
        } else if (password.isEmpty()) {
            enableInput();
            inputPassword.setError("Please enter your password.");
        } else if (confirm_password.isEmpty()) {
            enableInput();
            inputConfirmPassword.setError("Please confirm your password.");
        } else if (!confirm_password.equals(password)) {
            enableInput();
            inputPassword.setError("Passwords does not match.");
            inputConfirmPassword.setError("Passwords does not match.");
        } else {
            // REGISTER NEW USER
            onRegister(name, contact_number, email, password);
        }
    }

    private void onRegister(String name, String contact_number, String email, String password) {
        final ACProgressFlower dialog = new ACProgressFlower.Builder(RegisterActivity.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(getResources().getColor(R.color.colorAccent))
                .text("Processing...")
                .fadeColor(Color.DKGRAY).build();
        dialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            uploadInfo(dialog, name, email, contact_number);
                        } else {
                            dialog.dismiss();
                            enableInput();
                            Toast.makeText(RegisterActivity.this, "Sign up failed. " + task.getException().getMessage() , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void uploadInfo(ACProgressFlower dialog, String name, String email, String contact_number) {
        // UPLOAD USER INFO TO SERVER
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", name);
        hashMap.put("email", email);
        hashMap.put("contact_number", contact_number);
        usersReference.document(firebaseAuth.getCurrentUser().getUid())
                .set(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            if (firebaseAuth.getCurrentUser() != null) {
                                sendWelcomeMessage(dialog);
                            }
                        }
                    }
                });
    }

    private void sendWelcomeMessage(ACProgressFlower dialog) {
        systemReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            // SEND WELCOME MESSAGE TO NEW USER
                            String message = task.getResult().getString("welcome_message");
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("uid", admin_uid);
                            hashMap.put("message", message);
                            hashMap.put("timestamp", generateTimestamp());
                            conversationsReference.document(firebaseAuth.getCurrentUser().getUid())
                                    .collection("chat")
                                    .add(hashMap)
                                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            if (task.isSuccessful()) {
                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("uid", firebaseAuth.getCurrentUser().getUid());
                                                hashMap.put("timestamp", generateTimestamp());
                                                conversationsReference.document(firebaseAuth.getCurrentUser().getUid())
                                                        .set(hashMap)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    dialog.dismiss();
                                                                    btnRegister.setEnabled(true);
                                                                    Toast.makeText(RegisterActivity.this, "You have signed up successfully to BookVan.", Toast.LENGTH_SHORT).show();
                                                                    startMainActivity();
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

    private String generateTimestamp() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        return format.format(Calendar.getInstance().getTime());
    }

    private void startMainActivity() {
        Intent intent = new Intent(RegisterActivity.this, UserHomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}