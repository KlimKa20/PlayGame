package by.bsuir.playgame.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import by.bsuir.playgame.R;

public class DashboardActivity extends AppCompatActivity {
    private static final int REQUEST_HOST = 103;
    private static final int REQUEST_GUEST = 104;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private EditText textView;
    private String key;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        textView = findViewById(R.id.room);

        findViewById(R.id.logout).setOnClickListener(this::onLogOutClick);
        findViewById(R.id.create).setOnClickListener(this::onCreateClick);
        findViewById(R.id.connect).setOnClickListener(this::onConnectClick);
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "User Page");
        menu.add(0, 2, 0, "User Statistic");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case 1:
                intent = new Intent(this, UserPageActivity.class);
                break;
            case 2:
                intent = new Intent(this, StatisticActivity.class);
                break;
            default:
                intent = null;
        }
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

    public void moveToRoom(int index) {
        Intent intent = new Intent(DashboardActivity.this, PlacementRoomActivity.class);
        intent.putExtra("roomName", key);
        intent.putExtra("ViewModel", "Placement");
        startActivityForResult(intent, index);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_HOST) {
            if (resultCode == 0) {
                Toast.makeText(DashboardActivity.this, "Вы проиграли", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == REQUEST_GUEST) {
            if (resultCode == 0) {
                database.getReference("rooms/").child(key).removeValue();
                Toast.makeText(DashboardActivity.this, "Вы выиграли", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void onLogOutClick(View v) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void onCreateClick(View v) {
        String roomName = textView.getText().toString();
        if(roomName.isEmpty()){
            Toast.makeText(DashboardActivity.this, "Field is empty", Toast.LENGTH_LONG).show();
            return;
        }
        key = database.getReference().child("rooms").push().getKey();
        myRef = database.getReference().child("rooms");

        Map<String, Object> values = new HashMap<>();
        values.put("name", roomName);

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/rooms/" + key, values);
        database.getReference().updateChildren(childUpdates);

        values = new HashMap<>();
        values.put("user", Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid());
        values.put("ship", 10);

        childUpdates = new HashMap<>();
        childUpdates.put("/rooms/" + key + "/p1", values);
        database.getReference().updateChildren(childUpdates);
        moveToRoom(REQUEST_HOST);
    }

    private void onConnectClick(View v) {
        key = textView.getText().toString();
        if(key.isEmpty()){
            Toast.makeText(DashboardActivity.this, "Room doesn't exist", Toast.LENGTH_LONG).show();
            return;
        }
        myRef = database.getReference("rooms/").child(key);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    myRef.child("/p2").child("user").setValue(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid());
                    myRef.child("/p2").child("ship").setValue(10);
                    moveToRoom(REQUEST_GUEST);
                } else {
                    Toast.makeText(DashboardActivity.this, "Room doesn't exist", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
