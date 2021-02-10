package com.opustech.bookvan.ui.vanCompany;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class VanCompanyViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public VanCompanyViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is contact fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}