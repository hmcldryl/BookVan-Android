package com.opustech.bookvan.ui.fragments.transport.bookings;

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
import com.opustech.bookvan.model.Booking;
import com.opustech.bookvan.adapters.transport.AdapterBookingHistoryTransportListRV;

import java.util.Arrays;

public class BookingsHistoryTransportFragment extends Fragment {

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference partnersReference, bookingsReference;

    private TextView bookingStatusNone;
    private RecyclerView bookingList;

    private AdapterBookingHistoryTransportListRV adapterBookingHistoryTransportListRV;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_bookings_history_transport, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        bookingsReference = firebaseFirestore.collection("bookings");
        partnersReference = firebaseFirestore.collection("partners");

        populateList(root, getTransportUid());
        updateUi(getTransportUid());

        return root;
    }

    private void updateUi(String transport_uid) {
        bookingsReference.whereEqualTo("transport_uid", transport_uid)
                .whereIn("status", Arrays.asList("done", "cancelled"))
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
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

    private void populateList(View root, String transport_uid) {
        Query query = bookingsReference.whereEqualTo("transport_uid", transport_uid)
                .whereIn("status", Arrays.asList("done", "cancelled"))
                .orderBy("timestamp", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Booking> options = new FirestoreRecyclerOptions.Builder<Booking>()
                .setQuery(query, Booking.class)
                .build();

        adapterBookingHistoryTransportListRV = new AdapterBookingHistoryTransportListRV(options, getActivity());
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), manager.getOrientation());

        adapterBookingHistoryTransportListRV.setHasStableIds(true);

        bookingStatusNone = root.findViewById(R.id.bookingStatusNone);
        bookingList = root.findViewById(R.id.bookingList);

        bookingList.setHasFixedSize(true);
        bookingList.setLayoutManager(manager);
        bookingList.addItemDecoration(dividerItemDecoration);
        bookingList.setAdapter(adapterBookingHistoryTransportListRV);

        adapterBookingHistoryTransportListRV.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                bookingList.smoothScrollToPosition(adapterBookingHistoryTransportListRV.getItemCount());
            }
        });
    }

    private String getTransportUid() {
        Intent intent = getActivity().getIntent();
        return intent.getStringExtra("uid");
    }

    @Override
    public void onStart() {
        super.onStart();
        adapterBookingHistoryTransportListRV.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapterBookingHistoryTransportListRV.stopListening();
    }
}