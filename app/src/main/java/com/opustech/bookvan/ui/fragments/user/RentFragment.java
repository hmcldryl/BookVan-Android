package com.opustech.bookvan.ui.fragments.user;

import android.content.Context;
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
import com.opustech.bookvan.adapters.admin.AdapterRentalsAdminListRV;
import com.opustech.bookvan.model.Rental;

public class RentFragment extends Fragment {

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference rentalsReference;

    private TextView rentStatusNone;
    private RecyclerView rentList;

    private Context context;

    private AdapterRentalsAdminListRV adapterRentalsAdminListRV;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_rent, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        rentalsReference = firebaseFirestore.collection("rentals");

        Query query = rentalsReference.orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Rental> options = new FirestoreRecyclerOptions.Builder<Rental>()
                .setQuery(query, Rental.class)
                .build();

        adapterRentalsAdminListRV = new AdapterRentalsAdminListRV(options, context);

        LinearLayoutManager manager = new LinearLayoutManager(context);

        rentStatusNone = root.findViewById(R.id.rentStatusNone);
        rentList = root.findViewById(R.id.rentList);

        rentList.setHasFixedSize(true);
        rentList.setLayoutManager(manager);
        rentList.setAdapter(adapterRentalsAdminListRV);

        rentalsReference.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapterRentalsAdminListRV.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapterRentalsAdminListRV.stopListening();
    }
}