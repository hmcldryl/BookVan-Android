package com.opustech.bookvan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.opustech.bookvan.admin.AdminActivity;
import com.opustech.bookvan.transport.TransportCompanyAdminActivity;
import com.opustech.bookvan.transport.TransportLoginActivity;

public class SplashScreen extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference, partnersReference;

    private String admin_uid = "yEali5UosERXD1wizeJGN87ffff2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        usersReference = firebaseFirestore.collection("users");
        partnersReference = firebaseFirestore.collection("partners");

        if (firebaseAuth.getCurrentUser() != null) {
            if (firebaseAuth.getCurrentUser().getUid().equals(admin_uid)) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startAdminActivity();
                    }
                }, 1500);
            }
            else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        checkUserAccountType();
                    }
                }, 1500);
            }
        }
        else {
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
                            if (task.getResult().getString("account_type").equals("administrator")) {
                                partnersReference.whereEqualTo("name", task.getResult().getString("transport_company"))
                                        .limit(1)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    String uid = task.getResult().getDocuments().get(0).getString("uid");
                                                    startTransportAdminActivity(uid);
                                                }
                                            }
                                        });
                            }
                            else if (task.getResult().getString("account_type").equals("staff")) {
                                partnersReference.whereEqualTo("name", task.getResult().getString("transport_company"))
                                        .limit(1)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    String uid = task.getResult().getDocuments().get(0).getString("uid");
                                                    startTransportUserActivity(uid);
                                                }
                                            }
                                        });
                            }
                            else {
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
        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void startAdminActivity() {
        Intent intent = new Intent(SplashScreen.this, AdminActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void startTransportAdminActivity(String uid) {
        Intent intent = new Intent(SplashScreen.this, TransportCompanyAdminActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("uid", uid);
        startActivity(intent);
        finish();
    }

    private void startTransportUserActivity(String uid) {
        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("uid", uid);
        startActivity(intent);
        finish();
    }

}