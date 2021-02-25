package com.opustech.bookvan.ui.fragments.admin.rentals;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.opustech.bookvan.R;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.*;

public class RentalsFormFragment extends Fragment {

    private LinearLayout uploadedImage;

    private ImageView btnAddImage;

    private List<Uri> imageUriList = new ArrayList<>();
    private List<String> imageUrlList = new ArrayList<>();

    final static int PICK_IMAGE = 1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_rentals_form, container, false);

        uploadedImage = root.findViewById(R.id.postImagesContainer);
        btnAddImage = root.findViewById(R.id.btnAddImage);

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

        return root;
    }

    private void loadImage(String imageUri) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.upload_image_item_layout, uploadedImage, false);
        final ImageView imageItem = view.findViewById(R.id.imageItem);
        final ImageButton btnClose = view.findViewById(R.id.btnClose);
        if (imageItem.getParent() != null) {
            ((ViewGroup) imageItem.getParent()).removeView(imageItem);
        }
        uploadedImage.addView(imageItem);
        Glide.with(this)
                .load(imageUri)
                .into(imageItem);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadedImage.removeView(imageItem);
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
                        loadImage(imageUri.toString());
                        currentImageSelect = currentImageSelect + 1;
                    }
                } else {
                    Uri imageUri = data.getData();
                    imageUriList.add(imageUri);
                    loadImage(imageUri.toString());
                }

            }

        }
    }
}