package com.opustech.bookvan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.opustech.bookvan.ui.fragments.BookingsFragment;
import com.opustech.bookvan.ui.fragments.ContactAdminFragment;
import com.opustech.bookvan.ui.fragments.HomeAdminFragment;
import com.opustech.bookvan.ui.fragments.ProfileFragment;
import com.opustech.bookvan.ui.fragments.RentalsFragment;
import com.opustech.bookvan.ui.fragments.ScheduleAdminFragment;
import com.opustech.bookvan.ui.fragments.VanCompanyFragment;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference;

    private CircleImageView headerUserPhoto;
    private TextView headerUserName, headerUserEmail;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        usersReference = firebaseFirestore.collection("users");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        drawerLayout = findViewById(R.id.drawer_layout);
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

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.btnChat) {
                    Intent intent = new Intent(AdminActivity.this, ChatActivity.class);
                    startActivity(intent);
                }
                return false;
            }
        });

        headerUserPhoto = navView.findViewById(R.id.headerUserPhoto);
        headerUserName = navView.findViewById(R.id.headerUserName);
        headerUserEmail = navView.findViewById(R.id.headerUserEmail);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_bookings) {
                    replaceFragment(BookingsFragment.class);
                    drawerLayout.close();
                }
                if (item.getItemId() == R.id.nav_rentals) {
                    replaceFragment(RentalsFragment.class);
                    drawerLayout.close();
                }
                if (item.getItemId() == R.id.nav_schedule) {
                    replaceFragment(ScheduleAdminFragment.class);
                    drawerLayout.close();
                }
                return true;
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_home) {
                    replaceFragment(HomeAdminFragment.class);
                    drawerLayout.close();
                }
                if (item.getItemId() == R.id.nav_profile) {
                    replaceFragment(ProfileFragment.class);
                    drawerLayout.close();
                }
                if (item.getItemId() == R.id.nav_van_companies) {
                    replaceFragment(VanCompanyFragment.class);
                    drawerLayout.close();
                }
                if (item.getItemId() == R.id.nav_contact) {
                    replaceFragment(ContactAdminFragment.class);
                    drawerLayout.close();
                }
                if (item.getItemId() == R.id.nav_about) {
                    Intent intent = new Intent(AdminActivity.this, AboutActivity.class);
                    startActivity(intent);
                }
                if (item.getItemId() == R.id.btnLogout) {
                    firebaseAuth.signOut();
                    Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    finish();
                    startActivity(intent);
                }
                return false;
            }
        });
    }

    public void replaceFragment(Class fragmentClass) {
        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.drawer_nav_host_fragment, fragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();
            if (!currentUserId.isEmpty()) {
                usersReference.document(currentUserId)
                        .addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                if (error != null) {
                                    Toast.makeText(AdminActivity.this, "Error loading user data.", Toast.LENGTH_SHORT).show();
                                }
                                if (value != null) {
                                    if (value.exists()) {
                                        String name = value.getString("name");
                                        String email = value.getString("email");
                                        String photo_url = value.getString("photo_url");
                                        headerUserEmail.setText(email);
                                        headerUserName.setText(name);

                                        if (!photo_url.isEmpty()) {
                                            Glide.with(AdminActivity.this)
                                                    .load(photo_url)
                                                    .into(headerUserPhoto);
                                        }
                                    }
                                }
                            }
                        });
            }
        } else {
            Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            finish();
            startActivity(intent);
        }
    }
}