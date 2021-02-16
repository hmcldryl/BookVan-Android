package com.opustech.bookvan.ui.fragments.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.opustech.bookvan.MainActivity;
import com.opustech.bookvan.R;

import org.imaginativeworld.whynotimagecarousel.CarouselItem;
import org.imaginativeworld.whynotimagecarousel.ImageCarousel;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private CardView btnBooking, btnRental, btnPartner;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        btnBooking = root.findViewById(R.id.btnBooking);
        btnRental = root.findViewById(R.id.btnRental);
        btnPartner = root.findViewById(R.id.btnPartner);

        ImageCarousel imageCarousel = root.findViewById(R.id.carousel);
        List<CarouselItem> list = new ArrayList<>();

        btnBooking.setOnClickListener(new View.OnClickListener() {
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
        return root;
    }
}