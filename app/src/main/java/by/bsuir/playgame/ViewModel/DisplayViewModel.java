package by.bsuir.playgame.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import by.bsuir.playgame.Interfece.IFieldViewModel;
import by.bsuir.playgame.TypeField;

public class DisplayViewModel extends AndroidViewModel implements IFieldViewModel {

    private final MutableLiveData<int[]> iconId = new MutableLiveData<>();
    private final MutableLiveData<Map<String, Object>> destroy = new MutableLiveData<>();

    public DisplayViewModel(@NonNull Application application) {
        super(application);
        int[] temperIconId = new int[100];
        for (int i = 0; i < 10; i++)
            for (int j = 0; j < 10; j++) {
                temperIconId[i * 10 + j] = TypeField.EMPTY.getCode();
            }
        iconId.setValue(temperIconId);
    }

    @Override
    public void setPoint(String point) {

    }

    public void setIconId(int position, int type) {
        int[] temp = iconId.getValue();
        if (type == 2) {
            Objects.requireNonNull(temp)[position] = TypeField.HURT.getCode();
            checkDestroy(position);
        } else if (type == 3) {
            Objects.requireNonNull(temp)[position] = TypeField.LOSE.getCode();
        } else if (type == 4) {
            Objects.requireNonNull(temp)[position] = TypeField.DESTROY.getCode();
        }
        iconId.setValue(temp);
    }

    private void checkDestroy(int position) {
        int[] field = iconId.getValue();
        ArrayList<Integer> temp = new ArrayList<>();
        temp.add(position);
        if (position % 10 == 0) {
            for (int i = 1; i < 5; i++) {
                if (Objects.requireNonNull(field)[position + i] == TypeField.HURT.getCode()) {
                    temp.add(position + i);
                } else if (field[position + i] == TypeField.SHIP.getCode() || field[position + i] == TypeField.DESTROY.getCode()) {
                    return;
                } else {
                    break;
                }
            }
        } else if (position % 10 == 9) {
            for (int i = 1; i < 5; i++) {
                if (Objects.requireNonNull(field)[position - i] == TypeField.HURT.getCode()) {
                    temp.add(position - i);
                } else if (field[position - i] == TypeField.SHIP.getCode() || field[position + i] == TypeField.DESTROY.getCode()) {
                    return;
                } else {
                    break;
                }
            }
        } else {
            for (int i = 1; i < 5; i++) {
                if (Objects.requireNonNull(field)[position + i] == TypeField.HURT.getCode()) {
                    temp.add(position + i);
                } else if (field[position + i] == TypeField.SHIP.getCode() || field[position + i] == TypeField.DESTROY.getCode()) {
                    return;
                } else {
                    break;
                }
                if (position + i % 10 == 9) {
                    break;
                }
            }
            for (int i = 1; i < 5 - temp.size(); i++) {
                if (field[position - i] == TypeField.HURT.getCode()) {
                    temp.add(position - i);
                } else if (field[position - i] == TypeField.SHIP.getCode() || field[position + i] == TypeField.DESTROY.getCode()) {
                    return;
                } else {
                    break;
                }
                if (position - i % 10 == 0) {
                    break;
                }

            }
        }

        if (temp.size() != 1) {
            Collections.sort(temp);
            destroyShip(temp, true);
        } else if (position < 10) {
            for (int i = 1; i < 5; i++) {
                if (field[position + i * 10] == TypeField.HURT.getCode()) {
                    temp.add(position + i * 10);
                } else if (field[position + i * 10] == TypeField.SHIP.getCode() || field[position + i] == TypeField.DESTROY.getCode()) {
                    return;
                } else {
                    break;
                }
            }
        } else if (position > 89) {
            for (int i = 1; i < 5; i++) {
                if (field[position - i * 10] == TypeField.HURT.getCode()) {
                    temp.add(position - i * 10);
                } else if (field[position - i * 10] == TypeField.SHIP.getCode() || field[position + i] == TypeField.DESTROY.getCode()) {
                    return;
                } else {
                    break;
                }
            }
        } else {
            for (int i = 1; i < 5; i++) {
                if (field[position + i * 10] == TypeField.HURT.getCode()) {
                    temp.add(position + i * 10);
                } else if (field[position + i * 10] == TypeField.SHIP.getCode() || field[position + i] == TypeField.DESTROY.getCode()) {
                    return;
                } else {
                    break;
                }
                if (position + i > 89) {
                    break;
                }
            }
            for (int i = 1; i < 5 - temp.size(); i++) {
                if (field[position - i * 10] == TypeField.HURT.getCode()) {
                    temp.add(position - i * 10);
                } else if (field[position - i * 10] == TypeField.SHIP.getCode() || field[position + i] == TypeField.DESTROY.getCode()) {
                    return;
                } else {
                    break;
                }
                if (position - i < 10) {
                    break;
                }
            }
            Collections.sort(temp);
            destroyShip(temp, false);
        }
    }


