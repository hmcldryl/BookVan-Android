package com.opustech.bookvan.ui.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.opustech.bookvan.R;
import com.opustech.bookvan.adapters.transport.AdapterTransportPartnerListRV;
import com.opustech.bookvan.model.TransportCompany;

public class UserPartnersActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference partnersReference;

    private RecyclerView partnersList;

    private AdapterTransportPartnerListRV adapterTransportPartnerListRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_partners);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        partnersReference = firebaseFirestore.collection("partners");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Our Transport Partners");
        getSupportActionBar().setSubtitle("BookVan Official Transport Partners");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Query query = partnersReference.orderBy("name", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<TransportCompany> options = new FirestoreRecyclerOptions.Builder<TransportCompany>()
                .setQuery(query, TransportCompany.class)
                .build();

        adapterTransportPartnerListRV = new AdapterTransportPartnerListRV(options, this);
        LinearLayoutManager manager = new LinearLayoutManager(this);

        partnersList = findViewById(R.id.transportList);

        partnersList.setHasFixedSize(true);
        partnersList.setLayoutManager(manager);
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