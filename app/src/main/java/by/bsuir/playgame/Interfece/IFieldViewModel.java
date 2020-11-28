package by.bsuir.playgame.Interfece;

import androidx.lifecycle.LiveData;

public interface IFieldViewModel {

    void setPoint(String point);
    LiveData<int[]> getIcon();

}
