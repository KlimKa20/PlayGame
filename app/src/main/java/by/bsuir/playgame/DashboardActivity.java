package by.bsuir.playgame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class DashboardActivity extends AppCompatActivity {
    private Button logout, create, connect;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private String playerName;
    private EditText textView;
    private String roomName,key;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        Intent postIntent = getIntent();
        playerName = postIntent.getStringExtra("playerEmail");

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
            values.put("p1", playerName);
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/rooms/" + key, values);
            database.getReference().updateChildren(childUpdates);
            moveToRoom();

        });

        connect.setOnClickListener(v -> {
            roomName = textView.getText().toString();
            myRef  = database.getReference("rooms/").child(roomName);
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        myRef = myRef.child("/p2");
                        myRef.setValue(playerName);
                        moveToRoom();
                    }
                    else
                    {
                        Toast.makeText(DashboardActivity.this,"Room doesn't exist",Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        });
    }

    public void moveToRoom(){
        Intent intent = new Intent(DashboardActivity.this, RoomActivity.class);
        intent.putExtra("roomName", roomName);
        intent.putExtra("playerName", playerName);
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


    private void addRoomEventListener() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Intent intent = new Intent(DashboardActivity.this, RoomActivity.class);
                intent.putExtra("roomName", key);
                intent.putExtra("playerName", playerName);
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
