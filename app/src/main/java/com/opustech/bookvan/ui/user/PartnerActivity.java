package com.opustech.bookvan.ui.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.opustech.bookvan.R;
import com.opustech.bookvan.adapters.transport.AdapterTransportPartnerListRV;
import com.opustech.bookvan.model.TransportCompany;

public class PartnerActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference partnersReference;

    private RecyclerView partnersList;

    private AdapterTransportPartnerListRV adapterTransportPartnerListRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        partnersReference = firebaseFirestore.collection("partners");

        Query query = partnersReference.orderBy("name", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<TransportCompany> options = new FirestoreRecyclerOptions.Builder<TransportCompany>()
                .setQuery(query, TransportCompany.class)
                .build();

        adapterTransportPartnerListRV = new AdapterTransportPartnerListRV(options, this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, manager.getOrientation());

        partnersList = findViewById(R.id.transportList);

        partnersList.setHasFixedSize(true);
        partnersList.setLayoutManager(manager);
        partnersList.addItemDecoration(dividerItemDecoration);
        partnersList.setAdapter(adapterTransportPartnerListRV);
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