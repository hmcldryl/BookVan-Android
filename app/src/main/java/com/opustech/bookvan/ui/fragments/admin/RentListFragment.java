package com.opustech.bookvan.ui.fragments.admin;

import android.content.Context;
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

import com.opustech.bookvan.R;
import com.opustech.bookvan.adapters.admin.AdapterRentAdminListRV;
import com.opustech.bookvan.model.Rent;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class RentListFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference rentalsReference;

    private TextView rentalsStatusNone;
    private RecyclerView rentalsList;

    private Context context;

    private AdapterRentAdminListRV adapterRentAdminListRV;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_rent_list, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        rentalsReference = firebaseFirestore.collection("rentals");

        Query query = rentalsReference.orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Rent> options = new FirestoreRecyclerOptions.Builder<Rent>()
                .setQuery(query, Rent.class)
                .build();

        adapterRentAdminListRV = new AdapterRentAdminListRV(options, context);

        LinearLayoutManager manager = new LinearLayoutManager(context);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), manager.getOrientation());

        rentalsStatusNone = root.findViewById(R.id.rentalsStatusNone);
        rentalsList = root.findViewById(R.id.rentalsList);

        rentalsList.setHasFixedSize(true);
        rentalsList.setLayoutManager(manager);
        rentalsList.addItemDecoration(dividerItemDecoration);
        rentalsList.setAdapter(adapterRentAdminListRV);

        rentalsReference.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null) {
                    int size = value.size();
                    if (size > 0) {
                        rentalsList.setVisibility(View.VISIBLE);
                        rentalsStatusNone.setVisibility(View.GONE);
                    } else {
                        rentalsStatusNone.setVisibility(View.VISIBLE);
                        rentalsList.setVisibility(View.GONE);
                    }
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
        adapterRentAdminListRV.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapterRentAdminListRV.stopListening();
    }
}