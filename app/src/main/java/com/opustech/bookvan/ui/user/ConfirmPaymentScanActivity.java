package com.opustech.bookvan.ui.user;

import android.Manifest;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.PermissionListener;
import com.opustech.bookvan.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import de.hdodenhof.circleimageview.CircleImageView;
import github.nisrulz.qreader.QRDataListener;
import github.nisrulz.qreader.QREader;
import se.simbio.encryption.Encryption;

public class ConfirmPaymentScanActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference, partnersReference, bookingsReference, rentalsReference;

    private SurfaceView scannerView;
    private QREader reader;

    private final String OT_KEY = "TzA8gEdNHRphj6Hu";
    private final String OT_SALT = "N5yH5dvCqskEfCGd";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_confirm_booking_scan);

        firebaseFirestore = FirebaseFirestore.getInstance();
        usersReference = firebaseFirestore.collection("users");
        partnersReference = firebaseFirestore.collection("partners");
        bookingsReference = firebaseFirestore.collection("bookings");
        rentalsReference = firebaseFirestore.collection("rentals");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Confirm Payment");
        getSupportActionBar().setSubtitle("QR Scanner");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        scannerView = findViewById(R.id.camera_view);

        setupScanner();
    }

    private String decryptQR(String data) {
        byte[] iv = new byte[16];
        Encryption encryption = Encryption.getDefault(OT_KEY, OT_SALT, iv);
        return encryption.decryptOrNull(data);
    }

    private String generateTimestamp() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        return format.format(Calendar.getInstance().getTime());
    }

    public void setupScanner() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog alertDialog = builder.create();
        reader = new QREader.Builder(this, scannerView, new QRDataListener() {
            @Override
            public void onDetected(final String data) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (data != null) {
                            if (decryptQR(data) != null) {
                                String booking_transport_uid = getIntent().getStringExtra("transport_uid");
                                String uid = decryptQR(data);
                                if (uid.equals(booking_transport_uid)) {
                                    if (!alertDialog.isShowing()) {
                                        final LayoutInflater inflater = getLayoutInflater();
                                        final View view = inflater.inflate(R.layout.dialog_user_scan_layout, null);
                                        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        alertDialog.setCancelable(true);
                                        alertDialog.setView(view);

                                        if (getIntent().getStringExtra("type").equals("booking")) {
                                            TextView bookingCustomerName = view.findViewById(R.id.bookingCustomerName);
                                            TextView bookingCustomerEmail = view.findViewById(R.id.bookingCustomerEmail);
                                            TextView bookingContactNumber = view.findViewById(R.id.bookingContactNumber);
                                            TextView bookingReferenceNumber = view.findViewById(R.id.bookingReferenceNumber);
                                            TextView bookingTripRoute = view.findViewById(R.id.bookingTripRoute);
                                            TextView bookingScheduleDate = view.findViewById(R.id.bookingScheduleDate);
                                            TextView bookingScheduleTime = view.findViewById(R.id.bookingScheduleTime);
                                            TextView bookingCountAdult = view.findViewById(R.id.bookingCountAdult);
                                            TextView bookingCountChild = view.findViewById(R.id.bookingCountChild);
                                            TextView bookingCountSpecial = view.findViewById(R.id.bookingCountSpecial);
                                            TextView bookingTransportName = view.findViewById(R.id.bookingTransportName);
                                            TextView bookingDriverName = view.findViewById(R.id.bookingDriverName);
                                            TextView bookingPlateNumber = view.findViewById(R.id.bookingVanNumber);
                                            TextView bookingPrice = view.findViewById(R.id.bookingPrice);
                                            Button btnConfirmBooking = view.findViewById(R.id.btnConfirmBooking);
                                            CircleImageView customerPhoto = view.findViewById(R.id.customerPhoto);

                                            bookingsReference.document(getIntent().getStringExtra("booking_id"))
                                                    .get()
                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                String uid = task.getResult().getString("uid");
                                                                String name = task.getResult().getString("name");
                                                                String reference_number = task.getResult().getString("reference_number");
                                                                String contact_number = task.getResult().getString("contact_number");
                                                                String route_from = task.getResult().getString("route_from").equals("Puerto Princesa City") ? "PPC" : task.getResult().getString("route_from");
                                                                String route_to = task.getResult().getString("route_to").equals("Puerto Princesa City") ? "PPC" : task.getResult().getString("route_to");
                                                                String trip_route = route_from + " to " + route_to;
                                                                String schedule_date = task.getResult().getString("schedule_date");
                                                                String schedule_time = task.getResult().getString("schedule_time");
                                                                String transport_uid = task.getResult().getString("transport_uid");
                                                                String driver_name = task.getResult().getString("driver_name");
                                                                String plate_number = task.getResult().getString("plate_number");
                                                                int count_adult = task.getResult().getLong("count_adult").intValue();
                                                                int count_child = task.getResult().getLong("count_child").intValue();
                                                                int count_special = task.getResult().getLong("count_special").intValue();
                                                                double price = task.getResult().getLong("price").doubleValue();

                                                                bookingCustomerName.setText(name);
                                                                bookingContactNumber.setText(contact_number);
                                                                bookingReferenceNumber.setText(reference_number);
                                                                bookingTripRoute.setText(trip_route);
                                                                bookingScheduleDate.setText(schedule_date);
                                                                bookingScheduleTime.setText(schedule_time);
                                                                bookingCountAdult.setText(String.valueOf(count_adult));
                                                                bookingCountChild.setText(String.valueOf(count_child));
                                                                bookingCountSpecial.setText(String.valueOf(count_special));
                                                                bookingDriverName.setText(driver_name);
                                                                bookingPlateNumber.setText(plate_number);
                                                                bookingPrice.setText(String.valueOf(price));

                                                                partnersReference.document(transport_uid)
                                                                        .get()
                                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    bookingTransportName.setText(task.getResult().getString("name"));
                                                                                }
                                                                            }
                                                                        });

                                                                usersReference.document(uid)
                                                                        .get()
                                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    String photo_url = task.getResult().getString("photo_url");
                                                                                    String email = task.getResult().getString("email");

                                                                                    if (photo_url != null) {
                                                                                        Glide.with(ConfirmPaymentScanActivity.this)
                                                                                                .load(photo_url)
                                                                                                .into(customerPhoto);
                                                                                    }
                                                                                    bookingCustomerEmail.setText(email);
                                                                                }
                                                                            }
                                                                        });

                                                                btnConfirmBooking.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        btnConfirmBooking.setEnabled(false);
                                                                        final ACProgressFlower dialog = new ACProgressFlower.Builder(ConfirmPaymentScanActivity.this)
                                                                                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                                                                                .themeColor(getResources().getColor(R.color.white))
                                                                                .text("Processing...")
                                                                                .fadeColor(Color.DKGRAY).build();
                                                                        dialog.show();
                                                                        if (reference_number != null) {
                                                                            updateBooking(alertDialog, dialog, getIntent().getStringExtra("booking_id"), uid, price);
                                                                        } else {
                                                                            dialog.dismiss();
                                                                            alertDialog.dismiss();
                                                                            Toast.makeText(ConfirmPaymentScanActivity.this, "Invalid QR Code. Please try again.", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    });
                                        }

                                        alertDialog.show();
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(ConfirmPaymentScanActivity.this, "Invalid QR code.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).facing(QREader.BACK_CAM)
                .enableAutofocus(true)
                .height(scannerView.getHeight())
                .width(scannerView.getWidth())
                .build();
    }

    private void updateBooking(AlertDialog alertDialog, ACProgressFlower dialog, String booking_id, String uid, double price) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", "done");
        hashMap.put("timestamp", generateTimestamp());

        bookingsReference.document(booking_id)
                .update(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            alertDialog.dismiss();
                            updatePoints(uid, computePoints(price));
                            Toast.makeText(ConfirmPaymentScanActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                        } else {
                            dialog.dismiss();
                            alertDialog.dismiss();
                            Toast.makeText(ConfirmPaymentScanActivity.this, "Update booking failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateRental(AlertDialog alertDialog, ACProgressFlower dialog, String rental_id, String uid, double price) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", "done");
        hashMap.put("timestamp", generateTimestamp());

        rentalsReference.document(rental_id)
                .update(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            alertDialog.dismiss();
                            //updatePoints(uid, computePoints(price));
                            Toast.makeText(ConfirmPaymentScanActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                        } else {
                            dialog.dismiss();
                            alertDialog.dismiss();
                            Toast.makeText(ConfirmPaymentScanActivity.this, "Update booking failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updatePoints(String uid, double points) {
        usersReference.document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            double totalPoints = task.getResult().getLong("points").doubleValue() + points;
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("points", totalPoints);
                            usersReference.document(uid)
                                    .update(hashMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("DEBUG", "Update points success");
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        if (reader != null) {
                            reader.initAndStart(scannerView);
                        }
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(ConfirmPaymentScanActivity.this, "You must enable this permission.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(com.karumi.dexter.listener.PermissionRequest permission, PermissionToken token) {

                    }
                }).check();
    }

    @Override
    public void onPause() {
        super.onPause();
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        if (reader != null) {
                            reader.releaseAndCleanup();
                        }
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(ConfirmPaymentScanActivity.this, "You must enable this permission.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(com.karumi.dexter.listener.PermissionRequest permission, PermissionToken token) {

                    }
                }).check();
    }

    private double computePoints(double totalPrice) {
        return 0.01 * totalPrice;
    }
}