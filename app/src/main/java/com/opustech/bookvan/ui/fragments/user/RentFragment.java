package com.opustech.bookvan.ui.fragments.user;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.opustech.bookvan.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class RentFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference, rentalsReference;

    private TextInputLayout inputRentType, inputName, inputLocationPickUp, inputLocationDropOff, inputContactNumber, inputDestination, inputStartScheduleDate, inputEndScheduleDate;
    private AutoCompleteTextView inputRentTypeACT;

    private ExtendedFloatingActionButton btnRent;

    private Context context;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_rent, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        usersReference = firebaseFirestore.collection("users");
        rentalsReference = firebaseFirestore.collection("rentals");

        inputRentType = root.findViewById(R.id.inputRentType);
        inputRentTypeACT = root.findViewById(R.id.inputRentTypeACT);
        inputName = root.findViewById(R.id.inputName);
        inputLocationPickUp = root.findViewById(R.id.inputLocationPickUp);
        inputLocationDropOff = root.findViewById(R.id.inputLocationDropOff);
        inputContactNumber = root.findViewById(R.id.inputContactNumber);
        inputDestination = root.findViewById(R.id.inputDestination);
        inputStartScheduleDate = root.findViewById(R.id.inputStartScheduleDate);
        inputEndScheduleDate = root.findViewById(R.id.inputEndScheduleDate);
        btnRent = root.findViewById(R.id.btnRent);

        populateRentTypeList();
        initializeDatePickerStart();
        initializeDatePickerEnd();

        btnRent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disableInput();
                inputCheck();
            }
        });

        return root;
    }

    private void enableInput() {
        inputRentType.setEnabled(true);
        inputName.setEnabled(true);
        inputLocationPickUp.setEnabled(true);
        inputLocationDropOff.setEnabled(true);
        inputContactNumber.setEnabled(true);
        inputDestination.setEnabled(true);
        inputStartScheduleDate.setEnabled(true);
        inputEndScheduleDate.setEnabled(true);
        btnRent.setEnabled(true);
    }

    private void disableInput() {
        inputRentType.setEnabled(false);
        inputName.setEnabled(false);
        inputLocationPickUp.setEnabled(false);
        inputLocationDropOff.setEnabled(false);
        inputContactNumber.setEnabled(false);
        inputDestination.setEnabled(false);
        inputStartScheduleDate.setEnabled(false);
        inputEndScheduleDate.setEnabled(false);
        btnRent.setEnabled(false);
    }

    private String generateTimestamp() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        return format.format(Calendar.getInstance().getTime());
    }

    private void inputCheck() {
        String rent_type = inputRentType.getEditText().getText().toString();
        String name = inputName.getEditText().getText().toString();
        String location_pickup = inputLocationPickUp.getEditText().getText().toString();
        String location_dropoff = inputLocationDropOff.getEditText().getText().toString();
        String contact_number = inputContactNumber.getEditText().getText().toString();
        String destination = inputDestination.getEditText().getText().toString();
        String schedule_start_date = inputStartScheduleDate.getEditText().getText().toString();
        String schedule_end_date = inputEndScheduleDate.getEditText().getText().toString();

        if (rent_type.isEmpty()) {
            enableInput();
            inputRentType.setError("Select rent type.");
        } else if (name.isEmpty()) {
            enableInput();
            inputName.setError("Please enter a name.");
        } else if (contact_number.isEmpty()) {
            enableInput();
            inputContactNumber.setError("Please enter a contact number.");
        } else {
            submitRentInfo(rent_type, name, location_pickup, location_dropoff, contact_number, destination, schedule_start_date, schedule_end_date);
        }
    }

    private void submitRentInfo(String rent_type, String name, String location_pickup, String location_dropoff, String contact_number, String destination, String schedule_start_date, String schedule_end_date) {
        final ACProgressFlower dialog = new ACProgressFlower.Builder(context)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(context.getResources().getColor(R.color.white))
                .text("Processing...")
                .fadeColor(Color.DKGRAY).build();
        dialog.show();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("rent_type", rent_type);
        hashMap.put("name", name);
        hashMap.put("location_pickup", location_pickup);
        hashMap.put("location_dropoff", location_dropoff);
        hashMap.put("contact_number", contact_number);
        hashMap.put("destination", destination);
        hashMap.put("schedule_start_date", schedule_start_date);
        hashMap.put("schedule_end_date", schedule_end_date);
        hashMap.put("timestamp", generateTimestamp());
        hashMap.put("uid", firebaseAuth.getCurrentUser().getUid());
        rentalsReference.add(hashMap)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            enableInput();
                            Toast.makeText(context, "Success.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private void initializeDatePickerStart() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                inputStartScheduleDate.getEditText().setText(simpleDateFormat.format(calendar.getTime()));
            }
        };
        inputStartScheduleDate.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getActivity(), date,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void initializeDatePickerEnd() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                inputEndScheduleDate.getEditText().setText(simpleDateFormat.format(calendar.getTime()));
            }
        };
        inputEndScheduleDate.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getActivity(), date,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void populateRentTypeList() {
        ArrayList<String> rentTypeArray = new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.rent_type)));
        ArrayAdapter<String> rentTypeArrayAdapter = new ArrayAdapter<>(context, R.layout.support_simple_spinner_dropdown_item, rentTypeArray);
        inputRentTypeACT.setAdapter(rentTypeArrayAdapter);
        inputRentTypeACT.setThreshold(1);
    }
}