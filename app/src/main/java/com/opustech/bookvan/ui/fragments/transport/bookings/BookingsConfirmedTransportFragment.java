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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.opustech.bookvan.R;
import com.opustech.bookvan.model.Booking;
import com.opustech.bookvan.ui.adapters.admin.AdapterBookingConfirmedAdminListRV;
import com.opustech.bookvan.ui.adapters.transport.AdapterBookingConfirmedTransportListRV;
import com.opustech.bookvan.ui.adapters.transport.AdapterBookingHistoryTransportListRV;

public class BookingsConfirmedTransportFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference, partnersReference;

    private TextView bookingStatusNone;
    private RecyclerView bookingList;

    private AdapterBookingConfirmedTransportListRV adapterBookingConfirmedTransportListRV;

    private String admin_uid = "yEali5UosERXD1wizeJGN87ffff2";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_bookings_confirmed_transport, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        usersReference = firebaseFirestore.collection("users");
        partnersReference = firebaseFirestore.collection("partners");

        //currentUserID = firebaseAuth.getCurrentUser().getUid();

        partnersReference.document(getCompanyUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            populateList(root, task.getResult().getString("name"));
                            updateUi(task.getResult().getString("name"));
                        }
                    }
                });

        return root;
    }

    private void updateUi(String name) {
        usersReference.document(admin_uid)
                .collection("bookings")
                .whereEqualTo("transport_company", name)
                .whereEqualTo("status", "confirmed")
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

    private void populateList(View root, String name) {
        Query query = usersReference.document(admin_uid)
                .collection("bookings")
                .whereEqualTo("transport_company", name)
                .whereEqualTo("status", "confirmed")
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Booking> options = new FirestoreRecyclerOptions.Builder<Booking>()
                .setQuery(query, Booking.class)
                .build();

        adapterBookingConfirmedTransportListRV = new AdapterBookingConfirmedTransportListRV(options);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), manager.getOrientation());

        bookingStatusNone = root.findViewById(R.id.bookingStatusNone);
        bookingList = root.findViewById(R.id.bookingList);

        bookingList.setHasFixedSize(true);
        bookingList.setLayoutManager(manager);
        bookingList.addItemDecoration(dividerItemDecoration);
        bookingList.setAdapter(adapterBookingConfirmedTransportListRV);
    }

    private String getCompanyUid() {
        Intent intent = getActivity().getIntent();
        return intent.getStringExtra("uid");
    }

    @Override
    public void onStart() {
        super.onStart();
        adapterBookingConfirmedTransportListRV.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapterBookingConfirmedTransportListRV.stopListening();
    }
}