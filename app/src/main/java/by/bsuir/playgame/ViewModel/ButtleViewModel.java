package by.bsuir.playgame.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import by.bsuir.playgame.Interfece.IFieldViewModel;

public class ButtleViewModel extends AndroidViewModel implements IFieldViewModel {
    public ButtleViewModel(@NonNull Application application) {
        super(application);
    }

    @Override
    public void setPoint(String point) {

    }

    @Override
    public LiveData<int[]> getIcon() {
        return null;
    }
}
