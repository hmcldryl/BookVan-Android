package com.opustech.bookvan.ui.transport;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.opustech.bookvan.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import de.hdodenhof.circleimageview.CircleImageView;

public class TransportEditProfileAdminActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference partnersReference;

    private TextInputLayout companyName,
            companyDescription,
            companyAddress,
            companyTelephoneNumber,
            companyCellphoneNumber,
            companyEmail,
            companyWebsite;
    private CircleImageView companyPhoto;
    private ImageView companyBanner;
    private ImageButton btnAddTelephoneNumber, btnAddCellphoneNumber;
    private ChipGroup telephoneNumberCG, cellphoneNumberCG;
    private FloatingActionButton btnSave;

    final static int PICK_COMPANY_PHOTO = 1;
    final static int PICK_COMPANY_BANNER = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport_edit_profile_admin);

        firebaseFirestore = FirebaseFirestore.getInstance();
        partnersReference = firebaseFirestore.collection("partners");

        initializeUi();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        getSupportActionBar().setTitle("Edit Company Profile");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disableInput();
                inputCheck();
            }
        });

        companyPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_COMPANY_PHOTO);
            }
        });

        companyBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_COMPANY_BANNER);
            }
        });

        btnAddTelephoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!companyTelephoneNumber.getEditText().getText().toString().isEmpty()) {
                    final Chip chip = new Chip(TransportEditProfileAdminActivity.this);
                    String tag = companyTelephoneNumber.getEditText().getText().toString();
                    chip.setChipBackgroundColorResource(R.color.colorPrimary);
                    chip.setCloseIconVisible(true);
                    chip.setText(tag);
                    chip.setOnCloseIconClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            telephoneNumberCG.removeView(chip);
                        }
                    });
                    telephoneNumberCG.addView(chip);
                    companyTelephoneNumber.getEditText().getText().clear();
                }
            }
        });

        btnAddCellphoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!companyCellphoneNumber.getEditText().getText().toString().isEmpty()) {
                    final Chip chip = new Chip(TransportEditProfileAdminActivity.this);
                    String tag = companyCellphoneNumber.getEditText().getText().toString();
                    chip.setChipBackgroundColorResource(R.color.colorPrimary);
                    chip.setCloseIconVisible(true);
                    chip.setText(tag);
                    chip.setOnCloseIconClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            cellphoneNumberCG.removeView(chip);
                        }
                    });
                    cellphoneNumberCG.addView(chip);
                    companyCellphoneNumber.getEditText().getText().clear();
                }
            }
        });
    }

    private ArrayList<String> telephoneNumbers() {
        ArrayList<String> telephoneNumberArray = new ArrayList<>();
        for (int i = 0; i < telephoneNumberCG.getChildCount(); i++) {
            Chip chip = (Chip) telephoneNumberCG.getChildAt(i);
            telephoneNumberArray.add(chip.getText().toString());
        }
        return telephoneNumberArray;
    }

    private ArrayList<String> cellphoneNumbers() {
        ArrayList<String> cellphoneNumberArray = new ArrayList<>();
        for (int i = 0; i < cellphoneNumberCG.getChildCount(); i++) {
            Chip chip = (Chip) cellphoneNumberCG.getChildAt(i);
            cellphoneNumberArray.add(chip.getText().toString());
        }
        return cellphoneNumberArray;
    }

    private void inputCheck() {
        String name = companyName.getEditText().getText().toString();
        String description = companyDescription.getEditText().getText().toString();
        String address = companyAddress.getEditText().getText().toString();
        List<String> telephone_number = telephoneNumbers();
        List<String> cellphone_number = cellphoneNumbers();
        String email = companyEmail.getEditText().getText().toString();
        String website = companyWebsite.getEditText().getText().toString();

        if (name.isEmpty()) {
            enableInput();
            companyName.setError("Please enter a company name.");
        } else if (description.isEmpty()) {
            enableInput();
            companyDescription.setError("Please enter a description.");
        } else if (address.isEmpty()) {
            enableInput();
            companyAddress.setError("Please enter an address.");
        } else if (email.isEmpty()) {
            enableInput();
            companyEmail.setError("Please enter an email.");
        } else {
            updateInfo(name, description, address, email, website, telephone_number, cellphone_number);
        }

    }

    private void updateInfo(String name, String description, String address, String email, String website, List<String> telephone_number, List<String> cellphone_number) {
        final ACProgressFlower dialog = new ACProgressFlower.Builder(TransportEditProfileAdminActivity.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(getResources().getColor(R.color.white))
                .text("Uploading...")
                .fadeColor(Color.DKGRAY).build();
        dialog.show();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", name);
        hashMap.put("description", description);
        hashMap.put("address", address);
        hashMap.put("email", email);
        hashMap.put("website", website);
        hashMap.put("telephone_number", telephone_number);
        hashMap.put("cellphone_number", cellphone_number);

        partnersReference.document(getIntent().getStringExtra("uid"))
                .update(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(TransportEditProfileAdminActivity.this, "Company profile updated successfully.", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                            finish();
                        }
                    }
                });
    }

    private void enableInput() {
        companyName.setEnabled(true);
        companyDescription.setEnabled(true);
        companyAddress.setEnabled(true);
        companyTelephoneNumber.setEnabled(true);
        btnAddTelephoneNumber.setEnabled(true);
        companyCellphoneNumber.setEnabled(true);
        btnAddCellphoneNumber.setEnabled(true);
        companyEmail.setEnabled(true);
        companyWebsite.setEnabled(true);
    }

    private void disableInput() {
        companyName.setEnabled(true);
        companyDescription.setEnabled(true);
        companyAddress.setEnabled(true);
        companyTelephoneNumber.setEnabled(true);
        btnAddTelephoneNumber.setEnabled(true);
        companyCellphoneNumber.setEnabled(true);
        btnAddCellphoneNumber.setEnabled(true);
        companyEmail.setEnabled(true);
        companyWebsite.setEnabled(true);
    }

    private void initializeUi() {
        btnSave = findViewById(R.id.btnSave);
        companyPhoto = findViewById(R.id.companyPhoto);
        companyBanner = findViewById(R.id.companyBanner);
        companyName = findViewById(R.id.inputCompanyName);
        companyDescription = findViewById(R.id.inputCompanyDescription);
        companyAddress = findViewById(R.id.inputCompanyAddress);
        companyTelephoneNumber = findViewById(R.id.inputCompanyTelephone);
        telephoneNumberCG = findViewById(R.id.telephoneNumberChipGroup);
        btnAddTelephoneNumber = findViewById(R.id.btnAddTelephoneNumber);
        cellphoneNumberCG = findViewById(R.id.cellphoneNumberChipGroup);
        btnAddCellphoneNumber = findViewById(R.id.btnAddCellphoneNumber);
        companyCellphoneNumber = findViewById(R.id.inputCompanyCellphone);
        companyEmail = findViewById(R.id.inputCompanyEmail);
        companyWebsite = findViewById(R.id.inputCompanyWebsite);
    }

    @Override
    protected void onStart() {
        super.onStart();
        partnersReference.document(getIntent().getStringExtra("uid"))
                .addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null) {
                            if (value.exists()) {
                                String photo_url = value.getString("photo_url");
                                String banner_url = value.getString("banner_url");
                                String name = value.getString("name");
                                String description = value.getString("description");
                                String address = value.getString("address");
                                String email = value.getString("email");
                                String website = value.getString("website");
                                List<String> telephone_number = (List<String>) value.get("telephone_number");
                                List<String> cellphone_number = (List<String>) value.get("cellphone_number");
                                updateUi(photo_url, banner_url, name, description, address, email, website, telephone_number, cellphone_number);
                            }
                        }
                    }
                });
    }

    private void updateUi(String photo_url, String banner_url, String name, String description, String address, String email, String website, List<String> telephone_number, List<String> cellphone_number) {
        if (photo_url != null) {
            if (!photo_url.isEmpty()) {
                Glide.with(this)
                        .load(photo_url)
                        .into(companyPhoto);
            }
        }

        if (banner_url != null) {
            if (!banner_url.isEmpty()) {
                Glide.with(this)
                        .load(banner_url)
                        .into(companyBanner);
            }
        }

        if (companyName.getEditText().getText().toString().isEmpty()) {
            companyName.getEditText().setText(name);
        }
        if (companyDescription.getEditText().getText().toString().isEmpty()) {
            companyDescription.getEditText().setText(description);
        }
        if (companyAddress.getEditText().getText().toString().isEmpty()) {
            companyAddress.getEditText().setText(address);
        }
        if (companyEmail.getEditText().getText().toString().isEmpty()) {
            companyEmail.getEditText().setText(email);
        }
        if (companyWebsite.getEditText().getText().toString().isEmpty()) {
            companyWebsite.getEditText().setText(website);
        }

        if (telephoneNumberCG.getChildCount() == 0) {
            if (telephone_number.size() > 0) {
                for (int i = 0; i < telephone_number.size(); i++) {
                    final Chip chip = new Chip(TransportEditProfileAdminActivity.this);
                    String tag = telephone_number.get(i);
                    chip.setChipBackgroundColorResource(R.color.colorPrimary);
                    chip.setCloseIconVisible(true);
                    chip.setText(tag);
                    chip.setOnCloseIconClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            telephoneNumberCG.removeView(chip);
                        }
                    });
                    telephoneNumberCG.addView(chip);
                }
            }
        }

        if (cellphoneNumberCG.getChildCount() == 0) {
            if (cellphone_number.size() > 0) {
                for (int i = 0; i < cellphone_number.size(); i++) {
                    final Chip chip = new Chip(TransportEditProfileAdminActivity.this);
                    String tag = cellphone_number.get(i);
                    chip.setChipBackgroundColorResource(R.color.colorPrimary);
                    chip.setCloseIconVisible(true);
                    chip.setText(tag);
                    chip.setOnCloseIconClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            cellphoneNumberCG.removeView(chip);
                        }
                    });
                    cellphoneNumberCG.addView(chip);
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_COMPANY_PHOTO && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            uploadCompanyPhoto(imageUri);
        }
        if (requestCode == PICK_COMPANY_BANNER && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            uploadCompanyBanner(imageUri);
        }
    }

    private void uploadCompanyPhoto(Uri resultUri) {
        final ACProgressFlower dialog = new ACProgressFlower.Builder(TransportEditProfileAdminActivity.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(getResources().getColor(R.color.white))
                .text("Uploading...")
                .fadeColor(Color.DKGRAY).build();
        dialog.show();
        String imageFilename = "IMG_" + getIntent().getStringExtra("uid") + "_COMPANY_PHOTO" + ".jpg";
        FirebaseStorage.getInstance().getReference().child("images")
                .child(imageFilename)
                .putFile(resultUri)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();
                            result.addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        updatePhotoUrl(dialog, task.getResult());
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(TransportEditProfileAdminActivity.this, "Image upload failed. Please try again.", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });
    }

    private void uploadCompanyBanner(Uri resultUri) {
        final ACProgressFlower dialog = new ACProgressFlower.Builder(TransportEditProfileAdminActivity.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(getResources().getColor(R.color.white))
                .text("Uploading...")
                .fadeColor(Color.DKGRAY).build();
        dialog.show();
        String imageFilename = "IMG_" + getIntent().getStringExtra("uid") + "_COMPANY_BANNER" + ".jpg";
        FirebaseStorage.getInstance().getReference().child("images")
                .child(imageFilename)
                .putFile(resultUri)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();
                            result.addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        updateBannerUrl(dialog, task.getResult());
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(TransportEditProfileAdminActivity.this, "Image upload failed. Please try again.", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });
    }

    private void updatePhotoUrl(ACProgressFlower dialog, Uri uri) {
        String imageUrl = uri.toString();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("photo_url", imageUrl);
        partnersReference.document(getIntent().getStringExtra("uid"))
                .update(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            Glide.with(TransportEditProfileAdminActivity.this)
                                    .load(imageUrl)
                                    .into(companyPhoto);
                            Toast.makeText(TransportEditProfileAdminActivity.this, "Image upload success.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateBannerUrl(ACProgressFlower dialog, Uri uri) {
        String imageUrl = uri.toString();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("banner_url", imageUrl);
        partnersReference.document(getIntent().getStringExtra("uid"))
                .update(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            Glide.with(TransportEditProfileAdminActivity.this)
                                    .load(imageUrl)
                                    .into(companyBanner);
                            Toast.makeText(TransportEditProfileAdminActivity.this, "Image upload success.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}