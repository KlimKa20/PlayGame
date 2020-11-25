package by.bsuir.playgame;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Objects;

public class ShipViewModel extends AndroidViewModel {
    private final MutableLiveData<String> countPoint = new MutableLiveData<>();
    private final MutableLiveData<String> Points = new MutableLiveData<>();
    private final MutableLiveData<Integer> resultOfSetting = new MutableLiveData<>();
    String tempPoints = "";


    public ShipViewModel(@NonNull Application application) {
        super(application);
    }

    public void setShip(String count) {
        countPoint.postValue(count);
    }

    public void setPoint(String point) {
        String sizeShip = countPoint.getValue();
        if (Integer.parseInt(Objects.requireNonNull(sizeShip)) >= 2) {
            if (tempPoints.length() == 2) {
                Points.setValue(tempPoints + point + countPoint.getValue());
                tempPoints = "";
                countPoint.postValue("0");
            } else {
                tempPoints = point;
            }
        } else if (Integer.parseInt(sizeShip) == 1 || Integer.parseInt(sizeShip) == -1) {
            Points.setValue(point + countPoint.getValue());
            countPoint.postValue("0");
        }
    }

    public LiveData<String> getPoint() {
        return Points;
    }

    public LiveData<Integer> getResultOfSetShip() {
        return resultOfSetting;
    }

    public void setResultOfSetShip(int size) {
        resultOfSetting.setValue(size);
    }
}
