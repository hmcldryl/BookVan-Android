package com.opustech.bookvan.ui.book;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
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
import com.opustech.bookvan.AdapterBookingListRV;
import com.opustech.bookvan.R;
import com.opustech.bookvan.model.Booking;

public class BookingsFragment extends Fragment {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference usersReference = firebaseFirestore.collection("users");

    private TextView bookingStatusNone;
    private RecyclerView bookingList;

    private AdapterBookingListRV adapterBookingListRV;

    private BookingsViewModel bookingsViewModel;

    private String admin_uid = "btLTtUYnMuWvkrJspvKqZIirLce2";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        bookingsViewModel =
                new ViewModelProvider(this).get(BookingsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_bookings, container, false);

        //currentUserID = firebaseAuth.getCurrentUser().getUid();

        Query query = usersReference.document(admin_uid)
                .collection("pending_bookings")
                .orderBy("booking_schedule_date", Query.Direction.DESCENDING)
                .orderBy("booking_schedule_time", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Booking> options = new FirestoreRecyclerOptions.Builder<Booking>()
                .setQuery(query, Booking.class)
                .build();

        adapterBookingListRV = new AdapterBookingListRV(options);

        bookingStatusNone = root.findViewById(R.id.bookingStatusNone);
        bookingList = root.findViewById(R.id.bookingList);
        bookingList.setHasFixedSize(true);
        bookingList.setLayoutManager(new LinearLayoutManager(getActivity()));
        bookingList.setAdapter(adapterBookingListRV);

        usersReference.document(admin_uid).collection("pending_bookings")
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
        adapterBookingListRV.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapterBookingListRV.stopListening();
    }
}