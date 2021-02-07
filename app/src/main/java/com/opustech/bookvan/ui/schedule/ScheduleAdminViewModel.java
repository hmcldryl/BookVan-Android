package com.opustech.bookvan.ui.schedule;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ScheduleAdminViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ScheduleAdminViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is schedule fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}