package com.opustech.bookvan.ui.book;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BookingsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public BookingsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is contact fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}