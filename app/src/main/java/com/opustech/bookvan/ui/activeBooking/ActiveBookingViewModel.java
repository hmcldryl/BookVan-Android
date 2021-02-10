package com.opustech.bookvan.ui.activeBooking;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ActiveBookingViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ActiveBookingViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is schedule fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}