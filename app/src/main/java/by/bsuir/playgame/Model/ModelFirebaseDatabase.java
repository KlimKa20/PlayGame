package by.bsuir.playgame.Model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

public class ModelFirebaseDatabase {

    private final FirebaseDatabase database;

    public ModelFirebaseDatabase() {
        database = FirebaseDatabase.getInstance();
    }

    public void updateChild(String path, Map<String, Object> values) {
        database.getReference(path).updateChildren(values);
    }

    public void setValue(String path, Object value) {
        database.getReference(path).setValue(value);
    }

    public DatabaseReference getRef(String path) {
        return database.getReference(path);
    }

    public String push(String path) {
        return database.getReference(path).push().getKey();
    }

    public void remove(String path) {
        database.getReference(path).removeValue();
    }

}
