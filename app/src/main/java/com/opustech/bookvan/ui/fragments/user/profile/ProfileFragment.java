package com.opustech.bookvan.ui.fragments.user.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.opustech.bookvan.ui.adapters.ProfileViewPagerAdapter;
import com.opustech.bookvan.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private CircleImageView profilePhoto;
    private TextView profileName, profileEmail, profileContactNumber;

    private ProfileViewPagerAdapter profileViewPagerAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        usersReference = firebaseFirestore.collection("users");

        String currentUserId = firebaseAuth.getCurrentUser().getUid();

        profilePhoto = root.findViewById(R.id.profilePhoto);
        profileName = root.findViewById(R.id.profileName);
        profileEmail = root.findViewById(R.id.profileEmail);
        profileContactNumber = root.findViewById(R.id.profileContactNumber);

        usersReference.document(currentUserId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    String name = task.getResult().getString("name");
                    String photo_url = task.getResult().getString("photo_url");
                    String email = task.getResult().getString("email");
                    String contact_number = task.getResult().getString("contact_number");

                    if (!photo_url.isEmpty()) {
                        Glide.with(getActivity())
                                .load(photo_url)
                                .into(profilePhoto);
                    }
                    profileName.setText(name);
                    profileEmail.setText(email);
                    profileContactNumber.setText(contact_number);
                }
            }
        });

        viewPager = root.findViewById(R.id.profileViewPager);
        tabLayout = root.findViewById(R.id.profileTab);
        profileViewPagerAdapter = new ProfileViewPagerAdapter(getParentFragmentManager());
        viewPager.setAdapter(profileViewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return root;
    }
}