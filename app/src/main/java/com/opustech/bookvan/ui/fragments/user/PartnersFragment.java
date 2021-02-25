package com.opustech.bookvan.ui.fragments.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.opustech.bookvan.R;
import com.opustech.bookvan.model.TransportCompany;
import com.opustech.bookvan.adapters.transport.AdapterTransportPartnerListRV;

public class PartnersFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference partnersReference;

    private RecyclerView partnersList;

    private AdapterTransportPartnerListRV adapterTransportPartnerListRV;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_partners, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        partnersReference = firebaseFirestore.collection("partners");

        Query query = partnersReference.orderBy("name", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<TransportCompany> options = new FirestoreRecyclerOptions.Builder<TransportCompany>()
                .setQuery(query, TransportCompany.class)
                .build();

        adapterTransportPartnerListRV = new AdapterTransportPartnerListRV(options);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), manager.getOrientation());

        partnersList = root.findViewById(R.id.transportList);

        partnersList.setHasFixedSize(true);
        partnersList.setLayoutManager(manager);
        partnersList.addItemDecoration(dividerItemDecoration);
        partnersList.setAdapter(adapterTransportPartnerListRV);

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapterTransportPartnerListRV.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapterTransportPartnerListRV.stopListening();
    }

}