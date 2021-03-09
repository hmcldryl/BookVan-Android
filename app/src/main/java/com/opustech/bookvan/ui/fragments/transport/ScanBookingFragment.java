package com.opustech.bookvan.ui.fragments.transport;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
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

public class ScanBookingFragment extends Fragment {

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference, bookingsReference;

    private SurfaceView scannerView;
    private QREader reader;

    private Context context;

    private final String OT_KEY = "TEST";
    private final String OT_SALT = "TEST";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_scan_booking, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        usersReference = firebaseFirestore.collection("users");
        bookingsReference = firebaseFirestore.collection("bookings");

        scannerView = root.findViewById(R.id.camera_view);

        setupScanner();

        return root;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
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
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final AlertDialog alertDialog = builder.create();
        reader = new QREader.Builder(getActivity(), scannerView, new QRDataListener() {
            @Override
            public void onDetected(final String data) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (decryptQR(data) != null) {
                            if (!alertDialog.isShowing()) {
                                final LayoutInflater inflater = getLayoutInflater();
                                final View view = inflater.inflate(R.layout.dialog_scan_layout, null);
                                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                alertDialog.setCancelable(false);
                                alertDialog.setView(view);

                                String reference_number = decryptQR(data);

                                TextView bookingCustomerName = view.findViewById(R.id.bookingCustomerName);
                                TextView bookingCustomerEmail = view.findViewById(R.id.bookingCustomerEmail);
                                TextView bookingContactNumber = view.findViewById(R.id.bookingContactNumber);
                                TextView bookingReferenceNumber = view.findViewById(R.id.bookingReferenceNumber);
                                TextView bookingLocationFrom = view.findViewById(R.id.bookingLocationFrom);
                                TextView bookingLocationTo = view.findViewById(R.id.bookingLocationTo);
                                TextView bookingScheduleDate = view.findViewById(R.id.bookingScheduleDate);
                                TextView bookingScheduleTime = view.findViewById(R.id.bookingScheduleTime);
                                TextView bookingCountAdult = view.findViewById(R.id.bookingCountAdult);
                                TextView bookingCountChild = view.findViewById(R.id.bookingCountChild);
                                TextView bookingTransportName = view.findViewById(R.id.bookingTransportName);
                                TextView bookingDriverName = view.findViewById(R.id.bookingDriverName);
                                TextView bookingPlateNumber = view.findViewById(R.id.bookingPlateNumber);
                                TextView bookingPrice = view.findViewById(R.id.bookingPrice);
                                Button btnConfirmBooking = view.findViewById(R.id.btnConfirmBooking);
                                CircleImageView customerPhoto = view.findViewById(R.id.customerPhoto);

                                bookingsReference.whereEqualTo("reference_number", reference_number)
                                        .limit(1)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    if (task.getResult().getDocuments() != null) {
                                                        String uid = task.getResult().getDocuments().get(0).getString("uid");
                                                        String name = task.getResult().getDocuments().get(0).getString("name");
                                                        String contact_number = task.getResult().getDocuments().get(0).getString("contact_number");
                                                        String location_from = task.getResult().getDocuments().get(0).getString("location_from");
                                                        String location_to = task.getResult().getDocuments().get(0).getString("location_to");
                                                        String schedule_date = task.getResult().getDocuments().get(0).getString("schedule_date");
                                                        String schedule_time = task.getResult().getDocuments().get(0).getString("schedule_time");
                                                        String transport_name = task.getResult().getDocuments().get(0).getString("transport_name");
                                                        String driver_name = task.getResult().getDocuments().get(0).getString("driver_name");
                                                        String plate_number = task.getResult().getDocuments().get(0).getString("plate_number");
                                                        int count_adult = task.getResult().getDocuments().get(0).getLong("count_adult").intValue();
                                                        int count_child = task.getResult().getDocuments().get(0).getLong("count_child").intValue();
                                                        float price = task.getResult().getDocuments().get(0).getLong("price").floatValue();

                                                        bookingCustomerName.setText(name);
                                                        bookingContactNumber.setText(contact_number);
                                                        bookingReferenceNumber.setText(reference_number);
                                                        bookingLocationFrom.setText(location_from);
                                                        bookingLocationTo.setText(location_to);
                                                        bookingScheduleDate.setText(schedule_date);
                                                        bookingScheduleTime.setText(schedule_time);
                                                        bookingCountAdult.setText(String.valueOf(count_adult));
                                                        bookingCountChild.setText(String.valueOf(count_child));
                                                        bookingTransportName.setText(transport_name);
                                                        bookingDriverName.setText(driver_name);
                                                        bookingPlateNumber.setText(plate_number);
                                                        bookingPrice.setText(String.valueOf(price));

                                                        usersReference.document(uid)
                                                                .get()
                                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                        if (task.isSuccessful()) {
                                                                            String photo_url = task.getResult().getString("photo_url");
                                                                            String email = task.getResult().getString("email");

                                                                            if (photo_url != null) {
                                                                                Glide.with(getActivity())
                                                                                        .load(photo_url)
                                                                                        .into(customerPhoto);
                                                                            }
                                                                            bookingCustomerEmail.setText(email);
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                }
                                            }
                                        });

                                btnConfirmBooking.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        btnConfirmBooking.setEnabled(false);
                                        final ACProgressFlower dialog = new ACProgressFlower.Builder(getActivity())
                                                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                                                .themeColor(getResources().getColor(R.color.white))
                                                .text("Processing...")
                                                .fadeColor(Color.DKGRAY).build();
                                        dialog.show();
                                        if (reference_number != null) {
                                            getReferenceNumber(alertDialog, dialog, reference_number);
                                        } else {
                                            dialog.dismiss();
                                            alertDialog.dismiss();
                                            Toast.makeText(getActivity(), "Invalid QR Code, please scan again.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                                alertDialog.show();
                            }
                        } else {
                            Toast.makeText(context, "Invalid QR code.", Toast.LENGTH_SHORT).show();
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

    private void getReferenceNumber(AlertDialog alertDialog, ACProgressFlower dialog, String reference_number) {
        bookingsReference.whereEqualTo("reference_number", reference_number)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String id = task.getResult().getDocuments().get(0).getId();
                            updateBooking(alertDialog, dialog, id);
                        } else {
                            dialog.dismiss();
                            alertDialog.dismiss();
                            Toast.makeText(getActivity(), "Update booking failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateBooking(AlertDialog alertDialog, ACProgressFlower dialog, String id) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", "done");
        hashMap.put("timestamp", generateTimestamp());

        bookingsReference.document(id)
                .update(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            alertDialog.dismiss();
                            Toast.makeText(getActivity(), "Success!", Toast.LENGTH_SHORT).show();
                        } else {
                            dialog.dismiss();
                            alertDialog.dismiss();
                            Toast.makeText(getActivity(), "Update booking failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        Dexter.withActivity(getActivity())
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
                        Toast.makeText(getActivity(), "You must enable this permission.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(com.karumi.dexter.listener.PermissionRequest permission, PermissionToken token) {

                    }
                }).check();
    }

    @Override
    public void onPause() {
        super.onPause();
        Dexter.withActivity(getActivity())
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
                        Toast.makeText(getActivity(), "You must enable this permission.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(com.karumi.dexter.listener.PermissionRequest permission, PermissionToken token) {

                    }
                }).check();
    }
}