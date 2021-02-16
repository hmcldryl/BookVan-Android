package com.opustech.bookvan.ui.fragments.user.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.opustech.bookvan.R;
import com.opustech.bookvan.model.Booking;
import com.opustech.bookvan.ui.adapters.AdapterBookingHistoryListRV;

public class ProfileBookingHistoryFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference;

    private TextView bookingStatusNone;
    private RecyclerView bookingList;

    private AdapterBookingHistoryListRV adapterHistoryBookingListRV;

    private String admin_uid = "yEali5UosERXD1wizeJGN87ffff2";

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_bookings_history, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        usersReference = firebaseFirestore.collection("users");

        String currentUserId = firebaseAuth.getCurrentUser().getUid();

        Query confirmedBookingQuery = usersReference.document(admin_uid)
                .collection("bookings")
                .whereEqualTo("uid", currentUserId)
                .whereEqualTo("status", "confirmed")
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Booking> historyBookingOptions = new FirestoreRecyclerOptions.Builder<Booking>()
                .setQuery(confirmedBookingQuery, Booking.class)
                .build();

        adapterHistoryBookingListRV = new AdapterBookingHistoryListRV(historyBookingOptions);

        LinearLayoutManager historyBookingListManager = new LinearLayoutManager(getActivity());

        bookingStatusNone = root.findViewById(R.id.bookingStatusNone);
        bookingList = root.findViewById(R.id.bookingList);

        bookingList.setHasFixedSize(true);
        bookingList.setLayoutManager(historyBookingListManager);
        bookingList.setAdapter(adapterHistoryBookingListRV);

        return root;
    }
}