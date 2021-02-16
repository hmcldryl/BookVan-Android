package com.opustech.bookvan.ui.fragments.user.profile;

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
import com.opustech.bookvan.ui.adapters.AdapterBookingHistoryListRV;
import com.opustech.bookvan.R;
import com.opustech.bookvan.model.Booking;
import com.opustech.bookvan.ui.adapters.AdapterBookingPendingListRV;

public class ProfileActiveBookingFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference;

    private TextView confirmedBookingStatusNone, pendingBookingStatusNone;
    private RecyclerView confirmedBookingList, pendingBookingList;

    private AdapterBookingHistoryListRV adapterConfirmedBookingListRV;
    private AdapterBookingPendingListRV adapterBookingPendingListRV;

    private String admin_uid = "yEali5UosERXD1wizeJGN87ffff2";

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_active_booking, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        usersReference = firebaseFirestore.collection("users");

        String currentUserId = firebaseAuth.getCurrentUser().getUid();

        Query confirmedBookingQuery = usersReference.document(admin_uid)
                .collection("bookings")
                .whereEqualTo("uid", currentUserId)
                .whereEqualTo("status", "confirmed")
                .orderBy("timestamp", Query.Direction.DESCENDING);

        Query pendingBookingQuery = usersReference.document(admin_uid)
                .collection("bookings")
                .whereEqualTo("uid", currentUserId)
                .whereEqualTo("status", "pending")
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Booking> confirmedBookingOptions = new FirestoreRecyclerOptions.Builder<Booking>()
                .setQuery(confirmedBookingQuery, Booking.class)
                .build();

        FirestoreRecyclerOptions<Booking> pendingBookingOptions = new FirestoreRecyclerOptions.Builder<Booking>()
                .setQuery(pendingBookingQuery, Booking.class)
                .build();

        adapterConfirmedBookingListRV = new AdapterBookingHistoryListRV(confirmedBookingOptions);
        adapterBookingPendingListRV = new AdapterBookingPendingListRV(pendingBookingOptions);

        LinearLayoutManager confirmedBookingListManager = new LinearLayoutManager(getActivity());
        LinearLayoutManager pendingBookingListManager = new LinearLayoutManager(getActivity());

        confirmedBookingStatusNone = root.findViewById(R.id.confirmedBookingStatusNone);
        pendingBookingStatusNone = root.findViewById(R.id.pendingBookingStatusNone);

        confirmedBookingList = root.findViewById(R.id.confirmedBookingList);
        pendingBookingList = root.findViewById(R.id.pendingBookingList);

        confirmedBookingList.setHasFixedSize(true);
        confirmedBookingList.setLayoutManager(confirmedBookingListManager);
        confirmedBookingList.setAdapter(adapterConfirmedBookingListRV);

        pendingBookingList.setHasFixedSize(true);
        pendingBookingList.setLayoutManager(pendingBookingListManager);
        pendingBookingList.setAdapter(adapterBookingPendingListRV);

        usersReference.document(admin_uid)
                .collection("bookings")
                .whereEqualTo("uid", currentUserId)
                .whereEqualTo("status", "confirmed")
                .addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        int size = value.size();

                        if (size > 0) {
                            confirmedBookingList.setVisibility(View.VISIBLE);
                            confirmedBookingStatusNone.setVisibility(View.GONE);
                        } else {
                            confirmedBookingStatusNone.setVisibility(View.VISIBLE);
                            confirmedBookingList.setVisibility(View.GONE);
                        }
                    }
                });

        usersReference.document(admin_uid)
                .collection("bookings")
                .whereEqualTo("uid", currentUserId)
                .whereEqualTo("status", "pending")
                .addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        int size = value.size();

                        if (size > 0) {
                            pendingBookingList.setVisibility(View.VISIBLE);
                            pendingBookingStatusNone.setVisibility(View.GONE);
                        } else {
                            pendingBookingStatusNone.setVisibility(View.VISIBLE);
                            pendingBookingList.setVisibility(View.GONE);
                        }
                    }
                });


        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapterConfirmedBookingListRV.startListening();
        adapterBookingPendingListRV.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapterConfirmedBookingListRV.stopListening();
        adapterBookingPendingListRV.stopListening();
    }
}