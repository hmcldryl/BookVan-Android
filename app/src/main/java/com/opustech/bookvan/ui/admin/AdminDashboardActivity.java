package com.opustech.bookvan.ui.admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.dewinjm.monthyearpicker.MonthYearPickerDialog;
import com.github.dewinjm.monthyearpicker.MonthYearPickerDialogFragment;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import com.opustech.bookvan.ui.user.UserPartnersActivity;
import com.opustech.bookvan.ui.user.UserRentActivity;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminDashboardActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference, bookingsReference;

    private CircleImageView headerUserPhoto;
    private TextView headerUserName, headerUserEmail,
            dashboardBookingsToday,
            dashboardBookingsMonth,
            dashboardBookingsAllTime,
            dashboardBookingsMonthSelect,
            dashboardRentalsToday,
            dashboardRentalsMonth,
            dashboardRentalsAllTime,
            dashboardRentalsMonthSelect,
            dashboardEarningsToday,
            dashboardEarningsMonth,
            dashboardEarningsAllTime,
            dashboardEarningsMonthSelect,
            dateYear,
            today,
            todayDate;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private NestedScrollView nestedScrollView;
    private SwipeRefreshLayout refreshLayout;
    private LinearLayout transportList, selectMonth;

    private String name = "";
    private String email = "";
    private String photo_url = "";

    double allEarnings = 0.0;
    double allEarningsToday = 0.0;
    double allEarningsMonth = 0.0;

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

        nestedScrollView = findViewById(R.id.nestedScrollView);
        refreshLayout = findViewById(R.id.refreshLayout);

        transportList = findViewById(R.id.transportList);

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

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                loadAnalytics(getMonthDates());
            }
        });

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
        dashboardRentalsToday = findViewById(R.id.dashboardRentalsToday);
        dashboardEarningsToday = findViewById(R.id.dashboardEarningsToday);
        dashboardBookingsMonth = findViewById(R.id.dashboardBookingsMonth);
        dashboardRentalsMonth = findViewById(R.id.dashboardRentalsMonth);
        dashboardEarningsMonth = findViewById(R.id.dashboardEarningsMonth);
        dashboardBookingsAllTime = findViewById(R.id.dashboardBookingsAllTime);
        dashboardRentalsAllTime = findViewById(R.id.dashboardRentalsAllTime);
        dashboardEarningsAllTime = findViewById(R.id.dashboardEarningsAllTime);
        dashboardBookingsMonthSelect = findViewById(R.id.dashboardBookingsMonthSelect);
        dashboardRentalsMonthSelect = findViewById(R.id.dashboardRentalsMonthSelect);
        dashboardEarningsMonthSelect = findViewById(R.id.dashboardEarningsMonthSelect);

        selectMonth = findViewById(R.id.selectMonth);
        dateYear = findViewById(R.id.dateYear);

        initializeDatePickerPickUp();

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

        showVanTransportStats();
        loadAnalytics(getMonthDates());

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

    private String convertMonths(String monthYear) {
        SimpleDateFormat format = new SimpleDateFormat("MMMM, yyyy", Locale.ENGLISH);
        Date date = null;
        try {
            date = new SimpleDateFormat("MM, yyyy", Locale.ENGLISH).parse(monthYear);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return format.format(date);
    }

    private String getCurrentMonthYear() {
        SimpleDateFormat format = new SimpleDateFormat("MMMM, yyyy", Locale.ENGLISH);
        return format.format(Calendar.getInstance().getTime());
    }

    private void initializeDatePickerPickUp() {
        selectMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int yearSelected;
                int monthSelected;

                Calendar calendar = Calendar.getInstance();
                yearSelected = calendar.get(Calendar.YEAR);
                monthSelected = calendar.get(Calendar.MONTH);

                MonthYearPickerDialogFragment dialogFragment = MonthYearPickerDialogFragment
                        .getInstance(monthSelected, yearSelected);

                dialogFragment.show(getSupportFragmentManager(), null);

                dialogFragment.setOnDateSetListener(new MonthYearPickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(int year, int monthOfYear) {
                        dateYear.setText(convertMonths((monthOfYear + 1) + ", " + year));
                        monthTotalTransactionSelect(getMonthDatesSelect(monthOfYear, year));
                        monthTotalEarningSelect(getMonthDatesSelect(monthOfYear, year));
                    }
                });
            }
        });
    }

    private void showVanTransportStats() {
        firebaseFirestore.collection("partners")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            transportList.removeAllViews();
                            int count = task.getResult().getDocuments().size();
                            if (count > 0) {
                                for (int i = 0; i < count; i++) {
                                    loadVanTransportStats(i + 1, task.getResult().getDocuments().get(i).getString("uid"),
                                            task.getResult().getDocuments().get(i).getString("name"));
                                }
                                refreshLayout.setRefreshing(false);
                            }
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    private void loadAnalytics(List<String> monthDateList) {
        todayTotalTransaction();
        todayTotalEarning();
        monthTotalTransaction(monthDateList);
        monthTotalEarning(monthDateList);
        allTimeTotalTransaction();
        allTimeTotalEarning();
    }

    private void todayTotalTransaction() {
        bookingsReference.whereEqualTo("timestamp_date", getCurrentDate())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                dashboardBookingsToday.setText(String.valueOf(task.getResult().size()));
                                if (refreshLayout.isRefreshing()) {
                                    refreshLayout.setRefreshing(false);
                                }
                            }
                        }
                    }
                });
        firebaseFirestore.collection("rentals")
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

    private void todayTotalEarning() {
        allEarningsToday = 0.0;
        bookingsReference.whereEqualTo("status", "done")
                .whereEqualTo("timestamp_date", getCurrentDate())
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
                                                        Toast.makeText(AdminDashboardActivity.this, "Update failed. Please retry.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            }
                        } else {
                            if (refreshLayout.isRefreshing()) {
                                refreshLayout.setRefreshing(false);
                            }
                            Toast.makeText(AdminDashboardActivity.this, "Update failed. Please retry.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void monthTotalTransaction(List<String> monthDateList) {
        bookingsReference.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                int bookings = 0;
                                for (int i = 0; i < task.getResult().size(); i++) {
                                    if (monthDateList.contains(task.getResult().getDocuments().get(i).getString("timestamp_date"))) {
                                        bookings = bookings + 1;
                                    }
                                }
                                dashboardBookingsMonth.setText(String.valueOf(bookings));
                                if (refreshLayout.isRefreshing()) {
                                    refreshLayout.setRefreshing(false);
                                }
                            }
                        }
                    }
                });
        firebaseFirestore.collection("rentals")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                int rentals = 0;
                                for (int i = 0; i < task.getResult().size(); i++) {
                                    if (monthDateList.contains(task.getResult().getDocuments().get(i).getString("timestamp_date"))) {
                                        rentals = rentals + 1;
                                    }
                                }
                                dashboardRentalsMonth.setText(String.valueOf(rentals));
                                if (refreshLayout.isRefreshing()) {
                                    refreshLayout.setRefreshing(false);
                                }
                            }
                        }
                    }
                });
    }

    private void monthTotalEarning(List<String> monthDateList) {
        allEarningsMonth = 0.0;
        bookingsReference.whereEqualTo("status", "done")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                if (!task.getResult().isEmpty()) {
                                    for (int i = 0; i < task.getResult().getDocuments().size(); i++) {
                                        if (monthDateList.contains(task.getResult().getDocuments().get(i).getString("timestamp_date"))) {
                                            allEarningsMonth = allEarningsMonth + task.getResult().getDocuments().get(i).getLong("commission").doubleValue();
                                        }
                                    }
                                    firebaseFirestore.collection("rentals")
                                            .whereEqualTo("status", "done")
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        if (task.getResult() != null) {
                                                            if (!task.getResult().isEmpty()) {
                                                                for (int i = 0; i < task.getResult().getDocuments().size(); i++) {
                                                                    if (monthDateList.contains(task.getResult().getDocuments().get(i).getString("timestamp_date"))) {
                                                                        allEarningsMonth = allEarningsMonth + task.getResult().getDocuments().get(i).getLong("commission").doubleValue();
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        if (refreshLayout.isRefreshing()) {
                                                            refreshLayout.setRefreshing(false);
                                                        }
                                                        dashboardEarningsMonth.setText(String.format(Locale.ENGLISH, "%.2f", allEarningsMonth));
                                                    } else {
                                                        if (refreshLayout.isRefreshing()) {
                                                            refreshLayout.setRefreshing(false);
                                                        }
                                                        Toast.makeText(AdminDashboardActivity.this, "Update failed. Please retry.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            }
                        } else {
                            if (refreshLayout.isRefreshing()) {
                                refreshLayout.setRefreshing(false);
                            }
                            Toast.makeText(AdminDashboardActivity.this, "Update failed. Please retry.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void allTimeTotalTransaction() {
        bookingsReference
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

    private void allTimeTotalEarning() {
        allEarnings = 0.0;
        bookingsReference.whereEqualTo("status", "done")
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
                                                        Toast.makeText(AdminDashboardActivity.this, "Update failed. Please retry.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            }
                        } else {
                            if (refreshLayout.isRefreshing()) {
                                refreshLayout.setRefreshing(false);
                            }
                            Toast.makeText(AdminDashboardActivity.this, "Update failed. Please retry.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void monthTotalTransactionSelect(List<String> monthDateList) {
        bookingsReference.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                int bookings = 0;
                                for (int i = 0; i < task.getResult().size(); i++) {
                                    if (monthDateList.contains(task.getResult().getDocuments().get(i).getString("timestamp_date"))) {
                                        bookings = bookings + 1;
                                    }
                                }
                                dashboardBookingsMonthSelect.setText(String.valueOf(bookings));
                                if (refreshLayout.isRefreshing()) {
                                    refreshLayout.setRefreshing(false);
                                }
                            }
                        }
                    }
                });
        firebaseFirestore.collection("rentals")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                int rentals = 0;
                                for (int i = 0; i < task.getResult().size(); i++) {
                                    if (monthDateList.contains(task.getResult().getDocuments().get(i).getString("timestamp_date"))) {
                                        rentals = rentals + 1;
                                    }
                                }
                                dashboardRentalsMonthSelect.setText(String.valueOf(rentals));
                                if (refreshLayout.isRefreshing()) {
                                    refreshLayout.setRefreshing(false);
                                }
                            }
                        }
                    }
                });
    }

    private void monthTotalEarningSelect(List<String> monthDateList) {
        allEarningsMonth = 0.0;
        bookingsReference.whereEqualTo("status", "done")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                if (!task.getResult().isEmpty()) {
                                    for (int i = 0; i < task.getResult().getDocuments().size(); i++) {
                                        if (monthDateList.contains(task.getResult().getDocuments().get(i).getString("timestamp_date"))) {
                                            allEarningsMonth = allEarningsMonth + task.getResult().getDocuments().get(i).getLong("commission").doubleValue();
                                        }
                                    }
                                    firebaseFirestore.collection("rentals")
                                            .whereEqualTo("status", "done")
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        if (task.getResult() != null) {
                                                            if (!task.getResult().isEmpty()) {
                                                                for (int i = 0; i < task.getResult().getDocuments().size(); i++) {
                                                                    if (monthDateList.contains(task.getResult().getDocuments().get(i).getString("timestamp_date"))) {
                                                                        allEarningsMonth = allEarningsMonth + task.getResult().getDocuments().get(i).getLong("commission").doubleValue();
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        if (refreshLayout.isRefreshing()) {
                                                            refreshLayout.setRefreshing(false);
                                                        }
                                                        dashboardEarningsMonthSelect.setText(String.format(Locale.ENGLISH, "%.2f", allEarningsMonth));
                                                    } else {
                                                        if (refreshLayout.isRefreshing()) {
                                                            refreshLayout.setRefreshing(false);
                                                        }
                                                        Toast.makeText(AdminDashboardActivity.this, "Update failed. Please retry.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            }
                        } else {
                            if (refreshLayout.isRefreshing()) {
                                refreshLayout.setRefreshing(false);
                            }
                            Toast.makeText(AdminDashboardActivity.this, "Update failed. Please retry.", Toast.LENGTH_SHORT).show();
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

    private List<String> getMonthDates() {
        List<String> dateList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        for (int i = 0; i < maxDay; i++) {
            calendar.set(Calendar.DAY_OF_MONTH, i + 1);
            dateList.add(simpleDateFormat.format(calendar.getTime()));
        }
        return dateList;
    }

    private List<String> getMonthDatesSelect(int month, int year) {
        List<String> dateList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);
        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        for (int i = 0; i < maxDay; i++) {
            calendar.set(Calendar.DAY_OF_MONTH, i + 1);
            dateList.add(simpleDateFormat.format(calendar.getTime()));
        }
        return dateList;
    }

    private void loadVanTransportStats(int num, String uid, String transport_name) {
        View view = LayoutInflater.from(this).inflate(R.layout.stats_van_transport_admin_dashboard, transportList, false);
        LinearLayout item = view.findViewById(R.id.item);
        TextView itemNumber = view.findViewById(R.id.itemNumber);
        TextView transportName = view.findViewById(R.id.transportName);
        TextView numBooking = view.findViewById(R.id.numBooking);
        TextView numRent = view.findViewById(R.id.numRent);
        TextView totalEarnings = view.findViewById(R.id.totalEarnings);

        itemNumber.setText(String.valueOf(num));
        transportName.setText(transport_name);

        firebaseFirestore.collection("bookings")
                .whereEqualTo("status", "done")
                .whereEqualTo("timestamp_date", getCurrentDate())
                .whereEqualTo("transport_uid", uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                if (task.getResult().size() > 0) {
                                    numBooking.setText(String.valueOf(task.getResult().size()));
                                } else {
                                    numBooking.setText("0");
                                }
                            }
                        }
                    }
                });

        firebaseFirestore.collection("rentals")
                .whereEqualTo("status", "done")
                .whereEqualTo("timestamp_date", getCurrentDate())
                .whereEqualTo("transport_uid", uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                if (task.getResult().size() > 0) {
                                    numRent.setText(String.valueOf(task.getResult().size()));
                                } else {
                                    numRent.setText("0");
                                }
                            }
                        }
                    }
                });

        firebaseFirestore.collection("bookings")
                .whereEqualTo("transport_uid", uid)
                .whereEqualTo("status", "done")
                .whereEqualTo("timestamp_date", getCurrentDate())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        double earningsBookings = 0.0;
                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                if (!task.getResult().isEmpty()) {
                                    for (int i = 0; i < task.getResult().getDocuments().size(); i++) {
                                        earningsBookings = earningsBookings + task.getResult().getDocuments().get(i).getLong("commission").doubleValue();
                                    }
                                    totalEarnings.setText(String.format(Locale.ENGLISH, "%.2f", earningsBookings));
                                    firebaseFirestore.collection("rentals")
                                            .whereEqualTo("timestamp_date", getCurrentDate())
                                            .whereEqualTo("transport_uid", uid)
                                            .whereEqualTo("status", "done")
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    double earningsRentals = 0.0;
                                                    if (task.isSuccessful()) {
                                                        if (task.getResult() != null) {
                                                            if (!task.getResult().isEmpty()) {
                                                                for (int i = 0; i < task.getResult().getDocuments().size(); i++) {
                                                                    earningsRentals = earningsRentals + task.getResult().getDocuments().get(i).getLong("commission").doubleValue();
                                                                }
                                                            }
                                                        }
                                                        double earningsBookings = Double.parseDouble(totalEarnings.getText().toString());
                                                        String earnings = getString(R.string.peso_sign) + String.format(Locale.ENGLISH, "%.2f", earningsRentals + earningsBookings);
                                                        totalEarnings.setText(earnings);
                                                    }
                                                }
                                            });
                                }
                            }
                        }
                    }
                });

        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        transportList.addView(view);
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