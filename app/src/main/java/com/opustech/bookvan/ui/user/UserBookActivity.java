package com.opustech.bookvan.ui.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jhonnyx2012.horizontalpicker.DatePickerListener;
import com.github.jhonnyx2012.horizontalpicker.HorizontalPicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.opustech.bookvan.R;
import com.opustech.bookvan.adapters.user.AdapterDropdownSchedule;
import com.opustech.bookvan.adapters.user.AdapterDropdownScheduleTime;
import com.opustech.bookvan.model.Booking;
import com.opustech.bookvan.model.Schedule;
import com.opustech.bookvan.model.TransportCompany;
import com.opustech.bookvan.model.TripSchedule;
import com.opustech.bookvan.ui.transport.TransportAdminEditCompanyProfileActivity;
import com.opustech.bookvan.ui.transport.TransportAdminSchedulesActivity;

import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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
            bookingTime,
            bookingCountAdult,
            bookingCountChild,
            bookingCountSpecial;
    private HorizontalPicker picker;
    private ImageButton addAdultCount,
            addChildCount,
            addSpecialCount,
            subtractAdultCount,
            subtractChildCount,
            subtractSpecialCount;
    private TextView bookingTotal;
    private RadioGroup categoryRadio;
    private MaterialRadioButton btnRadioNorth,
            btnRadioSouth;
    private ExtendedFloatingActionButton btnBook;
    private AutoCompleteTextView inputVanTransportACT, bookingRouteACT, bookingTimeACT;

    private AdapterDropdownSchedule adapterDropdownSchedule;
    private AdapterDropdownScheduleTime adapterDropdownScheduleTime;
    private ArrayList<Schedule> routeArray;
    private ArrayList<TripSchedule> tripSchedulesTimeList;

    private String transportUid = "";
    private String route_from = "";
    private String route_to = "";
    private String schedule_date = "";
    private String schedule_time = "";
    private int countAdult = 1;
    private int countChild = 0;
    private int countSpecial = 0;
    private final double specialDiscount = 0.20;
    private final double commissionRate = 0.10;
    private double tripPrice = 0.00;
    private double totalPrice = 0.00;
    private double totalCommission = 0.00;

    @SuppressLint("ClickableViewAccessibility")
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
                finish();
            }
        });

        initializeUi();
        initPriceWatcher();

        bookingCustomerName.getEditText().setText(getIntent().getStringExtra("name"));
        bookingContactNumber.getEditText().setText(getIntent().getStringExtra("contact_number"));

        categoryRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                inputVanTransportACT.getText().clear();
                bookingRouteACT.getText().clear();
                inputVanTransportACT.clearListSelection();
                bookingRouteACT.clearListSelection();
                if (i == R.id.btnRadioNorth) {
                    populateVanTransportList("north");
                } else if (i == R.id.btnRadioSouth) {
                    populateVanTransportList("south");
                }
            }
        });

        picker.showTodayButton(true)
                .init();
        picker.setDate(new DateTime());

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
                Toast.makeText(UserBookActivity.this, "check1", Toast.LENGTH_SHORT).show();
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
        bookingTime = findViewById(R.id.bookingTime);
        bookingTimeACT = findViewById(R.id.bookingTimeACT);
        picker = findViewById(R.id.bookingDatePicker);
        bookingCountAdult = findViewById(R.id.bookingCountAdult);
        bookingCountChild = findViewById(R.id.bookingCountChild);
        bookingCountSpecial = findViewById(R.id.bookingCountSpecial);

        categoryRadio = findViewById(R.id.categoryRadio);
        btnRadioNorth = findViewById(R.id.btnRadioNorth);
        btnRadioSouth = findViewById(R.id.btnRadioSouth);

        addAdultCount = findViewById(R.id.btnCountAdultAdd);
        subtractAdultCount = findViewById(R.id.btnCountAdultSubtract);
        addChildCount = findViewById(R.id.btnCountChildAdd);
        subtractChildCount = findViewById(R.id.btnCountChildSubtract);
        addSpecialCount = findViewById(R.id.btnCountSpecialAdd);
        subtractSpecialCount = findViewById(R.id.btnCountSpecialSubtract);

        bookingTotal = findViewById(R.id.bookingTotal);

        btnBook = findViewById(R.id.btnBook);
    }

    private void disableInput() {
        btnBook.setEnabled(false);
        bookingCustomerName.setEnabled(false);
        bookingContactNumber.setEnabled(false);
        bookingVanTransport.setEnabled(false);
        bookingRoute.setEnabled(false);
        btnRadioNorth.setEnabled(false);
        btnRadioSouth.setEnabled(false);
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
        btnRadioNorth.setEnabled(true);
        btnRadioSouth.setEnabled(true);
        bookingCountAdult.setEnabled(true);
        bookingCountChild.setEnabled(true);
        bookingCountSpecial.setEnabled(true);
    }

    private void inputCheck() {
        String name = bookingCustomerName.getEditText().getText().toString();
        String contact_number = bookingContactNumber.getEditText().getText().toString();
        String transport_name = bookingVanTransport.getEditText().getText().toString();

        if (name.isEmpty()) {
            enableInput();
            bookingCustomerName.getEditText().setError("Please enter your name.");
        } else if (contact_number.isEmpty()) {
            enableInput();
            bookingContactNumber.getEditText().setError("Please enter a contact number.");
        } else if (transport_name.isEmpty()) {
            enableInput();
            bookingVanTransport.getEditText().setError("Please select your preferred van transport.");
        } else if (route_from.isEmpty() || route_to.isEmpty()) {
            enableInput();
            bookingRoute.getEditText().setError("Please enter your trip route.");
        } else if (schedule_date.isEmpty()) {
            enableInput();
            Toast.makeText(this, "Please select a valid booking date.", Toast.LENGTH_SHORT).show();
        } else if (schedule_time.isEmpty()) {
            enableInput();
            Toast.makeText(this, "Please select a time for your booking.", Toast.LENGTH_SHORT).show();
        } else if (countAdult == 0 && countChild == 0 && countSpecial == 0) {
            enableInput();
            bookingCountAdult.getEditText().setText(String.valueOf(1));
            Toast.makeText(this, "Must have at least 1 passenger.", Toast.LENGTH_SHORT).show();
        } else if (compareSchedule(schedule_date + " " + schedule_time)) {
            enableInput();
            Toast.makeText(this, "Please select a valid booking date.", Toast.LENGTH_SHORT).show();
        } else {
            generateRefNum(firebaseAuth.getCurrentUser().getUid(), name, contact_number, schedule_date, schedule_time);
        }
    }

    private boolean compareSchedule(String booking_schedule) {
        Date selectedDate = null;
        Date minDate = null;

        try {
            selectedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH).parse(booking_schedule);
            minDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH).parse(new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH).format(Calendar.getInstance().getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return selectedDate.compareTo(minDate) < 0;
    }

    private void generateRefNum(String uid, String name, String contact_number, String schedule_date, String schedule_time) {
        final ACProgressFlower dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(getResources().getColor(R.color.white))
                .fadeColor(Color.DKGRAY).build();
        dialog.show();
        bookingsReference.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int num = task.getResult().getDocuments().size() + 1;
                            String reference_number = "BV-B" + String.format(Locale.ENGLISH, "%06d", num);
                            addNewBooking(dialog, reference_number, uid, name, contact_number, schedule_date, schedule_time);
                        } else {
                            dialog.dismiss();
                            Toast.makeText(UserBookActivity.this, "Failed.", Toast.LENGTH_SHORT).show();
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

    private String convertDate12Hr(String time) {
        SimpleDateFormat format = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
        Date date = null;
        try {
            date = new SimpleDateFormat("HH:mm", Locale.ENGLISH).parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return format.format(date);
    }

    private void addNewBooking(ACProgressFlower dialog, String reference_number, String uid, String name, String contact_number, String schedule_date, String schedule_time) {
        Booking booking = new Booking(reference_number, uid, name, contact_number, route_from, route_to, schedule_date, schedule_time, transportUid, "pending", getCurrentDate(), generateTimestamp(), countAdult, countChild, countSpecial, totalPrice, totalCommission);
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
                        } else {
                            dialog.dismiss();
                            Toast.makeText(UserBookActivity.this, "Failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void populateVanTransportList(String route) {
        final ACProgressFlower dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(getResources().getColor(R.color.white))
                .fadeColor(Color.DKGRAY).build();
        dialog.show();
        ArrayList<TransportCompany> vanTransportList = new ArrayList<>();
        if (route.toLowerCase().equals("north")) {
            partnersReference.whereEqualTo("route_north", true)
                    .get()
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
                                        populateRouteList(transportUid);
                                    }
                                });
                                dialog.dismiss();
                            }
                        }
                    });
        } else if (route.toLowerCase().equals("south")) {
            partnersReference.whereEqualTo("route_south", true)
                    .get()
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
                                        populateRouteList(transportUid);
                                    }
                                });
                                dialog.dismiss();
                            }
                        }
                    });
        }
    }

    private void populateRouteList(String uid) {
        final ACProgressFlower dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(getResources().getColor(R.color.white))
                .fadeColor(Color.DKGRAY).build();
        dialog.show();
        routeArray = new ArrayList<>();
        schedulesReference.whereEqualTo("van_company_uid", uid)
                .orderBy("route_from", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (int i = 0; i < task.getResult().getDocuments().size(); i++) {
                                routeArray.add(i, new Schedule(task.getResult().getDocuments().get(i).getString("route_from"), task.getResult().getDocuments().get(i).getString("route_to"), task.getResult().getDocuments().get(i).getLong("price").doubleValue()));
                            }
                            adapterDropdownSchedule = new AdapterDropdownSchedule(UserBookActivity.this, routeArray);
                            bookingRouteACT.setAdapter(adapterDropdownSchedule);
                            bookingRouteACT.setThreshold(1);
                            bookingRouteACT.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Schedule selectedSchedule = (Schedule) parent.getItemAtPosition(position);
                                    tripPrice = selectedSchedule.getPrice();
                                    route_from = selectedSchedule.getRoute_from();
                                    route_to = selectedSchedule.getRoute_to();
                                    computeTotalPrice();
                                    computeCommission();
                                    picker.setListener(new DatePickerListener() {
                                        @Override
                                        public void onDateSelected(DateTime dateSelected) {
                                            if (dateSelected.isBeforeNow()) {
                                                Toast.makeText(UserBookActivity.this, "Please select a later booking date.", Toast.LENGTH_SHORT).show();
                                            } else {
                                                schedule_date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(dateSelected.toDate());
                                                loadScheduleTimeChips(task.getResult().getDocuments().get(position).getReference());
                                            }
                                        }
                                    });
                                }
                            });
                            dialog.dismiss();
                        }
                    }
                });
    }

    private void loadScheduleTimeChips(DocumentReference tripScheduleReference) {
        final ACProgressFlower dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(getResources().getColor(R.color.white))
                .fadeColor(Color.DKGRAY).build();
        dialog.show();
        tripSchedulesTimeList = new ArrayList<>();
        tripScheduleReference.collection("schedules")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (int i = 0; i < task.getResult().getDocuments().size(); i++) {
                                TripSchedule transportCompany = new TripSchedule(task.getResult().getDocuments().get(i).getString("time_depart"));
                                tripSchedulesTimeList.add(i, transportCompany);
                            }
                            adapterDropdownScheduleTime = new AdapterDropdownScheduleTime(UserBookActivity.this, tripSchedulesTimeList);
                            bookingTimeACT.setAdapter(adapterDropdownScheduleTime);
                            bookingTimeACT.setThreshold(1);
                            bookingTimeACT.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    TripSchedule selectedTime = (TripSchedule) parent.getItemAtPosition(position);
                                    schedule_time = convertDate12Hr(selectedTime.getTime_depart());
                                    bookingTimeACT.setText(schedule_time);
                                }
                            });
                            dialog.dismiss();
                        }
                    }
                });
    }

    private void computeTotalPrice() {
        if (tripPrice != 0) {
            if (countSpecial > 0) {
                totalPrice = (tripPrice * (countAdult + countChild)) + ((tripPrice * countSpecial) - (specialDiscount * (tripPrice * countSpecial)));
            } else {
                totalPrice = (tripPrice * (countAdult + countChild));
            }
            bookingTotal.setText(String.format(Locale.ENGLISH, "%.2f", totalPrice));
        }
    }

    private void computeCommission() {
        if (tripPrice != 0) {
            if (countSpecial > 0) {
                totalCommission = (commissionRate * (tripPrice * (countAdult + countChild))) + (commissionRate * ((tripPrice * countSpecial) - (specialDiscount * (tripPrice * countSpecial))));
            } else {
                totalCommission = commissionRate * (tripPrice * (countAdult + countChild));
            }
        }
    }
}