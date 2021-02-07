package com.opustech.bookvan.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.opustech.bookvan.MainActivity;
import com.opustech.bookvan.R;
import com.opustech.bookvan.ui.book.BookFragment;
import com.opustech.bookvan.ui.rent.RentFragment;

import org.imaginativeworld.whynotimagecarousel.CarouselItem;
import org.imaginativeworld.whynotimagecarousel.ImageCarousel;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private CardView btnBooking, btnRental, btnPartner;

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        btnBooking = root.findViewById(R.id.btnBooking);
        btnRental = root.findViewById(R.id.btnRental);
        btnPartner = root.findViewById(R.id.btnPartner);

        ImageCarousel imageCarousel = root.findViewById(R.id.carousel);
        List<CarouselItem> list = new ArrayList<>();

        btnBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firebaseAuth.getCurrentUser() == null) {
                    ((MainActivity)getActivity()).openLoginDialog();
                }
                else {
                    ((MainActivity)getActivity()).replaceFragment(BookFragment.class);
                }
            }
        });

        btnRental.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firebaseAuth.getCurrentUser() == null) {
                    ((MainActivity)getActivity()).openLoginDialog();
                }
                else {
                    ((MainActivity)getActivity()).replaceFragment(RentFragment.class);
                }
            }
        });

        return root;
    }
}