package com.opustech.bookvan.ui.transport;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.opustech.bookvan.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.List;
import java.util.HashMap;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import de.hdodenhof.circleimageview.CircleImageView;

public class TransportProfileAdminActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference partnersReference;

    private TextView companyName,
            companyDescription,
            companyAddress,
            companyTelephoneNumber,
            companyCellphoneNumber,
            companyEmail,
            companyWebsite;
    private CircleImageView companyPhoto;
    private ImageView companyBanner;

    final static int PICK_COMPANY_PHOTO = 1;
    final static int PICK_COMPANY_BANNER = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport_profile_admin);

        firebaseFirestore = FirebaseFirestore.getInstance();
        partnersReference = firebaseFirestore.collection("partners");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        getSupportActionBar().setTitle(getIntent().getStringExtra("uid"));

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.btnEdit) {
                    Intent intent = new Intent(TransportProfileAdminActivity.this, TransportEditProfileAdminActivity.class);
                    intent.putExtra("uid", getIntent().getStringExtra("uid"));
                    startActivity(intent);
                }
                return false;
            }
        });

        initializeUi();

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_profile, menu);
        return true;
    }

    private void initializeUi() {
        companyPhoto = findViewById(R.id.companyPhoto);
        companyBanner = findViewById(R.id.companyBanner);
        companyName = findViewById(R.id.companyName);
        companyDescription = findViewById(R.id.companyDescription);
        companyAddress = findViewById(R.id.companyAddress);
        companyTelephoneNumber = findViewById(R.id.companyTelephoneNumber);
        companyCellphoneNumber = findViewById(R.id.companyCellphoneNumber);
        companyEmail = findViewById(R.id.companyEmail);
        companyWebsite = findViewById(R.id.companyWebsite);
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
                        .load(photo_url)
                        .into(companyBanner);
            }
        }

        companyName.setText(name);
        companyDescription.setText(description);
        companyAddress.setText(address);

        if (telephone_number.size() == 1) {
            companyTelephoneNumber.setText(telephone_number.get(0));
        } else {
            companyTelephoneNumber.setText(formatArrayData(telephone_number));
        }

        if (cellphone_number.size() == 1) {
            companyCellphoneNumber.setText(telephone_number.get(0));
        } else {
            companyCellphoneNumber.setText(formatArrayData(cellphone_number));
        }

        companyEmail.setText(email);
        companyWebsite.setText(website);

        Toast.makeText(this, "Update finished.", Toast.LENGTH_SHORT).show();
    }

    private String formatArrayData(List<String> arrayList) {
        String result = null;
        if (arrayList.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            for (String s : arrayList) {
                stringBuilder.append(s).append("\n");
            }
            result = stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString();
        }
        return result;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_COMPANY_PHOTO && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(TransportProfileAdminActivity.this);
        }
        if (requestCode == PICK_COMPANY_BANNER && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(16, 9)
                    .start(TransportProfileAdminActivity.this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                if (requestCode == PICK_COMPANY_PHOTO) {
                    uploadCompanyPhoto(result);
                }
                if (requestCode == PICK_COMPANY_BANNER) {
                    uploadCompanyBanner(result);
                }
            } else {
                Toast.makeText(TransportProfileAdminActivity.this, "Image cannot be cropped. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadCompanyPhoto(CropImage.ActivityResult result) {
        final ACProgressFlower dialog = new ACProgressFlower.Builder(TransportProfileAdminActivity.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(getResources().getColor(R.color.white))
                .text("Uploading...")
                .fadeColor(Color.DKGRAY).build();
        dialog.show();
        String imageFilename = "IMG_" + getIntent().getStringExtra("uid") + "_COMPANY_PHOTO" + ".jpg";
        Uri resultUri = result.getUri();
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
                            Toast.makeText(TransportProfileAdminActivity.this, "Image upload failed. Please try again.", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });
    }

    private void uploadCompanyBanner(CropImage.ActivityResult result) {
        final ACProgressFlower dialog = new ACProgressFlower.Builder(TransportProfileAdminActivity.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(getResources().getColor(R.color.white))
                .text("Uploading...")
                .fadeColor(Color.DKGRAY).build();
        dialog.show();
        String imageFilename = "IMG_" + getIntent().getStringExtra("uid") + "_COMPANY_BANNER" + ".jpg";
        Uri resultUri = result.getUri();
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
                            Toast.makeText(TransportProfileAdminActivity.this, "Image upload failed. Please try again.", Toast.LENGTH_SHORT).show();
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
                            Glide.with(TransportProfileAdminActivity.this)
                                    .load(imageUrl)
                                    .into(companyPhoto);
                            Toast.makeText(TransportProfileAdminActivity.this, "Image upload success.", Toast.LENGTH_SHORT).show();
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
                            Glide.with(TransportProfileAdminActivity.this)
                                    .load(imageUrl)
                                    .into(companyBanner);
                            Toast.makeText(TransportProfileAdminActivity.this, "Image upload success.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}