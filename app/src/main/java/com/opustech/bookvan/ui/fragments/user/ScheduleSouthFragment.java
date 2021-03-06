package com.opustech.bookvan.ui.fragments.user;

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
import com.opustech.bookvan.ui.user.ScheduleActivity;

public class ScheduleSouthFragment extends Fragment {

    private FirebaseFirestore firebaseFirestore;
    private DocumentReference destinationsReference;

    private CardView btnScheduleAborlan,
            btnScheduleBataraza,
            btnScheduleBrookesPoint,
            btnScheduleNarra,
            btnScheduleQuezon,
            btnScheduleRioTuba,
            btnScheduleRizal,
            btnScheduleSicud,
            btnScheduleSofronioEspanola;
    private ImageView imageAborlan,
            imageBataraza,
            imageBrookesPoint,
            imageNarra,
            imageQuezon,
            imageRioTuba,
            imageRizal,
            imageSicud,
            imageSofronioEspanola;

    private Context context;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_schedule_user_south, container, false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        destinationsReference = firebaseFirestore.collection("system").document("destinations");

        initializeUi(root);
        updateUi();

        btnScheduleAborlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ScheduleActivity.class);
                intent.putExtra("destination", "aborlan");
                startActivity(intent);
            }
        });

        btnScheduleBataraza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ScheduleActivity.class);
                intent.putExtra("destination", "bataraza");
                startActivity(intent);
            }
        });

        btnScheduleBrookesPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ScheduleActivity.class);
                intent.putExtra("destination", "brookes point");
                startActivity(intent);
            }
        });

        btnScheduleNarra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ScheduleActivity.class);
                intent.putExtra("destination", "narra");
                startActivity(intent);
            }
        });

        btnScheduleQuezon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ScheduleActivity.class);
                intent.putExtra("destination", "quezon");
                startActivity(intent);
            }
        });

        btnScheduleRioTuba.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ScheduleActivity.class);
                intent.putExtra("destination", "rio tuba");
                startActivity(intent);
            }
        });

        btnScheduleRizal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ScheduleActivity.class);
                intent.putExtra("destination", "rizal");
                startActivity(intent);
            }
        });

        btnScheduleSicud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ScheduleActivity.class);
                intent.putExtra("destination", "sicud");
                startActivity(intent);
            }
        });

        btnScheduleSofronioEspanola.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ScheduleActivity.class);
                intent.putExtra("destination", "sofronio espanola");
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
        btnScheduleAborlan = root.findViewById(R.id.btnScheduleAborlan);
        btnScheduleBataraza = root.findViewById(R.id.btnScheduleBataraza);
        btnScheduleBrookesPoint = root.findViewById(R.id.btnScheduleBrookesPoint);
        btnScheduleNarra = root.findViewById(R.id.btnScheduleNarra);
        btnScheduleQuezon = root.findViewById(R.id.btnScheduleQuezon);
        btnScheduleRioTuba = root.findViewById(R.id.btnScheduleRioTuba);
        btnScheduleRizal = root.findViewById(R.id.btnScheduleRizal);
        btnScheduleSicud = root.findViewById(R.id.btnScheduleSicud);
        btnScheduleSofronioEspanola = root.findViewById(R.id.btnScheduleSofronioEspanola);

        imageAborlan = root.findViewById(R.id.imageAborlan);
        imageBataraza = root.findViewById(R.id.imageBataraza);
        imageBrookesPoint = root.findViewById(R.id.imageBrookesPoint);
        imageNarra = root.findViewById(R.id.imageNarra);
        imageQuezon = root.findViewById(R.id.imageQuezon);
        imageRioTuba = root.findViewById(R.id.imageRioTuba);
        imageRizal = root.findViewById(R.id.imageRizal);
        imageSicud = root.findViewById(R.id.imageSicud);
        imageSofronioEspanola = root.findViewById(R.id.imageSofronioEspanola);
    }

    private void updateUi() {
        destinationsReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            String aborlan_image_url = task.getResult().getString("aborlan_image_url");
                            String bataraza_image_url = task.getResult().getString("bataraza_image_url");
                            String brookespoint_image_url = task.getResult().getString("brookespoint_image_url");
                            String narra_image_url = task.getResult().getString("narra_image_url");
                            String quezon_image_url = task.getResult().getString("quezon_image_url");
                            String riotuba_image_url = task.getResult().getString("riotuba_image_url");
                            String rizal_image_url = task.getResult().getString("rizal_image_url");
                            String sicud_image_url = task.getResult().getString("sicud_image_url");
                            String sofronioespanola_image_url = task.getResult().getString("sofronioespanola_image_url");

                            if (aborlan_image_url != null) {
                                if (!aborlan_image_url.isEmpty()) {
                                    Glide.with(context)
                                            .load(aborlan_image_url)
                                            .into(imageAborlan);
                                }
                            }

                            if (bataraza_image_url != null) {
                                if (!bataraza_image_url.isEmpty()) {
                                    Glide.with(context)
                                            .load(bataraza_image_url)
                                            .into(imageBataraza);
                                }
                            }

                            if (brookespoint_image_url != null) {
                                if (!brookespoint_image_url.isEmpty()) {
                                    Glide.with(context)
                                            .load(brookespoint_image_url)
                                            .into(imageBrookesPoint);
                                }
                            }

                            if (narra_image_url != null) {
                                if (!narra_image_url.isEmpty()) {
                                    Glide.with(context)
                                            .load(narra_image_url)
                                            .into(imageNarra);
                                }
                            }

                            if (quezon_image_url != null) {
                                if (!quezon_image_url.isEmpty()) {
                                    Glide.with(context)
                                            .load(quezon_image_url)
                                            .into(imageQuezon);
                                }
                            }

                            if (riotuba_image_url != null) {
                                if (!riotuba_image_url.isEmpty()) {
                                    Glide.with(context)
                                            .load(riotuba_image_url)
                                            .into(imageRioTuba);
                                }
                            }

                            if (rizal_image_url != null) {
                                if (!rizal_image_url.isEmpty()) {
                                    Glide.with(context)
                                            .load(rizal_image_url)
                                            .into(imageRizal);
                                }
                            }

                            if (sicud_image_url != null) {
                                if (!sicud_image_url.isEmpty()) {
                                    Glide.with(context)
                                            .load(sicud_image_url)
                                            .into(imageSicud);
                                }
                            }

                            if (sofronioespanola_image_url != null) {
                                if (!sofronioespanola_image_url.isEmpty()) {
                                    Glide.with(context)
                                            .load(sofronioespanola_image_url)
                                            .into(imageSofronioEspanola);
                                }
                            }
                        }
                    }
                });
    }
}