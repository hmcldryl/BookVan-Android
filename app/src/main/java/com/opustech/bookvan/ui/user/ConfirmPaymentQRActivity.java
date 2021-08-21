package com.opustech.bookvan.ui.user;

import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.sumimakito.awesomeqr.AwesomeQrRenderer;
import com.github.sumimakito.awesomeqr.RenderResult;
import com.github.sumimakito.awesomeqr.option.RenderOption;
import com.github.sumimakito.awesomeqr.option.color.Color;
import com.github.sumimakito.awesomeqr.option.logo.Logo;
import com.opustech.bookvan.R;

import se.simbio.encryption.Encryption;

public class ConfirmPaymentQRActivity extends AppCompatActivity {

    private ImageView qrPlaceholder;

    private final String OT_KEY = "TzA8gEdNHRphj6Hu";
    private final String OT_SALT = "N5yH5dvCqskEfCGd";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_confirm_booking_qr);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Confirm Payment");
        getSupportActionBar().setSubtitle("Generate Transaction QR");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        qrPlaceholder = findViewById(R.id.qrPlaceholder);

        try {
            RenderResult result = AwesomeQrRenderer.render(renderQR());
            qrPlaceholder.setImageBitmap(result.getBitmap());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String encryptString(String data) {
        byte[] iv = new byte[16];
        Encryption encryption = Encryption.getDefault(OT_KEY, OT_SALT, iv);
        return encryption.encryptOrNull(data);
    }

    private RenderOption renderQR() {
        RenderOption renderOption = new RenderOption();
        if (getIntent().getStringExtra("type").equals("booking")) {
            renderOption.setContent(encryptString(getIntent().getStringExtra("booking_id"))); // content to encode
        } else if (getIntent().getStringExtra("type").equals("rental")) {
            renderOption.setContent(encryptString(getIntent().getStringExtra("rental_id"))); // content to encode
        }
        renderOption.setSize(500);
        renderOption.setRoundedPatterns(true);
        renderOption.setClearBorder(true);
        renderOption.setBorderWidth(0);
        renderOption.setColor(loadColor());
        renderOption.setLogo(loadLogo());
        return renderOption;
    }

    private Color loadColor() {
        Color color = new Color();
        color.setDark(getResources().getColor(R.color.colorAccent));
        color.setLight(getResources().getColor(R.color.white));
        color.setBackground(getResources().getColor(R.color.colorPrimary));
        return color;
    }

    private Logo loadLogo() {
        Logo logo = new Logo();
        logo.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.qr_logo));
        logo.setBorderRadius(10); // radius for logo's corners
        logo.setBorderWidth(10); // width of the border to be added around the logo
        logo.setScale(0.3f); // scale for the logo in the QR code
        logo.setClippingRect(new RectF(0, 0, 0, 0)); // crop the logo image before applying it to the QR code
        return logo;
    }
}