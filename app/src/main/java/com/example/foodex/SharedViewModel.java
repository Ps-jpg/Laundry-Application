package com.example.foodex;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {

    private static final MutableLiveData<String> workType = new MutableLiveData<>();
    private final MutableLiveData<String> username = new MutableLiveData<>();

    public void setWorkTypeForWash() {
        workType.setValue("For wash and fold");
    }

    public void setWorkTypeForIron() {
        workType.setValue("For Iron");
    }

    public void setWorkTypeForDryClean() {
        workType.setValue("For Dry clean");
    }

    public MutableLiveData<String> getWorkTypeLiveData() {
        return workType;
    }

    public String getWorkType() {
        return workType.getValue() != null ? workType.getValue() : "No Work Selected, Contact Customer Support (Null Case)";
    }

    // Method to update workType based on button press
    public void updateWorkType(String buttonPressed) {
        switch (buttonPressed) {
            case "WashButton":
                setWorkTypeForWash();
                break;
            case "IronButton":
                setWorkTypeForIron();
                break;
            case "DryCleanButton":
                setWorkTypeForDryClean();
                break;
            // Add more cases for other buttons if needed
        }
    }

    // Username methods
    public MutableLiveData<String> getUsernameLiveData() {
        return username;
    }

    public void setUsername(String fetchedUsername) {
        username.setValue(fetchedUsername);
    }

    public String getUsername() {
 return username.getValue()!= null ? username.getValue() : "Monkey";
    }
}
