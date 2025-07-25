package com.opustech.bookvan.ui.user;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.CompoundButtonCompat;

import com.github.jhonnyx2012.horizontalpicker.DatePickerListener;
import com.github.jhonnyx2012.horizontalpicker.HorizontalPicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.radiobutton.MaterialRadioButton;
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
import com.opustech.bookvan.adapters.user.AdapterDropdownTransportCompany;
import com.opustech.bookvan.model.Booking;
import com.opustech.bookvan.model.Schedule;
import com.opustech.bookvan.model.TransportCompany;
import com.opustech.bookvan.model.TripSchedule;
import com.opustech.bookvan.notification.APIService;
import com.opustech.bookvan.notification.Client;
import com.opustech.bookvan.notification.Data;
import com.opustech.bookvan.notification.NotificationSender;
import com.opustech.bookvan.notification.RequestResponse;

import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference usersReference, bookingsReference, partnersReference, schedulesReference;

    private APIService apiService;

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
    private ExtendedFloatingActionButton btnBook, bookingSeat;
    private AutoCompleteTextView inputVanTransportACT, bookingRouteACT, bookingTimeACT;

    private AdapterDropdownTransportCompany adapterDropdownTransportCompany;
    private AdapterDropdownSchedule adapterDropdownSchedule;
    private AdapterDropdownScheduleTime adapterDropdownScheduleTime;
    private ArrayList<Schedule> routeArray;
    private ArrayList<TripSchedule> tripSchedulesTimeList;
    private ArrayList<String> bookingSeatList;
    private ArrayList<String> totalTakenSeats;

    private String transport_uid = "";
    private final String token = "";
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

    private final String admin_uid = "yEali5UosERXD1wizeJGN87ffff2";

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

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

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
                    inputVanTransportACT.clearListSelection();
                    bookingVanTransport.getEditText().getText().clear();
                    bookingTimeACT.clearListSelection();
                    bookingTime.getEditText().getText().clear();
                    populateVanTransportList("north");
                } else if (i == R.id.btnRadioSouth) {
                    inputVanTransportACT.clearListSelection();
                    bookingVanTransport.getEditText().getText().clear();
                    bookingTimeACT.clearListSelection();
                    bookingTime.getEditText().getText().clear();
                    populateVanTransportList("south");
                }
            }
        });

        picker.showTodayButton(false)
                .setOffset(3)
                .setDays(10)
                .init();
        picker.setDate(new DateTime());
        schedule_date = getCurrentDate();
        picker.setListener(new DatePickerListener() {
            @Override
            public void onDateSelected(DateTime dateSelected) {
                if (compareDateInput(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(dateSelected.toDate()))) {
                    schedule_date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(dateSelected.toDate());
                } else {
                    picker.setDate(new DateTime());
                    Toast.makeText(BookActivity.this, "Please select a later booking date.", Toast.LENGTH_SHORT).show();
                }
            }
        });

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

        initializeSeatChooser();
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
        bookingSeat = findViewById(R.id.bookingSeat);

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
        } else if (bookingSeatList == null) {
            enableInput();
            Toast.makeText(this, "Please select seat/s.", Toast.LENGTH_SHORT).show();
        } else if (bookingSeatList.size() == 0) {
            enableInput();
            Toast.makeText(this, "Please select seat/s.", Toast.LENGTH_SHORT).show();
        } else if (bookingSeatList.size() != (countAdult + countChild + countSpecial)) {
            enableInput();
            Toast.makeText(this, "Please select additional " + (bookingSeatList.size() - (countAdult + countChild + countSpecial)) + " seat/s.", Toast.LENGTH_SHORT).show();
        } else {
            generateRefNum(firebaseAuth.getCurrentUser().getUid(), name, contact_number, schedule_date, schedule_time);
        }
    }

    private boolean compareDateInput(String date) {
        Date selectedDate = null;
        Date minDate = null;

        try {
            selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);
            minDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Calendar.getInstance().getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return selectedDate.equals(minDate) || selectedDate.after(minDate);
    }

    private boolean compareDate(String date) {
        Date selectedDate = null;
        Date minDate = null;

        try {
            selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);
            minDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Calendar.getInstance().getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return selectedDate.equals(minDate);
    }

    private boolean afterDate(String date) {
        Date selectedDate = null;
        Date minDate = null;

        try {
            selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);
            minDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Calendar.getInstance().getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return selectedDate.after(minDate);
    }

    private boolean compareTime(String time) {
        Date selectedDate = null;
        Date minDate = null;

        try {
            selectedDate = new SimpleDateFormat("HH:mm", Locale.ENGLISH).parse(time);
            minDate = new SimpleDateFormat("HH:mm", Locale.ENGLISH).parse(new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(Calendar.getInstance().getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return minDate.compareTo(selectedDate) < 0;
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
                            fetchToken(dialog, reference_number, uid, name, contact_number);
                        } else {
                            dialog.dismiss();
                            enableInput();
                            Toast.makeText(BookActivity.this, "Failed.", Toast.LENGTH_SHORT).show();
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

    private void addNewBooking(ACProgressFlower dialog, String reference_number, String uid, String name, String contact_number, String schedule_date, String schedule_time, String token) {
        Booking booking = new Booking(reference_number, uid, name, contact_number, route_from, route_to, schedule_date, schedule_time, transport_uid, "pending", getCurrentDate(), generateTimestamp(), countAdult, countChild, countSpecial, bookingSeatList, totalPrice, totalCommission);
        bookingsReference.add(booking)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            Toast.makeText(BookActivity.this, "Success.", Toast.LENGTH_LONG).show();
                            sendNotifToAdmin("New Booking", name + " has booked a trip from " + route_from + " to " + route_to + " on " + schedule_date + " " + schedule_time + ".");
                            sendNotification(token, "New Booking", name + " has booked a trip from " + route_from + " to " + route_to + " on " + schedule_date + " " + schedule_time + ".");
                            Intent intent = new Intent(BookActivity.this, BookingActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            dialog.dismiss();
                            enableInput();
                            Toast.makeText(BookActivity.this, "Failed. " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
        if (route.equalsIgnoreCase("north")) {
            partnersReference.whereEqualTo("route_north", true)
                    .whereEqualTo("account_disabled", false)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (int i = 0; i < task.getResult().getDocuments().size(); i++) {
                                    TransportCompany transportCompany = new TransportCompany(task.getResult().getDocuments().get(i).getString("uid"), task.getResult().getDocuments().get(i).getString("name"), task.getResult().getDocuments().get(i).getString("photo_url"));
                                    vanTransportList.add(i, transportCompany);
                                }
                                adapterDropdownTransportCompany = new AdapterDropdownTransportCompany(BookActivity.this, vanTransportList);
                                inputVanTransportACT.setAdapter(adapterDropdownTransportCompany);
                                inputVanTransportACT.setThreshold(1);
                                inputVanTransportACT.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        bookingRouteACT.clearListSelection();
                                        bookingRoute.getEditText().getText().clear();
                                        bookingTimeACT.clearListSelection();
                                        bookingTime.getEditText().getText().clear();
                                        TransportCompany selectedTransport = (TransportCompany) adapterView.getItemAtPosition(i);
                                        transport_uid = selectedTransport.getUid();
                                        populateRouteList(transport_uid);
                                    }
                                });
                                dialog.dismiss();
                            }
                        }
                    });
        } else if (route.equalsIgnoreCase("south")) {
            partnersReference.whereEqualTo("route_south", true)
                    .whereEqualTo("account_disabled", false)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (int i = 0; i < task.getResult().getDocuments().size(); i++) {
                                    TransportCompany transportCompany = new TransportCompany(task.getResult().getDocuments().get(i).getString("uid"), task.getResult().getDocuments().get(i).getString("name"), task.getResult().getDocuments().get(i).getString("photo_url"));
                                    vanTransportList.add(i, transportCompany);
                                }
                                adapterDropdownTransportCompany = new AdapterDropdownTransportCompany(BookActivity.this, vanTransportList);
                                inputVanTransportACT.setAdapter(adapterDropdownTransportCompany);
                                inputVanTransportACT.setThreshold(1);
                                inputVanTransportACT.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        bookingRoute.getEditText().getText().clear();
                                        bookingRouteACT.clearListSelection();
                                        TransportCompany selectedTransport = (TransportCompany) adapterView.getItemAtPosition(i);
                                        transport_uid = selectedTransport.getUid();
                                        populateRouteList(transport_uid);
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
                            adapterDropdownSchedule = new AdapterDropdownSchedule(BookActivity.this, routeArray);
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
                                    bookingTimeACT.clearListSelection();
                                    bookingTime.getEditText().getText().clear();
                                    loadScheduleTimeChips(task.getResult().getDocuments().get(position).getReference());
                                    picker.setListener(new DatePickerListener() {
                                        @Override
                                        public void onDateSelected(DateTime dateSelected) {
                                            if (compareDateInput(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(dateSelected.toDate()))) {
                                                schedule_date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(dateSelected.toDate());
                                                bookingTimeACT.clearListSelection();
                                                bookingTime.getEditText().getText().clear();
                                                loadScheduleTimeChips(task.getResult().getDocuments().get(position).getReference());
                                            } else {
                                                bookingTimeACT.clearListSelection();
                                                bookingTime.getEditText().getText().clear();
                                                Toast.makeText(BookActivity.this, "Please select a later booking date.", Toast.LENGTH_SHORT).show();
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
                .orderBy("time_depart", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (compareDate(schedule_date)) {
                                for (int i = 0; i < task.getResult().getDocuments().size(); i++) {
                                    if (compareTime(task.getResult().getDocuments().get(i).getString("time_depart"))) {
                                        if (task.getResult().getDocuments().get(i).getString("time_depart").equals("12:00")) {
                                            TripSchedule transportCompany = new TripSchedule("12:00 NN");
                                            tripSchedulesTimeList.add(transportCompany);
                                        } else if (task.getResult().getDocuments().get(i).getString("time_depart").equals("00:00")) {
                                            TripSchedule transportCompany = new TripSchedule("12:00 MN");
                                            tripSchedulesTimeList.add(transportCompany);
                                        } else {
                                            TripSchedule transportCompany = new TripSchedule(task.getResult().getDocuments().get(i).getString("time_depart"));
                                            tripSchedulesTimeList.add(transportCompany);
                                        }
                                    }
                                }
                            } else {
                                for (int i = 0; i < task.getResult().getDocuments().size(); i++) {
                                    if (task.getResult().getDocuments().get(i).getString("time_depart").equals("12:00")) {
                                        TripSchedule transportCompany = new TripSchedule("12:00 NN");
                                        tripSchedulesTimeList.add(transportCompany);
                                    } else if (task.getResult().getDocuments().get(i).getString("time_depart").equals("00:00")) {
                                        TripSchedule transportCompany = new TripSchedule("12:00 MN");
                                        tripSchedulesTimeList.add(transportCompany);
                                    } else {
                                        TripSchedule transportCompany = new TripSchedule(task.getResult().getDocuments().get(i).getString("time_depart"));
                                        tripSchedulesTimeList.add(transportCompany);
                                    }
                                }
                            }
                            adapterDropdownScheduleTime = new AdapterDropdownScheduleTime(BookActivity.this, tripSchedulesTimeList);
                            bookingTimeACT.setAdapter(adapterDropdownScheduleTime);
                            bookingTimeACT.setThreshold(1);

                            if (adapterDropdownScheduleTime.getCount() > 0) {
                                TripSchedule selectedTime = adapterDropdownScheduleTime.getItem(0);
                                schedule_time = convertDate12Hr(selectedTime.getTime_depart());
                                bookingTimeACT.setText(convertDate12Hr(selectedTime.getTime_depart()));
                            }

                            bookingTimeACT.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    bookingTime.getEditText().getText().clear();
                                }
                            });

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

    private void sendNotifToAdmin(String name, String message) {
        FirebaseFirestore.getInstance().collection("tokens")
                .document(admin_uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isComplete()) {
                            sendNotification(task.getResult().getString("token"), name, message);
                        }
                    }
                });
    }

    private void fetchToken(ACProgressFlower dialog, String reference_number, String uid, String name, String contact_number) {
        partnersReference.document(transport_uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                if (task.getResult().getString("admin_uid") != null)
                                    FirebaseFirestore.getInstance().collection("tokens")
                                            .document(task.getResult().getString("admin_uid"))
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        addNewBooking(dialog, reference_number, uid, name, contact_number, schedule_date, schedule_time, task.getResult().getString("token"));
                                                    } else {
                                                        dialog.dismiss();
                                                        enableInput();
                                                        Toast.makeText(BookActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                            }
                        }
                    }
                });
    }

    private void sendNotification(String token, String title, String message) {
        Data data = new Data(title, message);
        NotificationSender sender = new NotificationSender(data, token);
        apiService.sendNotification(sender).enqueue(new Callback<RequestResponse>() {
            @Override
            public void onResponse(Call<RequestResponse> call, Response<RequestResponse> response) {
                if (response.code() == 200) {
                    if (response.body().success != 1) {
                        //Toast.makeText(BookActivity.this, "Request failed.", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<RequestResponse> call, Throwable t) {

            }
        });
    }

    private void initializeSeatChooser() {
        bookingSeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSeatPicker();
            }
        });
    }

    private void startSeatPicker() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog alertDialog = builder.create();
        if (!alertDialog.isShowing()) {
            final LayoutInflater inflater = getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.dialog_seat_picker, null);
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.setCancelable(false);
            alertDialog.setView(dialogView);

            TextView seatCount;
            MaterialCheckBox cbA2, cbA3,
                    cbB1, cbB2, cbB3,
                    cbC1, cbC2, cbC3,
                    cbD1, cbD2, cbD3,
                    cbE1, cbE2, cbE3;
            MaterialButton btnConfirm;

            seatCount = dialogView.findViewById(R.id.seatCount);
            cbA2 = dialogView.findViewById(R.id.cbA2);
            cbA3 = dialogView.findViewById(R.id.cbA3);
            cbB1 = dialogView.findViewById(R.id.cbB1);
            cbB2 = dialogView.findViewById(R.id.cbB2);
            cbB3 = dialogView.findViewById(R.id.cbB3);
            cbC1 = dialogView.findViewById(R.id.cbC1);
            cbC2 = dialogView.findViewById(R.id.cbC2);
            cbC3 = dialogView.findViewById(R.id.cbC3);
            cbD1 = dialogView.findViewById(R.id.cbD1);
            cbD2 = dialogView.findViewById(R.id.cbD2);
            cbD3 = dialogView.findViewById(R.id.cbD3);
            cbE1 = dialogView.findViewById(R.id.cbE1);
            cbE2 = dialogView.findViewById(R.id.cbE2);
            cbE3 = dialogView.findViewById(R.id.cbE3);
            btnConfirm = dialogView.findViewById(R.id.btnConfirm);

            int pax = countAdult + countChild + countSpecial;
            String paxCount = "0/" + pax;
            seatCount.setText(paxCount);

            bookingsReference.whereEqualTo("transport_uid", transport_uid)
                    .whereEqualTo("route_from", route_from)
                    .whereEqualTo("route_to", route_to)
                    .whereEqualTo("schedule_date", schedule_date)
                    .whereEqualTo("schedule_time", schedule_time)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                bookingSeatList = new ArrayList<>();
                                if (task.getResult().getDocuments().size() > 0) {
                                    totalTakenSeats = new ArrayList<>();
                                    for (int i = 0; i < task.getResult().getDocuments().size(); i++) {
                                        ArrayList<String> list = (ArrayList<String>) task.getResult().getDocuments().get(i).get("seat");
                                        totalTakenSeats.addAll(list);
                                    }
                                    if (totalTakenSeats.contains("A2")) {
                                        CompoundButtonCompat.setButtonTintList(cbA2, ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                                        cbA2.setEnabled(false);
                                    } else {
                                        cbA2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                            @Override
                                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                                if (b) {
                                                    if (bookingSeatList.size() < pax) {
                                                        bookingSeatList.add("A2");
                                                        String display = bookingSeatList.size() + "/" + pax;
                                                        seatCount.setText(display);
                                                    } else {
                                                        Toast.makeText(BookActivity.this, "You have already selected the maximum number of seats.", Toast.LENGTH_SHORT).show();
                                                        cbA2.setChecked(false);
                                                    }
                                                } else {
                                                    if (bookingSeatList.size() > 0) {
                                                        bookingSeatList.remove("A2");
                                                        String display = bookingSeatList.size() + "/" + pax;
                                                        seatCount.setText(display);
                                                    }
                                                }
                                            }
                                        });
                                    }
                                    if (totalTakenSeats.contains("A3")) {
                                        CompoundButtonCompat.setButtonTintList(cbA3, ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                                        cbA3.setEnabled(false);
                                    } else {
                                        cbA3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                            @Override
                                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                                if (b) {
                                                    if (bookingSeatList.size() < pax) {
                                                        bookingSeatList.add("A3");
                                                        String display = bookingSeatList.size() + "/" + pax;
                                                        seatCount.setText(display);
                                                    } else {
                                                        Toast.makeText(BookActivity.this, "You have already selected the maximum number of seats.", Toast.LENGTH_SHORT).show();
                                                        cbA3.setChecked(false);
                                                    }
                                                } else {
                                                    if (bookingSeatList.size() > 0) {
                                                        bookingSeatList.remove("A3");
                                                        String display = bookingSeatList.size() + "/" + pax;
                                                        seatCount.setText(display);
                                                    }
                                                }
                                            }
                                        });
                                    }
                                    if (totalTakenSeats.contains("B1")) {
                                        CompoundButtonCompat.setButtonTintList(cbB1, ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                                        cbB1.setEnabled(false);
                                    } else {
                                        cbB1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                            @Override
                                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                                if (b) {
                                                    if (bookingSeatList.size() < pax) {
                                                        bookingSeatList.add("B1");
                                                        String display = bookingSeatList.size() + "/" + pax;
                                                        seatCount.setText(display);
                                                    } else {
                                                        Toast.makeText(BookActivity.this, "You have already selected the maximum number of seats.", Toast.LENGTH_SHORT).show();
                                                        cbB1.setChecked(false);
                                                    }
                                                } else {
                                                    if (bookingSeatList.size() > 0) {
                                                        bookingSeatList.remove("B1");
                                                        String display = bookingSeatList.size() + "/" + pax;
                                                        seatCount.setText(display);
                                                    }
                                                }
                                            }
                                        });
                                    }
                                    if (totalTakenSeats.contains("B2")) {
                                        CompoundButtonCompat.setButtonTintList(cbB2, ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                                        cbB2.setEnabled(false);
                                    } else {
                                        cbB2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                            @Override
                                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                                if (b) {
                                                    if (bookingSeatList.size() < pax) {
                                                        bookingSeatList.add("B2");
                                                        String display = bookingSeatList.size() + "/" + pax;
                                                        seatCount.setText(display);
                                                    } else {
                                                        Toast.makeText(BookActivity.this, "You have already selected the maximum number of seats.", Toast.LENGTH_SHORT).show();
                                                        cbB2.setChecked(false);
                                                    }
                                                } else {
                                                    if (bookingSeatList.size() > 0) {
                                                        bookingSeatList.remove("B2");
                                                        String display = bookingSeatList.size() + "/" + pax;
                                                        seatCount.setText(display);
                                                    }
                                                }
                                            }
                                        });
                                    }
                                    if (totalTakenSeats.contains("B3")) {
                                        CompoundButtonCompat.setButtonTintList(cbB3, ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                                        cbB3.setEnabled(false);
                                    } else {
                                        cbB3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                            @Override
                                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                                if (b) {
                                                    if (bookingSeatList.size() < pax) {
                                                        bookingSeatList.add("B3");
                                                        String display = bookingSeatList.size() + "/" + pax;
                                                        seatCount.setText(display);
                                                    } else {
                                                        Toast.makeText(BookActivity.this, "You have already selected the maximum number of seats.", Toast.LENGTH_SHORT).show();
                                                        cbB3.setChecked(false);
                                                    }
                                                } else {
                                                    if (bookingSeatList.size() > 0) {
                                                        bookingSeatList.remove("B3");
                                                        String display = bookingSeatList.size() + "/" + pax;
                                                        seatCount.setText(display);
                                                    }
                                                }
                                            }
                                        });
                                    }
                                    if (totalTakenSeats.contains("C1")) {
                                        CompoundButtonCompat.setButtonTintList(cbC1, ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                                        cbC1.setEnabled(false);
                                    } else {
                                        cbC1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                            @Override
                                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                                if (b) {
                                                    if (bookingSeatList.size() < pax) {
                                                        bookingSeatList.add("C1");
                                                        String display = bookingSeatList.size() + "/" + pax;
                                                        seatCount.setText(display);
                                                    } else {
                                                        Toast.makeText(BookActivity.this, "You have already selected the maximum number of seats.", Toast.LENGTH_SHORT).show();
                                                        cbC1.setChecked(false);
                                                    }
                                                } else {
                                                    if (bookingSeatList.size() > 0) {
                                                        bookingSeatList.remove("C1");
                                                        String display = bookingSeatList.size() + "/" + pax;
                                                        seatCount.setText(display);
                                                    }
                                                }
                                            }
                                        });
                                    }
                                    if (totalTakenSeats.contains("C2")) {
                                        CompoundButtonCompat.setButtonTintList(cbC2, ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                                        cbC2.setEnabled(false);
                                    } else {
                                        cbC2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                            @Override
                                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                                if (b) {
                                                    if (bookingSeatList.size() < pax) {
                                                        bookingSeatList.add("C2");
                                                        String display = bookingSeatList.size() + "/" + pax;
                                                        seatCount.setText(display);
                                                    } else {
                                                        Toast.makeText(BookActivity.this, "You have already selected the maximum number of seats.", Toast.LENGTH_SHORT).show();
                                                        cbC2.setChecked(false);
                                                    }
                                                } else {
                                                    if (bookingSeatList.size() > 0) {
                                                        bookingSeatList.remove("C2");
                                                        String display = bookingSeatList.size() + "/" + pax;
                                                        seatCount.setText(display);
                                                    }
                                                }
                                            }
                                        });
                                    }
                                    if (totalTakenSeats.contains("C3")) {
                                        CompoundButtonCompat.setButtonTintList(cbC3, ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                                        cbC3.setEnabled(false);
                                    } else {
                                        cbC3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                            @Override
                                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                                if (b) {
                                                    if (bookingSeatList.size() < pax) {
                                                        bookingSeatList.add("C3");
                                                        String display = bookingSeatList.size() + "/" + pax;
                                                        seatCount.setText(display);
                                                    } else {
                                                        Toast.makeText(BookActivity.this, "You have already selected the maximum number of seats.", Toast.LENGTH_SHORT).show();
                                                        cbC3.setChecked(false);
                                                    }
                                                } else {
                                                    if (bookingSeatList.size() > 0) {
                                                        bookingSeatList.remove("C3");
                                                        String display = bookingSeatList.size() + "/" + pax;
                                                        seatCount.setText(display);
                                                    }
                                                }
                                            }
                                        });
                                    }
                                    if (totalTakenSeats.contains("D1")) {
                                        CompoundButtonCompat.setButtonTintList(cbD1, ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                                        cbD1.setEnabled(false);
                                    } else {
                                        cbD1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                            @Override
                                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                                if (b) {
                                                    if (bookingSeatList.size() < pax) {
                                                        bookingSeatList.add("D1");
                                                        String display = bookingSeatList.size() + "/" + pax;
                                                        seatCount.setText(display);
                                                    } else {
                                                        Toast.makeText(BookActivity.this, "You have already selected the maximum number of seats.", Toast.LENGTH_SHORT).show();
                                                        cbD1.setChecked(false);
                                                    }
                                                } else {
                                                    if (bookingSeatList.size() > 0) {
                                                        bookingSeatList.remove("D1");
                                                        String display = bookingSeatList.size() + "/" + pax;
                                                        seatCount.setText(display);
                                                    }
                                                }
                                            }
                                        });
                                    }
                                    if (totalTakenSeats.contains("D2")) {
                                        CompoundButtonCompat.setButtonTintList(cbD2, ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                                        cbD2.setEnabled(false);
                                    } else {
                                        cbD2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                            @Override
                                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                                if (b) {
                                                    if (bookingSeatList.size() < pax) {
                                                        bookingSeatList.add("D2");
                                                        String display = bookingSeatList.size() + "/" + pax;
                                                        seatCount.setText(display);
                                                    } else {
                                                        Toast.makeText(BookActivity.this, "You have already selected the maximum number of seats.", Toast.LENGTH_SHORT).show();
                                                        cbD2.setChecked(false);
                                                    }
                                                } else {
                                                    if (bookingSeatList.size() > 0) {
                                                        bookingSeatList.remove("D2");
                                                        String display = bookingSeatList.size() + "/" + pax;
                                                        seatCount.setText(display);
                                                    }
                                                }
                                            }
                                        });
                                    }
                                    if (totalTakenSeats.contains("D3")) {
                                        CompoundButtonCompat.setButtonTintList(cbD3, ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                                        cbD3.setEnabled(false);
                                    } else {
                                        cbD3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                            @Override
                                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                                if (b) {
                                                    if (bookingSeatList.size() < pax) {
                                                        bookingSeatList.add("D3");
                                                        String display = bookingSeatList.size() + "/" + pax;
                                                        seatCount.setText(display);
                                                    } else {
                                                        Toast.makeText(BookActivity.this, "You have already selected the maximum number of seats.", Toast.LENGTH_SHORT).show();
                                                        cbD3.setChecked(false);
                                                    }
                                                } else {
                                                    if (bookingSeatList.size() > 0) {
                                                        bookingSeatList.remove("D3");
                                                        String display = bookingSeatList.size() + "/" + pax;
                                                        seatCount.setText(display);
                                                    }
                                                }
                                            }
                                        });
                                    }
                                    if (totalTakenSeats.contains("E1")) {
                                        CompoundButtonCompat.setButtonTintList(cbE1, ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                                        cbE1.setEnabled(false);
                                    } else {
                                        cbE1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                            @Override
                                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                                if (b) {
                                                    if (bookingSeatList.size() < pax) {
                                                        bookingSeatList.add("E1");
                                                        String display = bookingSeatList.size() + "/" + pax;
                                                        seatCount.setText(display);
                                                    } else {
                                                        Toast.makeText(BookActivity.this, "You have already selected the maximum number of seats.", Toast.LENGTH_SHORT).show();
                                                        cbE1.setChecked(false);
                                                    }
                                                } else {
                                                    if (bookingSeatList.size() > 0) {
                                                        bookingSeatList.remove("E1");
                                                        String display = bookingSeatList.size() + "/" + pax;
                                                        seatCount.setText(display);
                                                    }
                                                }
                                            }
                                        });
                                    }
                                    if (totalTakenSeats.contains("E2")) {
                                        CompoundButtonCompat.setButtonTintList(cbE2, ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                                        cbE2.setEnabled(false);
                                    } else {
                                        cbE2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                            @Override
                                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                                if (b) {
                                                    if (bookingSeatList.size() < pax) {
                                                        bookingSeatList.add("E2");
                                                        String display = bookingSeatList.size() + "/" + pax;
                                                        seatCount.setText(display);
                                                    } else {
                                                        Toast.makeText(BookActivity.this, "You have already selected the maximum number of seats.", Toast.LENGTH_SHORT).show();
                                                        cbE2.setChecked(false);
                                                    }
                                                } else {
                                                    if (bookingSeatList.size() > 0) {
                                                        bookingSeatList.remove("E2");
                                                        String display = bookingSeatList.size() + "/" + pax;
                                                        seatCount.setText(display);
                                                    }
                                                }
                                            }
                                        });
                                    }
                                    if (totalTakenSeats.contains("E3")) {
                                        CompoundButtonCompat.setButtonTintList(cbE3, ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                                        cbE3.setEnabled(false);
                                    } else {
                                        cbE3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                            @Override
                                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                                if (b) {
                                                    if (bookingSeatList.size() < pax) {
                                                        bookingSeatList.add("E3");
                                                        String display = bookingSeatList.size() + "/" + pax;
                                                        seatCount.setText(display);
                                                    } else {
                                                        Toast.makeText(BookActivity.this, "You have already selected the maximum number of seats.", Toast.LENGTH_SHORT).show();
                                                        cbE3.setChecked(false);
                                                    }
                                                } else {
                                                    if (bookingSeatList.size() > 0) {
                                                        bookingSeatList.remove("E3");
                                                        String display = bookingSeatList.size() + "/" + pax;
                                                        seatCount.setText(display);
                                                    }
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    cbA2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                            if (b) {
                                                if (bookingSeatList.size() < pax) {
                                                    bookingSeatList.add("A2");
                                                    String display = bookingSeatList.size() + "/" + pax;
                                                    seatCount.setText(display);
                                                } else {
                                                    Toast.makeText(BookActivity.this, "You have already selected the maximum number of seats.", Toast.LENGTH_SHORT).show();
                                                    cbA2.setChecked(false);
                                                }
                                            } else {
                                                if (bookingSeatList.size() > 0) {
                                                    bookingSeatList.remove("A2");
                                                    String display = bookingSeatList.size() + "/" + pax;
                                                    seatCount.setText(display);
                                                }
                                            }
                                        }
                                    });
                                    cbA3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                            if (b) {
                                                if (bookingSeatList.size() < pax) {
                                                    bookingSeatList.add("A3");
                                                    String display = bookingSeatList.size() + "/" + pax;
                                                    seatCount.setText(display);
                                                } else {
                                                    Toast.makeText(BookActivity.this, "You have already selected the maximum number of seats.", Toast.LENGTH_SHORT).show();
                                                    cbA3.setChecked(false);
                                                }
                                            } else {
                                                if (bookingSeatList.size() > 0) {
                                                    bookingSeatList.remove("A3");
                                                    String display = bookingSeatList.size() + "/" + pax;
                                                    seatCount.setText(display);
                                                }
                                            }
                                        }
                                    });
                                    cbB1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                            if (b) {
                                                if (bookingSeatList.size() < pax) {
                                                    bookingSeatList.add("B1");
                                                    String display = bookingSeatList.size() + "/" + pax;
                                                    seatCount.setText(display);
                                                } else {
                                                    Toast.makeText(BookActivity.this, "You have already selected the maximum number of seats.", Toast.LENGTH_SHORT).show();
                                                    cbB1.setChecked(false);
                                                }
                                            } else {
                                                if (bookingSeatList.size() > 0) {
                                                    bookingSeatList.remove("B1");
                                                    String display = bookingSeatList.size() + "/" + pax;
                                                    seatCount.setText(display);
                                                }
                                            }
                                        }
                                    });
                                    cbB2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                            if (b) {
                                                if (bookingSeatList.size() < pax) {
                                                    bookingSeatList.add("B2");
                                                    String display = bookingSeatList.size() + "/" + pax;
                                                    seatCount.setText(display);
                                                } else {
                                                    Toast.makeText(BookActivity.this, "You have already selected the maximum number of seats.", Toast.LENGTH_SHORT).show();
                                                    cbB2.setChecked(false);
                                                }
                                            } else {
                                                if (bookingSeatList.size() > 0) {
                                                    bookingSeatList.remove("B2");
                                                    String display = bookingSeatList.size() + "/" + pax;
                                                    seatCount.setText(display);
                                                }
                                            }
                                        }
                                    });
                                    cbB3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                            if (b) {
                                                if (bookingSeatList.size() < pax) {
                                                    bookingSeatList.add("B3");
                                                    String display = bookingSeatList.size() + "/" + pax;
                                                    seatCount.setText(display);
                                                } else {
                                                    Toast.makeText(BookActivity.this, "You have already selected the maximum number of seats.", Toast.LENGTH_SHORT).show();
                                                    cbB3.setChecked(false);
                                                }
                                            } else {
                                                if (bookingSeatList.size() > 0) {
                                                    bookingSeatList.remove("B3");
                                                    String display = bookingSeatList.size() + "/" + pax;
                                                    seatCount.setText(display);
                                                }
                                            }
                                        }
                                    });
                                    cbC1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                            if (b) {
                                                if (bookingSeatList.size() < pax) {
                                                    bookingSeatList.add("C1");
                                                    String display = bookingSeatList.size() + "/" + pax;
                                                    seatCount.setText(display);
                                                } else {
                                                    Toast.makeText(BookActivity.this, "You have already selected the maximum number of seats.", Toast.LENGTH_SHORT).show();
                                                    cbC1.setChecked(false);
                                                }
                                            } else {
                                                if (bookingSeatList.size() > 0) {
                                                    bookingSeatList.remove("C1");
                                                    String display = bookingSeatList.size() + "/" + pax;
                                                    seatCount.setText(display);
                                                }
                                            }
                                        }
                                    });
                                    cbC2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                            if (b) {
                                                if (bookingSeatList.size() < pax) {
                                                    bookingSeatList.add("C2");
                                                    String display = bookingSeatList.size() + "/" + pax;
                                                    seatCount.setText(display);
                                                } else {
                                                    Toast.makeText(BookActivity.this, "You have already selected the maximum number of seats.", Toast.LENGTH_SHORT).show();
                                                    cbC2.setChecked(false);
                                                }
                                            } else {
                                                if (bookingSeatList.size() > 0) {
                                                    bookingSeatList.remove("C2");
                                                    String display = bookingSeatList.size() + "/" + pax;
                                                    seatCount.setText(display);
                                                }
                                            }
                                        }
                                    });
                                    cbC3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                            if (b) {
                                                if (bookingSeatList.size() < pax) {
                                                    bookingSeatList.add("C3");
                                                    String display = bookingSeatList.size() + "/" + pax;
                                                    seatCount.setText(display);
                                                } else {
                                                    Toast.makeText(BookActivity.this, "You have already selected the maximum number of seats.", Toast.LENGTH_SHORT).show();
                                                    cbC3.setChecked(false);
                                                }
                                            } else {
                                                if (bookingSeatList.size() > 0) {
                                                    bookingSeatList.remove("C3");
                                                    String display = bookingSeatList.size() + "/" + pax;
                                                    seatCount.setText(display);
                                                }
                                            }
                                        }
                                    });
                                    cbD1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                            if (b) {
                                                if (bookingSeatList.size() < pax) {
                                                    bookingSeatList.add("D1");
                                                    String display = bookingSeatList.size() + "/" + pax;
                                                    seatCount.setText(display);
                                                } else {
                                                    Toast.makeText(BookActivity.this, "You have already selected the maximum number of seats.", Toast.LENGTH_SHORT).show();
                                                    cbD1.setChecked(false);
                                                }
                                            } else {
                                                if (bookingSeatList.size() > 0) {
                                                    bookingSeatList.remove("D1");
                                                    String display = bookingSeatList.size() + "/" + pax;
                                                    seatCount.setText(display);
                                                }
                                            }
                                        }
                                    });
                                    cbD2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                            if (b) {
                                                if (bookingSeatList.size() < pax) {
                                                    bookingSeatList.add("D2");
                                                    String display = bookingSeatList.size() + "/" + pax;
                                                    seatCount.setText(display);
                                                } else {
                                                    Toast.makeText(BookActivity.this, "You have already selected the maximum number of seats.", Toast.LENGTH_SHORT).show();
                                                    cbD2.setChecked(false);
                                                }
                                            } else {
                                                if (bookingSeatList.size() > 0) {
                                                    bookingSeatList.remove("D2");
                                                    String display = bookingSeatList.size() + "/" + pax;
                                                    seatCount.setText(display);
                                                }
                                            }
                                        }
                                    });
                                    cbD3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                            if (b) {
                                                if (bookingSeatList.size() < pax) {
                                                    bookingSeatList.add("D3");
                                                    String display = bookingSeatList.size() + "/" + pax;
                                                    seatCount.setText(display);
                                                } else {
                                                    Toast.makeText(BookActivity.this, "You have already selected the maximum number of seats.", Toast.LENGTH_SHORT).show();
                                                    cbD3.setChecked(false);
                                                }
                                            } else {
                                                if (bookingSeatList.size() > 0) {
                                                    bookingSeatList.remove("D3");
                                                    String display = bookingSeatList.size() + "/" + pax;
                                                    seatCount.setText(display);
                                                }
                                            }
                                        }
                                    });
                                    cbE1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                            if (b) {
                                                if (bookingSeatList.size() < pax) {
                                                    bookingSeatList.add("E1");
                                                    String display = bookingSeatList.size() + "/" + pax;
                                                    seatCount.setText(display);
                                                } else {
                                                    Toast.makeText(BookActivity.this, "You have already selected the maximum number of seats.", Toast.LENGTH_SHORT).show();
                                                    cbE1.setChecked(false);
                                                }
                                            } else {
                                                if (bookingSeatList.size() > 0) {
                                                    bookingSeatList.remove("E1");
                                                    String display = bookingSeatList.size() + "/" + pax;
                                                    seatCount.setText(display);
                                                }
                                            }
                                        }
                                    });
                                    cbE2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                            if (b) {
                                                if (bookingSeatList.size() < pax) {
                                                    bookingSeatList.add("E2");
                                                    String display = bookingSeatList.size() + "/" + pax;
                                                    seatCount.setText(display);
                                                } else {
                                                    Toast.makeText(BookActivity.this, "You have already selected the maximum number of seats.", Toast.LENGTH_SHORT).show();
                                                    cbE2.setChecked(false);
                                                }
                                            } else {
                                                if (bookingSeatList.size() > 0) {
                                                    bookingSeatList.remove("E2");
                                                    String display = bookingSeatList.size() + "/" + pax;
                                                    seatCount.setText(display);
                                                }
                                            }
                                        }
                                    });
                                    cbE3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                            if (b) {
                                                if (bookingSeatList.size() < pax) {
                                                    bookingSeatList.add("E3");
                                                    String display = bookingSeatList.size() + "/" + pax;
                                                    seatCount.setText(display);
                                                } else {
                                                    Toast.makeText(BookActivity.this, "You have already selected the maximum number of seats.", Toast.LENGTH_SHORT).show();
                                                    cbE3.setChecked(false);
                                                }
                                            } else {
                                                if (bookingSeatList.size() > 0) {
                                                    bookingSeatList.remove("E3");
                                                    String display = bookingSeatList.size() + "/" + pax;
                                                    seatCount.setText(display);
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });

            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });

            alertDialog.show();
        }
    }
}