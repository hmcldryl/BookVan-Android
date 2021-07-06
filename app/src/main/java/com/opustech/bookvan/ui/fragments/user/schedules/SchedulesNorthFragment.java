package com.opustech.bookvan.ui.fragments.user.schedules;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.opustech.bookvan.R;
import com.opustech.bookvan.ui.user.UserTripScheduleActivity;

public class SchedulesNorthFragment extends Fragment {

    private FirebaseFirestore firebaseFirestore;
    private DocumentReference destinationsReference;

    private CardView btnScheduleElnido,
            btnScheduleRoxas,
            btnScheduleSanVicente,
            btnSchedulePortBarton,
            btnScheduleTaytay;

    private ImageView imageElnido,
            imageRoxas,
            imageSanVicente,
            imageTaytay,
            imagePortBarton;

    private Context context;

    private String elnido_image_url, roxas_image_url, sanvicente_image_url, taytay_image_url, port_barton_image_url;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_schedules_north, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        destinationsReference = firebaseFirestore.collection("system").document("destinations");

        initializeUi(root);
        updateUi();

        btnSchedulePortBarton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UserTripScheduleActivity.class);
                intent.putExtra("destination", "port barton");
                intent.putExtra("image_url", port_barton_image_url);
                startActivity(intent);
            }
        });

        btnScheduleElnido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UserTripScheduleActivity.class);
                intent.putExtra("destination", "el nido");
                intent.putExtra("image_url", elnido_image_url);
                startActivity(intent);
            }
        });

        btnScheduleElnido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UserTripScheduleActivity.class);
                intent.putExtra("destination", "el nido");
                intent.putExtra("image_url", elnido_image_url);
                startActivity(intent);
            }
        });

        btnScheduleRoxas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UserTripScheduleActivity.class);
                intent.putExtra("destination", "roxas");
                intent.putExtra("image_url", roxas_image_url);
                startActivity(intent);
            }
        });

        btnScheduleSanVicente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UserTripScheduleActivity.class);
                intent.putExtra("destination", "san vicente");
                intent.putExtra("image_url", sanvicente_image_url);
                startActivity(intent);
            }
        });

        btnScheduleTaytay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UserTripScheduleActivity.class);
                intent.putExtra("destination", "taytay");
                intent.putExtra("image_url", taytay_image_url);
                startActivity(intent);
            }
        });

        return root;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private void initializeUi(View root) {
        btnScheduleElnido = root.findViewById(R.id.btnScheduleElnido);
        btnScheduleRoxas = root.findViewById(R.id.btnScheduleRoxas);
        btnScheduleSanVicente = root.findViewById(R.id.btnScheduleSanVicente);
        btnScheduleTaytay = root.findViewById(R.id.btnScheduleTaytay);
        btnSchedulePortBarton = root.findViewById(R.id.btnSchedulePortBarton);

        imageElnido = root.findViewById(R.id.imageElnido);
        imageRoxas = root.findViewById(R.id.imageRoxas);
        imageSanVicente = root.findViewById(R.id.imageSanVicente);
        imageTaytay = root.findViewById(R.id.imageTaytay);
        imagePortBarton = root.findViewById(R.id.imagePortBarton);
    }

    private void updateUi() {
        destinationsReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            elnido_image_url = task.getResult().getString("elnido_image_url");
                            roxas_image_url = task.getResult().getString("roxas_image_url");
                            sanvicente_image_url = task.getResult().getString("sanvicente_image_url");
                            taytay_image_url = task.getResult().getString("taytay_image_url");
                            port_barton_image_url = task.getResult().getString("port_barton_image_url");

                            if (elnido_image_url != null) {
                                if (!elnido_image_url.isEmpty()) {
                                    Glide.with(context)
                                            .load(elnido_image_url)
                                            .into(imageElnido);
                                }
                            }

                            if (port_barton_image_url != null) {
                                if (!port_barton_image_url.isEmpty()) {
                                    Glide.with(context)
                                            .load(port_barton_image_url)
                                            .into(imagePortBarton);
                                }
                            }

                            if (roxas_image_url != null) {
                                if (!roxas_image_url.isEmpty()) {
                                    Glide.with(context)
                                            .load(roxas_image_url)
                                            .into(imageRoxas);
                                }
                            }

                            if (sanvicente_image_url != null) {
                                if (!sanvicente_image_url.isEmpty()) {
                                    Glide.with(context)
                                            .load(sanvicente_image_url)
                                            .into(imageSanVicente);
                                }
                            }

                            if (taytay_image_url != null) {
                                if (!taytay_image_url.isEmpty()) {
                                    Glide.with(context)
                                            .load(taytay_image_url)
                                            .into(imageTaytay);
                                }
                            }
                        }
                    }
                });
    }
}