    public void destroyShip(ArrayList<Integer> ship, boolean horizonatal) {
        Map<String, Object> temp = new HashMap<>();
        if (horizonatal) {
            for (int element : ship) {
                temp.put(String.valueOf(element), 4);
                if (element > 9) {
                    temp.put(String.valueOf(element - 10), 3);
                }
                if (element < 90) {
                    temp.put(String.valueOf(element + 10), 3);
                }
            }
            int edge = ship.get(0);
            if (edge % 10 != 0) {
                temp.put(String.valueOf(edge - 1), 3);
                if (edge > 9) {
                    temp.put(String.valueOf(edge - 11), 3);
                }
                if (edge < 90) {
                    temp.put(String.valueOf(edge + 9), 3);
                }
            }
            edge = ship.get(ship.size() - 1);
            if (edge % 10 != 9) {
                temp.put(String.valueOf(edge + 1), 3);
                if (edge > 9) {
                    temp.put(String.valueOf(edge - 9), 3);
                }
                if (edge < 90) {
                    temp.put(String.valueOf(edge + 11), 3);
                }
            }
        } else {
            for (int element : ship) {
                temp.put(String.valueOf(element), 4);
                if (element % 10 != 0) {
                    temp.put(String.valueOf(element - 1), 3);
                }
                if (element % 10 != 9) {
                    temp.put(String.valueOf(element + 11), 3);
                }
            }
            int edge = ship.get(0);
            if (edge > 9) {
                temp.put(String.valueOf(edge - 10), 3);
                if (edge % 10 != 0) {
                    temp.put(String.valueOf(edge - 11), 3);
                }
                if (edge % 10 != 9) {
                    temp.put(String.valueOf(edge - 9), 3);
                }
            }
            edge = ship.get(ship.size() - 1);
            if (edge < 90) {
                temp.put(String.valueOf(edge + 10), 3);
                if (edge % 10 != 0) {
                    temp.put(String.valueOf(edge + 9), 3);
                }
                if (edge % 10 != 9) {
                    temp.put(String.valueOf(edge + 11), 3);
                }
            }
        }
        destroy.setValue(temp);
    }

    public LiveData<Map<String, Object>> getDestoy() {
        return destroy;
    }

    public void setIcon(int[] temp) {
        for (int i = 0; i < temp.length; i++) {
            if (temp[i] == 0) {
                temp[i] = TypeField.EMPTY.getCode();
            } else if (temp[i] == 2) {
                temp[i] = TypeField.HURT.getCode();
            } else if (temp[i] == 3) {
                temp[i] = TypeField.LOSE.getCode();
            } else if (temp[i] == 4) {
                temp[i] = TypeField.DESTROY.getCode();
            } else {
                temp[i] = TypeField.SHIP.getCode();
            }
        }
        iconId.setValue(temp);
    }

    @Override
    public LiveData<int[]> getIcon() {
        return iconId;
    }
}
