package com.opustech.bookvan.ui.transport;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.opustech.bookvan.AboutActivity;
import com.opustech.bookvan.LoginActivity;
import com.opustech.bookvan.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class TransportAdminDashboardActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference, bookingsReference, partnersReference;

    private CircleImageView headerUserPhoto, companyPhoto;
    private TextView headerUserName, headerUserEmail, headerUserAccountType, companyName, companyAddress,
            dashboardBookingsToday,
            dashboardEarningsToday,
            dashboardBookingsAllTime,
            dashboardEarningsAllTime,
            today,
            todayDate;
    private LineChart totalBookingLineChart;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

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
        setContentView(R.layout.activity_transport_admin_dashboard);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        usersReference = firebaseFirestore.collection("users");
        partnersReference = firebaseFirestore.collection("partners");
        bookingsReference = firebaseFirestore.collection("bookings");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Dashboard");
        getSupportActionBar().setSubtitle("Welcome to BookVan!");

        today = findViewById(R.id.today);
        todayDate = findViewById(R.id.todayDate);

        today.setText(getToday());
        todayDate.setText(getTodayDate());

        //totalBookingLineChart = findViewById(R.id.totalBookingLineChart);
        //initializeTotalBookingLineChart();

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
        dashboardEarningsToday = findViewById(R.id.dashboardEarningsToday);
        dashboardBookingsAllTime = findViewById(R.id.dashboardBookingsAllTime);
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

        loadAnalytics();
    }

    private void initializeTotalBookingLineChart() {
        LineDataSet lineDataSet = new LineDataSet(totalBookingLineChartDataSet(), "data set");
        ArrayList<ILineDataSet> iLineDataSets = new ArrayList<>();
        iLineDataSets.add(lineDataSet);

        LineData lineData = new LineData(iLineDataSets);
        totalBookingLineChart.setData(lineData);
        totalBookingLineChart.invalidate();

        lineDataSet.setColor(getResources().getColor(R.color.colorPrimary));
        lineDataSet.setCircleColor(getResources().getColor(R.color.colorPrimary));
        lineDataSet.setLineWidth(4);
        lineDataSet.setCircleRadius(6);
        lineDataSet.setValueTextColor(getResources().getColor(R.color.textColor));
        lineDataSet.setValueTextSize(14);

        totalBookingLineChart.setNoDataText("Data not available.");
        totalBookingLineChart.setNoDataTextColor(getResources().getColor(R.color.textColor));
    }

    private ArrayList<Entry> totalBookingLineChartDataSet() {
        ArrayList<Entry> dataSet = new ArrayList<>();
        bookingsReference.whereEqualTo("transport_uid", getCompanyUid())
                .whereEqualTo("status", "done")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult() != null) {
                            if (task.getResult().getDocuments().size() > 0) {
                                for (int i = 0; i < task.getResult().getDocuments().size(); i++) {
                                    String timestamp_date = task.getResult().getDocuments().get(i).getString("timestamp_date");
                                    dataSet.add(new Entry(i + 1, formatDate(timestamp_date)));
                                }
                            }
                        }
                    }
                });
        return dataSet;
    }

    private float formatDate(String timestamp_date) {
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(timestamp_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Float.parseFloat(new SimpleDateFormat("dd", Locale.ENGLISH).format(date));
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
        bookingsReference.whereEqualTo("transport_uid", getCompanyUid())
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        todayTotalBooking(getCompanyUid());
                        todayTotalEarning(getCompanyUid());
                        allTimeTotalBooking(getCompanyUid());
                        allTimeTotalEarning(getCompanyUid());
                    }
                });
    }

    private void todayTotalBooking(String uid) {
        bookingsReference.whereEqualTo("transport_uid", uid)
                .whereEqualTo("timestamp_date", getCurrentDate())
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

    private void todayTotalEarning(String uid) {
        bookingsReference.whereEqualTo("transport_uid", uid)
                .whereEqualTo("timestamp_date", getCurrentDate())
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
                                        earnings = earnings + task.getResult().getDocuments().get(i).getLong("price").doubleValue();
                                    }
                                }
                            }
                            dashboardEarningsToday.setText(String.format(Locale.ENGLISH, "%.2f", earnings));
                        }
                    }
                });
    }

    private void allTimeTotalBooking(String uid) {
        bookingsReference.whereEqualTo("transport_uid", uid)
                .whereIn("status", Arrays.asList("done", "cancelled", "pending"))
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

    private void allTimeTotalEarning(String uid) {
        bookingsReference.whereEqualTo("transport_uid", uid)
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
                                        earnings = earnings + task.getResult().getDocuments().get(i).getLong("price").doubleValue();
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