package com.opustech.bookvan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference;
    private GoogleSignInClient googleSignInClient;

    private TextInputEditText inputEmail, inputPassword;
    private MaterialButton btnLogin, btnLoginFacebook, btnLoginGoogle;
    private TextView btnRegister;

    private int RC_SIGN_IN = 1;

    private String admin_uid = "btLTtUYnMuWvkrJspvKqZIirLce2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);

        btnLogin = findViewById(R.id.btnLogin);
        btnLoginFacebook = findViewById(R.id.btnLoginFacebook);
        btnLoginGoogle = findViewById(R.id.btnLoginGoogle);
        btnRegister = findViewById(R.id.btnRegister);

        firebaseFirestore = FirebaseFirestore.getInstance();
        usersReference = firebaseFirestore.collection("users");

        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                if (email.isEmpty()) {
                    inputEmail.setError("Please enter a valid email address.");
                }
                if (password.isEmpty()) {
                    inputPassword.setError("Please enter your password.");
                } else {
                    onLogin(email, password);
                }
            }
        });

        btnLoginFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "Facebook Login", Toast.LENGTH_LONG).show();
            }
        });

        btnLoginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
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
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                dialog.dismiss();
                                String currentUserId = firebaseUser.getUid();
                                if (currentUserId.equals(admin_uid)) {
                                    Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    finish();
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                    startActivity(intent);
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                } else {
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    finish();
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                    startActivity(intent);
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                }
                            }
                        } else {
                            dialog.dismiss();
                            Snackbar.make(btnLogin, "Sign in failed. Please try again.", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                final ACProgressFlower dialog = new ACProgressFlower.Builder(LoginActivity.this)
                        .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                        .themeColor(getResources().getColor(R.color.colorAccent))
                        .bgColor(getResources().getColor(R.color.colorPrimary))
                        .fadeColor(getResources().getColor(R.color.colorPrimaryDark))
                        .text("Signing in...")
                        .fadeColor(Color.DKGRAY).build();
                dialog.show();
                firebaseAuth.signInWithCredential(credential)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    HashMap<String, Object> user = new HashMap<>();
                                    user.put("name", account.getGivenName() + " " + account.getFamilyName());
                                    user.put("email", account.getEmail());
                                    user.put("photo_url", account.getPhotoUrl().toString());
                                    if (firebaseAuth.getCurrentUser() != null) {
                                        String currentUserId = firebaseAuth.getCurrentUser().getUid();
                                        usersReference.document(currentUserId)
                                                .set(user)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            dialog.dismiss();
                                                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                                            if (firebaseUser != null) {
                                                                dialog.dismiss();
                                                                String currentUserId = firebaseUser.getUid();
                                                                if (currentUserId.equals(admin_uid)) {
                                                                    Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                    finish();
                                                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                                                    startActivity(intent);
                                                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                                                } else {
                                                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                    finish();
                                                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                                                    startActivity(intent);
                                                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                                                }
                                                            }

                                                        } else {
                                                            dialog.dismiss();
                                                            Snackbar.make(btnLoginGoogle, "Google sign in failed.", Snackbar.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    } else {
                                        dialog.dismiss();
                                    }
                                } else {
                                    dialog.dismiss();
                                    Snackbar.make(btnLoginGoogle, "Google sign in failed.", Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        });
            } catch (ApiException e) {
                Log.d("ERROR", e.getMessage());
                Snackbar.make(btnLoginGoogle, "Google sign in failed.", Snackbar.LENGTH_SHORT).show();
            }
        }
    }


}