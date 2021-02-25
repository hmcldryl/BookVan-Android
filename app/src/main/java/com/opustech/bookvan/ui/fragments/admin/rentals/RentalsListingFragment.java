package com.opustech.bookvan.ui.fragments.admin.rentals;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.opustech.bookvan.R;
import com.opustech.bookvan.adapters.transport.AdapterRentalsAdminListRV;
import com.opustech.bookvan.adapters.user.AdapterBookingPendingListRV;
import com.opustech.bookvan.model.Booking;
import com.opustech.bookvan.model.Rental;

public class RentalsListingFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference rentalsReference;

    private TextView rentalsStatusNone;
    private RecyclerView rentalsList;

    private AdapterRentalsAdminListRV adapterRentalsAdminListRV;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_rentals_listing, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        rentalsReference = firebaseFirestore.collection("rentals");

        Query query = rentalsReference.orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Rental> options = new FirestoreRecyclerOptions.Builder<Rental>()
                .setQuery(query, Rental.class)
                .build();

        adapterRentalsAdminListRV = new AdapterRentalsAdminListRV(options);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());

        rentalsStatusNone = root.findViewById(R.id.rentalsStatusNone);
        rentalsList = root.findViewById(R.id.rentalsList);

        rentalsList.setHasFixedSize(true);
        rentalsList.setLayoutManager(manager);
        rentalsList.setAdapter(adapterRentalsAdminListRV);

        rentalsReference.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null) {
                    int size = value.size();
                    if (size > 0) {
                        rentalsList.setVisibility(View.VISIBLE);
                        rentalsStatusNone.setVisibility(View.GONE);
                    } else {
                        rentalsStatusNone.setVisibility(View.VISIBLE);
                        rentalsList.setVisibility(View.GONE);
                    }
                }
            }
        });

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapterRentalsAdminListRV.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapterRentalsAdminListRV.stopListening();
    }
}