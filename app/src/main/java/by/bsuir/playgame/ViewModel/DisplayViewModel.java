package by.bsuir.playgame.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import by.bsuir.playgame.Activity.PlacementRoomActivity;
import by.bsuir.playgame.Enum.TypeField;
import by.bsuir.playgame.Interfece.IFieldViewModel;
import by.bsuir.playgame.Model.ModelFirebaseAuth;
import by.bsuir.playgame.Model.ModelFirebaseDatabase;

public class DisplayViewModel extends AndroidViewModel implements IFieldViewModel {

    private final MutableLiveData<int[]> iconId = new MutableLiveData<>();
    private final MutableLiveData<Boolean> finish = new MutableLiveData<>();
    private final MutableLiveData<Integer> RestShip = new MutableLiveData<>();
    ModelFirebaseAuth firebaseAuth;
    ModelFirebaseDatabase firebaseDatabase;

    String role;
    String displayRef;
    String restRef;
    String roomName;
    String staticticRef;

    public DisplayViewModel(@NonNull Application application) {
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


    @Override
    public LiveData<int[]> getIcon() {
        return iconId;
    }

    @Override
    public void setPoint(String point) {

    }

    public LiveData<Boolean> getFinish() {
        return finish;
    }

    public LiveData<Integer> getRestShip() {
        return RestShip;
    }

    public void Init(String roomName) {
        this.roomName = roomName;
        firebaseDatabase.getRef("rooms/" + roomName + "/p1/user").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (Objects.requireNonNull(dataSnapshot.getValue()).toString().equals(firebaseAuth.getUIDUser())) {
                    role = "host";
                    displayRef = "rooms/" + roomName + "/p1/Field";
                    restRef = "rooms/" + roomName + "/p1/ship";
                    staticticRef = "statistic/" + roomName + "/p1";
                } else {
                    role = "guest";
                    displayRef = "rooms/" + roomName + "/p2/Field";
                    restRef = "rooms/" + roomName + "/p2/ship";
                    staticticRef = "statistic/" + roomName + "/p2";

                }
                firebaseDatabase.setValue("rooms/" + roomName + "/message", role);
                readDisplayEventListener();
                addDisplayEventListener();
                addListenerOfFinish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void addListenerOfFinish() {
        firebaseDatabase.getRef(restRef).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                RestShip.setValue(Integer.parseInt(Objects.requireNonNull(snapshot.getValue()).toString()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        firebaseDatabase.getRef(restRef).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (Integer.parseInt(Objects.requireNonNull(snapshot.getValue()).toString()) == 0) {
                    firebaseDatabase.setValue("rooms/" + roomName + "/message", "Finish " + role);
                    createStatistic(PlacementRoomActivity.REST_SHIPS);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readDisplayEventListener() {
        firebaseDatabase.getRef(displayRef).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int[] temp = new int[100];
                for (DataSnapshot child : snapshot.getChildren()) {
                    temp[Integer.parseInt(Objects.requireNonNull(child.getKey()))] = Integer.parseInt(Objects.requireNonNull(child.getValue()).toString());
                }
                setIcon(temp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void addDisplayEventListener() {
        firebaseDatabase.getRef(displayRef).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                setIconId(Integer.parseInt(Objects.requireNonNull(snapshot.getKey())), Integer.parseInt(Objects.requireNonNull(snapshot.getValue()).toString()));
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void createStatistic(int count) {
        firebaseDatabase.setValue(staticticRef + "/name", firebaseAuth.getUIDUser());
        firebaseDatabase.setValue(staticticRef + "/ship", count);
        finish.setValue(true);

    }

    private void setIconId(int position, int type) {
        int[] temp = iconId.getValue();
        if (type == TypeField.HURT.getCodeField()) {
            Objects.requireNonNull(temp)[position] = TypeField.HURT.getCodeImage();
            checkDestroy(position);
        } else if (type == TypeField.LOSE.getCodeField()) {
            Objects.requireNonNull(temp)[position] = TypeField.LOSE.getCodeImage();
        } else if (type == TypeField.DESTROY.getCodeField()) {
            Objects.requireNonNull(temp)[position] = TypeField.DESTROY.getCodeImage();
        }
        iconId.setValue(temp);
    }

    private void checkDestroy(int position) {
        int[] field = iconId.getValue();
        ArrayList<Integer> temp = new ArrayList<>();
        temp.add(position);
        if (position % 10 == 0) {
            for (int i = 1; i < 5; i++) {
                if (Objects.requireNonNull(field)[position + i] == TypeField.HURT.getCodeImage()) {
                    temp.add(position + i);
                } else if (field[position + i] == TypeField.SHIP.getCodeImage() || field[position + i] == TypeField.DESTROY.getCodeImage()) {
                    return;
                } else {
                    break;
                }
            }
        } else if (position % 10 == 9) {
            for (int i = 1; i < 5; i++) {
                if (Objects.requireNonNull(field)[position - i] == TypeField.HURT.getCodeImage()) {
                    temp.add(position - i);
                } else if (field[position - i] == TypeField.SHIP.getCodeImage() || field[position + i] == TypeField.DESTROY.getCodeImage()) {
                    return;
                } else {
                    break;
                }
            }
        } else {
            for (int i = 1; i < 5; i++) {
                if (Objects.requireNonNull(field)[position + i] == TypeField.HURT.getCodeImage()) {
                    temp.add(position + i);
                } else if (field[position + i] == TypeField.SHIP.getCodeImage() || field[position + i] == TypeField.DESTROY.getCodeImage()) {
                    return;
                } else {
                    break;
                }
                if (position + i % 10 == 9) {
                    break;
                }
            }
            for (int i = 1; i < 5 - temp.size(); i++) {
                if (field[position - i] == TypeField.HURT.getCodeImage()) {
                    temp.add(position - i);
                } else if (field[position - i] == TypeField.SHIP.getCodeImage() || field[position + i] == TypeField.DESTROY.getCodeImage()) {
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
                if (field[position + i * 10] == TypeField.HURT.getCodeImage()) {
                    temp.add(position + i * 10);
                } else if (field[position + i * 10] == TypeField.SHIP.getCodeImage() || field[position + i] == TypeField.DESTROY.getCodeImage()) {
                    return;
                } else {
                    break;
                }
            }
        } else if (position > 89) {
            for (int i = 1; i < 5; i++) {
                if (field[position - i * 10] == TypeField.HURT.getCodeImage()) {
                    temp.add(position - i * 10);
                } else if (field[position - i * 10] == TypeField.SHIP.getCodeImage() || field[position + i] == TypeField.DESTROY.getCodeImage()) {
                    return;
                } else {
                    break;
                }
            }
        } else {
            for (int i = 1; i < 5; i++) {
                if (field[position + i * 10] == TypeField.HURT.getCodeImage()) {
                    temp.add(position + i * 10);
                } else if (field[position + i * 10] == TypeField.SHIP.getCodeImage() || field[position + i] == TypeField.DESTROY.getCodeImage()) {
                    return;
                } else {
                    break;
                }
                if (position + i > 89) {
                    break;
                }
            }
            for (int i = 1; i < 5 - temp.size(); i++) {
                if (field[position - i * 10] == TypeField.HURT.getCodeImage()) {
                    temp.add(position - i * 10);
                } else if (field[position - i * 10] == TypeField.SHIP.getCodeImage() || field[position + i] == TypeField.DESTROY.getCodeImage()) {
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
        firebaseDatabase.updateChild(displayRef, temp);
        int restShipValue = RestShip.getValue();
        firebaseDatabase.setValue(restRef, --restShipValue);
    }

    private void setIcon(int[] temp) {
        for (int i = 0; i < temp.length; i++) {
            if (temp[i] == TypeField.EMPTY.getCodeField()) {
                temp[i] = TypeField.EMPTY.getCodeImage();
            } else if (temp[i] == TypeField.HURT.getCodeField()) {
                temp[i] = TypeField.HURT.getCodeImage();
            } else if (temp[i] == TypeField.LOSE.getCodeField()) {
                temp[i] = TypeField.LOSE.getCodeImage();
            } else if (temp[i] == TypeField.DESTROY.getCodeField()) {
                temp[i] = TypeField.DESTROY.getCodeImage();
            } else {
                temp[i] = TypeField.SHIP.getCodeImage();
            }
        }
        iconId.setValue(temp);
    }
}
