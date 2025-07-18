package com.opustech.bookvan.ui.fragments.user.booking;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.opustech.bookvan.R;
import com.opustech.bookvan.adapters.user.AdapterBookingConfirmedListRV;
import com.opustech.bookvan.model.Booking;

public class ConfirmedBookingFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference, bookingsReference;

    private TextView bookingStatusNone;
    private RecyclerView bookingList;

    private AdapterBookingConfirmedListRV adapterConfirmedBookingListRV;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_booking_confirmed_user, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        usersReference = firebaseFirestore.collection("users");
        bookingsReference = firebaseFirestore.collection("bookings");

        String currentUserId = firebaseAuth.getCurrentUser().getUid();

        populateList(root, currentUserId);
        updateUi(currentUserId);

        return root;
    }

    private void updateUi(String currentUserId) {
        bookingsReference.whereEqualTo("uid", currentUserId)
                .whereEqualTo("status", "confirmed")
                .addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null) {
                            int size = value.size();
                            if (size > 0) {
                                bookingList.setVisibility(View.VISIBLE);
                                bookingStatusNone.setVisibility(View.GONE);
                            } else {
                                bookingStatusNone.setVisibility(View.VISIBLE);
                                bookingList.setVisibility(View.GONE);
                            }
                        }
                    }
                });
    }

    private void populateList(View root, String currentUserId) {
        Query query = bookingsReference.whereEqualTo("uid", currentUserId)
                .whereEqualTo("status", "confirmed")
                .orderBy("timestamp", Query.Direction.ASCENDING);


        FirestoreRecyclerOptions<Booking> options = new FirestoreRecyclerOptions.Builder<Booking>()
                .setQuery(query, Booking.class)
                .build();

        adapterConfirmedBookingListRV = new AdapterBookingConfirmedListRV(options, getActivity());

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);

        adapterConfirmedBookingListRV.setHasStableIds(true);

        bookingStatusNone = root.findViewById(R.id.bookingStatusNone);
        bookingList = root.findViewById(R.id.bookingList);

        bookingList.setHasFixedSize(true);
        bookingList.setLayoutManager(manager);
        bookingList.setAdapter(adapterConfirmedBookingListRV);

        adapterConfirmedBookingListRV.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                bookingList.smoothScrollToPosition(adapterConfirmedBookingListRV.getItemCount());
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        adapterConfirmedBookingListRV.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapterConfirmedBookingListRV.stopListening();
    }
}