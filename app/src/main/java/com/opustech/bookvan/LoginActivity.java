package com.opustech.bookvan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.opustech.bookvan.ui.admin.AdminActivity;
import com.opustech.bookvan.ui.transport.TransportCompanyAdminActivity;

import java.util.HashMap;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference;
    private GoogleSignInClient googleSignInClient;

    private TextInputEditText inputEmail, inputPassword;
    private MaterialButton btnLogin, btnLoginFacebook, btnLoginGoogle;
    private TextView btnRegister;

    private int RC_SIGN_IN = 1;

    private String admin_uid = "yEali5UosERXD1wizeJGN87ffff2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        usersReference = firebaseFirestore.collection("users");

        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);

        btnLogin = findViewById(R.id.btnLogin);
        btnLoginFacebook = findViewById(R.id.btnLoginFacebook);
        btnLoginGoogle = findViewById(R.id.btnLoginGoogle);
        btnRegister = findViewById(R.id.btnRegister);

        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableInput();
                inputCheck();
            }
        });

        btnLoginFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "This feature is not yet implemented.", Snackbar.LENGTH_SHORT).show();
            }
        });

        btnLoginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableInput();
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void inputCheck() {
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        if (email.isEmpty()) {
            enableInput();
            inputEmail.setError("Please enter a valid email address.");
        }
        if (password.isEmpty()) {
            enableInput();
            inputPassword.setError("Please enter your password.");
        } else {
            onLogin(email, password);
        }
    }

    private void disableInput() {
        btnLogin.setEnabled(false);
        btnRegister.setEnabled(false);
        btnLoginGoogle.setEnabled(false);
        btnLoginFacebook.setEnabled(false);
        inputEmail.setEnabled(false);
        inputPassword.setEnabled(false);
    }

    private void enableInput() {
        btnLogin.setEnabled(true);
        btnRegister.setEnabled(true);
        btnLoginGoogle.setEnabled(true);
        btnLoginFacebook.setEnabled(true);
        inputEmail.setEnabled(true);
        inputPassword.setEnabled(true);
    }

    private void onLogin(String email, String password) {
        final ACProgressFlower dialog = new ACProgressFlower.Builder(LoginActivity.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(getResources().getColor(R.color.white))
                .text("Signing in...")
                .fadeColor(Color.DKGRAY).build();
        dialog.show();
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            checkUserSession(dialog);
                        } else {
                            dialog.dismiss();
                            enableInput();
                            Toast.makeText(LoginActivity.this, "Sign in failed. Please check your sign in info and try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            final ACProgressFlower dialog = new ACProgressFlower.Builder(LoginActivity.this)
                    .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                    .themeColor(getResources().getColor(R.color.white))
                    .text("Signing in...")
                    .fadeColor(Color.DKGRAY).build();
            dialog.show();
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                firebaseAuth.signInWithCredential(credential)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    if (firebaseAuth.getCurrentUser() != null) {
                                        usersReference.document(firebaseAuth.getCurrentUser().getUid())
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            if (!task.getResult().exists()) {
                                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                                hashMap.put("name", account.getGivenName() + " " + account.getFamilyName());
                                                                hashMap.put("email", account.getEmail());
                                                                hashMap.put("photo_url", account.getPhotoUrl());
                                                                usersReference.document(firebaseAuth.getCurrentUser().getUid())
                                                                        .set(hashMap)
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    checkUserSession(dialog);
                                                                                }
                                                                            }
                                                                        });
                                                            } else {
                                                                checkUserSession(dialog);
                                                            }
                                                        }
                                                    }
                                                });
                                    }
                                }
                            }
                        });
            } catch (ApiException e) {
                dialog.dismiss();
                enableInput();
                Toast.makeText(this, "Google sign in failed. Please check your internet connection and try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkUserSession(ACProgressFlower dialog) {
        if (firebaseAuth.getCurrentUser() != null) {
            if (firebaseAuth.getCurrentUser().getUid().equals(admin_uid)) {
                dialog.dismiss();
                enableInput();
                startAdminActivity();
            } else {
                validateUserInfo(dialog);
            }
        } else {
            dialog.dismiss();
            enableInput();
            Toast.makeText(this, "Sign in failed. Please check your internet connection and try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void validateUserInfo(ACProgressFlower dialog) {
        usersReference.document(firebaseAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().getString("account_type") != null) {
                                if (task.getResult().getString("account_type").equals("administrator")) {
                                    dialog.dismiss();
                                    enableInput();
                                    startTransportAdminActivity(task.getResult().getString("transport_company"));
                                } else if (task.getResult().getString("account_type").equals("staff")) {
                                    dialog.dismiss();
                                    enableInput();
                                    startTransportUserActivity(task.getResult().getString("transport_company"));
                                }
                            } else {
                                dialog.dismiss();
                                enableInput();
                                startUserActivity();
                            }
                        }
                    }
                });
    }

    private void startTransportAdminActivity(String uid) {
        Intent intent = new Intent(LoginActivity.this, TransportCompanyAdminActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("uid", uid);
        startActivity(intent);
        finish();
    }

    private void startTransportUserActivity(String uid) {
        Intent intent = new Intent(LoginActivity.this, TransportCompanyAdminActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("uid", uid);
        startActivity(intent);
        finish();
    }

    private void startAdminActivity() {
        Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void startUserActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}