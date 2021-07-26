package com.opustech.bookvan.ui.user;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.messaging.FirebaseMessaging;
import com.opustech.bookvan.AboutActivity;
import com.opustech.bookvan.LoginActivity;
import com.opustech.bookvan.R;
import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.imaginativeworld.whynotimagecarousel.CarouselItem;
import org.imaginativeworld.whynotimagecarousel.ImageCarousel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserHomeActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference;
    private DocumentReference systemReference, destinationsReference;

    private CircleImageView headerUserPhoto;
    private TextView headerUserName, headerUserEmail;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private CardView btnBook, btnRent, btnPartners, btnBookElnido, btnBookTaytay;
    private ImageView imageElnido, imageTaytay;
    private ImageCarousel imageCarousel;

    private String name = "";
    private String contact_number = "";
    private String email = "";
    private String photo_url = "";

    String elnido_image_url;
    String taytay_image_url;

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
        setContentView(R.layout.activity_user_home);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        usersReference = firebaseFirestore.collection("users");
        systemReference = firebaseFirestore.collection("system").document("data");
        destinationsReference = firebaseFirestore.collection("system").document("destinations");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Home");
        getSupportActionBar().setSubtitle("Welcome to BookVan!");

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        View navView = navigationView.inflateHeaderView(R.layout.nav_header_main);

        btnBook = findViewById(R.id.btnBook);
        btnRent = findViewById(R.id.btnRent);
        btnPartners = findViewById(R.id.btnPartners);
        btnBookElnido = findViewById(R.id.btnBookElnido);
        btnBookTaytay = findViewById(R.id.btnBookTaytay);
        imageElnido = findViewById(R.id.imageElnido);
        imageTaytay = findViewById(R.id.imageTaytay);
        imageCarousel = findViewById(R.id.carousel);

        updateUi();

        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserHomeActivity.this, UserBookActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("contact_number", contact_number);
                startActivity(intent);
            }
        });

        btnBookElnido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserHomeActivity.this, UserTripScheduleActivity.class);
                intent.putExtra("destination", "el nido");
                intent.putExtra("image_url", elnido_image_url);
                startActivity(intent);
            }
        });

        btnBookTaytay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserHomeActivity.this, UserTripScheduleActivity.class);
                intent.putExtra("destination", "taytay");
                intent.putExtra("image_url", taytay_image_url);
                startActivity(intent);
            }
        });

        btnRent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(UserHomeActivity.this, "This feature is not yet implemented.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UserHomeActivity.this, UserRentActivity.class);
                startActivity(intent);
            }
        });

        btnPartners.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserHomeActivity.this, UserPartnersActivity.class);
                startActivity(intent);
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
                    Intent intent = new Intent(UserHomeActivity.this, UserChatActivity.class);
                    startActivity(intent);
                }
                return false;
            }
        });

        headerUserPhoto = navView.findViewById(R.id.headerUserPhoto);
        headerUserName = navView.findViewById(R.id.headerUserName);
        headerUserEmail = navView.findViewById(R.id.headerUserEmail);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.btnProfile) {
                    Intent intent = new Intent(UserHomeActivity.this, UserProfileActivity.class);
                    startActivity(intent);
                }
                if (item.getItemId() == R.id.btnBooking) {
                    Intent intent = new Intent(UserHomeActivity.this, UserBookingActivity.class);
                    startActivity(intent);
                }
                if (item.getItemId() == R.id.btnRentals) {
                    Intent intent = new Intent(UserHomeActivity.this, UserRentConversationActivity.class);
                    startActivity(intent);
                }
                if (item.getItemId() == R.id.btnPartners) {
                    Intent intent = new Intent(UserHomeActivity.this, UserPartnersActivity.class);
                    startActivity(intent);
                }
                if (item.getItemId() == R.id.btnSchedules) {
                    Intent intent = new Intent(UserHomeActivity.this, UserSchedulesActivity.class);
                    startActivity(intent);
                }
                if (item.getItemId() == R.id.btnAbout) {
                    Intent intent = new Intent(UserHomeActivity.this, AboutActivity.class);
                    startActivity(intent);
                }
                if (item.getItemId() == R.id.btnLogout) {
                    LoginManager.getInstance().logOut();
                    firebaseAuth.signOut();
                    Intent intent = new Intent(UserHomeActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                return false;
            }
        });

        updateToken();
    }

    private void updateUi() {
        systemReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> photo_url = (List<String>) task.getResult().get("home_image_carousel_urls");
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

        destinationsReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            elnido_image_url = task.getResult().getString("elnido_image_url");
                            taytay_image_url = task.getResult().getString("taytay_image_url");

                            if (elnido_image_url != null) {
                                if (!elnido_image_url.isEmpty()) {
                                    Glide.with(UserHomeActivity.this)
                                            .load(elnido_image_url)
                                            .into(imageElnido);
                                }
                            }

                            if (taytay_image_url != null) {
                                if (!taytay_image_url.isEmpty()) {
                                    Glide.with(UserHomeActivity.this)
                                            .load(taytay_image_url)
                                            .into(imageTaytay);
                                }
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

    @Override
    public void onStart() {
        super.onStart();
        if (firebaseAuth.getCurrentUser() != null) {
            String currentUserId = firebaseAuth.getCurrentUser().getUid();
            if (!currentUserId.isEmpty()) {
                usersReference.document(currentUserId)
                        .addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                if (value != null) {
                                    if (value.exists()) {
                                        name = value.getString("name");
                                        email = value.getString("email");
                                        contact_number = value.getString("contact_number");
                                        photo_url = value.getString("photo_url");

                                        if (photo_url != null) {
                                            if (!photo_url.isEmpty()) {
                                                Glide.with(UserHomeActivity.this)
                                                        .load(photo_url)
                                                        .into(headerUserPhoto);
                                            }
                                        }
                                        headerUserName.setText(name);
                                        headerUserEmail.setText(email);
                                    }
                                }
                            }
                        });
            }
        } else {
            Intent intent = new Intent(UserHomeActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
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
}