package com.opustech.bookvan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.auth.FacebookAuthProvider;
import com.opustech.bookvan.model.UserAccount;
import com.opustech.bookvan.ui.user.HomeActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.opustech.bookvan.ui.transport.DashboardActivity;

import org.imaginativeworld.whynotimagecarousel.CarouselItem;
import org.imaginativeworld.whynotimagecarousel.ImageCarousel;

import java.util.ArrayList;
import java.util.List;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference;
    private DocumentReference systemReference;
    private GoogleSignInClient googleSignInClient;

    private TextInputLayout inputEmail, inputPassword;
    private MaterialButton btnLogin, btnLoginFacebook, btnLoginGoogle;
    private TextView btnRegister, btnForgotPassword;
    private ImageCarousel imageCarousel;

    private LoginButton loginButton;
    private CallbackManager callbackManager;

    private final int RC_SIGN_IN = 1;

    private final String admin_uid = "yEali5UosERXD1wizeJGN87ffff2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        usersReference = firebaseFirestore.collection("users");
        systemReference = firebaseFirestore.collection("system").document("data");

        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);

        imageCarousel = findViewById(R.id.carousel);

        btnForgotPassword = findViewById(R.id.btnForgotPassword);

        btnLogin = findViewById(R.id.btnLogin);
        loginButton = findViewById(R.id.loginButton);
        btnLoginFacebook = findViewById(R.id.btnLoginFacebook);
        btnLoginGoogle = findViewById(R.id.btnLoginGoogle);
        btnRegister = findViewById(R.id.btnRegister);

        updateUi();

        callbackManager = CallbackManager.Factory.create();
        loginButton.setPermissions("email", "public_profile");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                facebookLogin(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                enableInput();
                Toast.makeText(LoginActivity.this, "Facebook sign in cancelled.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                enableInput();
                Toast.makeText(LoginActivity.this, "Facebook sign in failed. Please check your internet connection and try again. " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

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
                disableInput();
                loginButton.performClick();
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
            }
        });

        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                final AlertDialog alertDialog = builder.create();
                if (!alertDialog.isShowing()) {
                    final LayoutInflater inflater = getLayoutInflater();
                    final View dialogView = inflater.inflate(R.layout.dialog_forgot_password, null);
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alertDialog.setCancelable(true);
                    alertDialog.setView(dialogView);

                    TextInputLayout inputEmail = dialogView.findViewById(R.id.inputEmail);
                    MaterialButton btnConfirm = dialogView.findViewById(R.id.btnConfirm);

                    btnConfirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String email = inputEmail.getEditText().getText().toString();
                            firebaseAuth.sendPasswordResetEmail(email)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                alertDialog.dismiss();
                                                Toast.makeText(LoginActivity.this, "An email has been sent to " + email + " with details regarding your request to reset your password.", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        }
                    });
                    alertDialog.show();
                }
            }
        });
    }

    private void updateUi() {
        systemReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> photo_url = (List<String>) task.getResult().get("login_image_carousel_urls");
                            if (photo_url != null) {
                                List<CarouselItem> data = new ArrayList<>();
                                for (int i = 0; i < photo_url.size(); i++) {
                                    data.add(new CarouselItem(photo_url.get(i)));
                                }
                                imageCarousel.addData(data);
                            } else {
                                imageCarousel.setVisibility(View.GONE);
                            }
                        }
                    }
                });
    }

    private void inputCheck() {
        String email = inputEmail.getEditText().getText().toString().trim();
        String password = inputPassword.getEditText().getText().toString().trim();

        if (email.isEmpty()) {
            enableInput();
            inputEmail.setError("Please enter a valid email address.");
        } else if (password.isEmpty()) {
            enableInput();
            inputPassword.setError("Please enter your password.");
        } else {
            onLogin(email, password);
        }
    }

    private void disableInput() {
        btnLogin.setEnabled(false);
        btnLoginGoogle.setEnabled(false);
        btnLoginFacebook.setEnabled(false);
        inputEmail.setEnabled(false);
        inputPassword.setEnabled(false);
        btnRegister.setEnabled(false);
    }

    private void enableInput() {
        btnLogin.setEnabled(true);
        btnLoginGoogle.setEnabled(true);
        btnLoginFacebook.setEnabled(true);
        inputEmail.setEnabled(true);
        inputPassword.setEnabled(true);
        btnRegister.setEnabled(true);
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
                            Toast.makeText(LoginActivity.this, "Sign in failed. Please check your sign in info and try again. ", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void facebookLogin(AccessToken token) {
        final ACProgressFlower dialog = new ACProgressFlower.Builder(LoginActivity.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(getResources().getColor(R.color.white))
                .text("Signing in...")
                .fadeColor(Color.DKGRAY).build();
        dialog.show();
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (task.isSuccessful() && firebaseAuth.getCurrentUser() != null) {
                                usersReference.document(firebaseAuth.getCurrentUser().getUid())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    if (!task.getResult().exists()) {
                                                        UserAccount userAccount = new UserAccount(firebaseAuth.getCurrentUser().getDisplayName(), firebaseAuth.getCurrentUser().getEmail(), firebaseAuth.getCurrentUser().getPhoneNumber(), 0);
                                                        usersReference.document(firebaseAuth.getCurrentUser().getUid())
                                                                .set(userAccount)
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            checkUserSession(dialog);
                                                                        } else {
                                                                            dialog.dismiss();
                                                                            enableInput();
                                                                            Toast.makeText(LoginActivity.this, "Failed", Toast.LENGTH_SHORT).show();
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
                        } else {
                            dialog.dismiss();
                            enableInput();
                            Toast.makeText(LoginActivity.this, "Facebook sign in failed. Please check your internet connection and try again. " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d("DEBUG", task.getException().getMessage());
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN && data != null) {
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
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful() && firebaseAuth.getCurrentUser() != null) {
                                    usersReference.document(firebaseAuth.getCurrentUser().getUid())
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        if (!task.getResult().exists()) {
                                                            UserAccount userAccount = new UserAccount(account.getGivenName() + " " + account.getFamilyName(), account.getEmail(), 0);
                                                            usersReference.document(firebaseAuth.getCurrentUser().getUid())
                                                                    .set(userAccount)
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
                        });
            } catch (ApiException e) {
                dialog.dismiss();
                enableInput();
                Toast.makeText(this, "Google sign in failed. Please check your internet connection and try again. " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
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
                                    firebaseFirestore.collection("partners")
                                            .document(task.getResult().getString("transport_company"))
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        if (task.getResult().getBoolean("account_disabled") != null) {
                                                            if (!task.getResult().getBoolean("account_disabled")) {
                                                                startTransportAdminActivity(task.getResult().getString("uid"));
                                                            } else {
                                                                if (firebaseAuth.getCurrentUser() != null) {
                                                                    firebaseAuth.signOut();
                                                                    final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                                                    final AlertDialog alertDialog = builder.create();
                                                                    if (!alertDialog.isShowing()) {
                                                                        final LayoutInflater inflater = getLayoutInflater();
                                                                        final View dialogView = inflater.inflate(R.layout.dialog_account_disabled, null);
                                                                        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                                        alertDialog.setCancelable(true);
                                                                        alertDialog.setView(dialogView);

                                                                        alertDialog.show();
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            });
                                    dialog.dismiss();
                                    enableInput();
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
        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("uid", uid);
        startActivity(intent);
        finish();
    }

    private void startTransportUserActivity(String uid) {
        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("uid", uid);
        startActivity(intent);
        finish();
    }

    private void startAdminActivity() {
        Intent intent = new Intent(LoginActivity.this, com.opustech.bookvan.ui.admin.DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void startUserActivity() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}