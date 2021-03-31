package com.opustech.bookvan.ui.transport;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.opustech.bookvan.R;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import se.simbio.encryption.Encryption;

public class TransportAdminBookingQRActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference, bookingsReference;

    private ImageView qrPlaceholder;

    private final String OT_KEY = "TzA8gEdNHRphj6Hu";
    private final String OT_SALT = "N5yH5dvCqskEfCGd";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport_admin_booking_qr);

        firebaseFirestore = FirebaseFirestore.getInstance();
        usersReference = firebaseFirestore.collection("users");
        bookingsReference = firebaseFirestore.collection("bookings");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Confirm Payment");
        getSupportActionBar().setSubtitle("Generate QR");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        qrPlaceholder = findViewById(R.id.qrPlaceholder);
        qrPlaceholder.setImageBitmap(generateQR());
    }

    private Bitmap generateQR() {
        QRGEncoder qrgEncoder = new QRGEncoder(encryptString(getIntent().getStringExtra("uid")), null, QRGContents.Type.TEXT, 500);
        qrgEncoder.setColorBlack(getResources().getColor(R.color.white));
        qrgEncoder.setColorWhite(getResources().getColor(R.color.colorQRBG));
        return qrgEncoder.getBitmap();
    }

    private String encryptString(String data) {
        byte[] iv = new byte[16];
        Encryption encryption = Encryption.getDefault(OT_KEY, OT_SALT, iv);
        return encryption.encryptOrNull(data);
    }
}