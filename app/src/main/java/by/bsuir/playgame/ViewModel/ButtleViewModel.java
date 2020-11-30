package by.bsuir.playgame.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Objects;

import by.bsuir.playgame.Interfece.IFieldViewModel;
import by.bsuir.playgame.TypeField;

public class ButtleViewModel extends AndroidViewModel implements IFieldViewModel {

    private final MutableLiveData<int[]> iconId = new MutableLiveData<>();
    private final MutableLiveData<String> shutElement = new MutableLiveData<>();


    public ButtleViewModel(@NonNull Application application) {
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
        shutElement.setValue(point);
    }

    public LiveData<String> getShutElement() {
        return shutElement;
    }

    public void setIconId(int position, int type) {
        int[] temp = iconId.getValue();
        if (type == 2) {
            Objects.requireNonNull(temp)[position] = TypeField.HURT.getCode();
        } else if (type == 3) {
            Objects.requireNonNull(temp)[position] = TypeField.LOSE.getCode();
        } else if (type == 4) {
            Objects.requireNonNull(temp)[position] = TypeField.DESTROY.getCode();
        }
        iconId.setValue(temp);
    }

    @Override
    public LiveData<int[]> getIcon() {
        return iconId;
    }
}
