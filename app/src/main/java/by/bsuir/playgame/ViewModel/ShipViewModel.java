package by.bsuir.playgame.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Objects;

import by.bsuir.playgame.Interfece.IFieldViewModel;
import by.bsuir.playgame.TypeField;

public class ShipViewModel extends AndroidViewModel implements IFieldViewModel {
    private final MutableLiveData<String> countPoint = new MutableLiveData<>();
    private final MutableLiveData<String> Points = new MutableLiveData<>();
    private final MutableLiveData<Integer> resultOfSetting = new MutableLiveData<>();
    private final MutableLiveData<int[]> iconId = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    String tempPoints = "";


    public ShipViewModel(@NonNull Application application) {
        super(application);
        int[] temperIconId = new int[100];
        for (int i = 0; i < 10; i++)
            for (int j = 0; j < 10; j++) {
                temperIconId[i * 10 + j] = TypeField.EMPTY.getCode();
            }
        iconId.setValue(temperIconId);
    }

    public void setShip(String count) {
        countPoint.postValue(count);
    }

    public void setPoint(String point) {
        String sizeShip = countPoint.getValue();
        if (sizeShip != null && Integer.parseInt(sizeShip) >= 2) {
            if (tempPoints.length() == 2) {
                Points.setValue(tempPoints + point + countPoint.getValue());
                tempPoints = "";
                countPoint.postValue("0");
                SetShip(Objects.requireNonNull(Points.getValue()));
            } else {
                tempPoints = point;
            }
        } else if (sizeShip != null && (Integer.parseInt(sizeShip) == 1 || Integer.parseInt(sizeShip) == -1)) {
            Points.setValue(point + countPoint.getValue());
            countPoint.postValue("0");
            SetShip(Objects.requireNonNull(Points.getValue()));
        }
    }


    public LiveData<Integer> getResultOfSetShip() {
        return resultOfSetting;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<int[]> getIcon() {
        return iconId;
    }


    private void SetShip(String value) {
        int[] temperIconId = iconId.getValue();
        if (value.length() == 5) {
            if (!check(Integer.parseInt(value.substring(0, 2))) || !check(Integer.parseInt(value.substring(2, 4)))) {
                error.setValue("Нельзя расположить корабль");
                return;
            }
            if (value.charAt(0) == value.charAt(2) && Math.abs(value.charAt(1) - value.charAt(3)) == value.charAt(4) - '0' - 1) {
                for (int i = Math.min(value.charAt(1), value.charAt(3)) - '0'; i <= Math.max(value.charAt(1), value.charAt(3)) - '0'; i++) {
                    Objects.requireNonNull(temperIconId)[(value.charAt(0) - '0') * 10 + i] = TypeField.SHIP.getCode();
                }
            } else if (value.charAt(1) == value.charAt(3) && Math.abs(value.charAt(0) - value.charAt(2)) == value.charAt(4) - '0' - 1) {
                for (int i = Math.min(value.charAt(0), value.charAt(2)) - '0'; i <= Math.max(value.charAt(0), value.charAt(2)) - '0'; i++) {
                    Objects.requireNonNull(temperIconId)[i * 10 + (value.charAt(1) - '0')] = TypeField.SHIP.getCode();
                }
            } else {
                error.setValue("Нельзя расположить корабль");
                return;
            }
            resultOfSetting.setValue(value.charAt(value.length() - 1) - '0');
        } else if (value.length() == 4) {
            int size = deleteShip(Integer.parseInt(value.substring(0, 2)));
            resultOfSetting.setValue(size * -1);
        } else {
            if (!check(Integer.parseInt(value.substring(0, 2)))) {
                error.setValue("Нельзя расположить корабль");
                return;
            }
            Objects.requireNonNull(temperIconId)[(value.charAt(0) - '0') * 10 + (value.charAt(1) - '0')] = TypeField.SHIP.getCode();
            resultOfSetting.setValue(value.charAt(value.length() - 1) - '0');
        }
        iconId.setValue(temperIconId);
    }


    public int deleteShip(int position) {
        int[] temperIconId = iconId.getValue();
        int fullField = Objects.requireNonNull(temperIconId)[position];
        temperIconId[position] = TypeField.EMPTY.getCode();
        int sizeship = 1;
        if (position % 10 == 0 && fullField == temperIconId[position + 1]) {
            return sizeship + deleteShip(position + 1);
        } else if (position % 10 == 9 && fullField == temperIconId[position - 1]) {
            return sizeship + deleteShip(position - 1);
        } else {
            if (fullField == temperIconId[position + 1])
                sizeship += deleteShip(position + 1);
            if (fullField == temperIconId[position - 1])
                return sizeship + deleteShip(position - 1);
        }
        if (position - 10 < 0 && fullField == temperIconId[position + 10]) {
            return sizeship + deleteShip(position + 10);
        } else if (position + 10 > 100 && fullField == temperIconId[position - 10]) {
            return sizeship + deleteShip(position - 10);
        } else {
            if (position < 90 && fullField == temperIconId[position + 10])
                sizeship += deleteShip(position + 10);
            if (position > 9 && fullField == temperIconId[position - 10])
                return sizeship + deleteShip(position - 10);
        }
        return sizeship;
    }

    public boolean check(int position) {
        int[] temperIconId = iconId.getValue();
        int emptyField = Objects.requireNonNull(temperIconId)[position];
        int[] massCheck;
        if (position % 10 == 0) {
            massCheck = new int[]{
                    position - 10, position - 9,
                    position, position + 1,
                    position + 10, position + 11
            };
        } else if (position % 10 == 9) {
            massCheck = new int[]{
                    position - 11, position - 10,
                    position - 1, position,
                    position + 9, position + 10
            };
        } else {
            massCheck = new int[]{
                    position - 11, position - 10, position - 9,
                    position - 1, position, position + 1,
                    position + 9, position + 10, position + 11
            };
        }
        for (int item : massCheck) {
            if (item < 100 && item >= 0) {
                if (emptyField != temperIconId[item]) {
                    return false;
                }
            }
        }
        return true;
    }


}
