package com.opustech.bookvan.ui.van_companies;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class VanCompaniesViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public VanCompaniesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is contact fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}