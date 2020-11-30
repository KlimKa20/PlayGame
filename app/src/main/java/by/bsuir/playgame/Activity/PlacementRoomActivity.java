package by.bsuir.playgame.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

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
import by.bsuir.playgame.TypeField;
import by.bsuir.playgame.ViewModel.ShipViewModel;

public class PlacementRoomActivity extends AppCompatActivity {

    ShipViewModel shipViewModel;
    Button button5, button4, button3, button2, button1, buttlebutton;
    TextView textView4, textView3, textView2, textView1;
    String roomName;
    String role = "";
    String message = "";
    String connectionString;

    private ProgressDialog progressDialog;
    private FirebaseDatabase database;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference messageRef, myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placement_room);
        Objects.requireNonNull(getSupportActionBar()).hide();

        Intent postIntent = getIntent();
        roomName = postIntent.getStringExtra("roomName");
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("rooms/" + roomName).child("p1").child("user");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (Objects.requireNonNull(dataSnapshot.getValue()).toString().equals(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid())) {
                    connectionString = "/rooms/" + roomName + "/p1" + "/Field";
                    progressDialog.setMessage("Please wait second player\n ID room: " + roomName);
                    progressDialog.show();
                    message = "Waiting second player";
                    progressDialog.setCanceledOnTouchOutside(false);
                } else {
                    message = "Placement Start";
                    connectionString = "/rooms/" + roomName + "/p2" + "/Field";
                }
                messageRef = database.getReference("rooms/" + roomName + "/message");
                messageRef.setValue(message);
                addRoomEventListener();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        shipViewModel = ViewModelProviders.of(this).get(ShipViewModel.class);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        button5 = findViewById(R.id.button5);
        buttlebutton = findViewById(R.id.buttle);
        textView1 = findViewById(R.id.textView6);
        textView2 = findViewById(R.id.textView5);
        textView3 = findViewById(R.id.textView4);
        textView4 = findViewById(R.id.textView2);
        button1.setOnClickListener(item -> {
            if (Integer.parseInt(textView1.getText().toString()) > 0)
                shipViewModel.setShip("1");
            else
                Toast.makeText(this, "Выберите другой тип корабля", Toast.LENGTH_SHORT).show();

        });
        button2.setOnClickListener(item -> {
            if (Integer.parseInt(textView2.getText().toString()) > 0)
                shipViewModel.setShip("2");
            else
                Toast.makeText(this, "Выберите другой тип корабля", Toast.LENGTH_SHORT).show();

        });
        button3.setOnClickListener(item -> {
            if (Integer.parseInt(textView3.getText().toString()) > 0)
                shipViewModel.setShip("3");
            else
                Toast.makeText(this, "Выберите другой тип корабля", Toast.LENGTH_SHORT).show();

        });
        button4.setOnClickListener(item -> {
            if (Integer.parseInt(textView4.getText().toString()) > 0)
                shipViewModel.setShip("4");
            else
                Toast.makeText(this, "Выберите другой тип корабля", Toast.LENGTH_SHORT).show();

        });

        buttlebutton.setOnClickListener(item -> {
            if (Integer.parseInt(textView1.getText().toString()) +
                    Integer.parseInt(textView2.getText().toString()) +
                    Integer.parseInt(textView3.getText().toString()) +
                    Integer.parseInt(textView4.getText().toString()) == 0) {
                Toast.makeText(this, "В бой!", Toast.LENGTH_SHORT).show();

                messageRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (Objects.requireNonNull(dataSnapshot.getValue()).toString().equals("Placement Start")) {
                            progressDialog.setMessage("Please wait second player");
                            progressDialog.show();
                            progressDialog.setCanceledOnTouchOutside(false);
                            message = "Waiting second player";
                        } else {
                            message = "Start Game";
                        }
                        messageRef.setValue(message);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else {
                Toast.makeText(this, "Раставленны не все корабли", Toast.LENGTH_SHORT).show();
            }

        });

        button5.setOnClickListener(item -> {
            if (Integer.parseInt(textView1.getText().toString()) +
                    Integer.parseInt(textView2.getText().toString()) +
                    Integer.parseInt(textView3.getText().toString()) +
                    Integer.parseInt(textView4.getText().toString()) != 10) {
                shipViewModel.setShip("-1");
            } else {
                Toast.makeText(this, "На поле нет кораблей", Toast.LENGTH_SHORT).show();
            }
        });
        shipViewModel.getError().observe(this, s -> Toast.makeText(this, s, Toast.LENGTH_SHORT).show());

        shipViewModel.getIcon().observe(this, s -> {
            if (connectionString != null) {
                Map<String, Object> values = new HashMap<>();
                for (int i = 0; i < s.length; i++)
                    if (s[i] == TypeField.EMPTY.getCode()) {
                        values.put(String.valueOf(i), 0);

                    } else {
                        values.put(String.valueOf(i), 1);

                    }
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put(connectionString, values);
                database.getReference().updateChildren(childUpdates);
            }
        });

        shipViewModel.getResultOfSetShip().observe(this, s -> {
            int sign = -1;
            if (s < 0) {
                sign = 1;
                s *= -1;
            }
            switch (s) {
                case 1:
                    textView1.setText(String.valueOf(Integer.parseInt(textView1.getText().toString()) + sign));
                    break;
                case 2:
                    textView2.setText(String.valueOf(Integer.parseInt(textView2.getText().toString()) + sign));
                    break;
                case 3:
                    textView3.setText(String.valueOf(Integer.parseInt(textView3.getText().toString()) + sign));
                    break;
                case 4:
                    textView4.setText(String.valueOf(Integer.parseInt(textView4.getText().toString()) + sign));
                    break;
            }
        });

    }

    private void addRoomEventListener() {
        messageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (Objects.requireNonNull(snapshot.getValue(String.class)).equals("Placement Start")) {
                    progressDialog.dismiss();
                }
                else if (Objects.requireNonNull(snapshot.getValue(String.class)).equals("Start Game")) {
                    progressDialog.dismiss();
                    Intent intent = new Intent(PlacementRoomActivity.this, RoomActivity.class);
                    intent.putExtra("roomName", roomName);
                    intent.putExtra("ViewModel", "ButtleView");
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                messageRef.setValue(message);
            }
        });
    }
}