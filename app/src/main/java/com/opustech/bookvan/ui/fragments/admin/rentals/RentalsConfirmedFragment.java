package com.opustech.bookvan.ui.fragments.admin.rentals;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.opustech.bookvan.R;
import com.opustech.bookvan.adapters.admin.AdapterRentalConfirmedAdminListRV;
import com.opustech.bookvan.model.Rental;

public class RentalsConfirmedFragment extends Fragment {

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference partnersReference, rentalsReference;

    private TextView rentalStatusNone;
    private RecyclerView rentalList;

    private AdapterRentalConfirmedAdminListRV adapterRentalConfirmedAdminListRV;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_rentals_confirmed_transport, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        rentalsReference = firebaseFirestore.collection("rentals");
        partnersReference = firebaseFirestore.collection("partners");

        populateList(root);
        updateUi();

        return root;
    }

    private void updateUi() {
        rentalsReference.whereEqualTo("status", "confirmed")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null) {
                            int size = value.size();
                            if (size > 0) {
                                rentalList.setVisibility(View.VISIBLE);
                                rentalStatusNone.setVisibility(View.GONE);
                            } else {
                                rentalStatusNone.setVisibility(View.VISIBLE);
                                rentalList.setVisibility(View.GONE);
                            }
                        }
                    }
                });
    }

    private void populateList(View root) {
        Query query = rentalsReference.whereEqualTo("status", "confirmed")
                .orderBy("timestamp", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Rental> options = new FirestoreRecyclerOptions.Builder<Rental>()
                .setQuery(query, Rental.class)
                .build();

        adapterRentalConfirmedAdminListRV = new AdapterRentalConfirmedAdminListRV(options, getActivity());
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), manager.getOrientation());

        rentalStatusNone = root.findViewById(R.id.rentalStatusNone);
        rentalList = root.findViewById(R.id.rentalList);

        rentalList.setHasFixedSize(true);
        rentalList.setLayoutManager(manager);
        rentalList.addItemDecoration(dividerItemDecoration);
        rentalList.setAdapter(adapterRentalConfirmedAdminListRV);

        adapterRentalConfirmedAdminListRV.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                rentalList.smoothScrollToPosition(adapterRentalConfirmedAdminListRV.getItemCount());
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        adapterRentalConfirmedAdminListRV.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapterRentalConfirmedAdminListRV.stopListening();
    }
}