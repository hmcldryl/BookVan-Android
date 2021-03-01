package com.opustech.bookvan.ui.fragments.admin.rentals;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.opustech.bookvan.ProfileActivity;
import com.opustech.bookvan.R;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

import static android.app.Activity.*;

public class RentalsFormFragment extends Fragment {

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference rentalsReference;

    private LinearLayout uploadedImage;
    private ImageView btnAddImage;
    private TextInputLayout rentalsVanModel, rentalsVanOwner, rentalsVanDescription, rentalsPrice;
    private Button btnAddToListing;

    private List<Uri> imageUriList = new ArrayList<>();
    private List<String> imageUrlList = new ArrayList<>();

    private Context context;

    final static int PICK_IMAGE = 1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_rentals_form, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        rentalsReference = firebaseFirestore.collection("rentals");

        initializeUi(root);

        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(intent, PICK_IMAGE);
            }
        });

        btnAddToListing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disableInput();
                checkInput();
            }
        });

        return root;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private void initializeUi(View root) {
        uploadedImage = root.findViewById(R.id.postImagesContainer);
        btnAddImage = root.findViewById(R.id.btnAddImage);
        rentalsVanModel = root.findViewById(R.id.rentalsVanModel);
        rentalsVanOwner = root.findViewById(R.id.rentalsVanOwner);
        rentalsVanDescription = root.findViewById(R.id.rentalsVanDescription);
        rentalsPrice = root.findViewById(R.id.price);
        btnAddToListing = root.findViewById(R.id.btnAddToListing);
    }

    private void checkInput() {
        String van_model = rentalsVanModel.getEditText().getText().toString();
        String owner = rentalsVanOwner.getEditText().getText().toString();
        String description = rentalsVanDescription.getEditText().getText().toString();
        float price = Float.parseFloat(rentalsPrice.getEditText().getText().toString());

        if (van_model.isEmpty()) {
            enableInput();
            rentalsVanModel.setError("Please enter van model.");
        } else if (owner.isEmpty()) {
            enableInput();
            rentalsVanOwner.setError("Please enter the owner's name.");
        } else if (description.isEmpty()) {
            enableInput();
            rentalsVanDescription.setError("Please enter van description.");
        } else if (rentalsPrice.getEditText().getText().toString().isEmpty()) {
            enableInput();
            rentalsPrice.setError("Please enter rental price.");
        } else if (imageUriList.size() == 0) {
            enableInput();
            Toast.makeText(context, "Please add at least one (1) photo.", Toast.LENGTH_SHORT).show();
        } else {
            addToListing(van_model, owner, description, price);
        }
    }

    private String generateString() {
        byte[] array = new byte[7];
        new Random().nextBytes(array);
        return new String(array, StandardCharsets.UTF_8);
    }

    private void addToListing(String van_model, String owner, String description, float price) {
        final ACProgressFlower dialog = new ACProgressFlower.Builder(getActivity())
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(getResources().getColor(R.color.colorAccent))
                .text("Processing...")
                .fadeColor(Color.DKGRAY).build();
        dialog.show();
        if (imageUriList.size() == 0) {
            for (int i = 0; i < imageUriList.size() - 1; i++) {
                Uri uri = imageUriList.get(i);
                String imageFilename = "IMG_" + generateString() + ".jpg";
                FirebaseStorage.getInstance().getReference()
                        .child("images")
                        .child("rentals")
                        .child(imageFilename)
                        .putFile(uri)
                        .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();
                                    result.addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            if (task.isSuccessful()) {
                                                String photo_url = task.getResult().toString();
                                                imageUrlList.add(photo_url);
                                                if (imageUriList.size() == imageUrlList.size()) {
                                                    HashMap<String, Object> hashMap = new HashMap<>();
                                                    hashMap.put("van_model", van_model);
                                                    hashMap.put("owner", owner);
                                                    hashMap.put("description", description);
                                                    hashMap.put("photo_url", imageUrlList);
                                                    hashMap.put("timestamp", generateTimestamp());
                                                    hashMap.put("price", price);

                                                    rentalsReference.add(hashMap)
                                                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                                    if (task.isSuccessful()) {
                                                                        imageUrlList.clear();
                                                                        imageUriList.clear();
                                                                        uploadedImage.removeAllViews();
                                                                        dialog.dismiss();
                                                                        enableInput();
                                                                        Toast.makeText(context, "Success.", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                }
                                            }
                                        }
                                    });
                                } else {
                                    dialog.dismiss();
                                    enableInput();
                                    Toast.makeText(context, "Image upload failed. Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        } else {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("van_model", van_model);
            hashMap.put("owner", owner);
            hashMap.put("description", description);
            hashMap.put("photo_url", imageUrlList);
            hashMap.put("timestamp", generateTimestamp());
            hashMap.put("price", price);

            rentalsReference.add(hashMap)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                imageUrlList.clear();
                                imageUriList.clear();
                                uploadedImage.removeAllViews();
                                dialog.dismiss();
                                enableInput();
                                Toast.makeText(context, "Success.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private String generateTimestamp() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        return format.format(Calendar.getInstance().getTime());
    }

    private void enableInput() {
        btnAddImage.setEnabled(true);
        rentalsVanModel.setEnabled(true);
        rentalsVanOwner.setEnabled(true);
        rentalsVanDescription.setEnabled(true);
        rentalsPrice.setEnabled(true);
    }

    private void disableInput() {
        btnAddImage.setEnabled(false);
        rentalsVanModel.setEnabled(false);
        rentalsVanOwner.setEnabled(false);
        rentalsVanDescription.setEnabled(false);
        rentalsPrice.setEnabled(false);
    }

    private void loadImage(String imageUri, int position) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.upload_image_item_layout, uploadedImage, false);
        final ImageView imageItem = view.findViewById(R.id.imageItem);
        final ImageButton btnClose = view.findViewById(R.id.btnClose);
        if (imageItem.getParent() != null) {
            ((ViewGroup) imageItem.getParent()).removeView(imageItem);
            ((ViewGroup) btnClose.getParent()).removeView(btnClose);
        }
        uploadedImage.addView(imageItem);
        uploadedImage.addView(btnClose);
        Glide.with(this)
                .load(imageUri)
                .into(imageItem);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageUriList.remove(position);
                uploadedImage.removeView(imageItem);
                uploadedImage.removeView(btnClose);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && data != null) {
            if (resultCode == RESULT_OK) {
                if (data.getClipData() != null) {
                    int countClipData = data.getClipData().getItemCount();
                    int currentImageSelect = 0;
                    while (currentImageSelect < countClipData) {
                        Uri imageUri = data.getClipData().getItemAt(currentImageSelect).getUri();
                        imageUriList.add(imageUri);
                        loadImage(imageUri.toString(), currentImageSelect);
                        currentImageSelect = currentImageSelect + 1;
                    }
                } else {
                    Uri imageUri = data.getData();
                    imageUriList.add(imageUri);
                    loadImage(imageUri.toString(), imageUriList.size() - 1);
                }

            }

        }
    }
}