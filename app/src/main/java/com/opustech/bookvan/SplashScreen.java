package com.opustech.bookvan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.messaging.FirebaseMessaging;
import com.opustech.bookvan.ui.user.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.opustech.bookvan.ui.transport.DashboardActivity;

import java.util.HashMap;

public class SplashScreen extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference, partnersReference;

    private final String admin_uid = "yEali5UosERXD1wizeJGN87ffff2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        usersReference = firebaseFirestore.collection("users");
        partnersReference = firebaseFirestore.collection("partners");

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful()) {
                            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("token", task.getResult());
                                FirebaseFirestore.getInstance().collection("tokens")
                                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .set(hashMap);
                            }
                        }
                    }
                });

        if (firebaseAuth.getCurrentUser() != null) {
            if (firebaseAuth.getCurrentUser().getUid().equals(admin_uid)) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startAdminActivity();
                    }
                }, 1500);
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        checkUserAccountType();
                    }
                }, 1500);
            }
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startLoginActivity();
                }
            }, 1500);
        }
    }

    private void checkUserAccountType() {
        usersReference.document(firebaseAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            String account_type = task.getResult().getString("account_type");
                            if (account_type != null) {
                                if (account_type.equals("administrator")) {
                                    partnersReference.document(task.getResult().getString("transport_company"))
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        String uid = task.getResult().getString("uid");
                                                        String transport_name = task.getResult().getString("name");
                                                        startTransportAdminActivity(uid, transport_name);
                                                    }
                                                }
                                            });
                                } else if (account_type.equals("staff")) {
                                    partnersReference.document(task.getResult().getString("transport_company"))
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        String uid = task.getResult().getString("uid");
                                                        String transport_name = task.getResult().getString("name");
                                                        startTransportUserActivity(uid, transport_name);
                                                    }
                                                }
                                            });
                                }
                            } else {
                                startMainActivity();
                            }
                        }
                    }
                });
    }

    private void startLoginActivity() {
        Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void startMainActivity() {
        Intent intent = new Intent(SplashScreen.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void startAdminActivity() {
        Intent intent = new Intent(SplashScreen.this, com.opustech.bookvan.ui.admin.DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void startTransportAdminActivity(String uid, String transport_name) {
        Intent intent = new Intent(SplashScreen.this, DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("uid", uid);
        intent.putExtra("transport_name", transport_name);
        startActivity(intent);
        finish();
    }

    private void startTransportUserActivity(String uid, String transport_name) {
        Intent intent = new Intent(SplashScreen.this, DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("uid", uid);
        intent.putExtra("transport_name", transport_name);
        startActivity(intent);
        finish();
    }

}