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

import by.bsuir.playgame.Enum.TypeField;
import by.bsuir.playgame.R;
import by.bsuir.playgame.ViewModel.ShipViewModel;

import static by.bsuir.playgame.Enum.ShipType.FOUR_SECTION;
import static by.bsuir.playgame.Enum.ShipType.ONE_SECTION;
import static by.bsuir.playgame.Enum.ShipType.THREE_SECTION;
import static by.bsuir.playgame.Enum.ShipType.TWO_SECTION;
import static by.bsuir.playgame.Enum.StatusGame.PLACEMENT_START;
import static by.bsuir.playgame.Enum.StatusGame.START_GAME;
import static by.bsuir.playgame.Enum.StatusGame.WAITING_SECOND_PLAYER;

public class PlacementRoomActivity extends AppCompatActivity {

    ShipViewModel shipViewModel;
    Button button5, button4, button3, button2, button1, buttlebutton;
    TextView textView4, textView3, textView2, textView1;
    String roomName;
    String message = "";
    String connectionString;

    private ProgressDialog progressDialog;
    private FirebaseDatabase database;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference messageRef, myRef;

    public static final int REST_SHIPS = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placement_room);
        Objects.requireNonNull(getSupportActionBar()).hide();

        Intent postIntent = getIntent();
        roomName = postIntent.getStringExtra(DashboardActivity.PARAM_INTENT_NAME_OF_ROOM);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("rooms/" + roomName).child("p1").child("user");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (Objects.requireNonNull(dataSnapshot.getValue()).toString().equals(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid())) {
                    connectionString = "/rooms/" + roomName + "/p1" + "/Field";
                    progressDialog.setMessage(getString(R.string.Waiting_request) + getString(R.string.Id_room) + roomName);
                    progressDialog.show();
                    message = WAITING_SECOND_PLAYER.getName();
                    progressDialog.setCanceledOnTouchOutside(false);
                } else {
                    message = PLACEMENT_START.getName();
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
            if (Integer.parseInt(textView1.getText().toString()) > REST_SHIPS)
                shipViewModel.setShip(ONE_SECTION);
            else
                Toast.makeText(this, getString(R.string.Choose_other_type_of_ship), Toast.LENGTH_SHORT).show();

        });
        button2.setOnClickListener(item -> {
            if (Integer.parseInt(textView2.getText().toString()) > REST_SHIPS)
                shipViewModel.setShip(TWO_SECTION);
            else
                Toast.makeText(this, getString(R.string.Choose_other_type_of_ship), Toast.LENGTH_SHORT).show();

        });
        button3.setOnClickListener(item -> {
            if (Integer.parseInt(textView3.getText().toString()) > REST_SHIPS)
                shipViewModel.setShip(THREE_SECTION);
            else
                Toast.makeText(this, getString(R.string.Choose_other_type_of_ship), Toast.LENGTH_SHORT).show();

        });
        button4.setOnClickListener(item -> {
            if (Integer.parseInt(textView4.getText().toString()) > REST_SHIPS)
                shipViewModel.setShip(FOUR_SECTION);
            else
                Toast.makeText(this, getString(R.string.Choose_other_type_of_ship), Toast.LENGTH_SHORT).show();

        });

        buttlebutton.setOnClickListener(item -> {
            if (Integer.parseInt(textView1.getText().toString()) +
                    Integer.parseInt(textView2.getText().toString()) +
                    Integer.parseInt(textView3.getText().toString()) +
                    Integer.parseInt(textView4.getText().toString()) == REST_SHIPS) {
                Toast.makeText(this, getString(R.string.Fight), Toast.LENGTH_SHORT).show();

                messageRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (Objects.requireNonNull(dataSnapshot.getValue()).toString().equals(PLACEMENT_START.getName())) {
                            progressDialog.setMessage(getString(R.string.Waiting_request));
                            progressDialog.show();
                            progressDialog.setCanceledOnTouchOutside(false);
                            message = WAITING_SECOND_PLAYER.getName();
                        } else {
                            message = START_GAME.getName();
                        }
                        messageRef.setValue(message);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else {
                Toast.makeText(this, getString(R.string.set_not_all_ships), Toast.LENGTH_SHORT).show();
            }

        });

        button5.setOnClickListener(item -> {
            if (Integer.parseInt(textView1.getText().toString()) +
                    Integer.parseInt(textView2.getText().toString()) +
                    Integer.parseInt(textView3.getText().toString()) +
                    Integer.parseInt(textView4.getText().toString()) != DashboardActivity.MAX_SHIP_COUNT) {
                shipViewModel.deleteShip();
            } else {
                Toast.makeText(this, getString(R.string.Field_is_empty), Toast.LENGTH_SHORT).show();
            }
        });
        shipViewModel.getError().observe(this, s -> Toast.makeText(this, s, Toast.LENGTH_SHORT).show());

        shipViewModel.getIcon().observe(this, s -> {
            if (connectionString != null) {
                Map<String, Object> values = new HashMap<>();
                for (int i = 0; i < s.length; i++)
                    if (s[i] == TypeField.EMPTY.getCodeImage()) {
                        values.put(String.valueOf(i), TypeField.EMPTY.getCodeField());
                    } else {
                        values.put(String.valueOf(i), TypeField.SHIP.getCodeField());
                    }
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put(connectionString, values);
                database.getReference().updateChildren(childUpdates);
            }
        });

        shipViewModel.getResultOfSetShip().observe(this, size -> {
            switch (size) {
                case -4:
                    textView4.setText(String.valueOf(Integer.parseInt(textView4.getText().toString()) + 1));
                    break;
                case -3:
                    textView3.setText(String.valueOf(Integer.parseInt(textView3.getText().toString()) + 1));
                    break;
                case -2:
                    textView2.setText(String.valueOf(Integer.parseInt(textView2.getText().toString()) + 1));
                    break;
                case -1:
                    textView1.setText(String.valueOf(Integer.parseInt(textView1.getText().toString()) + 1));
                    break;
                case 1:
                    textView1.setText(String.valueOf(Integer.parseInt(textView1.getText().toString()) - 1));
                    break;
                case 2:
                    textView2.setText(String.valueOf(Integer.parseInt(textView2.getText().toString()) - 1));
                    break;
                case 3:
                    textView3.setText(String.valueOf(Integer.parseInt(textView3.getText().toString()) - 1));
                    break;
                case 4:
                    textView4.setText(String.valueOf(Integer.parseInt(textView4.getText().toString()) - 1));
                    break;
            }
        });

    }

    private void addRoomEventListener() {
        messageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (Objects.requireNonNull(snapshot.getValue(String.class)).equals(PLACEMENT_START.getName())) {
                    progressDialog.dismiss();
                } else if (Objects.requireNonNull(snapshot.getValue(String.class)).equals(START_GAME.getName())) {
                    progressDialog.dismiss();
                    Intent intent = new Intent(PlacementRoomActivity.this, RoomActivity.class);
                    intent.putExtra(DashboardActivity.PARAM_INTENT_NAME_OF_ROOM, roomName);
                    intent.putExtra(DashboardActivity.PARAM_INTENT_TYPE_VIEWMODEL, DashboardActivity.TYPE_VIEWMODEL_BATTLE);
                    startActivity(intent);
                    messageRef.removeEventListener(this);
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