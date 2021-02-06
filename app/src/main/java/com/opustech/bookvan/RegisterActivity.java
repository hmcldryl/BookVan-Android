package com.opustech.bookvan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    Button btnRegister;
    TextInputLayout registerName, registerEmail, registerPassword, registerConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        btnRegister = findViewById(R.id.btnRegister);

        registerName = findViewById(R.id.registerFullName);
        registerEmail = findViewById(R.id.registerEmail);
        registerPassword = findViewById(R.id.registerPassword);
        registerConfirmPassword = findViewById(R.id.registerConfirmPassword);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnRegister.setEnabled(false);
                final ACProgressFlower dialog = new ACProgressFlower.Builder(RegisterActivity.this)
                        .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                        .themeColor(getResources().getColor(R.color.colorAccent))
                        .text("Processing...")
                        .fadeColor(Color.DKGRAY).build();
                dialog.show();
                if (registerName.getEditText().getText().toString().isEmpty()) {
                    dialog.dismiss();
                    registerName.setError("Please enter your name.");
                    btnRegister.setEnabled(true);
                }
                if (registerEmail.getEditText().getText().toString().isEmpty()) {
                    dialog.dismiss();
                    registerEmail.setError("Please enter your email.");
                    btnRegister.setEnabled(true);
                }
                if (registerPassword.getEditText().getText().toString().isEmpty()) {
                    dialog.dismiss();
                    registerPassword.setError("Please enter your password.");
                    btnRegister.setEnabled(true);
                }
                if (registerConfirmPassword.getEditText().getText().toString().isEmpty()) {
                    dialog.dismiss();
                    registerConfirmPassword.setError("Please confirm your password.");
                    btnRegister.setEnabled(true);
                }
                if (!registerConfirmPassword.getEditText().getText().toString().equals(registerPassword.getEditText().getText().toString())) {
                    dialog.dismiss();
                    registerPassword.setError("Passwords does not match.");
                    registerConfirmPassword.setError("Passwords does not match.");
                    btnRegister.setEnabled(true);
                }
                else {
                    firebaseAuth.createUserWithEmailAndPassword(registerEmail.getEditText().getText().toString().trim(), registerPassword.getEditText().getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        HashMap<String, Object> user = new HashMap<>();
                                        user.put("name", registerName.getEditText().getText().toString());
                                        user.put("email", registerEmail.getEditText().getText().toString());
                                        db.collection("users")
                                                .add(user)
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        dialog.dismiss();
                                                        btnRegister.setEnabled(true);
                                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(RegisterActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                    else {
                                        dialog.dismiss();
                                        btnRegister.setEnabled(true);
                                    }
                                }
                            });
                }
            }
        });
    }


}