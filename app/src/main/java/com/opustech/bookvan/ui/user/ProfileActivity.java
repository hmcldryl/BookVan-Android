package com.opustech.bookvan.ui.user;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.QuerySnapshot;
import com.opustech.bookvan.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference, bookingsReference;

    private CircleImageView profilePhoto;
    private TextView profileName, profileEmail, profileContactNumber, profileSuccessfulTrips, profileTotalBookings;

    final static int PICK_IMAGE = 1;

    private String name = "";
    private String email = "";
    private String contact_number = "";
    private String photo_url = "";

    private FloatingActionButton btnEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        usersReference = firebaseFirestore.collection("users");
        bookingsReference = firebaseFirestore.collection("bookings");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Your Profile");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnEdit = findViewById(R.id.btnEdit);
        profilePhoto = findViewById(R.id.profilePhoto);
        profileName = findViewById(R.id.profileName);
        profileEmail = findViewById(R.id.profileEmail);
        profileContactNumber = findViewById(R.id.profileContactNumber);
        profileSuccessfulTrips = findViewById(R.id.profileSuccessfulTrips);
        profileTotalBookings = findViewById(R.id.profileTotalBookings);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startUpdateInfoDialog();
            }
        });

        profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE);
            }
        });

        loadSuccessfulTrips();
        loadTotalBookings();
    }

    private void startUpdateInfoDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        final AlertDialog alertDialog = builder.create();
        if (!alertDialog.isShowing()) {
            final View dialogView = LayoutInflater.from(ProfileActivity.this).inflate(R.layout.dialog_edit_profile, null);
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.setCancelable(true);
            alertDialog.setView(dialogView);

            TextInputLayout inputName = dialogView.findViewById(R.id.inputName);
            TextInputLayout inputContactNumber = dialogView.findViewById(R.id.inputContactNumber);

            MaterialButton btnConfirm = dialogView.findViewById(R.id.btnConfirm);

            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    btnConfirm.setEnabled(false);
                    inputName.setEnabled(false);
                    inputContactNumber.setEnabled(false);
                    final ACProgressFlower dialog = new ACProgressFlower.Builder(ProfileActivity.this)
                            .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                            .themeColor(getResources().getColor(R.color.white))
                            .text("Updating...")
                            .fadeColor(Color.DKGRAY).build();
                    dialog.show();

                    if (inputName.getEditText().getText().toString().isEmpty()) {
                        btnConfirm.setEnabled(true);
                        inputName.setEnabled(true);
                        inputContactNumber.setEnabled(true);
                        dialog.dismiss();
                        inputName.setError("Please enter your name.");
                    } else if (inputContactNumber.getEditText().getText().toString().isEmpty()) {
                        btnConfirm.setEnabled(true);
                        inputName.setEnabled(true);
                        inputContactNumber.setEnabled(true);
                        dialog.dismiss();
                        inputName.setError("Please enter your contact number.");
                    } else {
                        String name = inputName.getEditText().getText().toString();
                        String contact_number = inputContactNumber.getEditText().getText().toString();

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("name", name);
                        hashMap.put("contact_number", contact_number);

                        usersReference.document(firebaseAuth.getCurrentUser().getUid())
                                .update(hashMap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        dialog.dismiss();
                                        alertDialog.dismiss();
                                        Toast.makeText(ProfileActivity.this, "Success.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                }
            });

            alertDialog.show();
        }
    }

    private void loadSuccessfulTrips() {
        bookingsReference.whereEqualTo("uid", firebaseAuth.getCurrentUser().getUid())
                .whereEqualTo("status", "done")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null) {
                            if (value.size() >= 0) {
                                profileSuccessfulTrips.setText(String.valueOf(value.size()));
                            }
                        }
                    }
                });
    }

    private void loadTotalBookings() {
        bookingsReference.whereEqualTo("uid", firebaseAuth.getCurrentUser().getUid())
                //.whereIn("status", Arrays.asList("confirmed", "done", "cancelled"))
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null) {
                            if (value.size() >= 0) {
                                profileTotalBookings.setText(String.valueOf(value.size()));
                            }
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
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
                                        contact_number = value.getString("contact_number");
                                        photo_url = value.getString("photo_url");

                                        if (photo_url != null) {
                                            if (!photo_url.isEmpty()) {
                                                Glide.with(ProfileActivity.this)
                                                        .load(photo_url)
                                                        .into(profilePhoto);
                                            }
                                        }
                                        profileName.setText(name);
                                        profileEmail.setText(email);
                                        profileContactNumber.setText(contact_number);
                                    }
                                }
                            }
                        });
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            uploadImage(imageUri);
        }
    }

    private void uploadImage(Uri resultUri) {
        final ACProgressFlower dialog = new ACProgressFlower.Builder(ProfileActivity.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(getResources().getColor(R.color.white))
                .text("Uploading...")
                .fadeColor(Color.DKGRAY).build();
        dialog.show();
        String imageFilename = "IMG_" + firebaseAuth.getCurrentUser().getUid() + ".jpg";
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
                            Toast.makeText(ProfileActivity.this, "Image upload failed. Please try again.", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });
    }

    private void updatePhotoUrl(ACProgressFlower dialog, Uri uri) {
        String imageUrl = uri.toString();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("photo_url", imageUrl);
        usersReference.document(firebaseAuth.getCurrentUser().getUid())
                .update(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            Glide.with(ProfileActivity.this)
                                    .load(imageUrl)
                                    .into(profilePhoto);
                            Toast.makeText(ProfileActivity.this, "Image upload success.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}