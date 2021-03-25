package com.opustech.bookvan.ui.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.opustech.bookvan.R;
import com.opustech.bookvan.adapters.user.AdapterDropdownTripSchedule;
import com.opustech.bookvan.model.Booking;
import com.opustech.bookvan.model.Schedule;
import com.opustech.bookvan.model.TransportCompany;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class UserBookActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference, bookingsReference, partnersReference, schedulesReference;

    private TextInputLayout bookingCustomerName,
            bookingContactNumber,
            bookingVanTransport,
            bookingRoute,
            bookingScheduleDate,
            bookingScheduleTime,
            bookingCountAdult,
            bookingCountChild,
            bookingCountSpecial;
    private ImageButton addAdultCount,
            addChildCount,
            addSpecialCount,
            subtractAdultCount,
            subtractChildCount,
            subtractSpecialCount;
    private TextView bookingTotal;
    private ExtendedFloatingActionButton btnBook;
    private AutoCompleteTextView inputVanTransportACT, bookingRouteACT;

    private AdapterDropdownTripSchedule adapterDropdownTripSchedule;
    private ArrayList<Schedule> routeArray;

    private String transportUid = "";
    private int countAdult = 1;
    private int countChild = 0;
    private int countSpecial = 0;
    private final double specialDiscount = 0.20;
    private final double commissionRate = 0.10;
    private double tripPrice = 0.00;
    private double totalPrice = 0.00;
    private double totalCommission = 0.00;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_book);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        usersReference = firebaseFirestore.collection("users");
        bookingsReference = firebaseFirestore.collection("bookings");
        partnersReference = firebaseFirestore.collection("partners");
        schedulesReference = firebaseFirestore.collection("schedules");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Booking Form");
        getSupportActionBar().setSubtitle("Fill up the following fields to book your trip.");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        initializeUi();
        initPriceWatcher();
        populateVanTransportList();
        vanTransportTextWatcher();
        initializeDatePicker();
        //initializeTimePicker();

        bookingCountAdult.getEditText().setText((String.valueOf(countAdult)));
        bookingCountChild.getEditText().setText((String.valueOf(countChild)));
        bookingCountSpecial.getEditText().setText((String.valueOf(countSpecial)));

        addAdultCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countAdult = Integer.parseInt(bookingCountAdult.getEditText().getText().toString());
                if (countAdult >= 0 && passengerCapacityAdd()) {
                    countAdult = countAdult + 1;
                    bookingCountAdult.getEditText().setText(String.valueOf(countAdult));
                }
            }
        });

        subtractAdultCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countAdult = Integer.parseInt(bookingCountAdult.getEditText().getText().toString());
                if (countAdult > 0 && passengerCapacityRemove()) {
                    countAdult = countAdult - 1;
                    bookingCountAdult.getEditText().setText(String.valueOf(countAdult));
                }
            }
        });

        addChildCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countChild = Integer.parseInt(bookingCountChild.getEditText().getText().toString());
                if (countChild >= 0 && passengerCapacityAdd()) {
                    countChild = countChild + 1;
                    bookingCountChild.getEditText().setText(String.valueOf(countChild));
                }
            }
        });

        subtractChildCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countChild = Integer.parseInt(bookingCountChild.getEditText().getText().toString());
                if (countChild > 0 && passengerCapacityRemove()) {
                    countChild = countChild - 1;
                    bookingCountChild.getEditText().setText(String.valueOf(countChild));
                }
            }
        });

        addSpecialCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countSpecial = Integer.parseInt(bookingCountSpecial.getEditText().getText().toString());
                if (countSpecial >= 0 && passengerCapacityAdd()) {
                    countSpecial = countSpecial + 1;
                    bookingCountSpecial.getEditText().setText(String.valueOf(countSpecial));
                }
            }
        });

        subtractSpecialCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countSpecial = Integer.parseInt(bookingCountSpecial.getEditText().getText().toString());
                if (countSpecial > 0 && passengerCapacityRemove()) {
                    countSpecial = countSpecial - 1;
                    bookingCountSpecial.getEditText().setText(String.valueOf(countSpecial));
                }
            }
        });

        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disableInput();
                inputCheck();
            }
        });
    }

    private boolean passengerCapacityAdd() {
        return countAdult + countChild + countSpecial < 14;
    }

    private boolean passengerCapacityRemove() {
        return countAdult + countChild + countSpecial <= 14;
    }

    private void initPriceWatcher() {
        bookingCountAdult.getEditText().addTextChangedListener(priceWatcher);
        bookingCountChild.getEditText().addTextChangedListener(priceWatcher);
        bookingCountSpecial.getEditText().addTextChangedListener(priceWatcher);
    }

    private final TextWatcher priceWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            computeTotalPrice();
            computeCommission();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void initializeUi() {
        bookingCustomerName = findViewById(R.id.bookingCustomerName);
        bookingContactNumber = findViewById(R.id.bookingContactNumber);
        bookingVanTransport = findViewById(R.id.inputVanTransport);
        inputVanTransportACT = findViewById(R.id.inputVanTransportACT);
        bookingRoute = findViewById(R.id.bookingRoute);
        bookingRouteACT = findViewById(R.id.bookingRouteACT);
        bookingScheduleDate = findViewById(R.id.bookingScheduleDate);
        bookingScheduleTime = findViewById(R.id.bookingScheduleTime);
        bookingCountAdult = findViewById(R.id.bookingCountAdult);
        bookingCountChild = findViewById(R.id.bookingCountChild);
        bookingCountSpecial = findViewById(R.id.bookingCountSpecial);

        addAdultCount = findViewById(R.id.btnCountAdultAdd);
        subtractAdultCount = findViewById(R.id.btnCountAdultSubtract);
        addChildCount = findViewById(R.id.btnCountChildAdd);
        subtractChildCount = findViewById(R.id.btnCountChildSubtract);
        addSpecialCount = findViewById(R.id.btnCountSpecialAdd);
        subtractSpecialCount = findViewById(R.id.btnCountSpecialSubtract);

        bookingTotal = findViewById(R.id.bookingTotal);

        btnBook = findViewById(R.id.btnBook);
    }

    private void vanTransportTextWatcher() {
        bookingVanTransport.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String transport_name = bookingVanTransport.getEditText().getText().toString();
                partnersReference.whereEqualTo("name", transport_name)
                        .limit(1)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if (queryDocumentSnapshots != null) {
                                    if (queryDocumentSnapshots.size() != 0) {
                                        populateRouteList(queryDocumentSnapshots.getDocuments().get(0).getString("uid"));
                                    }
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                bookingVanTransport.setError("Van transport does not exist.");
                            }
                        });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void disableInput() {
        btnBook.setEnabled(false);
        bookingCustomerName.setEnabled(false);
        bookingContactNumber.setEnabled(false);
        bookingVanTransport.setEnabled(false);
        bookingRoute.setEnabled(false);
        bookingScheduleDate.setEnabled(false);
        bookingScheduleTime.setEnabled(false);
        bookingCountAdult.setEnabled(false);
        bookingCountChild.setEnabled(false);
        bookingCountSpecial.setEnabled(false);
    }

    private void enableInput() {
        btnBook.setEnabled(true);
        bookingCustomerName.setEnabled(true);
        bookingContactNumber.setEnabled(true);
        bookingVanTransport.setEnabled(true);
        bookingRoute.setEnabled(true);
        bookingScheduleDate.setEnabled(true);
        bookingScheduleTime.setEnabled(true);
        bookingCountAdult.setEnabled(true);
        bookingCountChild.setEnabled(true);
        bookingCountSpecial.setEnabled(true);
    }

    private void inputCheck() {
        String name = bookingCustomerName.getEditText().getText().toString();
        String contact_number = bookingContactNumber.getEditText().getText().toString();
        String transport_name = bookingVanTransport.getEditText().getText().toString();
        String trip_route = bookingRoute.getEditText().getText().toString();
        String schedule_date = bookingScheduleDate.getEditText().getText().toString();
        String schedule_time = bookingScheduleTime.getEditText().getText().toString();

        if (name.isEmpty()) {
            enableInput();
            bookingCustomerName.getEditText().setError("Please enter your name.");
        } else if (contact_number.isEmpty()) {
            enableInput();
            bookingContactNumber.getEditText().setError("Please enter a contact number.");
        } else if (transport_name.isEmpty()) {
            enableInput();
            bookingVanTransport.getEditText().setError("Please select your preferred van transport.");
        } else if (trip_route.isEmpty()) {
            enableInput();
            bookingRoute.getEditText().setError("Please enter your starting location.");
        } else if (schedule_date.isEmpty()) {
            enableInput();
            bookingScheduleDate.getEditText().setError("Please enter desired schedule date.");
        } else if (schedule_time.isEmpty()) {
            enableInput();
            bookingScheduleTime.getEditText().setError("Please enter desired schedule time.");
        } else if (countAdult == 0 && countChild == 0 && countSpecial == 0) {
            enableInput();
            bookingCountAdult.getEditText().setError("Must have at least 1 passenger.");
        } else {
            generateRefNum(firebaseAuth.getCurrentUser().getUid(), name, contact_number, trip_route, schedule_date, schedule_time);
        }
    }

    private void generateRefNum(String uid, String name, String contact_number, String trip_route, String schedule_date, String schedule_time) {
        final ACProgressFlower dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(getResources().getColor(R.color.white))
                .text("Processing...")
                .fadeColor(Color.DKGRAY).build();
        dialog.show();
        bookingsReference.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int num = task.getResult().getDocuments().size() + 1;
                            String reference_number = "BV-" + String.format(Locale.ENGLISH, "%06d", num);
                            addNewBooking(dialog, reference_number, uid, name, contact_number, trip_route, schedule_date, schedule_time);
                        }
                    }
                });
    }

    private String generateTimestamp() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        return format.format(Calendar.getInstance().getTime());
    }

    private String getCurrentDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        return format.format(Calendar.getInstance().getTime());
    }

    private void addNewBooking(ACProgressFlower dialog, String reference_number, String uid, String name, String contact_number, String trip_route, String schedule_date, String schedule_time) {
        Booking booking = new Booking(reference_number, uid, name, contact_number, trip_route, schedule_date, schedule_time, transportUid, "pending", getCurrentDate(), generateTimestamp(), countAdult, countChild, countSpecial, totalPrice, totalCommission);
        bookingsReference.add(booking)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            Toast.makeText(UserBookActivity.this, "Success.", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(UserBookActivity.this, UserBookingActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }

/*    private void initializeTimePicker() {
        final Calendar calendar = Calendar.getInstance();
        TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                calendar.set(Calendar.HOUR_OF_DAY, i);
                calendar.set(Calendar.MINUTE, i1);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
                bookingScheduleTime.getEditText().setText(simpleDateFormat.format(calendar.getTime()));
            }
        };
        bookingScheduleTime.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog(UserBookActivity.this, time,
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE), false)
                        .show();
            }
        });
    }*/

    private void initializeDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                bookingScheduleDate.getEditText().setText(simpleDateFormat.format(calendar.getTime()));
            }
        };
        bookingScheduleDate.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(UserBookActivity.this, date,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void populateVanTransportList() {
        ArrayList<TransportCompany> vanTransportList = new ArrayList<>();
        partnersReference.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (int i = 0; i < task.getResult().getDocuments().size(); i++) {
                                TransportCompany transportCompany = new TransportCompany(task.getResult().getDocuments().get(i).getString("uid"), task.getResult().getDocuments().get(i).getString("name"));
                                vanTransportList.add(i, transportCompany);
                            }
                            ArrayAdapter<TransportCompany> vanTransportAdapter = new ArrayAdapter<>(UserBookActivity.this, R.layout.support_simple_spinner_dropdown_item, vanTransportList);
                            inputVanTransportACT.setAdapter(vanTransportAdapter);
                            inputVanTransportACT.setThreshold(1);
                            inputVanTransportACT.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    TransportCompany selectedTransport = (TransportCompany) adapterView.getItemAtPosition(i);
                                    transportUid = selectedTransport.getUid();
                                }
                            });
                        }
                    }
                });
    }

    private void populateRouteList(String uid) {
        routeArray = new ArrayList<>();
        schedulesReference.whereEqualTo("van_company_uid", uid)
                .orderBy("route_from", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (int i = 0; i < task.getResult().getDocuments().size(); i++) {
                                Schedule schedule = new Schedule(task.getResult().getDocuments().get(i).getString("time_queue"), task.getResult().getDocuments().get(i).getString("route_from"), task.getResult().getDocuments().get(i).getString("route_to"), task.getResult().getDocuments().get(i).getLong("price").doubleValue());
                                routeArray.add(i, schedule);
                            }
                            adapterDropdownTripSchedule = new AdapterDropdownTripSchedule(UserBookActivity.this, routeArray);
                            bookingRouteACT.setAdapter(adapterDropdownTripSchedule);
                            bookingRouteACT.setThreshold(1);
                            bookingRouteACT.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Schedule selectedSchedule = (Schedule) parent.getItemAtPosition(position);
                                    tripPrice = selectedSchedule.getPrice();
                                    bookingScheduleTime.getEditText().setText(selectedSchedule.getTime_queue());
                                    bookingScheduleDate.getEditText().setText(getCurrentDate());
                                    computeTotalPrice();
                                    computeCommission();
                                }
                            });
                        }
                    }
                });
    }

    private void computeTotalPrice() {
        if (tripPrice != 0) {
            if (countSpecial > 0) {
                totalPrice = (tripPrice * (countAdult + countChild + countSpecial) - (specialDiscount * (tripPrice * countSpecial)));
            } else {
                totalPrice = (tripPrice * (countAdult + countChild));
            }
            bookingTotal.setText(String.format(Locale.ENGLISH, "%.2f", totalPrice));
        }
    }

    private void computeCommission() {
        if (tripPrice != 0) {
            if (countSpecial > 0) {
                totalCommission = commissionRate * (tripPrice * (countAdult + countChild)) + commissionRate * ((tripPrice * countSpecial) - (specialDiscount * (tripPrice * countSpecial)));
            } else {
                totalCommission = commissionRate * (tripPrice * (countAdult + countChild));
            }
        }
    }
}