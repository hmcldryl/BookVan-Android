package com.opustech.bookvan.ui.transport;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.opustech.bookvan.AboutActivity;
import com.opustech.bookvan.LoginActivity;
import com.opustech.bookvan.R;
import com.opustech.bookvan.ui.admin.AdminDashboardActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class TransportAdminDashboardActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference, bookingsReference, partnersReference;

    private CircleImageView headerUserPhoto, companyPhoto;
    private TextView headerUserName, headerUserEmail, headerUserAccountType, companyName, companyAddress,
            dashboardBookingsToday,
            dashboardRentalsToday,
            dashboardEarningsToday,
            dashboardBookingsAllTime,
            dashboardRentalsAllTime,
            dashboardEarningsAllTime,
            today,
            todayDate;
    private CardView btnScanMode, btnQRMode;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    private NestedScrollView nestedScrollView;
    private SwipeRefreshLayout refreshLayout;

    private static final int TIME_INTERVAL = 2000;
    private long backPressed;

    double allEarnings = 0.0;
    double allEarningsToday = 0.0;

    String name = "";
    String address = "";
    String photo_url = "";

    @Override
    public void onBackPressed() {
        if (backPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, "Press back again to exit BookVan.", Toast.LENGTH_SHORT).show();
        }
        backPressed = System.currentTimeMillis();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport_admin_dashboard);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        usersReference = firebaseFirestore.collection("users");
        partnersReference = firebaseFirestore.collection("partners");
        bookingsReference = firebaseFirestore.collection("bookings");

        nestedScrollView = findViewById(R.id.nestedScrollView);
        refreshLayout = findViewById(R.id.refreshLayout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Dashboard");
        getSupportActionBar().setSubtitle("Welcome to BookVan!");

        today = findViewById(R.id.today);
        todayDate = findViewById(R.id.todayDate);

        btnScanMode = findViewById(R.id.btnScanMode);
        btnQRMode = findViewById(R.id.btnQRMode);

        today.setText(getToday());
        todayDate.setText(getTodayDate());

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                loadAnalytics();
            }
        });

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        View navView = navigationView.inflateHeaderView(R.layout.nav_header_transport_main);

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

        dashboardBookingsToday = findViewById(R.id.dashboardBookingsToday);
        dashboardRentalsToday = findViewById(R.id.dashboardRentalsToday);
        dashboardEarningsToday = findViewById(R.id.dashboardEarningsToday);
        dashboardBookingsAllTime = findViewById(R.id.dashboardBookingsAllTime);
        dashboardRentalsAllTime = findViewById(R.id.dashboardRentalsAllTime);
        dashboardEarningsAllTime = findViewById(R.id.dashboardEarningsAllTime);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.btnCompanyProfile) {
                    Intent intent = new Intent(TransportAdminDashboardActivity.this, TransportAdminCompanyProfileActivity.class);
                    intent.putExtra("uid", getCompanyUid());
                    startActivity(intent);
                }
                if (item.getItemId() == R.id.btnProfile) {
                    Intent intent = new Intent(TransportAdminDashboardActivity.this, TransportAdminUserProfileActivity.class);
                    startActivity(intent);
                }
                if (item.getItemId() == R.id.btnBookings) {
                    Intent intent = new Intent(TransportAdminDashboardActivity.this, TransportAdminBookingsActivity.class);
                    intent.putExtra("uid", getCompanyUid());
                    startActivity(intent);
                }
                if (item.getItemId() == R.id.btnRentals) {
                    Intent intent = new Intent(TransportAdminDashboardActivity.this, TransportRentConversationActivity.class);
                    intent.putExtra("uid", getCompanyUid());
                    intent.putExtra("name", name);
                    startActivity(intent);
                }
                if (item.getItemId() == R.id.btnSchedules) {
                    Intent intent = new Intent(TransportAdminDashboardActivity.this, TransportAdminSchedulesActivity.class);
                    intent.putExtra("uid", getCompanyUid());
                    startActivity(intent);
                }
                if (item.getItemId() == R.id.btnAbout) {
                    Intent intent = new Intent(TransportAdminDashboardActivity.this, AboutActivity.class);
                    startActivity(intent);
                }
                if (item.getItemId() == R.id.btnLogout) {
                    firebaseAuth.signOut();
                    Intent intent = new Intent(TransportAdminDashboardActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                return false;
            }
        });

        btnScanMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TransportAdminDashboardActivity.this, TransportAdminBookingScannerActivity.class);
                intent.putExtra("uid", getCompanyUid());
                startActivity(intent);
            }
        });

        btnQRMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TransportAdminDashboardActivity.this, TransportAdminBookingQRActivity.class);
                intent.putExtra("uid", getCompanyUid());
                startActivity(intent);
            }
        });
        loadAnalytics();

        updateToken();
    }

    private void updateToken() {
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
                                name = value.getString("name");
                                address = value.getString("address");
                                photo_url = value.getString("photo_url");

                                if (photo_url != null) {
                                    if (!photo_url.isEmpty()) {
                                        Glide.with(TransportAdminDashboardActivity.this)
                                                .load(photo_url)
                                                .into(companyPhoto);
                                    }
                                }
                                getSupportActionBar().setSubtitle(name);
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
                                        Glide.with(TransportAdminDashboardActivity.this)
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

    private void loadAnalytics() {
        todayTotalTransaction(getCompanyUid());
        todayTotalEarning(getCompanyUid());
        allTimeTotalTransaction(getCompanyUid());
        allTimeTotalEarning(getCompanyUid());
    }

    private void todayTotalTransaction(String uid) {
        bookingsReference.whereEqualTo("transport_uid", uid)
                .whereEqualTo("timestamp_date", getCurrentDate())
                .whereEqualTo("status", "done")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                dashboardBookingsToday.setText(String.valueOf(task.getResult().size()));
                            }
                        }
                    }
                });
        firebaseFirestore.collection("rentals")
                .whereEqualTo("transport_uid", uid)
                .whereEqualTo("timestamp_date", getCurrentDate())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                dashboardRentalsToday.setText(String.valueOf(task.getResult().size()));
                                if (refreshLayout.isRefreshing()) {
                                    refreshLayout.setRefreshing(false);
                                }
                            }
                        }
                    }
                });
    }

    private void todayTotalEarning(String uid) {
        allEarningsToday = 0.0;
        bookingsReference.whereEqualTo("transport_uid", uid)
                .whereEqualTo("timestamp_date", getCurrentDate())
                .whereEqualTo("status", "done")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                if (!task.getResult().isEmpty()) {
                                    for (int i = 0; i < task.getResult().getDocuments().size(); i++) {
                                        allEarningsToday = allEarningsToday + task.getResult().getDocuments().get(i).getLong("commission").doubleValue();
                                    }
                                    firebaseFirestore.collection("rentals")
                                            .whereEqualTo("transport_uid", uid)
                                            .whereEqualTo("timestamp_date", getCurrentDate())
                                            .whereEqualTo("status", "done")
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        if (task.getResult() != null) {
                                                            if (!task.getResult().isEmpty()) {
                                                                for (int i = 0; i < task.getResult().getDocuments().size(); i++) {
                                                                    allEarningsToday = allEarningsToday + task.getResult().getDocuments().get(i).getLong("commission").doubleValue();
                                                                }
                                                            }
                                                        }
                                                        if (refreshLayout.isRefreshing()) {
                                                            refreshLayout.setRefreshing(false);
                                                        }
                                                        dashboardEarningsToday.setText(String.format(Locale.ENGLISH, "%.2f", allEarnings));
                                                    } else {
                                                        if (refreshLayout.isRefreshing()) {
                                                            refreshLayout.setRefreshing(false);
                                                        }
                                                        Toast.makeText(TransportAdminDashboardActivity.this, "Update failed. Please retry.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            }
                        } else {
                            if (refreshLayout.isRefreshing()) {
                                refreshLayout.setRefreshing(false);
                            }
                            Toast.makeText(TransportAdminDashboardActivity.this, "Update failed. Please retry.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void allTimeTotalTransaction(String uid) {
        bookingsReference.whereEqualTo("transport_uid", uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            dashboardBookingsAllTime.setText(String.valueOf(task.getResult().size()));
                            if (refreshLayout.isRefreshing()) {
                                refreshLayout.setRefreshing(false);
                            }
                        }
                    }
                });

        firebaseFirestore.collection("rentals")
                .whereEqualTo("transport_uid", uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            dashboardRentalsAllTime.setText(String.valueOf(task.getResult().size()));
                            if (refreshLayout.isRefreshing()) {
                                refreshLayout.setRefreshing(false);
                            }
                        }
                    }
                });
    }

    private void allTimeTotalEarning(String uid) {
        allEarnings = 0.0;
        bookingsReference.whereEqualTo("transport_uid", uid)
                .whereEqualTo("status", "done")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                if (!task.getResult().isEmpty()) {
                                    for (int i = 0; i < task.getResult().getDocuments().size(); i++) {
                                        allEarnings = allEarnings + task.getResult().getDocuments().get(i).getLong("commission").doubleValue();
                                    }
                                    firebaseFirestore.collection("rentals")
                                            .whereEqualTo("transport_uid", uid)
                                            .whereEqualTo("status", "done")
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        if (task.getResult() != null) {
                                                            if (!task.getResult().isEmpty()) {
                                                                for (int i = 0; i < task.getResult().getDocuments().size(); i++) {
                                                                    allEarnings = allEarnings + task.getResult().getDocuments().get(i).getLong("commission").doubleValue();
                                                                }
                                                            }
                                                        }
                                                        if (refreshLayout.isRefreshing()) {
                                                            refreshLayout.setRefreshing(false);
                                                        }
                                                        dashboardEarningsAllTime.setText(String.format(Locale.ENGLISH, "%.2f", allEarnings));
                                                    } else {
                                                        if (refreshLayout.isRefreshing()) {
                                                            refreshLayout.setRefreshing(false);
                                                        }
                                                        Toast.makeText(TransportAdminDashboardActivity.this, "Update failed. Please retry.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            }
                        } else {
                            if (refreshLayout.isRefreshing()) {
                                refreshLayout.setRefreshing(false);
                            }
                            Toast.makeText(TransportAdminDashboardActivity.this, "Update failed. Please retry.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private String getCurrentDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        return format.format(Calendar.getInstance().getTime());
    }

    private String getTodayDate() {
        SimpleDateFormat format = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
        return format.format(Calendar.getInstance().getTime());
    }

    private String getToday() {
        SimpleDateFormat format = new SimpleDateFormat("E", Locale.ENGLISH);
        return format.format(Calendar.getInstance().getTime());
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
            Intent intent = new Intent(TransportAdminDashboardActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }
}