package by.bsuir.playgame.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import by.bsuir.playgame.Enum.ShipType;
import by.bsuir.playgame.Enum.TypeField;
import by.bsuir.playgame.Interfece.IFieldViewModel;
import by.bsuir.playgame.Model.ModelFirebaseAuth;
import by.bsuir.playgame.Model.ModelFirebaseDatabase;
import by.bsuir.playgame.R;

import static by.bsuir.playgame.Enum.StatusGame.PLACEMENT_START;
import static by.bsuir.playgame.Enum.StatusGame.START_GAME;
import static by.bsuir.playgame.Enum.StatusGame.WAITING_SECOND_PLAYER;

public class ShipViewModel extends AndroidViewModel implements IFieldViewModel {
    private final MutableLiveData<Integer> countPoint = new MutableLiveData<>();
    private final MutableLiveData<Integer> resultOfSetting = new MutableLiveData<>();
    private final MutableLiveData<int[]> iconId = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<String> showDialog = new MutableLiveData<>();
    private final MutableLiveData<Boolean> startGame = new MutableLiveData<>();

    boolean deleter = false;
    String tempPoints = "";
    String connectionString = "";
    String roomName = "";

    ModelFirebaseAuth firebaseAuth;
    ModelFirebaseDatabase firebaseDatabase;

    public ShipViewModel(@NonNull Application application) {
        super(application);
        firebaseAuth = new ModelFirebaseAuth();
        firebaseDatabase = new ModelFirebaseDatabase();
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

    public LiveData<Integer> getResultOfSetShip() {
        return resultOfSetting;
    }

    public LiveData<String> getShowDialog() {
        return showDialog;
    }

    public LiveData<Boolean> getStartGame() {
        return startGame;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<int[]> getIcon() {

        return iconId;
    }

    public void initPage(String roomName) {
        this.roomName = roomName;
        firebaseDatabase.getRef("rooms/" + roomName + "/p1" + "/user").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String message;
                if (Objects.requireNonNull(dataSnapshot.getValue()).toString().equals(firebaseAuth.getUIDUser())) {
                    connectionString = "/rooms/" + roomName + "/p1" + "/Field";
                    showDialog.setValue(getApplication().getString(R.string.Waiting_request) + getApplication().getString(R.string.Id_room) + roomName);
                    message = WAITING_SECOND_PLAYER.getName();
                } else {
                    message = PLACEMENT_START.getName();
                    connectionString = "/rooms/" + roomName + "/p2" + "/Field";
                }
                firebaseDatabase.setValue("rooms/" + roomName + "/message", message);
                addRoomEventListener();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void addRoomEventListener() {
        firebaseDatabase.getRef("rooms/" + roomName + "/message").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (Objects.requireNonNull(snapshot.getValue(String.class)).equals(PLACEMENT_START.getName())) {
                    startGame.setValue(false);
                } else if (Objects.requireNonNull(snapshot.getValue(String.class)).equals(START_GAME.getName())) {
                    firebaseDatabase.getRef("rooms/" + roomName + "/message").removeEventListener(this);
                    startGame.setValue(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void buttle() {
        firebaseDatabase.getRef("rooms/" + roomName + "/message").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String message;
                if (Objects.requireNonNull(dataSnapshot.getValue()).toString().equals(PLACEMENT_START.getName())) {
                    showDialog.setValue(getApplication().getString(R.string.Waiting_request));
                    message = WAITING_SECOND_PLAYER.getName();
                } else {
                    message = START_GAME.getName();
                }
                firebaseDatabase.setValue("rooms/" + roomName + "/message", message);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void changeIconDB(int[] s) {
        if (connectionString != null) {
            Map<String, Object> values = new HashMap<>();
            for (int i = 0; i < s.length; i++)
                if (s[i] == TypeField.EMPTY.getCodeImage()) {
                    values.put(String.valueOf(i), TypeField.EMPTY.getCodeField());
                } else {
                    values.put(String.valueOf(i), TypeField.SHIP.getCodeField());
                }
            firebaseDatabase.updateChild(connectionString, values);
        }
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
        changeIconDB(temperIconId);
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
