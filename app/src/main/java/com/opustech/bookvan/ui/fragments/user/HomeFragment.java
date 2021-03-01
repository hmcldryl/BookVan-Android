package com.opustech.bookvan.ui.fragments.user;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.opustech.bookvan.MainActivity;
import com.opustech.bookvan.R;

import org.imaginativeworld.whynotimagecarousel.CarouselItem;
import org.imaginativeworld.whynotimagecarousel.ImageCarousel;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference systemReference, destinationsReference;

    private CardView btnBooking, btnRental, btnPartner, btnBookElnido, btnBookTaytay;
    private ImageView imageElnido, imageTaytay;
    private ImageCarousel imageCarousel;

    private Context context;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        systemReference = firebaseFirestore.collection("system").document("data");
        destinationsReference = firebaseFirestore.collection("system").document("destinations");

        btnBooking = root.findViewById(R.id.btnBooking);
        btnRental = root.findViewById(R.id.btnRental);
        btnPartner = root.findViewById(R.id.btnPartner);
        btnBookElnido = root.findViewById(R.id.btnBookElnido);
        btnBookTaytay = root.findViewById(R.id.btnBookTaytay);
        imageElnido = root.findViewById(R.id.imageElnido);
        imageTaytay = root.findViewById(R.id.imageTaytay);
        imageCarousel = root.findViewById(R.id.carousel);

        updateUi();

        btnBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).replaceFragment(BookFragment.class);
            }
        });

        btnBookElnido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).replaceFragment(BookFragment.class);
            }
        });

        btnBookTaytay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).replaceFragment(BookFragment.class);
            }
        });

        btnRental.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).replaceFragment(RentFragment.class);
            }
        });

        btnPartner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).replaceFragment(PartnersFragment.class);
            }
        });
        return root;
    }

    private void updateUi() {
        systemReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> photo_url = (List<String>) task.getResult().get("home_image_carousel_urls");
                            if (photo_url != null) {
                                List<CarouselItem> data = new ArrayList<>();
                                for (int i = 0; i < photo_url.size(); i++) {
                                    data.add(new CarouselItem(photo_url.get(i)));
                                }
                                imageCarousel.addData(data);
                            }
                            else {
                                imageCarousel.setVisibility(View.GONE);
                            }
                        }
                    }
                });

        destinationsReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            String elnido_image_url = task.getResult().getString("elnido_image_url");
                            String taytay_image_url = task.getResult().getString("taytay_image_url");

                            if (elnido_image_url != null) {
                                if (!elnido_image_url.isEmpty()) {
                                    Glide.with(context)
                                            .load(elnido_image_url)
                                            .into(imageElnido);
                                }
                            }

                            if (taytay_image_url != null) {
                                if (!taytay_image_url.isEmpty()) {
                                    Glide.with(context)
                                            .load(taytay_image_url)
                                            .into(imageTaytay);
                                }
                            }
                        }
                    }
                });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }
}