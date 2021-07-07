package com.opustech.bookvan.ui.admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.opustech.bookvan.AboutActivity;
import com.opustech.bookvan.LoginActivity;
import com.opustech.bookvan.R;
import com.opustech.bookvan.ui.user.UserPartnersActivity;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminDashboardActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference, bookingsReference;

    private CircleImageView headerUserPhoto;
    private TextView headerUserName, headerUserEmail,
            dashboardBookingsToday,
            dashboardEarningsToday,
            dashboardBookingsAllTime,
            dashboardEarningsAllTime,
            today,
            todayDate;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    private String name = "";
    private String email = "";
    private String photo_url = "";

    private static final int TIME_INTERVAL = 2000;
    private long backPressed;

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
        setContentView(R.layout.activity_admin_dashboard);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        usersReference = firebaseFirestore.collection("users");
        bookingsReference = firebaseFirestore.collection("bookings");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        View navView = navigationView.inflateHeaderView(R.layout.nav_header_main);

        today = findViewById(R.id.today);
        todayDate = findViewById(R.id.todayDate);

        today.setText(getToday());
        todayDate.setText(getTodayDate());

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
                    Intent intent = new Intent(AdminDashboardActivity.this, AdminMessageActivity.class);
                    startActivity(intent);
                }
                return false;
            }
        });

        headerUserPhoto = navView.findViewById(R.id.headerUserPhoto);
        headerUserName = navView.findViewById(R.id.headerUserName);
        headerUserEmail = navView.findViewById(R.id.headerUserEmail);

        dashboardBookingsToday = findViewById(R.id.dashboardBookingsToday);
        dashboardEarningsToday = findViewById(R.id.dashboardEarningsToday);
        dashboardBookingsAllTime = findViewById(R.id.dashboardBookingsAllTime);
        dashboardEarningsAllTime = findViewById(R.id.dashboardEarningsAllTime);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.btnGenerateQR) {
                    Intent intent = new Intent(AdminDashboardActivity.this, AdminGenerateQRActivity.class);
                    startActivity(intent);
                }
                if (item.getItemId() == R.id.btnBookings) {
                    Intent intent = new Intent(AdminDashboardActivity.this, AdminBookingsActivity.class);
                    startActivity(intent);
                }
                if (item.getItemId() == R.id.btnRentals) {
                    Intent intent = new Intent(AdminDashboardActivity.this, AdminRentalsActivity.class);
                    startActivity(intent);
                }
                if (item.getItemId() == R.id.btnSchedules) {
                    Intent intent = new Intent(AdminDashboardActivity.this, AdminSystemSchedulesActivity.class);
                    startActivity(intent);
                }
                if (item.getItemId() == R.id.btnPartners) {
                    Intent intent = new Intent(AdminDashboardActivity.this, UserPartnersActivity.class);
                    startActivity(intent);
                }
                if (item.getItemId() == R.id.btnAbout) {
                    Intent intent = new Intent(AdminDashboardActivity.this, AboutActivity.class);
                    startActivity(intent);
                }
                if (item.getItemId() == R.id.btnLogout) {
                    firebaseAuth.signOut();
                    Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                return false;
            }
        });

        loadAnalytics();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    private void loadAnalytics() {
        bookingsReference.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                todayTotalBooking();
                todayTotalEarning();
                allTimeTotalBooking();
                allTimeTotalEarning();
            }
        });
    }

    private void todayTotalBooking() {
        bookingsReference.whereEqualTo("timestamp_date", getCurrentDate())
                .whereIn("status", Arrays.asList("done", "cancelled", "pending"))
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
    }

    private void todayTotalEarning() {
        bookingsReference.whereEqualTo("timestamp_date", getCurrentDate())
                .whereEqualTo("status", "done")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            double earnings = 0.00;
                            if (task.getResult() != null) {
                                if (!task.getResult().isEmpty()) {
                                    for (int i = 0; i < task.getResult().getDocuments().size(); i++) {
                                        earnings = earnings + task.getResult().getDocuments().get(i).getLong("commission").doubleValue();
                                    }
                                }
                            }
                            dashboardEarningsToday.setText(String.format(Locale.ENGLISH, "%.2f", earnings));
                        }
                    }
                });
    }

    private void allTimeTotalBooking() {
        bookingsReference.whereIn("status", Arrays.asList("done", "cancelled", "pending"))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            dashboardBookingsAllTime.setText(String.valueOf(task.getResult().size()));
                        }
                    }
                });
    }

    private void allTimeTotalEarning() {
        bookingsReference.whereEqualTo("status", "done")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            double earnings = 0.00;
                            if (task.getResult() != null) {
                                if (!task.getResult().isEmpty()) {
                                    for (int i = 0; i < task.getResult().getDocuments().size(); i++) {
                                        earnings = earnings + task.getResult().getDocuments().get(i).getLong("commission").doubleValue();
                                    }
                                }
                            }
                            dashboardEarningsAllTime.setText(String.format(Locale.ENGLISH, "%.2f", earnings));
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
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();
            if (!currentUserId.isEmpty()) {
                usersReference.document(currentUserId)
                        .addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                if (value != null) {
                                    if (value.exists()) {
                                        name = value.getString("name");
                                        email = value.getString("email");
                                        photo_url = value.getString("photo_url");

                                        if (photo_url != null) {
                                            if (!photo_url.isEmpty()) {
                                                Glide.with(AdminDashboardActivity.this)
                                                        .load(photo_url)
                                                        .into(headerUserPhoto);
                                            }
                                        }

                                        if (!name.isEmpty()) {
                                            headerUserName.setText(name);
                                        }

                                        if (!email.isEmpty()) {
                                            headerUserEmail.setText(email);
                                        }
                                    }
                                }
                            }
                        });
            }
        } else {
            Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }
}