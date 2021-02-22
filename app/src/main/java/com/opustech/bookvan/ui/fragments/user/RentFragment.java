package com.opustech.bookvan.ui.fragments.user;

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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.opustech.bookvan.R;
import com.opustech.bookvan.model.Rental;

public class RentFragment extends Fragment {

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference;

    private TextView rentStatusNone;
    private RecyclerView rentList;

    private String admin_uid = "yEali5UosERXD1wizeJGN87ffff2";

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_rent, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        usersReference = firebaseFirestore.collection("users");

        Query query = usersReference.document(admin_uid)
                .collection("rent")
                .orderBy("price", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Rental> options = new FirestoreRecyclerOptions.Builder<Rental>()
                .setQuery(query, Rental.class)
                .build();

        //adapterHistoryBookingListRV = new AdapterBookingHistoryListRV(options);

        LinearLayoutManager rentListManager = new LinearLayoutManager(getActivity());

        rentStatusNone = root.findViewById(R.id.rentStatusNone);
        rentList = root.findViewById(R.id.rentList);

        rentList.setHasFixedSize(true);
        rentList.setLayoutManager(rentListManager);
        //rentList.setAdapter(adapterHistoryBookingListRV);

        usersReference.document(admin_uid)
                .collection("rent")
                .addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        int size = value.size();

                        if (size > 0) {
                            rentList.setVisibility(View.VISIBLE);
                            rentStatusNone.setVisibility(View.GONE);
                        } else {
                            rentStatusNone.setVisibility(View.VISIBLE);
                            rentList.setVisibility(View.GONE);
                        }
                    }
                });

        return root;
    }
}