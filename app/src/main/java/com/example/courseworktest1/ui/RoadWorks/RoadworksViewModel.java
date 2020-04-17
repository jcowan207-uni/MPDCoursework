package com.example.courseworktest1.ui.RoadWorks;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RoadworksViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public RoadworksViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}