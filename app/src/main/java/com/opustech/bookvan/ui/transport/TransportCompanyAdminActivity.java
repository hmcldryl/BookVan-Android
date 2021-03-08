package com.opustech.bookvan.ui.transport;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.opustech.bookvan.AboutActivity;
import com.opustech.bookvan.LoginActivity;
import com.opustech.bookvan.R;
import com.opustech.bookvan.ui.fragments.admin.ScheduleAdminFragment;
import com.opustech.bookvan.ui.fragments.admin.bookings.BookingsFragment;
import com.opustech.bookvan.ui.fragments.transport.DashboardTransportAdminFragment;
import com.opustech.bookvan.ui.fragments.transport.ScanBookingFragment;
import com.opustech.bookvan.ui.fragments.transport.bookings.BookingsTransportFragment;

import de.hdodenhof.circleimageview.CircleImageView;

public class TransportCompanyAdminActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference;
    private CollectionReference partnersReference;

    private CircleImageView headerUserPhoto, companyPhoto;
    private TextView headerUserName, headerUserEmail, headerUserAccountType, companyName, companyAddress;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    private static final int TIME_INTERVAL = 2000;
    private long backPressed;

    @Override
    public void onBackPressed() {
        if (backPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed();
        }
        else {
            Toast.makeText(this, "Press back again to exit BookVan.", Toast.LENGTH_SHORT).show();
        }
        backPressed = System.currentTimeMillis();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport_company_admin);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        usersReference = firebaseFirestore.collection("users");
        partnersReference = firebaseFirestore.collection("partners");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        View navView = navigationView.inflateHeaderView(R.layout.nav_header_transport_main);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_view);

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

        headerUserPhoto = navView.findViewById(R.id.headerUserPhoto);
        headerUserName = navView.findViewById(R.id.headerUserName);
        headerUserEmail = navView.findViewById(R.id.headerUserEmail);
        headerUserAccountType = navView.findViewById(R.id.headerUserAccountType);
        companyPhoto = navView.findViewById(R.id.companyPhoto);
        companyName = navView.findViewById(R.id.companyName);
        companyAddress = navView.findViewById(R.id.companyAddress);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_bookings) {
                    replaceFragment(BookingsTransportFragment.class);
                    drawerLayout.close();
                }
                if (item.getItemId() == R.id.nav_scan) {
                    replaceFragment(ScanBookingFragment.class);
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
                if (item.getItemId() == R.id.nav_dashboard) {
                    replaceFragment(DashboardTransportAdminFragment.class);
                    drawerLayout.close();
                }
                if (item.getItemId() == R.id.btnProfile) {
                    Intent intent = new Intent(TransportCompanyAdminActivity.this, TransportProfileAdminActivity.class);
                    intent.putExtra("uid", getCompanyUid());
                    startActivity(intent);
                }
                if (item.getItemId() == R.id.btnAbout) {
                    Intent intent = new Intent(TransportCompanyAdminActivity.this, AboutActivity.class);
                    startActivity(intent);
                }
                if (item.getItemId() == R.id.btnLogout) {
                    firebaseAuth.signOut();
                    Intent intent = new Intent(TransportCompanyAdminActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
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

    private String getCompanyUid() {
        Intent intent = getIntent();
        return intent.getStringExtra("uid");
    }

    private void updateUiCompanyInfo() {
        partnersReference.document(getCompanyUid())
                .addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null) {
                            if (value.exists()) {
                                String name = value.getString("name");
                                String address = value.getString("address");
                                String photo_url = value.getString("photo_url");

                                if (photo_url != null) {
                                    if (!photo_url.isEmpty()) {
                                        Glide.with(TransportCompanyAdminActivity.this)
                                                .load(photo_url)
                                                .into(companyPhoto);
                                    }
                                }
                                companyName.setText(name);
                                companyAddress.setText(address);
                            }
                        }
                    }
                });
    }

    private void updateUiAdminInfo() {
        usersReference.document(firebaseAuth.getCurrentUser().getUid())
                .addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null) {
                            if (value.exists()) {
                                String name = value.getString("name");
                                String email = value.getString("email");
                                String account_type = value.getString("account_type");
                                String photo_url = value.getString("photo_url");

                                if (photo_url != null) {
                                    if (!photo_url.isEmpty()) {
                                        Glide.with(TransportCompanyAdminActivity.this)
                                                .load(photo_url)
                                                .into(headerUserPhoto);
                                    }
                                }
                                headerUserName.setText(name);
                                headerUserEmail.setText(email);
                                headerUserAccountType.setText(account_type);
                            }
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (firebaseAuth.getCurrentUser() != null) {
            if (!firebaseAuth.getCurrentUser().getUid().isEmpty()) {
                updateUiCompanyInfo();
                updateUiAdminInfo();
            }
        } else {
            Intent intent = new Intent(TransportCompanyAdminActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }
}