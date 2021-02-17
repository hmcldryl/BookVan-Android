package com.opustech.bookvan.ui.fragments.admin.bookings;

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

public class BookingsPendingAdminFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference;

    private TextView bookingStatusNone;
    private RecyclerView bookingList;

    private AdapterBookingHistoryListRV adapterBookingHistoryListRV;

    private String admin_uid = "yEali5UosERXD1wizeJGN87ffff2";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_bookings_pending, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        usersReference = firebaseFirestore.collection("users");

        //currentUserID = firebaseAuth.getCurrentUser().getUid();

        Query query = usersReference.document(admin_uid)
                .collection("bookings")
                .whereEqualTo("status", "pending")
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Booking> options = new FirestoreRecyclerOptions.Builder<Booking>()
                .setQuery(query, Booking.class)
                .build();

        adapterBookingHistoryListRV = new AdapterBookingHistoryListRV(options);

        bookingStatusNone = root.findViewById(R.id.bookingStatusNone);
        bookingList = root.findViewById(R.id.bookingList);

        bookingList.setHasFixedSize(true);
        bookingList.setLayoutManager(new LinearLayoutManager(getActivity()));
        bookingList.setAdapter(adapterBookingHistoryListRV);

        usersReference.document(admin_uid)
                .collection("bookings")
                .whereEqualTo("status", "pending")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        int size = value.size();

                        if (size > 0) {
                            bookingList.setVisibility(View.VISIBLE);
                            bookingStatusNone.setVisibility(View.GONE);
                        } else {
                            bookingStatusNone.setVisibility(View.VISIBLE);
                            bookingList.setVisibility(View.GONE);
                        }
                    }
                });
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapterBookingHistoryListRV.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapterBookingHistoryListRV.stopListening();
    }
}