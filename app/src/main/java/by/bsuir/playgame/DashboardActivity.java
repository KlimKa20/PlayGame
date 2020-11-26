package by.bsuir.playgame;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DashboardActivity extends AppCompatActivity {
    private Button logout, create, connect;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private EditText textView;
    private String roomName, key;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        logout = findViewById(R.id.logout);
        textView = findViewById(R.id.room);
        create = findViewById(R.id.create);
        connect = findViewById(R.id.connect);
        logout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        create.setOnClickListener(v -> {
            roomName = textView.getText().toString();
            key = database.getReference().child("rooms").push().getKey();
            myRef = database.getReference().child("rooms");
            Map<String, Object> values = new HashMap<>();
            values.put("name", roomName);
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/rooms/" + key, values);
            database.getReference().updateChildren(childUpdates);
            values = new HashMap<>();
            values.put("user", Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid());
            childUpdates = new HashMap<>();
            childUpdates.put("/rooms/" + key + "/p1", values);
            database.getReference().updateChildren(childUpdates);
            moveToRoom();

        });

        connect.setOnClickListener(v -> {
            key = textView.getText().toString();
            myRef = database.getReference("rooms/").child(key);
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        myRef = myRef.child("/p2").child("user");
                        myRef.setValue(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid());
                        moveToRoom();
                    } else {
                        Toast.makeText(DashboardActivity.this, "Room doesn't exist", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        });
    }

    public void moveToRoom() {
        Intent intent = new Intent(DashboardActivity.this, PlacementRoomActivity.class);
        intent.putExtra("roomName", key);
        startActivity(intent);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "UserPage");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent(this, UserPageActivity.class);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }
}
