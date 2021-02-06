package com.opustech.bookvan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.ui.AppBarConfiguration;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.opustech.bookvan.ui.book.BookFragment;
import com.opustech.bookvan.ui.chat.ChatFragment;
import com.opustech.bookvan.ui.contact.ContactFragment;
import com.opustech.bookvan.ui.home.HomeFragment;
import com.opustech.bookvan.ui.rent.RentFragment;
import com.opustech.bookvan.ui.schedule.ScheduleFragment;

import java.util.HashMap;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference;

    LinearLayout headerUser;
    CircleImageView headerUserPhoto;
    Button btnLogin, btnLoginFacebook, btnLoginGoogle;
    TextView headerUserName, headerUserEmail;
    NavigationView navigationView;

    int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseFirestore = FirebaseFirestore.getInstance();
        usersReference = firebaseFirestore.collection("users");

        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        View navView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_view);

        toolbar.setTitle("");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!drawerLayout.isOpen()) {
                    drawerLayout.open();
                } else {
                    drawerLayout.close();
                }
            }
        });

        headerUser = navView.findViewById(R.id.headerUser);
        headerUserPhoto = navView.findViewById(R.id.headerUserPhoto);
        headerUserName = navView.findViewById(R.id.headerUserName);
        headerUserEmail = navView.findViewById(R.id.headerUserEmail);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment;
                if (item.getItemId() == R.id.navigation_book) {
                    selectedFragment = new BookFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.drawer_nav_host_fragment, selectedFragment).commit();
                }
                if (item.getItemId() == R.id.navigation_rent) {
                    selectedFragment = new RentFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.drawer_nav_host_fragment, selectedFragment).commit();
                }
                if (item.getItemId() == R.id.navigation_schedule) {
                    selectedFragment = new ScheduleFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.drawer_nav_host_fragment, selectedFragment).commit();
                }
                if (item.getItemId() == R.id.navigation_chat) {
                    selectedFragment = new ChatFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.drawer_nav_host_fragment, selectedFragment).commit();
                }
                return true;
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment;
                if (item.getItemId() == R.id.nav_home) {
                    selectedFragment = new HomeFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.drawer_nav_host_fragment, selectedFragment).commit();
                    drawerLayout.close();
                }
                if (item.getItemId() == R.id.nav_schedule) {
                    selectedFragment = new ScheduleFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.drawer_nav_host_fragment, selectedFragment).commit();
                    drawerLayout.close();
                }
                if (item.getItemId() == R.id.nav_contact) {
                    selectedFragment = new ContactFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.drawer_nav_host_fragment, selectedFragment).commit();
                    drawerLayout.close();
                }
                if (item.getItemId() == R.id.nav_about) {
                    Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                    startActivity(intent);
                }
                if (item.getItemId() == R.id.btnLoginNav) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    final AlertDialog alertDialog = builder.create();
                    if (!alertDialog.isShowing()) {
                        final LayoutInflater inflater = getLayoutInflater();
                        final View dialogView = inflater.inflate(R.layout.dialog_login_layout, null);
                        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        alertDialog.setCancelable(true);
                        alertDialog.setView(dialogView);

                        TextInputLayout loginEmail = dialogView.findViewById(R.id.loginEmail);
                        TextInputLayout loginPassword = dialogView.findViewById(R.id.loginPassword);

                        btnLogin = dialogView.findViewById(R.id.btnLogin);
                        btnLoginFacebook = dialogView.findViewById(R.id.btnLoginFacebook);
                        btnLoginGoogle = dialogView.findViewById(R.id.btnLoginGoogle);

                        btnLogin.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                btnLogin.setEnabled(false);
                                if (loginEmail.getEditText().getText().toString().isEmpty()) {
                                    loginEmail.setError("Please enter a valid email.");
                                    btnLogin.setEnabled(true);
                                }
                                if (loginPassword.getEditText().getText().toString().isEmpty()) {
                                    loginPassword.setError("Please enter your password.");
                                    btnLogin.setEnabled(true);
                                } else {
                                    final ACProgressFlower dialog = new ACProgressFlower.Builder(MainActivity.this)
                                            .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                                            .themeColor(getResources().getColor(R.color.colorAccent))
                                            .text("Logging in...")
                                            .fadeColor(Color.DKGRAY).build();
                                    dialog.show();
                                    firebaseAuth.signInWithEmailAndPassword(
                                            loginEmail.getEditText().getText().toString(),
                                            loginPassword.getEditText().getText().toString())
                                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if (task.isSuccessful()) {
                                                        dialog.dismiss();
                                                        alertDialog.dismiss();
                                                    }
                                                }
                                            });
                                }
                            }
                        });

                        btnLoginGoogle.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent signInIntent = googleSignInClient.getSignInIntent();
                                startActivityForResult(signInIntent, RC_SIGN_IN);
                                alertDialog.dismiss();
                            }
                        });

                        btnLoginFacebook.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(MainActivity.this, "Facebook Login", Toast.LENGTH_LONG).show();
                                alertDialog.dismiss();
                            }
                        });

                        alertDialog.show();
                    }
                }
                if (item.getItemId() == R.id.btnRegisterNav) {
                    Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                    startActivity(intent);
                }
                if (item.getItemId() == R.id.btnLogout) {
                    firebaseAuth.signOut();
                    resetUi();
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity, menu);
        return true;
    }

    private void resetUi() {
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();
            if (!currentUserId.isEmpty()) {
                usersReference.document(currentUserId).addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            retrieveUserData();
                        }
                        if (value != null) {
                            if (value.exists()) {
                                String name = value.getString("name");
                                String email = value.getString("email");
                                String photo_url = value.getString("photo_url");
                                updateUi(name, email, photo_url);
                            }
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                final ACProgressFlower dialog = new ACProgressFlower.Builder(MainActivity.this)
                        .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                        .themeColor(getResources().getColor(R.color.colorAccent))
                        .text("Logging in...")
                        .fadeColor(Color.DKGRAY).build();
                dialog.show();
                firebaseAuth.signInWithCredential(credential)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    dialog.dismiss();
                                    Toast.makeText(MainActivity.this, "Google login successful.", Toast.LENGTH_LONG).show();
                                    uploadData(account);
                                } else {
                                    dialog.dismiss();
                                    Toast.makeText(MainActivity.this, "Google login failed.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            } catch (ApiException e) {
                Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void uploadData(GoogleSignInAccount account) {
        final ACProgressFlower dialog = new ACProgressFlower.Builder(MainActivity.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(getResources().getColor(R.color.colorAccent))
                .text("Processing...")
                .fadeColor(Color.DKGRAY).build();
        dialog.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        HashMap<String, Object> user = new HashMap<>();
        user.put("name", account.getGivenName() + " " + account.getFamilyName());
        user.put("email", account.getEmail());
        user.put("photo_url", account.getPhotoUrl().toString());
        if (firebaseAuth.getCurrentUser() != null) {
            String currentUserId = firebaseAuth.getCurrentUser().getUid();
            db.collection("users")
                    .document(currentUserId)
                    .set(user)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                dialog.dismiss();
                                retrieveUserData();
                            } else {
                                dialog.dismiss();
                            }
                        }
                    });
        } else {
            dialog.dismiss();
        }
    }

    private void retrieveUserData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserId = firebaseAuth.getCurrentUser().getUid();
        db.collection("users")
                .document(currentUserId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            String name = task.getResult().getString("name");
                            String email = task.getResult().getString("email");
                            String photo_url = task.getResult().getString("photo_url");
                            updateUi(name, email, photo_url);
                        }
                    }
                });
    }

    private void updateUi(String name, String email, String photo_url) {
        if (navigationView.getMenu().findItem(R.id.btnLoginNav).isVisible() && navigationView.getMenu().findItem(R.id.btnRegisterNav).isVisible() && !navigationView.getMenu().findItem(R.id.btnLogout).isVisible()) {
            navigationView.getMenu().findItem(R.id.btnLoginNav).setVisible(false);
            navigationView.getMenu().findItem(R.id.btnRegisterNav).setVisible(false);
            navigationView.getMenu().findItem(R.id.btnLogout).setVisible(true);
        }
        Glide.with(MainActivity.this)
                .load(photo_url)
                .into(headerUserPhoto);
        headerUserName.setText(name);
        headerUserEmail.setText(email);
        headerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Profile", Toast.LENGTH_LONG).show();
            }
        });
    }
}