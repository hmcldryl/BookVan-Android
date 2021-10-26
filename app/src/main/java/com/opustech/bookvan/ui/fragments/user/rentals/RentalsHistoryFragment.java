package com.opustech.bookvan.ui.fragments.user.rentals;

import android.content.Intent;
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
import com.opustech.bookvan.adapters.user.AdapterRentalHistoryUserListRV;
import com.opustech.bookvan.model.Rental;

import java.util.Arrays;

public class RentalsHistoryFragment extends Fragment {

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference partnersReference, rentalsReference;

    private TextView rentalStatusNone;
    private RecyclerView rentalList;

    private AdapterRentalHistoryUserListRV adapterRentalHistoryUserListRV;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_rentals_history_transport, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        rentalsReference = firebaseFirestore.collection("rentals");
        partnersReference = firebaseFirestore.collection("partners");

        populateList(root, getUid());
        updateUi(getUid());

        return root;
    }

    private void updateUi(String uid) {
        rentalsReference.whereEqualTo("uid", uid)
                .whereIn("status", Arrays.asList("done", "cancelled"))
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

    private void populateList(View root, String uid) {
        Query query = rentalsReference.whereEqualTo("uid", uid)
                .whereIn("status", Arrays.asList("done", "cancelled"))
                .orderBy("timestamp", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Rental> options = new FirestoreRecyclerOptions.Builder<Rental>()
                .setQuery(query, Rental.class)
                .build();

        adapterRentalHistoryUserListRV = new AdapterRentalHistoryUserListRV(options, getName(), getActivity());
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), manager.getOrientation());

        adapterRentalHistoryUserListRV.setHasStableIds(true);

        rentalStatusNone = root.findViewById(R.id.rentalStatusNone);
        rentalList = root.findViewById(R.id.rentalList);

        rentalList.setHasFixedSize(true);
        rentalList.setLayoutManager(manager);
        rentalList.addItemDecoration(dividerItemDecoration);
        rentalList.setAdapter(adapterRentalHistoryUserListRV);

        adapterRentalHistoryUserListRV.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                rentalList.smoothScrollToPosition(adapterRentalHistoryUserListRV.getItemCount());
            }
        });
    }

    private String getUid() {
        Intent intent = getActivity().getIntent();
        return intent.getStringExtra("uid");
    }

    private String getName() {
        Intent intent = getActivity().getIntent();
        return intent.getStringExtra("name");
    }

    @Override
    public void onStart() {
        super.onStart();
        adapterRentalHistoryUserListRV.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapterRentalHistoryUserListRV.stopListening();
    }
}