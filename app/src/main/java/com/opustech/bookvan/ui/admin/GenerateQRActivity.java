package com.opustech.bookvan.ui.admin;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.github.sumimakito.awesomeqr.AwesomeQrRenderer;
import com.github.sumimakito.awesomeqr.RenderResult;
import com.github.sumimakito.awesomeqr.option.RenderOption;
import com.github.sumimakito.awesomeqr.option.color.Color;
import com.github.sumimakito.awesomeqr.option.logo.Logo;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.opustech.bookvan.R;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGSaver;
import se.simbio.encryption.Encryption;

public class GenerateQRActivity extends AppCompatActivity {

    private TextInputLayout inputOrdinaryQR;
    private ImageView qrPlaceholder;
    private MaterialButton btnSave;

    private final String OT_KEY = "TzA8gEdNHRphj6Hu";
    private final String OT_SALT = "N5yH5dvCqskEfCGd";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_generate_qr);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Generate QR");
        getSupportActionBar().setSubtitle("BookVan Custom QR");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        inputOrdinaryQR = findViewById(R.id.inputOrdinaryQR);
        qrPlaceholder = findViewById(R.id.qrPlaceholder);
        btnSave = findViewById(R.id.btnSave);

        inputOrdinaryQR.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!inputOrdinaryQR.getEditText().getText().toString().isEmpty()) {
                    try {
                        RenderResult result = AwesomeQrRenderer.render(renderQR(inputOrdinaryQR.getEditText().getText().toString()));
                        qrPlaceholder.setImageBitmap(result.getBitmap());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (inputOrdinaryQR.getEditText().getText().toString().isEmpty()) {
                    qrPlaceholder.setImageResource(0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(GenerateQRActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(GenerateQRActivity.this, "You must allow storage permissions for this feature.", Toast.LENGTH_SHORT).show();
                } else {
                    String savePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/BOOKVAN/";
                    try {
                        RenderResult result = AwesomeQrRenderer.render(renderQR(inputOrdinaryQR.getEditText().getText().toString()));
                        String qr_filename = "BOOKVAN_QR_" + "test";
                        QRGSaver qrgSaver = new QRGSaver();
                        qrgSaver.save(savePath, qr_filename, result.getBitmap(), QRGContents.ImageType.IMAGE_JPEG);
                        Toast.makeText(GenerateQRActivity.this, "QR code saved in Downloads/BOOKVAN/.", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private String encryptString(String data) {
        byte[] iv = new byte[16];
        Encryption encryption = Encryption.getDefault(OT_KEY, OT_SALT, iv);
        return encryption.encryptOrNull(data);
    }

    private RenderOption renderQR(String content) {
        RenderOption renderOption = new RenderOption();
        //renderOption.setContent(encryptString(content)); // content to encode
        renderOption.setContent(content); // content to encode
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