package com.opustech.bookvan.ui.transport;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
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
    private CollectionReference usersReference, partnersReference, bookingsReference;

    private SurfaceView scannerView;
    private QREader reader;

    private final String OT_KEY = "TzA8gEdNHRphj6Hu";
    private final String OT_SALT = "N5yH5dvCqskEfCGd";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport_admin_booking_scanner);

        firebaseFirestore = FirebaseFirestore.getInstance();
        usersReference = firebaseFirestore.collection("users");
        bookingsReference = firebaseFirestore.collection("bookings");
        partnersReference = firebaseFirestore.collection("partners");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Confirm Payment");
        getSupportActionBar().setSubtitle("Scan Customer Booking QR");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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
                                if (!alertDialog.isShowing()) {
                                    if (getIntent().getStringExtra("type").equals("booking")) {
                                        final LayoutInflater inflater = getLayoutInflater();
                                        final View view = inflater.inflate(R.layout.dialog_scan_layout, null);
                                        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        alertDialog.setCancelable(true);
                                        alertDialog.setView(view);

                                        String booking_id = decryptQR(data);

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
                                        Button btnOptions = view.findViewById(R.id.btnOptions);
                                        CircleImageView customerPhoto = view.findViewById(R.id.customerPhoto);

                                        bookingsReference.document(booking_id)
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            if (task.getResult() != null) {
                                                                DocumentReference documentReference = task.getResult().getReference();

                                                                String uid = task.getResult().getString("uid");
                                                                String booking_id = task.getResult().getString("booking_id");
                                                                String name = task.getResult().getString("name");
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
                                                                bookingReferenceNumber.setText(booking_id);
                                                                bookingTripRoute.setText(trip_route);
                                                                bookingScheduleDate.setText(schedule_date);
                                                                bookingScheduleTime.setText(schedule_time);
                                                                bookingCountAdult.setText(String.valueOf(count_adult));
                                                                bookingCountChild.setText(String.valueOf(count_child));
                                                                bookingCountSpecial.setText(String.valueOf(count_special));
                                                                bookingDriverName.setText(driver_name);
                                                                bookingPlateNumber.setText(plate_number);
                                                                bookingPrice.setText(String.format(Locale.ENGLISH, "%.2f", price));

                                                                partnersReference.document(transport_uid)
                                                                        .get()
                                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    String transport_name = task.getResult().getString("name");
                                                                                    bookingTransportName.setText(transport_name);
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

                                                                btnOptions.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        PopupMenu popup = new PopupMenu(ConfirmPaymentScanActivity.this, v);
                                                                        MenuInflater inflater = popup.getMenuInflater();
                                                                        inflater.inflate(R.menu.transport_scan_item_menu, popup.getMenu());
                                                                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                                                            @Override
                                                                            public boolean onMenuItemClick(MenuItem item) {
                                                                                if (item.getItemId() == R.id.btnCancelBooking) {
                                                                                    final ACProgressFlower dialog = new ACProgressFlower.Builder(ConfirmPaymentScanActivity.this)
                                                                                            .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                                                                                            .themeColor(getResources().getColor(R.color.white))
                                                                                            .text("Processing...")
                                                                                            .fadeColor(Color.DKGRAY).build();
                                                                                    dialog.show();
                                                                                    documentReference.delete()
                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                    if (task.isSuccessful()) {
                                                                                                        dialog.dismiss();
                                                                                                        alertDialog.dismiss();
                                                                                                        Toast.makeText(ConfirmPaymentScanActivity.this, "Success.", Toast.LENGTH_SHORT).show();
                                                                                                    } else {
                                                                                                        dialog.dismiss();
                                                                                                        alertDialog.dismiss();
                                                                                                        Toast.makeText(ConfirmPaymentScanActivity.this, "Cancellation failed. Please try again.", Toast.LENGTH_SHORT).show();
                                                                                                    }
                                                                                                }
                                                                                            });
                                                                                }
                                                                                return false;
                                                                            }
                                                                        });
                                                                        popup.show();
                                                                    }
                                                                });

                                                                btnConfirmBooking.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        final ACProgressFlower dialog = new ACProgressFlower.Builder(ConfirmPaymentScanActivity.this)
                                                                                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                                                                                .themeColor(getResources().getColor(R.color.white))
                                                                                .text("Processing...")
                                                                                .fadeColor(Color.DKGRAY).build();
                                                                        dialog.show();
                                                                        if (decryptQR(data) != null) {
                                                                            updateBooking(alertDialog, dialog, documentReference, uid, price);
                                                                        } else {
                                                                            dialog.dismiss();
                                                                            alertDialog.dismiss();
                                                                            Toast.makeText(ConfirmPaymentScanActivity.this, "Invalid QR Code. Please try again.", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    }
                                                });
                                        alertDialog.show();
                                    }

                                }
                            } else {
                                Toast.makeText(ConfirmPaymentScanActivity.this, "Invalid QR code.", Toast.LENGTH_SHORT).show();
                            }
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

    private void updateBooking(AlertDialog alertDialog, ACProgressFlower dialog, DocumentReference documentReference, String uid, double price) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", "done");
        hashMap.put("timestamp", generateTimestamp());

        documentReference.update(hashMap)
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

    private void updatePoints(String uid, double points) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("points", points);
        firebaseFirestore.collection("users")
                .document(uid)
                .update(hashMap);
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