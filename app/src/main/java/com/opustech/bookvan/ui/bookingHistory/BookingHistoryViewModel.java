package com.opustech.bookvan.ui.bookingHistory;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BookingHistoryViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public BookingHistoryViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is schedule fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}