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

import java.util.Objects;

import by.bsuir.playgame.Enum.TypeField;
import by.bsuir.playgame.Interfece.IFieldViewModel;
import by.bsuir.playgame.Model.ModelFirebaseAuth;
import by.bsuir.playgame.Model.ModelFirebaseDatabase;
import by.bsuir.playgame.R;

import static by.bsuir.playgame.Enum.StatusGame.FINISH_GUEST;
import static by.bsuir.playgame.Enum.StatusGame.FINISH_HOST;
import static by.bsuir.playgame.Enum.StatusGame.GUEST;
import static by.bsuir.playgame.Enum.StatusGame.HOST;

public class ButtleViewModel extends AndroidViewModel implements IFieldViewModel {

    private final MutableLiveData<int[]> iconId = new MutableLiveData<>();
    private final MutableLiveData<String> information = new MutableLiveData<>();
    private final MutableLiveData<Boolean> finish = new MutableLiveData<>();
    private final MutableLiveData<String> toast = new MutableLiveData<>();


    ModelFirebaseAuth firebaseAuth;
    ModelFirebaseDatabase firebaseDatabase;

    String roomName;
    String role;
    String buttleRef;
    String staticticRef;
    boolean available;
    int RestShip;


    public ButtleViewModel(@NonNull Application application) {
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
    public void setPoint(String point) {
        if (available) {
            firebaseDatabase.getRef(buttleRef + '/' + Integer.parseInt(point)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (Integer.parseInt(Objects.requireNonNull(snapshot.getValue()).toString()) == TypeField.EMPTY.getCodeField()) {
                        firebaseDatabase.setValue(buttleRef + Integer.parseInt(point), TypeField.LOSE.getCodeField());
                        firebaseDatabase.setValue("rooms/" + roomName + "/message", role);
                        available = false;
                        information.setValue(getApplication().getString(R.string.Opponent_move));
                    } else if (Integer.parseInt(snapshot.getValue().toString()) == TypeField.SHIP.getCodeField()) {
                        firebaseDatabase.setValue(buttleRef + Integer.parseInt(point), TypeField.HURT.getCodeField());
                    } else {
                        toast.setValue(getApplication().getString(R.string.Chose_other_field));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public LiveData<String> getInformation() {
        return information;
    }

    public LiveData<String> getToast() {
        return toast;
    }


    public LiveData<Boolean> getFinish() {
        return finish;
    }

    public void setRestShip(int restShip) {
        RestShip = restShip;
    }

    @Override
    public LiveData<int[]> getIcon() {
        return iconId;
    }

    public void Init(String roomName) {
        this.roomName = roomName;
        firebaseDatabase.getRef("rooms/" + roomName + "/p1/user").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (Objects.requireNonNull(dataSnapshot.getValue()).toString().equals(firebaseAuth.getUIDUser())) {
                    role = "host";
                    buttleRef = "rooms/" + roomName + "/p2/Field";
                    staticticRef = "statistic/" + roomName + "/p1";
                } else {
                    role = "guest";
                    buttleRef = "rooms/" + roomName + "/p1/Field";
                    staticticRef = "statistic/" + roomName + "/p2";
                }
                firebaseDatabase.getRef("rooms/" + roomName + "/message").setValue(role);
                addButtleEventListener();
                addRoomEventListener();
                addWriterNameOfRoom();
                information.setValue(getApplication().getString(R.string.Opponent_move));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void addWriterNameOfRoom() {
        firebaseDatabase.getRef("rooms/" + roomName + "/name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                firebaseDatabase.setValue("statistic/" + roomName + "/name", Objects.requireNonNull(snapshot.getValue()).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void addButtleEventListener() {
        firebaseDatabase.getRef(buttleRef).addChildEventListener(new ChildEventListener() {
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

    private void addRoomEventListener() {
        firebaseDatabase.getRef("rooms/" + roomName + "/message").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (role.equals("host")) {
                    if (Objects.equals(snapshot.getValue(String.class), GUEST.getName())) {
                        available = true;
                        information.setValue(getApplication().getString(R.string.your_move));
                    } else if (Objects.equals(snapshot.getValue(String.class), FINISH_GUEST.getName())) {
                        available = false;
                        createStatistic(RestShip);
                    }
                } else {
                    if (Objects.equals(snapshot.getValue(String.class), HOST.getName())) {
                        available = true;
                        information.setValue(getApplication().getString(R.string.your_move));
                    } else if (Objects.equals(snapshot.getValue(String.class), FINISH_HOST.getName())) {
                        available = false;
                        createStatistic(RestShip);
                    }
                }
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

    public void setIconId(int position, int type) {
        int[] temp = iconId.getValue();
        if (type == TypeField.HURT.getCodeField()) {
            Objects.requireNonNull(temp)[position] = TypeField.HURT.getCodeImage();
        } else if (type == TypeField.LOSE.getCodeField()) {
            Objects.requireNonNull(temp)[position] = TypeField.LOSE.getCodeImage();
        } else if (type == TypeField.DESTROY.getCodeField()) {
            Objects.requireNonNull(temp)[position] = TypeField.DESTROY.getCodeImage();
        }
        iconId.setValue(temp);
    }


}
