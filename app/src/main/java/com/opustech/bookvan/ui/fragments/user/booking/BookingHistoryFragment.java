package com.opustech.bookvan.ui.fragments.user.booking;

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
import com.opustech.bookvan.model.Booking;
import com.opustech.bookvan.ui.adapters.user.AdapterBookingHistoryListRV;

public class BookingHistoryFragment extends Fragment {

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
                .whereEqualTo("status", "done")
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

        usersReference.document(admin_uid)
                .collection("bookings")
                .whereEqualTo("uid", currentUserId)
                .whereEqualTo("status", "done")
                .addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
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
}