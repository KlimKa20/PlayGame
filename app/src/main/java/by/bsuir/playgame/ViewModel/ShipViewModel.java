package by.bsuir.playgame.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Objects;

import by.bsuir.playgame.Interfece.IFieldViewModel;
import by.bsuir.playgame.R;
import by.bsuir.playgame.Enum.ShipType;
import by.bsuir.playgame.Enum.TypeField;

public class ShipViewModel extends AndroidViewModel implements IFieldViewModel {
    private final MutableLiveData<Integer> countPoint = new MutableLiveData<>();
    private final MutableLiveData<Integer> resultOfSetting = new MutableLiveData<>();
    private final MutableLiveData<int[]> iconId = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    boolean deleter = false;
    String tempPoints = "";


    public ShipViewModel(@NonNull Application application) {
        super(application);
        int[] temperIconId = new int[100];
        for (int i = 0; i < 10; i++)
            for (int j = 0; j < 10; j++) {
                temperIconId[i * 10 + j] = TypeField.EMPTY.getCodeImage();
            }
        iconId.setValue(temperIconId);
    }

    public void setShip(ShipType count) {
        countPoint.postValue(count.getSize());
    }

    public void deleteShip() {
        deleter = true;
    }

    public void setPoint(String point) {
        int sizeShip = countPoint.getValue();
        if (sizeShip >= 2) {
            if (tempPoints.length() >= 2) {
                countPoint.postValue(0);
                SetShip(tempPoints + point, sizeShip);
                tempPoints = "";
            } else {
                tempPoints = point;
            }
        } else if (sizeShip == 1) {
            countPoint.postValue(0);
            SetShip(point, sizeShip);
        } else if (deleter) {
            SetShip(point, 0);
            deleter = false;
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


    private void SetShip(String value, int sizeShip) {
        int[] temperIconId = iconId.getValue();
        if (sizeShip >= 2) {
            if (!check(Integer.parseInt(value.substring(0, 2))) || !check(Integer.parseInt(value.substring(2, 4)))) {
                error.setValue(getApplication().getResources().getString(R.string.Placement_error));
                return;
            }
            if (value.charAt(0) == value.charAt(2) && Math.abs(value.charAt(1) - value.charAt(3)) == sizeShip - 1) {
                for (int i = Math.min(value.charAt(1), value.charAt(3)) - '0'; i <= Math.max(value.charAt(1), value.charAt(3)) - '0'; i++) {
                    Objects.requireNonNull(temperIconId)[(value.charAt(0) - '0') * 10 + i] = TypeField.SHIP.getCodeImage();
                }
            } else if (value.charAt(1) == value.charAt(3) && Math.abs(value.charAt(0) - value.charAt(2)) == sizeShip - 1) {
                for (int i = Math.min(value.charAt(0), value.charAt(2)) - '0'; i <= Math.max(value.charAt(0), value.charAt(2)) - '0'; i++) {
                    Objects.requireNonNull(temperIconId)[i * 10 + (value.charAt(1) - '0')] = TypeField.SHIP.getCodeImage();
                }
            } else {
                error.setValue(getApplication().getResources().getString(R.string.Placement_error));
                return;
            }
            resultOfSetting.setValue(sizeShip);
        } else if (sizeShip == 0 && deleter) {
            int size = deleteShip(Integer.parseInt(value));
            resultOfSetting.setValue(size * -1);
        } else {
            if (!check(Integer.parseInt(value.substring(0, 2)))) {
                error.setValue(getApplication().getResources().getString(R.string.Placement_error));
                return;
            }
            Objects.requireNonNull(temperIconId)[(value.charAt(0) - '0') * 10 + (value.charAt(1) - '0')] = TypeField.SHIP.getCodeImage();
            resultOfSetting.setValue(sizeShip);
        }
        iconId.setValue(temperIconId);
    }


    public int deleteShip(int position) {
        int[] temperIconId = iconId.getValue();
        int fullField = Objects.requireNonNull(temperIconId)[position];
        temperIconId[position] = TypeField.EMPTY.getCodeImage();
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
