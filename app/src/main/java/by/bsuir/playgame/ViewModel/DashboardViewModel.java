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

import by.bsuir.playgame.Activity.DashboardActivity;
import by.bsuir.playgame.Model.ModelFirebaseAuth;
import by.bsuir.playgame.Model.ModelFirebaseDatabase;
import by.bsuir.playgame.R;

public class DashboardViewModel extends AndroidViewModel {

    private final MutableLiveData<String> outputMessage = new MutableLiveData<>();
    private final MutableLiveData<HashMap<String, Object>> moveToRoom = new MutableLiveData<>();


    ModelFirebaseAuth firebaseAuth;
    ModelFirebaseDatabase firebaseDatabase;

    public DashboardViewModel(@NonNull Application application) {
        super(application);
        firebaseAuth = new ModelFirebaseAuth();
        firebaseDatabase = new ModelFirebaseDatabase();
    }

    public LiveData<String> getOutputMessage() {
        return outputMessage;
    }

    public LiveData<HashMap<String, Object>> getMoveToRoom() {
        return moveToRoom;
    }

    public void createRoom(String roomName) {
        if (roomName.isEmpty()) {
            outputMessage.setValue(getApplication().getString(R.string.Empty_testView));
            return;
        }
        String key = firebaseDatabase.push("rooms");

        Map<String, Object> values = new HashMap<>();
        values.put("name", roomName);
        firebaseDatabase.updateChild("/rooms/" + key, values);

        values = new HashMap<>();
        values.put("user", firebaseAuth.getUIDUser());
        values.put("ship", DashboardActivity.MAX_SHIP_COUNT);
        firebaseDatabase.updateChild("/rooms/" + key + "/p1", values);

        HashMap<String, Object> value = new HashMap<>();
        value.put("role", DashboardActivity.REQUEST_HOST);
        value.put("name", key);
        moveToRoom.setValue(value);
    }

    public void connectToRoom(String key) {
        if (key.isEmpty()) {
            outputMessage.setValue(getApplication().getString(R.string.Room_doesn_t_exist));
            return;
        }
        firebaseDatabase.getRef("rooms/" + key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    firebaseDatabase.setValue("rooms/" + key + "/p2/user", firebaseAuth.getUIDUser());
                    firebaseDatabase.setValue("rooms/" + key + "/p2/ship", DashboardActivity.MAX_SHIP_COUNT);
                    HashMap<String, Object> value = new HashMap<>();
                    value.put("role", DashboardActivity.REQUEST_GUEST);
                    value.put("name", key);
                    moveToRoom.setValue(value);
                } else {
                    outputMessage.setValue(getApplication().getString(R.string.Room_doesn_t_exist));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void removeRoom() {
        firebaseDatabase.remove("rooms/" + moveToRoom.getValue().get("name").toString());
    }

    public void signOut() {
        firebaseAuth.signOut();
    }
}
